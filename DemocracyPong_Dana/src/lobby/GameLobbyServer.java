package lobby;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;


public class GameLobbyServer {
	
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("Usage:\n\t java GameLobbyServer <hostname> <port>");
		}
		
		// Set up game room manager thread
		GameRoomManager manager = new GameRoomManager();
		manager.start();
		
		int port = Integer.parseInt(args[1]);
		int userID = 0;
		ServerSocket lobbySocket = null;
		try {
			InetAddress addr = InetAddress.getByName(args[0]);
			
			// Note: backlog = 0 -> use default backlog
			lobbySocket = new ServerSocket(port, 0, addr);
			
			while (true) {
				Socket clientSocket = lobbySocket.accept();
				System.out.println("Found new client!");
				User newUser = new User(userID++, clientSocket);
				manager.addUserToQueue(newUser);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lobbySocket.close();
		}
	}
}
