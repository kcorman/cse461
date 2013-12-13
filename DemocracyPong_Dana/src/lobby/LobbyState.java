package lobby;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public interface LobbyState extends Serializable{
	public LobbyState deepCopy();
	public List<Room> getRooms();
	
	public Room getRoomContainingUser(int uid);
	
	public static class Room implements Serializable {
		int roomID;		// same as hostID
		private List<Integer> players;
		
		public Room deepCopy(){
			Room r = new Room(roomID);
			for(Integer player : players){
				r.addPlayer(player);
			}
			return r;
		}
		
		public void addPlayer(Integer playerId){
			players.add(playerId);
		}
		
		public void removePlayer(Integer playerId){
			players.remove((Integer)playerId);
		}
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
