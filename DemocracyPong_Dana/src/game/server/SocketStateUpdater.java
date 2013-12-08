package game.server;

import game.entities.ClientState;
import game.entities.GameState;

import java.util.List;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import lobby.User;

/**
 * @author Jonathan Ellington
 * A StateUpdater that interfaces with Clients via Sockets.
 */
public class SocketStateUpdater extends StateUpdater implements Runnable {
	DatagramSocket udpSocket;
	
	/**
	 * Creates a new SocketStateUpdater
	 * @param state reference to a GameState
	 */
	public SocketStateUpdater(GameState state, Map<Integer, User> players, DatagramSocket udpSocket) {
		this.state = state;
		this.players = players;
		this.udpSocket = udpSocket;
	}
	
	@Override
	public void run() {
		while (running) {
			getClientState();
			sendGameState();
		}
	}

	@Override
	public Queue<ClientState> getVotes() {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public void start() {
		running = true;
		(new Thread(this)).start();
	}

	@Override
	public void stop() {
		running = false;
	}
	
	/*
	 * Attempts to get a ClientState from each player
	 */
	private void getClientState() {
		// TODO Auto-generated method stub
	}
	
	private void sendGameState() {
		// TODO Auto-generated method stub
		
	}

}
