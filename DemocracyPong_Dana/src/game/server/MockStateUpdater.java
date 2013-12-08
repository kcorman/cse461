package game.server;

import game.entities.ClientState;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import lobby.User;

public class MockStateUpdater extends StateUpdater implements Runnable{
	static int count;
	Map<Integer, User> players;
	
	public MockStateUpdater(Map<Integer, User> players) {
		this.players = players;
		votes = newQueue();
	}

	@Override
	public void run() {
		System.out.print("Hello, from MockStateUpdater.");
		while (running) {
			int i = count;
			for (User u : players.values()) {
				ClientState cs = new ClientState(u.getUserID());
				//cs.yVote = (new Random()).nextInt(700);
				cs.yVote = 300;
				votes.add(cs);
			}
			
			Arrays.toString(votes.toArray());
		}
	}

	private static Queue<ClientState> newQueue() {
		return new LinkedList<ClientState>();
	}
	
	@Override
	public Queue<ClientState> getVotes() {
		Queue<ClientState> vote = votes;
		votes = newQueue();
		
		return vote;
	}

	@Override
	public void start() {
		if (running) 	// do nothing if already running
			return;
		
		running = true;
		(new Thread(this)).start();
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		running = false;
	}

}
