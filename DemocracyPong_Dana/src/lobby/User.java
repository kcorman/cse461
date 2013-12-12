package lobby;

import game.server.GamePlayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.Socket;
import java.net.SocketAddress;


public class User implements GamePlayer{
	// Lobby related fields
	private int userID;
	private Socket userSocket;
	private PrintWriter out;
	private BufferedReader in;
	
	// Game related fields
	private SocketAddress address;
	private int yVote;
	private int team;
	
	public User(int userID, Socket userSocket) {
		this.userID = userID;
		this.userSocket = userSocket;
		try {
			// using printwriter means you don't have to flush after each write
			out = new PrintWriter(userSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
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
	
	public PrintWriter getUserOutputStream() {
		return out;
	}
	
	public BufferedReader getUserInputStream() {
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
}
