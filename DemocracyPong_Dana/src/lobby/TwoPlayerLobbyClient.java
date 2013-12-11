package lobby;

import game.client.GameClientConnection;
import game.client.GameClientMockConnection;
import game.client.GameClientModel;
import game.client.GameClientNetworkConnection;
import game.client.GameClientWindow;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;

/**
 * 
 * @author kenny
 * A simple lobby client for the TwoPlayerLobby
 */
public class TwoPlayerLobbyClient {
	public static void main(String[] args){
		if(args.length != 2){
			System.err.println("Usage: java TwoPlayerLobbyClient <hostname> <port>");
		}
		String host = args[0];
		int port = Integer.parseInt(args[1]);
		Socket s = null;
		try {
			s = new Socket(host,port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(s == null) System.exit(1);
		try {
			DataInputStream ds = new DataInputStream(s.getInputStream());
			
			int id = ds.readInt();
			int udpport = -1;
			while(udpport == -1){
				udpport = ds.readInt();
			}
			//should have id and udppport now
			System.out.printf("Received id= %d, port =%d\nTrying to connect now...\n",id,udpport);
			GameClientModel m = new GameClientModel();
			GameClientWindow w = new GameClientWindow(m);
			GameClientConnection c = new GameClientNetworkConnection(host, udpport, id, w, m);
			boolean success = c.connect();
			if(!success){
				System.err.println("Could not connect");
			}else{
				w.run();
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
