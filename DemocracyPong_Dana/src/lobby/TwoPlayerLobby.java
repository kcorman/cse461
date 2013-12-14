package lobby;

import game.server.GameServer;
import game.server.GameSocketServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 
 * @author kenny
 * A simple command-line lobby
 * for use with testing that follows the lobby protocol and sets up
 * a single two person game
 * After the game is set up, the lobby will exit
 */
public class TwoPlayerLobby implements Runnable{
	Map<Integer, User> userMap;
	static final int UDP_PORT = 65234;
	ServerSocket s;
	static final Random RAND = new Random();
	public static void main(String[] args){
		if(args.length != 1){
			System.err.println("Usage:\n\t java TwoPlayerLobby <port>");
		}
		int port = Integer.parseInt(args[0]);
		TwoPlayerLobby lob = new TwoPlayerLobby(port);
		lob.run();
		
	}
	
	public TwoPlayerLobby(int port){
		userMap = new HashMap<Integer, User>();
		try {
			s = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void run() {
		System.out.println("Server waiting for players...");
		while(userMap.size() < 2)
			try {
				Socket usrSock = s.accept();
				//build a user
				Integer id = RAND.nextInt();
				User u = new User(id, usrSock);
				//write user's id
				getDataOutputStream(u).writeInt(id);
				userMap.put(id,  u);
				System.out.println("Server added player with id: "+id);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		//now we can assume we have 2 users
		System.out.println("Server starting game...");
		try {
			DatagramSocket dgSocket = new DatagramSocket(UDP_PORT);
			GameServer s = new GameSocketServer(userMap, dgSocket);
			s.start();
			for(User usr : userMap.values()){
				getDataOutputStream(usr).writeInt(UDP_PORT);
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	static DataOutputStream getDataOutputStream(User u) throws IOException{
		return new DataOutputStream(u.getUserSocket().getOutputStream());
	}
}
