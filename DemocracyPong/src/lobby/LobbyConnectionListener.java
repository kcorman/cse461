package lobby;

/**
 * 
 * @author kcorman
 * A call-back enabling interface that lets components change their functionality
 * when a connection to the lobby is successful
 */
public interface LobbyConnectionListener {
	public void onSuccessfulConnect();
}
