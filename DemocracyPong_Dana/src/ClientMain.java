import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class ClientMain {
	public static void main(String[] args) throws IOException {
		//if (args.length != 2) {
			//System.err.println("Usage:\n\t java GameLobbyServer <hostname> <port>");
		//}

		//GameLobbyClient client = new GameLobbyClient(args[0], Integer.parseInt(args[1]));
		//client.start();
		
		JFrame frame = new JFrame("Democracy Pong Lobby");
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    RoomPanel panel = new RoomPanel();
	    frame.add(panel);
	    frame.pack();
	    frame.setVisible(true);
	    
	    List<String> player = new ArrayList<String>();
	    player.add("10");
	    player.add("4");
	    player.add("8");
	    player.add("6");
	    player.add("11");
	    panel.notifyViewer(player, "8");
	}
}
