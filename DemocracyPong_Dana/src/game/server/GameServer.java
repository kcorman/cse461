package game.server;

/**
 * Responsible for sending clients up-to-date GameState, as well as
 * keeping votes from clients current.
 * @author Jonathan Ellington
 *
 */
public interface GameServer {
	
	/**
	 * Begin sending/receiving from clients
	 */
	public void start();

}