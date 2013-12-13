package lobby;

import game.server.GameServer;
import game.server.GameSocketServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import lobby.DemocracyConstants.ClientOption;
import lobby.DemocracyConstants.ServerOption;
import lobby.LobbyState.Room;


public class GameRoomManager extends Thread {
	private static final int BASE_UDP_PORT = 12346;
	private Queue<User> userQueue;		/* Manager's queue of user's to process */
	private Queue<User> startQueue;		/* Queue of players who want to start a game */
	private Map<Integer, User> users;	/* Map of userIDs -> user objects */
	private List<Room> rooms;			/* List of existing rooms */
	private int udpPort;				/* Port number for new game */
	
	/**
	 * Creates and initializes the GameRoomManager.
	 */
	public GameRoomManager() {
		userQueue = new LinkedList<User>();
		startQueue = new LinkedList<User>();
		users  = new HashMap<Integer, User>();
		rooms = new ArrayList<Room>();
		udpPort = BASE_UDP_PORT;
	}
	
	/**
	 * Starts the GameRoomManager.
	 */
	public void run() {
		LobbyState ls;
		while (true) {
			
			// process start game queue
			while (!startQueue.isEmpty()) {
				User u = startQueue.remove();
				startGame(u);
			}
			
			// process new user queue
			ls = new LobbyStateImpl(rooms);
			while (!userQueue.isEmpty()) {
				User u = userQueue.remove();
				int uid = u.getUserID();
				ObjectOutputStream out = u.getUserOutputStream();
				try {
					out.writeObject(ServerOption.UID);
					out.writeInt(uid);
				} catch (IOException e) {
					e.printStackTrace();
				}
				users.put(uid, u);
			}
			// update users
			for (int uid : users.keySet()) {
				processUser(uid, ls);
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
	
	public void startGame(User u) {
		int uid = u.getUserID();
		int roomIdx = -1;
		
		for (int i = 0; i < rooms.size(); ++i) {
			if (rooms.get(i).roomID == uid) {
				roomIdx = i;
				break;
			}
		}
		if (roomIdx < 0) {
			System.out.println("cannot start game, room id dne");
			return;
		}
		Room room = rooms.remove(roomIdx);
		int port = udpPort++;
		try {
			DatagramSocket dgSocket = new DatagramSocket(port);
			GameServer s = new GameSocketServer(room.players, dgSocket);
			s.start();
			for(User usr : room.players.values()){
				ObjectOutputStream out = usr.getUserOutputStream();
				out.writeObject(ServerOption.START);
				out.writeInt(port);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void processUser(int uid, LobbyState ls) {
		User u = users.get(uid);
		ObjectInputStream in = u.getUserInputStream();
		ObjectOutputStream out = u.getUserOutputStream();
		
		try {
			// process any user requests
			ClientOption opt;
			if ((opt = (ClientOption) in.readObject()) != null) {
				switch(opt) {
					case HOST:
						Room room = new Room(uid);
						room.players.put(uid, u);
						rooms.add(room);
						break;
					case JOIN:
						Integer roomNum;
						Room reqRoom;
						if ((roomNum = in.readInt()) != null) {
							int roomIdx = -1;
							for (int i = 0; i < rooms.size(); ++i) {
								if ((reqRoom = rooms.get(i)).roomID == roomNum) {
									roomIdx = i;
									reqRoom.players.put(uid, u);
								}
							}
							
							// if room dne, add to any room
							if (roomIdx < 0)
								rooms.get(0).players.put(uid, u);
						} else {
							System.out.println("Client did not specify room# in join room!!!"
									+ " (added to random room)");
							rooms.get(0).players.put(uid, u);
						}
						break;
					case LEAVE:
						Room lvRoom = ls.getRoomContainingUser(uid);
						lvRoom.players.remove(uid);
						break;
					case START:
						startQueue.add(u);
						break;
				}
				
				// update lobby info
				out.writeObject(ServerOption.UPDATE);
				out.writeObject(ls);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
