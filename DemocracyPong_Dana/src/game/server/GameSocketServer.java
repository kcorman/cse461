package game.server;

import game.entities.ClientState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import lobby.User;

/**
 * GameServer implementation.  Upon start()ing, follows this protocol:
 * 
 * Registration:
 *   -Will listen for registration packets for REG_TIME ms.
 *   -Sends ack containing team side (LEFT or RIGHT) upon receipt of
 *    any registration packets
 * Gameplay:
 *   -Starts a new Game after registration period ends
 *   -Sends serialized GameState to each registered client
 *   -Receives serialized ClientState from registered clients
 *     +Waits TIMEOUT ms, if nothing received, increments that User's timeout count
 *     +If user has more than MAX_TIMEOUTS, user is disconnected from game
 *	   +User can send disconnect signal, upon receipt will be disconnected 
 * Teardown:
 *   -TBD
 * @author Jonathan Ellington
 *
 */
public class GameSocketServer implements GameServer, Runnable {
	static final int REG_TIME = 3000;	// registration time, ms
	static final int TIMEOUT = 50;		// client timeout, in ms
	static final int MAX_TIMEOUTS = 3;
	
	static final int DISCONNECT_SIGNAL = -1;
	
	private ConcurrentMap<Integer, User> players;
	private DatagramSocket udpSocket;
	private Game game;

	/**
	 * Create a new GameSocketServer.
	 * @param players list of players to register.  Each User.Address field should be null, indicating
	 * 		  the player is not registered prior to starting the game.
	 * @param udpSocket the socket to listen for registration requests and to send GameState from.
	 */
	public GameSocketServer(Map<Integer, User> players, DatagramSocket udpSocket) {
		this.players = new ConcurrentHashMap<Integer, User>(players);
		this.udpSocket = udpSocket;

		game = new Game(players);
	}

	@Override
	public void run() {
		registerPlayers();
		System.out.println("SERVER: Registration over, starting game...");
		game.start();

		int clientPacketSize = ClientState.getMaxSize();
		byte[] sb;	// send buffer
		byte[] rb = new byte[clientPacketSize]; // receive buffer

		while (true) {	// TODO shouldn't do this forever
			try {
				udpSocket.setSoTimeout(TIMEOUT);
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			sb = game.getState().toBytes();

			// send gamestate to each user, receive client state from each user
			Iterator<Entry<Integer, User>> it = players.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<Integer,User> pairs = it.next();

				User u = pairs.getValue();
				SocketAddress address = u.getAddress();

				// player was never registered
				if (address == null) {
					it.remove();
					System.out.println("SERVER: Player " + pairs.getKey() + " failed to register, disconnecting...");
					continue;
				}

				DatagramPacket clientPacket = new DatagramPacket(rb, clientPacketSize);
				try {
					// connect to particular user
					udpSocket.connect(address);

					// send GameState packet
					DatagramPacket statePacket = new DatagramPacket(sb, clientPacketSize);
					udpSocket.send(statePacket);

					// receive from client, clear timeouts on successful receive
					udpSocket.receive(clientPacket);
					u.clearTimeouts();

					// deserialize client state and update vote
					ClientState cs = ClientState.fromBytes(rb);

					/*
					 * Discarding packets from users who are not the one being processed.
					 * Wasteful, but makes things easy.  Consider making faster if problems
					 * arise.
					 */
					if (u.getUserID() != cs.userId) {	
						continue;
					}

					if (!verifyUserID(cs.userId))
						continue;
					
					if (cs.yVote == DISCONNECT_SIGNAL) {
						it.remove();
						continue;
					}

					System.out.println("SERVER: Received from userid: " + pairs.getKey() + " vote: " + cs.yVote);
					players.get(cs.userId).setVote(cs.yVote);
				} catch (SocketTimeoutException e) {
					u.incTimeouts();
					System.out.println("SERVER: " + pairs.getKey() + " timed out and has " + pairs.getValue().getTimeouts() + " timeouts");
					if (u.getTimeouts() > MAX_TIMEOUTS) {
						// TODO send disconnect notification?
						System.out.println("SERVER: Removing userid: " + pairs.getKey());
						it.remove();
					}
					continue;
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
		int numRegistered = 0;		// used for debug
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

		boolean left_team = true;
		while (System.currentTimeMillis() < endTime ) { //&& registeredPlayers < players.size()) {
			try {
				// ensure socket doesn't sit waiting to receive for longer than REG_TIME
				int timeleft = (int) (endTime - System.currentTimeMillis());
				udpSocket.setSoTimeout(timeleft);

				// Receive a packet
				udpSocket.receive(regPacket);	// TODO figure out why this receives when nothing is sent
				int uid = bb.getInt(0);

				// throw away bad packets
				if (!verifyUserID(uid))
					continue;
				
				/*
				 * Only register a user if they haven't already been registered.
				 * Send the ack under the assumption that the client did not receive
				 * the ack, and that's why we have duplicate registration requests.
				 */
				SocketAddress sa = regPacket.getSocketAddress();
				if (!alreadyRegistered(uid)) {
					// register user
					players.get(uid).setAddress(sa);
					System.out.println("SERVER: Registered player with uid: " + uid);
					numRegistered++;
				}
				
				// send 1-byte ack with team info
				// TODO this may be incorrect -- not sure if changes to sb affect the internals of ackPacket
				//		if this needs to be fixed, add ackPacket.setData(sb);
				sb[0] = (left_team) ? (byte) Game.TEAM_LEFT : Game.TEAM_RIGHT;
				udpSocket.connect(sa);
				DatagramPacket ackPacket = new DatagramPacket(sb, 1);
				udpSocket.send(ackPacket);
				udpSocket.disconnect();		// want to receive from anyone after sending ack

				left_team = !left_team;
			} catch (SocketTimeoutException e) {
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

		System.out.println("SERVER: number registered: " + numRegistered);
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
	
	private boolean alreadyRegistered(int uid) {
		return players.get(uid).getAddress() != null;
	}
}