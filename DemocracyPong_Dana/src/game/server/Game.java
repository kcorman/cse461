package game.server;

import game.entities.ClientState;
import game.entities.GameState;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import lobby.User;

/**
 * The logic for a game of pong.
 * @author Jon
 */
public class Game implements Runnable {
	public enum Team { LEFT, RIGHT };
	
	private GameState state;
	private StateUpdater updater;
	private Map<Integer, User> players;

	public Game(Map<Integer, User> p) {
		players = p;
    	state = new GameState();
    	updater = new SocketStateUpdater(state, p);
	}
	
	public GameState getState() {
		return state;
	}
	
	/**
	 * Updates the votes for users in s
	 * @param s queue holding the users whose votes are to be updated
	 */
	public void updateUserVotes(Queue<ClientState> q) {
		for (ClientState cs : q) {
			User currUser = players.get(cs.userId);
			currUser.setVote(cs.yVote);
		}
	}
	
	/**
	 * Updates the paddles based on all player's votes
	 */
	private void updatePaddles() {

	}
	
	@Override
	public void run() {
		
	}
}
