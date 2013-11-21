package server.execute;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

/**
 * CSE461 Project 3
 * Kenny Corman
 * Jonathan Ellington
 * Dana Van Aken
 * 
 * This is the main entry point for the server to start up
 * It listens for two users to connect and then starts a game thread
 * 
 * The connection process is as follows:
 * Every packet in this phase is exactly 8 bytes long (both client and server)
 * If the content consists of less than eight bytes (for example a user's id) the last bytes are padded
 * 
 * Client connects to server and sends packet consisting of "hello\0"
 * Server responds with client's unique 4 byte id and a \0 appended
 * Client acknowladges unique id by responding with same id
 * This id is used to identify the client for the rest of the session
 * 
 * Once two clients have completed this process, both clients receive a "start game" packet consisting of
 * the string "start"
 * 
 * Clients acknowledge this by sending an ACK consisting of their unique id followed by the byte 1
 * 
 * Once this happens, the game is started
 */
public class Main {
	public static final String HOST = "localhost";
	private static final int PORT = 14001;
	private static final int GAME_PORT_LOWER = 14002;
	private static final int GAME_PORT_RANGE = 100;
	private static int current_game_port = GAME_PORT_LOWER;
	public static final int CONNECTION_PACKET_LEN = 8;
	private static final Random rand = new Random();
	public static void main(String[] args){
		ArrayList<User> users = new ArrayList<User>();
		
		DatagramSocket ds = getDatagramSocket(PORT);
		if(ds == null){
			System.exit(1);
		}
		//Main processing loop for starting games
		while(true){
			byte[] received = new byte[CONNECTION_PACKET_LEN];
			DatagramPacket packet = new DatagramPacket(received, CONNECTION_PACKET_LEN);
			try {
				System.out.println("[MAIN_SERVER] Waiting for packet..");
				ds.receive(packet);
				System.out.println("[MAIN_SERVER] Received packet: "+Arrays.toString(received));
			} catch (SocketTimeoutException e) {
				continue;
			} catch (Exception e) {
				ds.close();
				e.printStackTrace();
			}
			//Check if we received a hello
			ByteBuffer buf = ByteBuffer.wrap(received);
			byte[] helloBytes = new byte[5];
			buf.get(helloBytes,0,5);
			String helloStr = new String(helloBytes);
			if(helloStr.equals("hello")){
				//Respond with id for this user
				int id = rand.nextInt();
				User user = new User();
				user.initialPacket = packet;
				user.id = id;
				users.add(user);
				System.out.println("[MAIN_SERVER] Created user with id= "+id);
				byte[] id_bytes = new byte[4];
				ByteBuffer id_buf = ByteBuffer.wrap(id_bytes);
				id_buf.putInt(id);
				respond_to_packet(ds, packet, id_bytes);
				continue;
			}
			buf.rewind();
			int possible_id = buf.getInt();
			int ackVal = buf.get();
			boolean isIdAck = ackVal == 0;
			boolean isStartAck = ackVal == 1;
			User possible_user = getUserById(users, possible_id);
			if(possible_user != null && isIdAck){
				possible_user.acked_has_id = true;
				System.out.println("[MAIN_SERVER] Received ack for id from user: "+possible_id);
				List<User> usersWithAckedIds = getUsersWithAckedIds(users);
				if(usersWithAckedIds.size() > 1){
					//Construct start game command
					byte[] start_cmd = "start\0\0\0".getBytes();
					for(User u : usersWithAckedIds){
						respond_to_packet(ds, u.initialPacket, start_cmd);
					}
				}
			}else if(possible_user != null && isStartAck){
				possible_user.acked_start = true;
				System.out.println("[MAIN_SERVER] Received start game ack from user with id= "+possible_id);
				List<User> usersWithAckedStart = getUsersWithAckedStarts(users);
				if(usersWithAckedStart.size() > 1){
					startGame(usersWithAckedStart,ds);
					users.clear();
					System.out.println("[MAIN_SERVER] Successfully started a game with users");
				}
			}
			
		}
	}
	
	/**
	 * Starts a game for the given users
	 * @param users
	 */
	public static void startGame(List<User> users, DatagramSocket oldSocket){
		DatagramSocket ds = getDatagramSocket(current_game_port++);
		if(ds == null) throw new NullPointerException("Could not create socket on port: "+(current_game_port-1));
		Game g = new Game(users, ds);
		Executors.newSingleThreadExecutor().execute(g);
		byte[] init_packet = new byte[8];
		ByteBuffer buf = ByteBuffer.wrap(init_packet);
		buf.putInt(ds.getLocalPort());
		buf.putInt(Game.MAX_PAYLOAD_SIZE);
		for(User u : users){
			u.sendPacket(oldSocket, init_packet);
		}
	}
	
	/**
	 * Returns a user with the given id in the given collection of users
	 * If no such user is found, returns null
	 * @param users the collection to search in
	 * @param id the id to search for
	 * @return the user or null if none is ofund
	 */
	public static User getUserById(Collection<User> users, final int id){
		List<User> result = getUsersMatchingCondition(users, new Matcher<User>(){
			public boolean isMatch(User e) {
				return e.id == id;
			}
		});
		if(result.isEmpty()) return null;
		return result.get(0);
	}
	
	public static List<User> getUsersWithAckedIds(Collection<User> users){
		return getUsersMatchingCondition(users, new Matcher<User>(){
			public boolean isMatch(User e) {
				return e.acked_has_id;
			}
		});
	}
	
	public static List<User> getUsersWithAckedStarts(Collection<User> users){
		return getUsersMatchingCondition(users, new Matcher<User>(){
			public boolean isMatch(User e) {
				return e.acked_start;
			}
		});
	}
	
	/**
	 * Returns a list of Users in the given collection matching the condtion specified in the matcher
	 * If no users match, returns an empty list
	 * @param users the collection to iterate through
	 * @param matcher the matcher to use
	 * @return a list of users matching the conditions
	 */
	private static List<User> getUsersMatchingCondition(Collection<User> users, Matcher<User> matcher){
		if(users == null) throw new NullPointerException("users cannot be null");
		List<User> result = new ArrayList<User>();
		Iterator<User> itr = users.iterator();
		while(itr.hasNext()){
			User u = itr.next();
			if(matcher.isMatch(u)) result.add(u);
		}
		return result;
	}
	
	/**
	 * Attempts to create a datagram socket with the given port
	 * If unable to, prints a stack trace and returns null
	 * @param port the port to create the socket on
	 * @return a datagram socket
	 */
	private static DatagramSocket getDatagramSocket(int port){
		
		try {
			InetAddress addr  = InetAddress.getByName(HOST);
			DatagramSocket ds = new DatagramSocket(port, addr);
			ds.setSoTimeout(10000);
			return ds;
		} catch (Exception e) {
			e.printStackTrace();
			
		}
		return null;
	}
	
	/**
	 * Responds to the given original datagram packet with the given bytes
	 * @param ds The datagram socket to use
	 * @param original the packet to respond to
	 * @param response_bytes the message to respnd with
	 */
	public static void respond_to_packet(DatagramSocket ds,DatagramPacket original, byte[] response_bytes){
		DatagramPacket response = new DatagramPacket(response_bytes, response_bytes.length, 
						original.getAddress(), original.getPort());
		try {
			ds.send(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static abstract class Matcher<E>{
		public abstract boolean isMatch(E e);
	}
}
