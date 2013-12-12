package lobby;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lobby.LobbyState.Room;

public class LobbyStateImpl implements LobbyState {
	private List<Room> rooms;
	
	public LobbyStateImpl() {
		rooms = new ArrayList<Room>();
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
}
