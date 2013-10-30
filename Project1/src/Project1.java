import java.net.DatagramSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Project1{
	static final int HEADER_LENGTH = 12;		//header length in bytes
	public static void main(String[] args){
		/*byte[] message = "Hello".getBytes();
		//System.out.println(message.length);
		byte[] withHeader = prependHeader(message, message.length, "scrt".getBytes(), (short)1, (short)555);
		
		System.out.println("arr= "+Arrays.toString(withHeader));
		System.out.println(new String(withHeader, HEADER_LENGTH,withHeader.length-HEADER_LENGTH));*/
	}
	
	public static void messWithobject(Object o){
		o = new Object();
	}
	
	/**
	 * 
	 * @param input The message to send
	 * @param len The length of the message
	 * @param secret The secret, expected to be an array of bytes of the exact length
	 * @param step the step in the sequence
	 * @param digits the last 3 digits of your student id number
	 * @return a ByteBuffer consisting of a header with your message appended to it
	 */
	public static byte[] prependHeader(byte[] input, int len, byte[] secret, short step, short digits){
		byte[] message = new byte[len+HEADER_LENGTH];
		ByteBuffer messageBuffer = ByteBuffer.wrap(message);
		messageBuffer.order(ByteOrder.BIG_ENDIAN);
		messageBuffer.putInt(len);
		messageBuffer.put(secret);
		messageBuffer.putShort(step);
		messageBuffer.putShort(digits);
		messageBuffer.put(input);
		return messageBuffer.array();
	}
}



class JunkDrawer{
	Socket s;
	DatagramSocket d;
	int port;
	String secret;
}

/*public JunkDrawer stageA();

public JunkDrawer stageB(Socket s, String secret);

public JunkDrawer stageC(int, String secret);

public void stageD(UDPSocket s, String secret);*/
