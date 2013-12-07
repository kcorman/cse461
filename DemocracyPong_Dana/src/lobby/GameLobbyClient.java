package lobby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


public class GameLobbyClient extends Thread {
	private Socket socket;
	private String hostname;
	private int port;
	
	public GameLobbyClient(String hostname, int port) {
		this.hostname = hostname;
		this.port = port;
	}
	
	public void run() {
		Scanner input = new Scanner(System.in);
		try {
		    // host, join specific game, join random game
		    int opt = -1;
	    	int rm = 0;
		    while (true) {
		    	System.out.println("Press 0 if you would like to host a game room,\n"
		    			+ "1 if you would like to join a random game room,\n"
		    			+ "or 2 if you would like to join a specific game room: ");
		    	String option = input.nextLine();
		    	try {
		    		opt = Integer.parseInt(option);
		    	} catch (NumberFormatException e) {
		    		continue;
		    	}
		    	if (opt != 0 && opt != 1 && opt != 2)
		    		continue;
		    	if (opt == 2) {
		    		while (true) {
		    			System.out.println("Please enter the game room you would like to join: ");
			    		String room = input.nextLine();
		    			try {
		    				rm = Integer.parseInt(room);
		    			} catch (NumberFormatException e) {
		    				continue;
		    			}
		    			break;
		    		}
		    	}
		    	break;
		    }
		    InetAddress addr = InetAddress.getByName(hostname);
			socket = new Socket(addr, port);
			PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		    BufferedReader in = new BufferedReader(
		        new InputStreamReader(socket.getInputStream()));
		    out.println(opt);
		    out.println(rm);
		    
		    String myUserIDStr = in.readLine();
		    String roomStatusStr = in.readLine();
		    String roomNumberStr = in.readLine();
		    int myUserID = Integer.parseInt(myUserIDStr);
		    int roomStatus = Integer.parseInt(roomStatusStr);
		    int roomNumber = Integer.parseInt(roomNumberStr);
		    System.out.println("My UID = " + myUserID + ", Option = " + roomStatus + ", room number = " + roomNumber);
		    
		    System.out.println("\nYour user ID is: " + myUserID);
		    DemocracyConstants.UserRoomOption userStatus = DemocracyConstants.UserRoomOption.values()[roomStatus];
		    switch(userStatus) {
		    	case HOST:
		    		System.out.println("You are now hosting room " + roomNumber);
		    		break;
		    	case JOIN_SPECIFIC:
		    		System.out.println("You have successfully joined your requested room " + roomNumber);
		    		break;
		    	case JOIN_RANDOM:
		    		System.out.println("You have joined room " + roomNumber);
		    		break;
		    }
		    DemocracyConstants.RoomStatus rs;
		    while ((rs = DemocracyConstants.RoomStatus.values()[Integer.parseInt(in.readLine())]) 
		    		!= DemocracyConstants.RoomStatus.READY) {
		    	if (rs != null) {
		    		getRoomInfo(in);
		    	}
		    }
		    System.out.println("Ready to play!");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				socket.close();
			} catch (IOException e) {}
		}
		input.close();
	}
	
	public static void getRoomInfo(BufferedReader in) throws IOException {
		int roomSize = Integer.parseInt(in.readLine());
		System.out.println("\nHost:\t" + in.readLine());
		if (roomSize > 1)
			System.out.println("Players:");
		for (int i = 1; i < roomSize; ++i) {
			System.out.println("\t" + in.readLine());
		}
	}
}
