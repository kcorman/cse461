package game.server;

import java.net.DatagramSocket;
import java.util.Map;

import lobby.User;

/**
 * @author Jon
 *
 */
public class GameplayServer implements Runnable {
		
	private Map<Integer, User> players;
	private DatagramSocket udpSocket;
	
	public GameplayServer(Map<Integer, User> players, DatagramSocket udpSocket) {
		this.players = players;
		this.udpSocket = udpSocket;
	}

	@Override
	public void run() {

	}
	
	/**
	 * Initial registration phase.  At the end, should purge the players list
	 * so that anybody not connected gets dropped
	 */
	private void registerPlayers() {

	}
}
