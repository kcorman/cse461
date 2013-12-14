package lobby;

import java.io.IOException;
import java.net.UnknownHostException;

/**
 * 
 * @author danava04
 * Interface used for communication between the client front-end and back-end.
 */
public interface ConnectionBean {
	
	public LobbyState getLobbyState();
	
	public int getUserID();
	
	public void joinRoom(int roomID);
	
	public void hostRoom();
	
	public void leaveRoom();
	
	public void startGame();
	
	/**
	 * Attempts to set the current user's username to the given name
	 * this does not guarantee that the name will be set
	 */
	public void setUserName(String name);
	
	public void connect(String hostname, int port) throws UnknownHostException, IOException;
}
