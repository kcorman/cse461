package lobby;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public interface LobbyState {
	
	public List<Room> getRooms();
	
	public Room getRoomContainingUser(int uid);
	
	public static class Room implements Serializable {
		int roomID;		// same as hostID
		List<Integer> players;
		
		public Room(int roomID) {
			this.roomID = roomID;
			players = new ArrayList<Integer>();
		}
		
		public List<Integer> getPlayers() {
			return Collections.unmodifiableList(players);
		}
	}
	
}
