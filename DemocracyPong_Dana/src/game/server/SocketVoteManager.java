package game.server;

import game.entities.ClientState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Map;

import lobby.User;

/**
 * A vote manager constantly reads and updates players votes
 * @author Jon
 *
 */
public class SocketVoteManager implements Runnable {
	Map<Integer, User> players;
	DatagramSocket udpSocket;
	boolean running;

	public SocketVoteManager(Map<Integer, User> players, DatagramSocket udpSocket) {
		this.players = players;
		this.udpSocket = udpSocket;
	}
	/**
	 * Begin updating players votes.
	 */
	public void start() {
		(new Thread(this)).start();
		running = true;
	}
	
	public void stop() {
		running = false;
	}

	@Override
	public void run() {
		int packetSize = ClientState.getMaxSize();
		byte[] buf = new byte[packetSize];
		DatagramPacket csPacket = new DatagramPacket(buf, packetSize);

		while (running) {
			try {
				udpSocket.receive(csPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}

			ClientState cs = ClientState.fromBytes(buf);
			User u = players.get(cs.userId);
			u.setVote(cs.yVote);
		}
	}
}
