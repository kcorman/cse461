package lobby;

import game.server.Game;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramSocket;
import java.net.Socket;


public class User {
	// Lobby related fields
	private int userID;
	private Socket userSocket;
	private PrintWriter out;
	private BufferedReader in;
	
	// Game related fields
	private DatagramSocket gameSocket;
	private int yVote;
	private Game.Team team;
	private int timeouts = 0;	// keep track of number of timeouts in-game
	
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
	public User(int userID, Game.Team team, int yVote) {
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
	
	public Game.Team getTeam() {
		return team;
	}
	
	public void setTeam(Game.Team team) {
		this.team = team;
	}
	
	public int getTimeouts() {
		return timeouts;
	}
	
	public int incTimeouts() {
		timeouts++;
		return timeouts;
	}
	
	public void clearTimeouts() {
		this.timeouts = 0;
	}
	
	public DatagramSocket getGameSocket() {
		return gameSocket;
	}
}
