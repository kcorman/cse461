package lobby;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import lobby.DemocracyConstants.ClientOption;
import lobby.DemocracyConstants.ServerOption;


public class GameLobbyClient extends Thread implements ConnectionBean {
	private LobbyState lobbyState;
	private ObjectOutputStream out;
	private Socket socket;
	private int uid;
	
	public void run() {
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
		    
			
			// 1. read state object
			// 2. read corresponding object after
			ServerOption opt;
		    while (true) {
		    	opt = (ServerOption) in.readObject();
		    	switch(opt) {
		    		case UID:
		    			uid = in.readInt();
		    			break;
		    		case START:
		    			// do something!
		    		case UPDATE:
		    			lobbyState = (LobbyState) in.readObject();
		    			break;
		    	}
		    }


		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {}
		}
	}

	@Override
	public int getUserID() {
		return uid;
	}

	@Override
	public void joinRoom(int roomID) {
		try {
			out.writeObject(ClientOption.JOIN);
			out.writeInt(roomID);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void hostRoom() {
		try {
			out.writeObject(ClientOption.HOST);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void leaveRoom() {
		try {
			out.writeObject(ClientOption.LEAVE);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void startGame() {
		try {
			out.writeObject(ClientOption.START);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void connect(String hostname, int port) throws UnknownHostException, IOException{
		InetAddress addr = InetAddress.getByName(hostname);
		socket = new Socket(addr, port);
		
	}

	@Override
	public LobbyState getLobbyState() {
		return lobbyState;
	}
}
