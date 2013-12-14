package lobby;

import java.io.IOException;

public class ClientMain {
	public static void main(String[] args) throws IOException {
		//if (args.length != 2) {
			//System.err.println("Usage:\n\t java GameLobbyServer <hostname> <port>");
		//}

		//GameLobbyClient client = new GameLobbyClient(args[0], Integer.parseInt(args[1]));
		//client.start();
		//JOptionPane op = new JOptionPane();
		
		ConnectionBean bean = new GameLobbyClient();
		@SuppressWarnings("unused")
		LobbyWindow lobby = new LobbyWindow(bean);
		
	}
}
