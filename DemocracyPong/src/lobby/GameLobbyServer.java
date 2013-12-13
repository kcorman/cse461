package lobby;

import java.awt.Dimension;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;


public class GameLobbyServer {
	static final int DEFAULT_PORT = 34543;
	private static boolean visualMode = false;
	public static void main(String[] args) throws IOException {
		int port = DEFAULT_PORT;
		if (args.length != 1) {
			System.err.println("Usage:\n\t java GameLobbyServer <hostname> <port>");
			System.out.println("Since you did not specify, you will be prompted");
			int portselect = -1;
			visualMode = true;
			String result = JOptionPane.showInputDialog("Enter the port number");
			try{
				portselect = Integer.parseInt(result);
			}catch(NumberFormatException e){
				portselect = -1;
				System.err.println("Invalid port: "+result+", will use default port: "+DEFAULT_PORT);
			}
			
		}else{
			port = Integer.parseInt(args[0]);
		}
		if(visualMode){
			displayVisualHandle();
		}
		if(port == DEFAULT_PORT){
			System.out.println("Using default port: "+DEFAULT_PORT);
		}
		// Set up game room manager thread
		GameRoomManager manager = new GameRoomManager();
		manager.start();
		
		int userID = 0;
		ServerSocket lobbySocket = null;
		try {
			
			// Note: backlog = 0 -> use default backlog
			lobbySocket = new ServerSocket(port);
			
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
	
	/*
	 * Displays a frame that enables users to close the server without using the command line
	 */
	private static void displayVisualHandle(){
		JFrame handleFrame = new JFrame("Democracy Pong Lobby Server");
		handleFrame.setPreferredSize(new Dimension(300,300));
		handleFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		handleFrame.add(new JLabel("Closing this frame will exit the server"));
		handleFrame.pack();
		handleFrame.setVisible(true);
		handleFrame.repaint();
	}
}
