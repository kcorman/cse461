package lobby;

import java.util.Collections;
import java.util.List;

public class LobbyStateImpl implements LobbyState {
	private List<Room> rooms;
	
	public LobbyStateImpl(List<Room> rooms) {
		this.rooms = rooms;
	}
	
	public List<Room> getRooms() {
		return Collections.unmodifiableList(rooms);
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
