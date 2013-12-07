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
	
	/**
	 * Returns the current queue client states, and clears the queue
	 * @modifies votes Clears the queue
	 * @return votes
	 */
	abstract Queue<ClientState> getClientState();
	
	/**
	 * Sends state to clients
	 * @effects sends the current state to clients
	 */
	abstract void sendGameState();
}
