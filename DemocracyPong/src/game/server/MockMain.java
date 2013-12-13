package game.server;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import lobby.User;

public class MockMain {
	// create players list
	// Start new GameServer
	// spawn clients, which should have same userids as in player list

	static final int NUM_PLAYERS = 50;
	static Map<Integer, User> players = new HashMap<>();
	static String HOST = "localhost";
	static int PORT;

	public static void main(String args[]) {

		// populate players
		for (int i = 0; i < NUM_PLAYERS; i++) {
			int team = i % 2;
			int yVote = (new Random()).nextInt(700);
			players.put(i, new User(i, team, yVote));
		}
		
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket(PORT);
			PORT = ds.getLocalPort();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GameServer g = new GameSocketServer(players, ds);
		g.start();
		
		for (int i = 0; i < NUM_PLAYERS; i++) {
			MockClient mc = new MockClient(HOST, PORT, i);
			(new Thread(mc)).start();
		}
	}
}