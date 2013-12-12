package game.client;

import java.util.Collections;
import java.util.Map;

import game.entities.GameState;
import game.server.Game;

/**
 * 
 * @author kcorman
 * An extension of the GameClientModel, this class defines a model which
 * internally uses a game.server.Game to update its state, but can also be synchronized periodically
 * presumably by a server
 */
public class SynchronizingGameClientModel extends GameClientModel{
	private Game game;
	
	public SynchronizingGameClientModel(){
		Map<Integer, lobby.User> emptyMap = Collections.emptyMap();
		game = new Game(emptyMap);
		game.start();
	}
	
	@Override
	public void setState(GameState currentState) {
		game.setState(currentState);
	}
	
	public GameState getState() {
		return game.getState();
	}
}
