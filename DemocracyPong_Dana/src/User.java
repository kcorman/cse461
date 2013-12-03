import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class User {
	private int userID;
	private Socket userSocket;
	private PrintWriter out;
	private BufferedReader in;
	
	public User(int userID, Socket userSocket) {
		this.userID = userID;
		this.userSocket = userSocket;
		try {
			// using printwriter means you don't have to flush after each write
			out = new PrintWriter(userSocket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getUserID() {
		return userID;
	}
	
	public Socket getUserSocket() {
		return userSocket;
	}
	
	public PrintWriter getUserOutputStream() {
		return out;
	}
	
	public BufferedReader getUserInputStream() {
		return in;
	}
}
