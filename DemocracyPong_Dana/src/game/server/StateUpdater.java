package game.server;

import java.util.List;
import java.util.Queue;

import game.entities.ClientState;
import game.entities.GameState;

/**
 * Abstract class for sending data to and receiving data from clients.
 * Has a queue of ClientState, representing client votes.
 * @author Jonathan Ellington
 */
public abstract class StateUpdater {
	GameState state;
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
	 * Gets a single client state.  Has no effect if the updater
	 * is running (ie after a call to start())
	 * @modifies votes adds a single client state to votes if running==false
	 */
	abstract public void getClientState();
	
	/**
	 * Sends state to clients  Has no effect if the updater is
	 * running (ie after a call to start())
	 * @effects sends the current state to clients if running==false
	 */
	abstract public void sendGameState();
	
	/**
	 * Start the StateUpdater.  After calling start(),
	 * the votes queue will automatically be populated and the 
	 * current GameState will automatically be sent to the client.
	 * @modifies running sets running = true
	 */
	abstract public void start();
	
	/**
	 * Stop the StateUpdater.  Callign stop() will shutdown the
	 * StateUpdater.
	 * @modifies running sets running = false
	 */
	abstract public void stop();
}
