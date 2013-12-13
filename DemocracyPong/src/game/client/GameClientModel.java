package game.client;

import game.entities.GameState;

public class GameClientModel {
	private long timeStateSet = System.currentTimeMillis();
	private GameState currentState = new GameState();
	private boolean isOnLeftSide = true;	//Whether or not the main player is on the left
											//side or not
	

	public GameState getState() {
		return currentState;
	}

	public void setState(GameState currentState) {
		this.currentState = currentState;
		timeStateSet = System.currentTimeMillis();
	}

	public boolean isOnLeftSide() {
		return isOnLeftSide;
	}

	public void setOnLeftSide(boolean isOnLeftSide) {
		this.isOnLeftSide = isOnLeftSide;
	}
	
	/**
	 * Returns true if this game is over, false otherwise.
	 * For endless games, this will always return false
	 * @return
	 */
	public boolean isGameOver(){
		return currentState.maxPoints > 0 && (currentState.getLeftScore() >= currentState.maxPoints 
				|| currentState.getRightScore() >= currentState.maxPoints);
	}
	
	/**
	 * Returns true if this player has won, false if they have lost
	 * If this is called before the game is over (ie isGameOver() returns false),
	 * this will throw an illegalStateException
	 * @return true if this player has won, false otherwise
	 */
	public boolean hasWon(){
		if(! isGameOver()){
			throw new IllegalStateException("This should only be called after the game is over");
		}
		if(isOnLeftSide){
			return currentState.leftScore >= currentState.maxPoints;
		}else{
			return currentState.rightScore >= currentState.maxPoints;
		}
	}
	
	
}
