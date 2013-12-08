package game.server;

import game.entities.ClientState;
import game.entities.GameState;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import lobby.User;

/**
 * The logic for a game of pong.
 * @author Jon
 */
public class Game implements Runnable {
	public enum Team { LEFT, RIGHT };
	static final double MAX_BOUNCE_ANGLE = 3*Math.PI/4;	// Should go in GameState
	double BALL_SPEED = 25; // should go in GameState
	int serveCounter = 20;	// probably replace this with some kind of sleep()
	private boolean isServing;
	
	private GameState state;			// state != null
	private Map<Integer, User> players;	// players != null
	private StateUpdater updater;		// can be null
	
	public Game(Map<Integer, User> p) {
		if (p == null)
			throw new NullPointerException();
		
		players = p;
    	state = new GameState();
    	isServing = true;
	}
	
	@Override
	public void run() {
		updater.start();
		
		while (true) {
			updateBall();
			updatePaddles();
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
		if(s.ballY > s.lowerBoundsY){
			s.ballY = s.lowerBoundsY - GameState.BALL_SIZE;
			s.ballDy *= -1;
		}else if(s.ballY < s.upperBoundsY){
			s.ballY = s.upperBoundsY;
			s.ballDy *= -1;
		}
		//check for paddle collision or miss
		if(s.ballX < s.leftPaddleX+s.paddleWidth){
			if(s.ballY+GameState.BALL_SIZE > s.leftPaddleY && s.ballY < s.leftPaddleY + s.paddleHeight){
				//bounce off left paddle
				//s.ballDx *= -1;
				s.ballX = s.leftPaddleX+s.paddleWidth;
				int relativeIntersectY = (s.leftPaddleY+(s.paddleHeight/2)) - (s.ballY+GameState.BALL_SIZE/2);
				//normalize
				double normalized = ((double)relativeIntersectY)/s.paddleHeight;
				double angle = normalized * MAX_BOUNCE_ANGLE;
				s.ballDx = (int)Math.abs(BALL_SPEED *Math.cos(angle));
				s.ballDy = (int)(BALL_SPEED *-Math.sin(angle));
			}
			else{
				s.rightScore++;
				serveBall();
			}
		}
		
		if(s.ballX+GameState.BALL_SIZE > s.rightPaddleX){
			if(s.ballY+GameState.BALL_SIZE > s.rightPaddleY && s.ballY < s.rightPaddleY + s.paddleHeight){
				//bounce off right paddle
				s.ballX = s.rightPaddleX-GameState.BALL_SIZE;
				int relativeIntersectY = (s.leftPaddleY+(s.paddleHeight/2)) - (s.ballY+GameState.BALL_SIZE/2);
				//normalize
				double normalized = ((double)relativeIntersectY)/s.paddleHeight;
				double angle = normalized * MAX_BOUNCE_ANGLE;
				s.ballDx = (int)-Math.abs((BALL_SPEED *Math.cos(angle)));
				s.ballDy = (int)(BALL_SPEED *-Math.sin(angle));
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
		if(!isServing){
			isServing = true;
			//Just started serving
			serveCounter = 20;
			s.ballX = -50;	//offscreen
			s.ballDx = 0;
			s.ballDy = 0;
			return;
		}
		//tick counter down
		if(isServing && serveCounter > 0){
			serveCounter--;
			return;
		}
		//actual logic
		s.ballDx = (int)(BALL_SPEED * Math.sin(Math.PI/4));
		s.ballDy = (int)(BALL_SPEED * Math.cos(Math.PI/4));
		s.ballX = s.leftPaddleX+GameState.BALL_SIZE;
		s.ballY= s.upperBoundsY;
		serveCounter--;
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
		if (updater == null) 
			return; 	// do nothing, ie keep default votes

		updateUserVotes(updater.getVotes());
		
		int leftVote = 0;
		int rightVote = 0;
		for (User p : players.values()) {
			if (p.getTeam() == Game.Team.LEFT)
				leftVote += p.getVote();
			else
				rightVote += p.getVote();
		}
		
		state.leftPaddleY = leftVote;
		state.rightPaddleY = rightVote;
	}
	
	/**
	 * Updates the votes for users in s
	 * @param s queue holding the users whose votes are to be updated
	 */
	private void updateUserVotes(Queue<ClientState> q) {
		for (ClientState cs : q) {
			User currUser = players.get(cs.userId);
			currUser.setVote(cs.yVote);
		}
	}
	
	public GameState getState() {
		return state;
	}
	
	public Map<Integer, User> getPlayers() {
		return players;
	}
	
	public StateUpdater getStateUpdater() {
		return updater;
	}
	
	public void setStateUpdater(StateUpdater updater) {
		this.updater = updater;
	}
}
