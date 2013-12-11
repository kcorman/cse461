package game.client;

import game.entities.GameState;

/**
 * 
 * @author kcorman
 * A class designed for use with the graphical front end of the pong game
 * This will update a pong state so that the graphical display will be smoother
 * 
 */
public class StateSmoother {
	/**
	 * Updates s based on its current settings if the state has not been updated recently
	 * @param s the state to update
	 * @modifies s
	 */
	public static void smoothState(GameState s){
		//only smooth the state if it has been longer than the minimum update time
				if((System.currentTimeMillis() - s.timeUpdated) > GameState.TIME_BETWEEN_UPDATES_MS){
					s.ballX+=s.ballDx;
					s.ballY+=s.ballDy;
					
				}
	}
}
