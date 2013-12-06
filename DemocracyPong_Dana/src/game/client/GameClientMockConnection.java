package game.client;

import java.util.concurrent.Executors;

public class GameClientMockConnection implements GameClientConnection, Runnable{
	GameMousePositionSource src;
	GameClientModel m;
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
		while(true){
			m.getState().leftPaddleY = src.getMouseY()+noise();
			m.getState().rightPaddleY = m.getState().rightPaddleY+noise();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private int noise(){
		return (int)(Math.random()*60)-30;
	}
	
	
}
