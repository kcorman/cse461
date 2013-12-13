package lobby;

/**
 * 
 * @author danava04
 * 
 * Constants used by the lobby client and server to communicate.
 */
public class DemocracyConstants {
	public enum ClientOption {
		HOST, JOIN, LEAVE, START
	}
	
	public enum ServerOption {
		UID, UPDATE, START
	}
}
