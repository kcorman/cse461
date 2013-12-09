package game.server;

/**
 * Runs a game, and keeps clients up-to-date with GameState.  Also responsible
 * for receiving client data and updating the players list appropriately.
 * @author Jonathan Ellington
 *
 */
public interface GameServer {
	
	/**
	 * Start a game and begin client communications.
	 */
	public void start();

}