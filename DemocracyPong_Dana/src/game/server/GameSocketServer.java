package game.server;

import game.entities.ClientState;
import game.entities.GameState;

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
	static final boolean DEBUG = true;
	static final int REG_TIME = 3000;	// registration time, ms
	static final int TIMEOUT = 10;		// client timeout, in ms
	static final int MAX_TIMEOUTS = 5;
	
	static final int DISCONNECT_SIGNAL = -1;
	
	private int timeouts;
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
		byte[] stateBuf;	// send buffer
		byte[] clientBuf = new byte[clientPacketSize]; // receive buffer

		try {
			udpSocket.setSoTimeout(TIMEOUT);
		} catch (SocketException e1) {
			e1.printStackTrace();
		}

		boolean playing = true;
		while (playing) {	
			// End game if there is a winner
			if (game.getState().getWinner() != Game.NO_WINNER)
				playing = false;

			// Serialize current game state
			stateBuf = game.getState().toBytes();
			
			// Send GameState to each user, receive votes
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

				try {
					// Send GameState packet to current user
					DatagramPacket statePacket = new DatagramPacket(stateBuf, GameState.getMaxSize(), address);
					udpSocket.send(statePacket);

					// Receive from client at address, see comment below, clear timeouts on successful receive
					DatagramPacket clientPacket = new DatagramPacket(clientBuf, clientPacketSize);
					udpSocket.receive(clientPacket);

					// Deserialize client state
					ClientState cs = ClientState.fromBytes(clientBuf);

					// Verify packet
					if (!verifyUserID(cs.userId))
						continue;

					if (cs.yVote == DISCONNECT_SIGNAL) {
						it.remove();
						continue;
					}

					// Update vote
					if (DEBUG) System.out.println("SERVER: Received from userid: " + pairs.getKey() + " vote: " + cs.yVote);
					players.get(cs.userId).setVote(cs.yVote);
					timeouts = 0;
				} catch (SocketTimeoutException e) {
					if (timeouts > MAX_TIMEOUTS) {
						System.out.println("SERVER: No data received in " + timeouts + " + timeouts.  Quitting...");
						playing = false;
						break;
					}
					timeouts++;
					continue;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		udpSocket.close();
		game.stop();
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
				if (DEBUG) System.out.println("SERVER: Received registration request from uid=" + uid);

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
				int team = (left_team) ? Game.TEAM_LEFT : Game.TEAM_RIGHT;
				sb[0] = (byte) team;
				players.get(uid).setTeam(team);
				DatagramPacket ackPacket = new DatagramPacket(sb, 1, sa);
				udpSocket.send(ackPacket);

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

		System.out.println("SERVER: number of users registered: " + numRegistered);
	}

	/*
	 * Ensures the userid passed is in the game
	 */
	private boolean verifyUserID(int uid) {
		if (!players.containsKey(uid))
			return false;

		return true;
	}
	
	private boolean alreadyRegistered(int uid) {
		return players.get(uid).getAddress() != null;
	}
}