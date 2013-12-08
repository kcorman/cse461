package game.server;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import lobby.User;

/**
 * A small test that loads the players field and runs a Game
 * @author Jon
 */
public class GameTest {
	static final int PLAYERS = 20;
	
	public static void main(String args[]) {
		Map<Integer, User> players = new HashMap<Integer, User>();
		for (int i = 0; i < PLAYERS; i++) {
			Game.Team team = (i % 2 == 0) ? Game.Team.LEFT : Game.Team.RIGHT;
			players.put(i, new User(i, team, 0));
		}
		Game g = new Game(players);
		(new Thread(g)).start();
		while (true) {
			try {
				Thread.sleep(25);
			} catch (Exception e) {
				
			}
			System.out.print(g.getState() + "\r");
		}
	}
}
