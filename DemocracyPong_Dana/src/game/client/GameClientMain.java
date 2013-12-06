package game.client;

/**
 * 
 * @author kenny
 * This class acts as a container for a GameClient window, model, and connection
 * Game Client Main starts up a GameClientWindow
 * GameClientWindow listens for mouse movement and updates its mouse position
 * GameClientModel stores the logic of the game and is meant to be updated
 * by some external source
 * GameClientConnection is in charge of updating the model as well as handling
 * connection specific information
 */
public class GameClientMain {
	public static void main(String[] arr){
		GameClientModel m = new GameClientModel();
		GameClientWindow w = new GameClientWindow(m);
		GameClientConnection c = new GameClientMockConnection(w, m);
		if(c.connect()){
			w.run(); /* blocks until game is over */
		}else{
			System.out.println("Could not connect to server.");
			System.exit(1);
		}
	}
}
