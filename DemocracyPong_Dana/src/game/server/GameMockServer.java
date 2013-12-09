package game.server;

import game.entities.ClientState;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import lobby.User;

public class GameMockServer implements GameServer, Runnable {
	Game g;
	Map<Integer, User> players = new HashMap<>();
	Collection<ClientState> votes = new HashSet<>();
 
	public GameMockServer() {
		
		// Populate players and votes
		for (int i = 0; i < 20; i++) {
			
			// Players
			Game.Team team;
			team = (i%2 == 0) ? Game.Team.LEFT : Game.Team.RIGHT;
			players.put(i, new User(i, team, 300));
			
			// Votes
			ClientState cs = new ClientState(i);
			cs.yVote = 236;
			votes.add(cs);
		}
		
		g = new Game(players);
	}

	@Override
	public void start() {
		g.start();
		(new Thread(this)).start();
	}

	@Override
	public void run() {
		while (true) {
			g.updateVotes(votes);
			System.out.println(g.getState());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String args[]) {
		(new GameMockServer()).start();
	}

}
