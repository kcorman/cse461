package lobby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;


public class GameRoomManager extends Thread { 
	Queue<User> userQueue;		/* Manager's queue of user's to process */
	List<Room> rooms;			/* List of existing rooms */
	
	private enum RoomCapacity {
		OK, FULL
	}
	
	/**
	 * Creates and initializes the GameRoomManager.
	 */
	public GameRoomManager() {
		userQueue  = new LinkedList<User>();
		rooms = new ArrayList<Room>();
	}
	
	/**
	 * Starts the GameRoomManager.
	 */
	public void run() {
		while (true) {
			// process queue
			while (!userQueue.isEmpty()) {
				processUser(userQueue.remove());
			}
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Used by the GameLobbyServer to add a new user to the queue to be processed
	 * by this GameRoomManager.
	 * @param u the user to add to the queue
	 */
	public void addUserToQueue(User u) {
		userQueue.add(u);
	}
	
	private void processUser(User u) {
		BufferedReader in = u.getUserInputStream();
		PrintWriter out = u.getUserOutputStream();
		String userInput1, userInput2;
		try {
			if ((userInput1 = in.readLine()) == null || (userInput2 = in.readLine()) == null) {
				System.out.println("User " + u.getUserID() + " failed to send a valid request");
				u.getUserSocket().close();
			} else if (rooms.isEmpty()) {
				// No choice, user has to host game
				System.out.println("userInput1 = " + userInput1 + ", userInput2 = " + userInput2);
				hostRoom(u, out);
				System.out.println("Created new room!");
			} else {
				RoomCapacity r = null;
				Room room = null;
				DemocracyConstants.UserRoomOption userOption = 
						DemocracyConstants.UserRoomOption.values()[Integer.parseInt(userInput1)];
				switch (userOption) {
					case HOST:
						hostRoom(u, out);
						break;
					case JOIN_SPECIFIC:
						int roomNum = Integer.parseInt(userInput2);
						for (int i = 0; i < rooms.size(); ++i) {
							room = rooms.get(i);
							if (room.hostID == roomNum) {
								r = room.joinRoom(u);
								sendUserStatusInfo(out, u.getUserID(), 
										DemocracyConstants.UserRoomOption.JOIN_SPECIFIC, room.hostID);
								room.sendRoomInfo();
							}
						}
						if (r != null)
							break;
						// else fall through to JOIN_RANDOM
					case JOIN_RANDOM:
						room = rooms.get(0);
						r = room.joinRoom(u);
						sendUserStatusInfo(out, u.getUserID(), 
								DemocracyConstants.UserRoomOption.JOIN_RANDOM, room.hostID);
						room.sendRoomInfo();
						break;
				}
				System.out.println("r = " + r.toString());
				if (r == RoomCapacity.FULL) {
					// TODO: hand off players to GameServer
					room.sendRoomReady();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/* Creates a new room and sends the room info back to the user */
	private void hostRoom(User u, PrintWriter out) {
		int uid = u.getUserID();
		Room r = new Room(u);
		rooms.add(r);
		sendUserStatusInfo(out, uid, DemocracyConstants.UserRoomOption.HOST, uid);
		r.sendRoomInfo();
	}
	
	private static void sendUserStatusInfo(PrintWriter out, int uid, 
			DemocracyConstants.UserRoomOption opt, int hid) {
		out.println(uid);
		out.println(opt.ordinal());
		out.println(hid);
	}
	
	/* Represents a game lobby room. The host's user id
	 * is used as the room id.
	 */
	private class Room {
		static final int MAX_PLAYERS = 4;
		int hostID;
		Map<Integer, User> players;
		
		/**
		 * Creates a new room with the given host.
		 * @param host the user to host the room
		 */
		public Room(User host) {
			this.hostID = host.getUserID();
			players = new HashMap<Integer, User>();
			players.put(hostID, host);
		}
		
		/**
		 * Adds a user to this room and returns whether the room is now full.
		 * @param u the user to add to this room
		 * @return FULL if the room is now full, or OK if not
		 */
		public RoomCapacity joinRoom(User u) {
			players.put(u.getUserID(), u);
			if (players.size() == MAX_PLAYERS)
				return RoomCapacity.FULL;
			return RoomCapacity.OK;
		}
		
		/**
		 * Sends this room's information to each user in this room.
		 */
		public void sendRoomInfo() {
			for (int i : players.keySet()) {
				PrintWriter out = players.get(i).getUserOutputStream();
				out.println(DemocracyConstants.RoomStatus.UPDATE.ordinal());
				out.println(players.size());
				out.println(hostID);
				for (int j : players.keySet()) {
					if (j != hostID)
						out.println(j);
				}
			}
		}
		
		public void sendRoomReady() {
			for (int i : players.keySet()) {
				PrintWriter out = players.get(i).getUserOutputStream();
				out.println(DemocracyConstants.RoomStatus.READY.ordinal());
			}
		}
	}
}
