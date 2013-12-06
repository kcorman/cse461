package game.client;

import game.entities.GameState;

public class GameClientModel {
	private GameState currentState = new GameState();
	

	public GameState getState() {
		return currentState;
	}

	public void setState(GameState currentState) {
		this.currentState = currentState;
	}
}
