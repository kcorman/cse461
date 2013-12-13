package lobby;

import game.client.GameClientConnection;
import game.client.GameClientModel;
import game.client.GameClientNetworkConnection;
import game.client.GameClientWindow;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import lobby.DemocracyConstants.ClientOption;
import lobby.DemocracyConstants.ServerOption;
import lobby.LobbyState.Room;


public class GameLobbyClient extends Thread implements ConnectionBean {
	private LobbyState lobbyState;
	private ObjectOutputStream out;
	private String hostname;
	private Socket socket;
	private int uid;
	
	public GameLobbyClient() {
		lobbyState = new LobbyStateImpl(new ArrayList<Room>());
		uid = -1;
		hostname = null;
	}
	
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
		    			int port = in.readInt();
		    			System.out.printf("Received id= %d, port =%d\nTrying to connect now...\n",uid,port);
		    			GameClientModel m = new GameClientModel();
		    			GameClientWindow w = new GameClientWindow(m);
		    			GameClientConnection c = new GameClientNetworkConnection(hostname, port, uid, w, m);
		    			boolean success = c.connect();
		    			if(!success){
		    				System.err.println("Could not connect");
		    			}else{
		    				w.run();
		    			}
		    			break;
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
	public void connect(String hostname, int port) throws UnknownHostException, IOException {
		this.hostname = hostname;
		InetAddress addr = InetAddress.getByName(hostname);
		socket = new Socket(addr, port);
		
	}

	@Override
	public LobbyState getLobbyState() {
		return lobbyState;
	}
}
