package game.client;

import game.entities.GameState;
import game.server.Game;
import game.server.GamePlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class GameClientMockConnection implements GameClientConnection, Runnable{
	static final boolean USE_LAG_SIMULATION = true;
	static final double LAG_FACTOR = .5;	//Percentage of states to drop
	static final int USER_ID = 0;
	static final int AI_ID = 1;
	Map<Integer, GamePlayer> players;
	GameMousePositionSource src;
	GameClientModel m;
	Game game;
	GamePlayer userPlayer;
	//AI fields
	GamePlayer aiPlayer;
	int aiSpeed = 30;
	boolean goingUp = false;
	double aiNoise = 0.1;
	//
	int serveCounter = 20; //positive number indicates waiting to serve
	boolean isServing = false;
	
	
	public GameClientMockConnection(GameMousePositionSource src, GameClientModel m){
		this.src = src;
		this.m = m;
		userPlayer = new MockGamePlayer();
		aiPlayer = new MockGamePlayer();
		userPlayer.setTeam(Game.TEAM_LEFT);
		aiPlayer.setTeam(Game.TEAM_RIGHT);
		players = new HashMap<Integer, GamePlayer>();
		players.put(USER_ID,userPlayer);
		players.put(AI_ID,aiPlayer);
		
	}
	@Override
	public boolean connect() {
		Executors.newSingleThreadExecutor().execute(this);
		return true;
	}
	@Override
	public void run() {
		game = new Game(players);
		game.start();
		while(m.getState().isRunning()){
			/*
			 * All of the following methods internally update recentState, not m.getState()
			 */
			updateOpponent();
			userPlayer.setVote(src.getMouseY());
			//serialize and deserialize state to test functionality
			if(!USE_LAG_SIMULATION || Math.random() > LAG_FACTOR)
				m.setState(GameState.fromBytes(game.getState().toBytes()));
			try {
				Thread.sleep(GameState.TIME_BETWEEN_UPDATES_MS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	public void updateOpponent(){
		//assume ai is right side
		GameState s = game.getState();
		if(s.ballY > s.getRightPaddleY()+s.paddleHeight/2){
			goingUp = !(Math.random() > aiNoise);
		}else if(s.ballY < s.getRightPaddleY()){
			goingUp = (Math.random() > aiNoise);
		}
		if(goingUp){
			aiPlayer.setVote(aiPlayer.getVote() - aiSpeed);
		}else{
			aiPlayer.setVote(aiPlayer.getVote() + aiSpeed);
		}
		if(aiPlayer.getVote() > s.upperBoundsY){
			aiPlayer.setVote(s.upperBoundsY);
		}else if(aiPlayer.getVote() < s.lowerBoundsY){
			aiPlayer.setVote(s.lowerBoundsY);
		}
	}
	
	private int noise(){
		return (int)(Math.random()*60)-30;
	}
	
	
}
