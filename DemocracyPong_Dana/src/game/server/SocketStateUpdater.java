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
	
	/**
	 * Creates a new SocketStateUpdater
	 * @param state reference to a GameState
	 */
	public SocketStateUpdater(Game g) {
		this.state = g.getState();
		g.setStateUpdater(this);
	}
	
	@Override
	public void run() {
		while (running) {
			
		}
	}

	@Override
	public Queue<ClientState> getVotes() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void getClientState() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void sendGameState() {
		// TODO Auto-generated method stub
		
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

}
