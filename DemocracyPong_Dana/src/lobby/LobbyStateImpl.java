package lobby;

import java.util.Collections;
import java.util.List;

public class LobbyStateImpl implements LobbyState {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5361415065760402416L;
	private List<Room> rooms;
	public int number;
	public void setRooms(List<Room> rms){
		this.rooms = rms;
	}
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
		return "LobbyStateImpl [rooms=" + rooms + "], number="+number;
	}
}
