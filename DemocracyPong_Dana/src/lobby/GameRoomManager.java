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
			System.out.println("Looping!");
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
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
				users.put(uid, u);
			}
			//ls = new LobbyStateImpl(rooms);
			
			// update users
			for (int uid : users.keySet()) {
				processUser(uid);
			}
			
			ls = new LobbyStateImpl(rooms);
			System.out.println("lobbystate = " + ls);
			for (User u : users.values()) {
				ObjectOutputStream out = u.getUserOutputStream();
				try {
					// update lobby info
					System.out.println("lobbystate = " + ls);
					out.writeObject(ServerOption.UPDATE);
					out.writeObject(ls.deepCopy());
					out.flush();
				} catch (Exception e) {
					e.printStackTrace();
				}
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
		Map<Integer, User> userMap = new HashMap<Integer, User>();
		for (int id : room.getPlayers()) {
			userMap.put(id, users.get(id));
		}
		int port = udpPort++;
		try {
			DatagramSocket dgSocket = new DatagramSocket(port);
			GameServer s = new GameSocketServer(userMap, dgSocket);
			s.start();
			for(User usr : userMap.values()){
				ObjectOutputStream out = usr.getUserOutputStream();
				out.writeObject(ServerOption.START);
				out.writeInt(port);
				out.flush();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void processUser(int uid) {
		User u = users.get(uid);
		ObjectInputStream in = u.getUserInputStream();
		
		try {
			// process any user requests
			ClientOption opt;
			//System.out.println("available = " + u.getUserSocket().getInputStream().available());
			if (u.getUserSocket().getInputStream().available() > 0) {
				opt = (ClientOption) in.readObject();
				System.out.println(opt);
				switch(opt) {
					case HOST:
						Room room = new Room(uid);
						room.addPlayer(uid);
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
									reqRoom.addPlayer(uid);
								}
							}
							
							// if room dne, add to any room
							if (roomIdx < 0)
								rooms.get(0).addPlayer(uid);
						} else {
							System.out.println("Client did not specify room# in join room!!!"
									+ " (added to random room)");
							rooms.get(0).addPlayer(uid);
						}
						break;
					case LEAVE:
						Room lvRoom = getRoomContainingUser(uid);
						lvRoom.removePlayer((Integer)uid);
						if (lvRoom.getPlayers().isEmpty())
							rooms.remove(lvRoom);
						break;
					case START:
						startQueue.add(u);
						break;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Room getRoomContainingUser(int uid) {
		for (Room r : rooms) {
			for (int p : r.getPlayers()) {
				if (p == uid) {
					return r;
				}
			}
		}
		return null;
	}
}
