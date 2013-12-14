package lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class LobbyStateImpl implements LobbyState {
	private List<Room> rooms;
	private Map<Integer, String> userNames;
	
	public LobbyState deepCopy(){
		LobbyStateImpl copy = new LobbyStateImpl(new ArrayList<Room>());
		for(Room r : rooms){
			copy.rooms.add(r.deepCopy());
		}
		copy.userNames = new HashMap<Integer,String>(userNames);
		return copy;
	}
	public LobbyStateImpl(List<Room> rooms) {
		this.rooms = new ArrayList<Room>(rooms);
		this.userNames = new HashMap<Integer, String>();
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
	@Override
	public Map<Integer, String> getUserNames() {
		return userNames;
	}
	@Override
	public void setUserNames(Map<Integer, String> names) {
		this.userNames = names;
		
	}
}
