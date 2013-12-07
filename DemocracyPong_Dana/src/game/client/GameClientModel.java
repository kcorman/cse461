package game.client;

import game.entities.GameState;

public class GameClientModel {
	private GameState currentState = new GameState();
	private boolean isOnLeftSide = true;	//Whether or not the main player is on the left
											//side or not

	public GameState getState() {
		return currentState;
	}

	public void setState(GameState currentState) {
		this.currentState = currentState;
	}

	public boolean isOnLeftSide() {
		return isOnLeftSide;
	}

	public void setOnLeftSide(boolean isOnLeftSide) {
		this.isOnLeftSide = isOnLeftSide;
	}
}
