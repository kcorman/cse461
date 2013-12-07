package game.client;

/**
 * 
 * @author kenny
 * The GameClientConnection is responsible for keeping the Model up to date with the
 * actual game state received from the server, as well as specifying (on the model)
 * what side the local user is on
 */
public interface GameClientConnection {
	
	/**
	 * Returns true if this successfully connects, false otherwise
	 * @return
	 */
	public boolean connect();

}
