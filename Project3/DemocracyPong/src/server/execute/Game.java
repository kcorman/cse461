package server.execute;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

import state.GameState;

/**
 * 
 * CSE461 Project 3
 * Kenny Corman
 * Jonathan Ellington
 * Dana Van Aken
 * 
 * Handles the server side of the game and communicating with clients
 * The network flow goes like this:
 * Server sends packets to clients consisting of 8 bytes. The first four bytes are the port
 * to use for a new UDP socket, and the last four bytes are the future payload size for serialized
 * state objects
 * 
 * Clients respond by sending their id followed by 4 0s (8 byte packet) on the new port
 */
public class Game implements Runnable{
	public static int MAX_PAYLOAD_SIZE = 512;
	List<User> waitingUsers;	//Users that have not sent an ack on the new connection
	List<User> leftTeam;
	List<User> rightTeam;
	List<User> allUsers;
	boolean gameReady = false;
	boolean addLeft = true;
	DatagramSocket ds;
	public Game(List<User> users, DatagramSocket socket){
		if(users == null || users.size() < 2) 
			throw new IllegalArgumentException("Users must contain more than 2 users");
		this.waitingUsers = users;
		this.ds = socket;
		leftTeam = new ArrayList<User>();
		rightTeam = new ArrayList<User>();
		allUsers = new ArrayList<User>();
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		GameState state = new GameState(800, 600);
		System.out.println("Game starting up on port: "+ds.getLocalPort());
		PacketListener pl = new PacketListener();
		Executors.newSingleThreadExecutor().execute(pl);
		while(true){
			byte[] stateBytes = serializeGameState(state);
			System.out.println("[GAME SERVER] Serialized state successfully. Sending to users");
			for(User u : allUsers){
				System.out.println("[GAME SERVER] Sending to user: "+u.id+", packetlen= "+stateBytes.length);
				u.sendPacket(ds, stateBytes);
			}
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			
		}
	}
	
	
	/**
	 * Processes the given datagram packet
	 * At different points in the game, this may have different effects
	 * @param buf the bytes to process
	 */
	public void processPacket(byte[] buf, DatagramPacket packet){
		if(!waitingUsers.isEmpty()){
			ByteBuffer bb = ByteBuffer.wrap(buf);
			int possible_id = bb.getInt();
			int should_be_zero = bb.getInt();
			User u = Main.getUserById(waitingUsers, possible_id);
			if(u != null){
				waitingUsers.remove(u);
				if(addLeft)
					leftTeam.add(u);
				else
					rightTeam.add(u);
				addLeft = !addLeft;
				allUsers.add(u);
				u.initialPacket = packet;
			}
			System.out.println("User with id= "+u.id+" has acked on new port");
			if(waitingUsers.isEmpty())
				System.out.println("All users have acked");
		}else{
			/* Main branch for when game is actually in progress */
		}
	}
	
	
	/**
	 * Serializes the given game state and returns it as a byte array
	 * @param state
	 * @return
	 */
	private byte[] serializeGameState(GameState state){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] objectBytes = new byte[MAX_PAYLOAD_SIZE];
		try {
		  out = new ObjectOutputStream(bos);   
		  out.writeObject(state);
		  objectBytes = Arrays.copyOf(bos.toByteArray(),MAX_PAYLOAD_SIZE);;
		} catch(IOException e){
			e.printStackTrace();
		}finally {
			try{
		  out.close();
		  bos.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return objectBytes; 
	}
	
	class PacketListener implements Runnable{

		@Override
		public void run() {
			while(true){
				byte[] received = new byte[Main.CONNECTION_PACKET_LEN];
				DatagramPacket packet = new DatagramPacket(received, Main.CONNECTION_PACKET_LEN);
				try {
					System.out.println("Waiting for packet..");
					ds.receive(packet);
					System.out.println("Received packet: "+Arrays.toString(received));
					processPacket(received,packet);
				} catch (SocketTimeoutException e) {
					continue;
				} catch (Exception e) {
					ds.close();
					e.printStackTrace();
				}
			}
		}
		
	}

	
}
