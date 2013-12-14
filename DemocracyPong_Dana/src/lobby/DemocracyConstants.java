package lobby;

/**
 * 
 * @author danava04
 * 
 * Constants used by the lobby client and server to communicate.
 */
public class DemocracyConstants {
	public enum ClientOption {
		HOST, JOIN, LEAVE, START, SET_NAME
	}
	
	public enum ServerOption {
		UID, UPDATE, START
	}
}
