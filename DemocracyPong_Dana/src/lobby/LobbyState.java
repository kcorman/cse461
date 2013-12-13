package lobby;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


public interface LobbyState extends Serializable{
	
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

		@Override
		public String toString() {
			return "Room [roomID=" + roomID + ", players=" + players + "]";
		}
	}
	
}
