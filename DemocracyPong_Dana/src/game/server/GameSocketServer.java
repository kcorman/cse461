package game.server;

import game.entities.ClientState;

import java.net.DatagramSocket;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import lobby.User;

public class GameSocketServer implements GameServer, Runnable {
	static final int REG_TIME = 3000;	// registration time
	
	private Map<Integer, User> players;
	private DatagramSocket udpSocket;
	private Game game;
	private Collection<ClientState> votes;	// should be cleared after updating game

	public GameSocketServer(Map<Integer, User> players, DatagramSocket udpSocket) {
		this.players = players;
		this.udpSocket = udpSocket;
		game = new Game(players);
	}

	@Override
	public void run() {
		// TODO
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub
	}
		
	private void registerPlayers() {
		// TODO
	}

	private void sendGameState() {
		// TODO Auto-generated method stub
	}

	private void receiveVotes() {
		// TODO Auto-generated method stub
	}
	
	private Collection<ClientState> newCollection() {
		return new LinkedList<ClientState>();
	}
}