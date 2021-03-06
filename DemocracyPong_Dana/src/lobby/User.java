package lobby;

import game.server.GamePlayer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketAddress;


public class User implements GamePlayer{
	// Lobby related fields
	private int userID;
	private Socket userSocket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	
	// Game related fields
	private SocketAddress address;
	private int yVote;
	private int team;
	private String name = null;
	
	public User(int userID, Socket userSocket) {
		this.userID = userID;
		this.userSocket = userSocket;
		try {
			out = new ObjectOutputStream(userSocket.getOutputStream());
			in = new ObjectInputStream(userSocket.getInputStream());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// included for game.server testing purposes
	public User(int userID, int team, int yVote) {
		this.userID = userID;
		this.team = team;
		this.yVote = yVote;
	}
	
	public int getUserID() {
		return userID;
	}
	
	public Socket getUserSocket() {
		return userSocket;
	}
	
	public ObjectOutputStream getUserOutputStream() {
		return out;
	}
	
	public ObjectInputStream getUserInputStream() {
		return in;
	}
	
	public int getVote() {
		return yVote;
	}
	
	public void setVote(int yVote) {
		this.yVote = yVote;
	}
	
	public int getTeam() {
		return team;
	}
	
	public void setTeam(int team) {
		this.team = team;
	}
	
	public void setAddress(SocketAddress address) {
		this.address = address;
	}
	
	public SocketAddress getAddress() {
		return address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
