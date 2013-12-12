package lobby;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class ClientMain {
	public static void main(String[] args) throws IOException {
		//if (args.length != 2) {
			//System.err.println("Usage:\n\t java GameLobbyServer <hostname> <port>");
		//}

		//GameLobbyClient client = new GameLobbyClient(args[0], Integer.parseInt(args[1]));
		//client.start();
		//JOptionPane op = new JOptionPane();
		
		
		JFrame frame = new JFrame("Democracy Pong Lobby");
		String[] test = {"9", "12", "3", "6"};
	    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    frame.setPreferredSize(new Dimension(800, 600));
	    RoomPanel roomPanel = new RoomPanel(test);
	    LobbyPanel lobbyPanel = new LobbyPanel(test);
	    ConnectPanel connectPanel = new ConnectPanel();
	    frame.setLayout(new GridLayout(0, 2));
	    JPanel leftPanel = new JPanel();
	    leftPanel.setLayout(new BorderLayout());
	    leftPanel.add(connectPanel, BorderLayout.NORTH);
	    leftPanel.add(lobbyPanel, BorderLayout.CENTER);
	    frame.add(leftPanel);
	    frame.add(roomPanel);
	    frame.pack();
	    frame.setVisible(true);
	    frame.repaint();
	}
}
