package lobby;

import java.io.IOException;
import java.net.UnknownHostException;

public interface ConnectionBean {
	
	public LobbyState getState();
	
	public int getUserID();
	
	public void joinRoom(int roomID);
	
	public void hostRoom();
	
	public void leaveRoom();
	
	public void startGame();
	
	public void connect(String hostname, int port) throws UnknownHostException, IOException;
}
