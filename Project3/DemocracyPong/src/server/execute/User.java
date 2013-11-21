package server.execute;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class User{
	//the initial packet that this user sent
	DatagramPacket initialPacket = null;
	//User's id
	int id = 0;
	//Has the user acknowledged that the game has started?
	boolean acked_start = false;
	//Has the user acknowledged that they have an id?
	boolean acked_has_id = false;
	public void sendPacket(DatagramSocket ds, byte[] response_bytes){
		if(initialPacket == null) throw new IllegalStateException("Initial packet must be set before" +
				"calling sendPacket");
		DatagramPacket response = new DatagramPacket(response_bytes, response_bytes.length, 
						initialPacket.getAddress(), initialPacket.getPort());
		try {
			ds.send(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}