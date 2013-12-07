package game.entities;

import java.nio.ByteBuffer;

/**
 * 
 * @author kenny
 * A container for the fields that the client must communicate to the server
 */
public class ClientState {
	public int userId;
	public int yVote;
	
	public ClientState(int userId){
		this.userId = userId;
	}
	
	public static ClientState fromBytes(byte[] state){
		ClientState s = new ClientState(-1);
		ByteBuffer buf = ByteBuffer.wrap(state);
		//order is very important here
		s.userId = buf.getInt();
		s.yVote = buf.getInt();
		return s;
	}
	
	public byte[] toBytes(){
		byte[] array = new byte[getMaxSize()];
		ByteBuffer buf = ByteBuffer.wrap(array);
		//Order is very important here
		buf.putInt(userId);
		buf.putInt(yVote);
		return array;
	}

	/**
	 * Returns the max client size in bytes
	 * @return
	 */
	public static int getMaxSize(){
		return 2* 4;
	}
	
	
	
}
