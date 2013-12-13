package lobby;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface LobbyState {
	
	public List<Room> getRooms();
	
	public Room getRoomContainingUser(int uid);
	
	public static class Room implements Serializable {
		int roomID;		// same as hostID
		Map<Integer, User> players;
		
		public Room(int roomID) {
			this.roomID = roomID;
			//players = new ArrayList<Integer>();
			players = new HashMap<Integer, User>();
		}
		
		public Set<Integer> getPlayers() {
			return Collections.unmodifiableSet(players.keySet());
		}
	}
	
}
