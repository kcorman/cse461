package game.server;

import game.entities.ClientState;
import game.entities.GameState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import lobby.User;

public class GameSocketServer implements GameServer, Runnable {
	static final int REG_TIME = 3000;	// registration time, ms
	
	private Map<Integer, User> players;
	private DatagramSocket udpSocket;
	private Game game;
	private SocketVoteManager voteManager;

	public GameSocketServer(Map<Integer, User> players, DatagramSocket udpSocket) {
		this.players = players;
		this.udpSocket = udpSocket;

		game = new Game(players);
		voteManager = new SocketVoteManager(players, udpSocket);
	}

	@Override
	public void run() {
		registerPlayers();
		game.start();
		voteManager.start();	// manages receiving votes

		byte[] sb;
		while (true) {	// TODO shouldn't do this forever
			
			sb = game.getState().toBytes();
			DatagramPacket statePacket = new DatagramPacket(sb, GameState.getMaxSize());

			// send gamestate to each user
			for (User u : players.values()) {
				DatagramSocket clientSocket = u.getGameSocket();
				try {
					clientSocket.send(statePacket);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void start() {
		(new Thread(this)).start();
	}
		
	/*
	 * Listens and registers users for REG_TIME ms, or until all players have been registered.
	 */
	private void registerPlayers() {
		long startTime = System.currentTimeMillis();
		long endTime = startTime + REG_TIME;
		int registeredPlayers = 0;
		int timeout = 0;

		// This is to remember the previous value of the timeout, so it can be reset before returning
		try {
			timeout = udpSocket.getSoTimeout();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		// Receive stuff
		byte[] rb = new byte[4];
		ByteBuffer bb = ByteBuffer.wrap(rb);
		DatagramPacket regPacket = new DatagramPacket(rb, 4);
		
		// Send stuff
		byte[] sb = new byte[1];
		DatagramPacket ackPacket = new DatagramPacket(sb, 1);

		boolean left_team = true;
		while (System.currentTimeMillis() < endTime && registeredPlayers < players.size()) {
			try {
				// ensure socket doesn't sit waiting to receive for longer than REG_TIME
				int timeleft = (int) (endTime - System.currentTimeMillis());
				udpSocket.setSoTimeout(timeleft);

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

				registeredPlayers++;
				left_team = !left_team;
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				break;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// return timeout to start value
		try {
			udpSocket.setSoTimeout(timeout);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Ensures the userid passed is in the game
	 */
	private boolean verifyUserID(int uid) {
		// TODO add check to see if we've already seen this uid?
		
		if (!players.containsKey(uid))
			return false;

		return true;
	}
}