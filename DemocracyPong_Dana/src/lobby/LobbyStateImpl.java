package lobby;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LobbyStateImpl implements LobbyState {
	private List<Room> rooms;
	public LobbyState deepCopy(){
		LobbyStateImpl copy = new LobbyStateImpl(new ArrayList<Room>());
		for(Room r : rooms){
			copy.rooms.add(r.deepCopy());
		}
		return copy;
	}
	public LobbyStateImpl(List<Room> rooms) {
		this.rooms = new ArrayList<Room>(rooms);
	}
	
	public List<Room> getRooms() {
		return rooms;
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

	@Override
	public String toString() {
		return "LobbyStateImpl [rooms=" + rooms + "]";
	}
}
