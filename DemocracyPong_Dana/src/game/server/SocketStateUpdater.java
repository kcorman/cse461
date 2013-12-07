package game.server;

import game.entities.ClientState;
import game.entities.GameState;

import java.util.List;
import java.net.DatagramPacket;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import lobby.User;

/**
 * @author Jonathan Ellington
 * A StateUpdater that interfaces with Clients via Sockets.
 */
public class SocketStateUpdater extends StateUpdater implements Runnable {
	private GameState state;
	private Queue<ClientState> votes = new LinkedList<ClientState>();
	
	public SocketStateUpdater(GameState state, Map<Integer, User> players) {
		this.state = state;
	}
	
	@Override
	public void run() {

	}

	@Override
	Queue<ClientState> getClientState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	void sendGameState() {
		// TODO Auto-generated method stub
		
	}
}
