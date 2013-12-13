package lobby;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import lobby.mocks.MockConnectionBean;

public class ClientMain {
	public static void main(String[] args) throws IOException {
		//if (args.length != 2) {
			//System.err.println("Usage:\n\t java GameLobbyServer <hostname> <port>");
		//}

		//GameLobbyClient client = new GameLobbyClient(args[0], Integer.parseInt(args[1]));
		//client.start();
		//JOptionPane op = new JOptionPane();
		
		ConnectionBean bean = new MockConnectionBean();
		LobbyWindow lobby = new LobbyWindow(bean);
		
	}
}
