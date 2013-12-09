package game.server;

import game.entities.ClientState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import lobby.User;

public class GameSocketServer implements GameServer, Runnable {
	static final int REG_TIME = 3000;	// registration time, ms
	
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
		long startTime = System.currentTimeMillis();
		long endTime = startTime + REG_TIME;
		
		// Receive stuff
		byte[] rb = new byte[4];
		ByteBuffer bb = ByteBuffer.wrap(rb);
		DatagramPacket regPacket = new DatagramPacket(rb, 4);
		
		// Send stuff
		byte[] sb = new byte[1];
		DatagramPacket ackPacket = new DatagramPacket(sb, 1);

		boolean left_team = true;
		while (startTime < endTime) {
			try {
				// Receive a packet
				udpSocket.receive(regPacket);
				int uid = bb.getInt(0);

				// throw away bad packets
				if (!verifyUserID(uid))
					continue;
				
				// register user
				DatagramSocket ds = new DatagramSocket(regPacket.getSocketAddress());
				players.get(uid).setGameSocket(ds);
				
				// send 1-byte ack with team info
				// TODO this may be incorrect -- not sure if changes to sb affect the internals of ackPacket
				//		if this needs to be fixed, add ackPacket.setData(sb);
				sb[0] = (left_team) ? (byte) Game.TEAM_LEFT : Game.TEAM_RIGHT;
				ds.send(ackPacket);

				// alternate teams
				left_team = !left_team;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/*
	 * Ensures the userid passed is valid
	 */
	private boolean verifyUserID(int uid) {
		if (!players.containsKey(uid))
			return false;

		return true;
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