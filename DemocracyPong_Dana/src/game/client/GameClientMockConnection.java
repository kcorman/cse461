package game.client;

import game.entities.GameState;

import java.util.concurrent.Executors;

public class GameClientMockConnection implements GameClientConnection, Runnable{
	static final double MAX_BOUNCE_ANGLE = Math.PI/3;
	double BALL_SPEED = 25;
	GameMousePositionSource src;
	GameClientModel m;
	//AI fields
	int aiSpeed = 30;
	boolean goingUp = false;
	double aiNoise = .9;
	//
	int serveCounter = 20; //positive number indicates waiting to serve
	boolean isServing = false;
	
	
	public GameClientMockConnection(GameMousePositionSource src, GameClientModel m){
		this.src = src;
		this.m = m;
	}
	@Override
	public boolean connect() {
		Executors.newSingleThreadExecutor().execute(this);
		return true;
	}
	@Override
	public void run() {
		initializeGameState();
		while(true){
			updateBall();
			updateOpponent();
			m.getState().leftPaddleY = src.getMouseY();//+noise();
			//serialize and deserialize state to test functionality
			m.setState(GameState.fromBytes(m.getState().toBytes()));
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void initializeGameState(){
		GameState s = new GameState();
		m.setState(s);
		serveBall();
	}
	
	/**
	 * Updates the ball in the game state
	 */
	public void updateBall(){
		if(isServing){
			serveBall();
			return;
		}
		GameState s = m.getState();
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
				int relativeIntersectY = (s.rightPaddleY+(s.paddleHeight/2)) - (s.ballY+GameState.BALL_SIZE/2);
				//normalize
				double normalized = ((double)relativeIntersectY)/s.paddleHeight;
				System.out.println("Normalized = "+normalized);
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
		GameState s = m.getState();
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
	
	public void updateOpponent(){
		//assume ai is right side
		GameState s = m.getState();
		if(s.ballY > s.getRightPaddleY()+s.paddleHeight/2){
			if(goingUp = !(Math.random() > aiNoise));
		}else if(s.ballY < s.getRightPaddleY()){
			if(goingUp = (Math.random() > aiNoise));;
		}
		if(goingUp){
			s.rightPaddleY += aiSpeed;
		}else{
			s.rightPaddleY -= aiSpeed;
		}
	}
	
	private int noise(){
		return (int)(Math.random()*60)-30;
	}
	
	
}
