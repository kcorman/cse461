package game.client;

import game.entities.GameState;

import java.util.concurrent.Executors;

public class GameClientMockConnection implements GameClientConnection, Runnable{
	GameMousePositionSource src;
	GameClientModel m;
	//AI fields
	int aiSpeed = 30;
	boolean goingUp = false;
	double aiNoise = .9;
	//
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
		GameState s = m.getState();
		if(s.ballY > s.lowerBoundsY){
			s.ballY = s.lowerBoundsY - GameState.BALL_SIZE;
			s.ballDy *= -1;
		}else if(s.ballY < s.upperBoundsY){
			s.ballY = s.upperBoundsY;
			s.ballDy *= -1;
		}
		//check for paddle collision or miss
		if(s.ballX < s.leftPaddleX+s.paddleWidth){
			if(s.ballY > s.leftPaddleY && s.ballY < s.leftPaddleY + s.paddleHeight){
				//bounce off left paddle
				s.ballDx *= -1;
				s.ballX = s.leftPaddleX+s.paddleWidth;
			}
			else{
				s.rightScore++;
				serveBall();
			}
		}
		
		if(s.ballX+GameState.BALL_SIZE > s.rightPaddleX){
			if(s.ballY > s.rightPaddleY && s.ballY < s.rightPaddleY + s.paddleHeight){
				//bounce off right paddle
				s.ballDx *= -1;
				s.ballX = s.rightPaddleX-GameState.BALL_SIZE;
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
		s.ballDx = 20;
		s.ballDy = 20;
		s.ballX = s.leftPaddleX;
		s.ballY= s.leftPaddleY;
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
