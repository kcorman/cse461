package game.server;

import game.entities.ClientState;
import game.entities.GameState;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import lobby.User;

/**
 * The logic for a game of pong.
 * @author Jon
 */
public class Game implements Runnable {
	// Game management
	// public enum Team { LEFT, RIGHT };
	public static final int TEAM_LEFT = 0, TEAM_RIGHT = 1;
	public static final int TIMEOUT = -1, DISCONNECT = -2;
	
	// Related to ball logic
	static final double MAX_BOUNCE_ANGLE = 3*Math.PI/8;
	static final double BALL_SPEED = 25;
	static final int SERVE_TIME = 100;			// in ms
	
	//static final int BALL_UPDATE_TIME = 100;	// in ms
	private boolean isServing;
	
	// Data
	private Map<Integer, ? extends GamePlayer> players;
	private GameState state;

	public Game(Map<Integer, ? extends GamePlayer> p) {
		if (p == null)
			throw new NullPointerException();
		
		players = p;
		state = new GameState();
    	isServing = true;
	}
	
	@Override
	public void run() {
		while (true) {
			//Synchronizing in case setState is called during this part
			synchronized(this){
				updateBall();
				updatePaddles();
			}
			try {
				Thread.sleep(GameState.TIME_BETWEEN_UPDATES_MS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Updates the ball's position
	 */
	/**
	 * Updates the ball in the game state
	 */
	public void updateBall(){
		if(isServing){
			serveBall();
			return;
		}
		GameState s = state;
		if(s.ballY > s.upperBoundsY){
			s.ballY = s.upperBoundsY - GameState.BALL_SIZE;
			s.ballDy *= -1;
		}else if(s.ballY < s.lowerBoundsY){
			s.ballY = s.lowerBoundsY;
			s.ballDy *= -1;
		}
		//check for paddle collision or miss
		if(s.ballX < s.leftPaddleX+s.paddleWidth){
			if(s.ballY+GameState.BALL_SIZE > s.leftPaddleY && s.ballY < s.leftPaddleY + s.paddleHeight){
				//bounce off left paddle
				s.ballDx *= -1;
				s.ballX = s.leftPaddleX+s.paddleWidth;
				int relativeIntersectY = (s.leftPaddleY+(s.paddleHeight/2)) - (s.ballY+GameState.BALL_SIZE/2);
				//normalize
				double normalized = ((double)relativeIntersectY)/s.paddleHeight;
				double angle = normalized * MAX_BOUNCE_ANGLE;
				// s.ballDx = (int)Math.abs(BALL_SPEED *Math.cos(angle));
				// s.ballDy = (int)(BALL_SPEED *-Math.sin(angle));
				s.ballDy = (int) (normalized * BALL_SPEED);
			}
			else{
				s.rightScore++;
				serveBall();
			}
		}
		
		if(s.ballX+GameState.BALL_SIZE > s.rightPaddleX){
			if(s.ballY+GameState.BALL_SIZE > s.rightPaddleY && s.ballY < s.rightPaddleY + s.paddleHeight){
				//bounce off right paddle
				s.ballDx *= -1;
				s.ballX = s.rightPaddleX-GameState.BALL_SIZE;
				int relativeIntersectY = (s.rightPaddleY+(s.paddleHeight/2)) - (s.ballY+GameState.BALL_SIZE/2);
				//normalize
				double normalized = ((double)relativeIntersectY)/s.paddleHeight;
				double angle = normalized * MAX_BOUNCE_ANGLE;
				// s.ballDx = (int)-Math.abs((BALL_SPEED *Math.cos(angle)));
				// s.ballDy = (int)(BALL_SPEED *-Math.sin(angle));
				s.ballDy = (int) (normalized * BALL_SPEED);
			}else{
				s.leftScore++;
				serveBall();
			}
		}
		
		s.ballX += s.ballDx;
		s.ballY += s.ballDy;
		
	}
	
	public void serveBall(){
		GameState s = state;
		/*
		 * Stuff related to waiting before serving
		 */
		isServing = true;
		
		s.ballX = -50;	//offscreen
		s.ballDx = 0;
		s.ballDy = 0;
		
		try {
			Thread.sleep(SERVE_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		//actual logic
		Random rgen = new Random();
		int xDir = rgen.nextInt(2);
		int yDir = rgen.nextInt(2);
		xDir = (xDir == 0) ? -1 : 1;
		yDir = (yDir == 0) ? -1 : 1;
		
		//s.ballDx = (int)(BALL_SPEED * Math.sin(Math.PI/4)) * xDir;
		s.ballDx = (int) BALL_SPEED * xDir;
		s.ballDy = 0;//(int)(BALL_SPEED * Math.cos(Math.PI/4)) * yDir;
		s.ballX = (s.leftPaddleX + s.rightPaddleX)/2;
		s.ballY= (s.upperBoundsY + s.lowerBoundsY)/2;
		isServing = false;
	}
	
	
	/**
	 * Updates the paddles based on all player's votes
	 * 
	 * @modifies state.leftPaddleY
	 * @modifies state.rightPaddleY
	 */
	private void updatePaddles() {
		// TODO ensure votes are somehow initialized to default value

		int leftVote = 0, rightVote = 0;
		int leftNum = 0, rightNum = 0;

		for (GamePlayer p : players.values()) {
			if (p.getTeam() == TEAM_LEFT) {
				leftVote += p.getVote();
				leftNum++;
			}
			else {
				rightVote += p.getVote();
				rightNum++;
			}
		}
		if (leftNum > 0)
			state.leftPaddleY = leftVote/leftNum;
		
		if (rightNum > 0)
			state.rightPaddleY = rightVote/rightNum;
	}
	
	public GameState getState() {
		return state;
	}
	
	/**
	 * Sets the current game state to the given state
	 * @param s
	 */
	public synchronized void setState(GameState s){
		state = s;
	}
	
	public Map<Integer, ? extends GamePlayer> getPlayers() {
		return players;
	}
	
	// TODO return boolean?
	public void start() {
		(new Thread(this)).start();
	}
}
