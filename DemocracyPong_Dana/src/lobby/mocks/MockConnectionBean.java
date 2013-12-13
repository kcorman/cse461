package lobby.mocks;

import java.io.IOException;
import java.net.UnknownHostException;

import lobby.ConnectionBean;
import lobby.LobbyState;
import lobby.LobbyStateImpl;

/**
 * 
 * @author kcorman
 * A skeletal implementation of ConnectionBean that cleanly returns upon calling
 * connect
 */
public class MockConnectionBean implements ConnectionBean{
	LobbyState state;
	
	public MockConnectionBean(){
		state = new LobbyStateImpl(null);
	}

	@Override
	public int getUserID() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void joinRoom(int roomID) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hostRoom() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void leaveRoom() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startGame() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void connect(String hostname, int port) throws UnknownHostException,
			IOException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public LobbyState getLobbyState() {
		// TODO Auto-generated method stub
		return null;
	}

}
