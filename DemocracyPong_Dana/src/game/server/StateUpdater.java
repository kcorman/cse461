package game.server;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import lobby.User;
import game.entities.ClientState;
import game.entities.GameState;

/**
 * Abstract class for sending data to and receiving data from clients.
 * Has a queue of ClientState, representing client votes.
 * @author Jonathan Ellington
 */
public abstract class StateUpdater {
	GameState state;
	Map<Integer, User> players;
	Queue<ClientState> votes;
	boolean running;

	// TODO add constructor?
	
	/**
	 * Returns the current vote queue client states, and clears the queue
	 * @modifies votes Clears the queue
	 * @return votes
	 */
	abstract public Queue<ClientState> getVotes();
	
	/**
	 * Start the StateUpdater.  After calling start(),
	 * the votes queue will automatically be populated and the 
	 * current GameState will automatically be sent to the client.
	 * 
	 * A call to start() is ignored if start() has previously been called
	 * without first calling stop().
	 * @modifies running if running = false, sets running = true
	 */
	abstract public void start();
	
	/**
	 * Stop the StateUpdater.  Callign stop() will shutdown the
	 * StateUpdater.
	 * @modifies running sets running = false
	 */
	abstract public void stop();
}
