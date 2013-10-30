import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Project1{
	static final int HEADER_LENGTH = 12;		//header length in bytes
	static final String HOST = "bicycle.cs.washington.edu";
	public static void main(String[] args){
		JunkDrawer relay = StageA.stageA((short)780);
		relay = StageB.stageB(relay);
		
		
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
	public static byte[] prependHeader(byte[] input, byte[] secret, short step, short digits){
		int alignedLen = input.length % 4 == 0 ? input.length : input.length + (4 - input.length%4);
		//System.out.println("Msg len= "+input.length+", alignedlen= "+alignedLen);
		byte[] message = new byte[alignedLen+HEADER_LENGTH];
		ByteBuffer messageBuffer = ByteBuffer.wrap(message);
		messageBuffer.order(ByteOrder.BIG_ENDIAN);
		messageBuffer.putInt(alignedLen);
		messageBuffer.put(secret);
		messageBuffer.putShort(step);
		messageBuffer.putShort(digits);
		messageBuffer.put(input);
		return messageBuffer.array();
	}
	
	
	/**
	 * Reads a header and returns the length of the (rest of) the payload
	 * @param s the socket to read from
	 * @return the length of the rest of the message
	 * @throws IOException
	 */
	public static int readHeaderLength(byte[] buffer) throws IOException{
		ByteBuffer buf = ByteBuffer.wrap(buffer);
		int len = buf.getInt()-HEADER_LENGTH;
		byte[] secret = new byte[4];
		buf.get(secret);
		short step = buf.getShort();
		short sid = buf.getShort();
		System.out.printf("Header read: len=%d, secret=%s, step=%d, sid=%d\n",len,Arrays.toString(secret),step,sid);
		return len;
	}
	
	/**
	 * Attempts to read len bytes from s and returns a byte buffer
	 * @param s
	 * @param len
	 * @return
	 * @throws IOException 
	 */
	public static ByteBuffer buildBuf(DatagramSocket s, int len) throws IOException{
		System.out.println("Attempting to read buffer.. of len= "+len);
		byte[] bytes = new byte[len];
		s.receive(new DatagramPacket(bytes,bytes.length));
		System.out.println("Read bytebuffer");
		ByteBuffer buf = ByteBuffer.wrap(bytes);
		buf.order(ByteOrder.BIG_ENDIAN);
		return buf;
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
