import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Project1_Finished {
	static final int HEADER_LENGTH = 12;	//header length in bytes
	static final String HOST = "bicycle.cs.washington.edu";
	
	static final String step_a_msg = "hello world\0";
	static final int step_a_port = 12235;
	
	static final short STEP1 = 1;
	static final short STEP2 = 2;
	static final short SID = 83;
	
	public static void main(String[] args) {
		
		//////////////////////////////////////////////
		/////	STAGE A1/A2
		//////////////////////////////////////////////
		
		int num, len, udp_port;
		num = len = udp_port = -1;
		byte[] secret_a = new byte[4];
		
		try {
			// Create socket and connect to port
			DatagramSocket ds = new DatagramSocket();
			ds.connect(InetAddress.getByName(Project1_Finished.HOST), step_a_port);
			
			// Append header & byte-align message and send packet
			byte[] step_a_with_header = prepend_and_align(step_a_msg.getBytes(), new byte[4], STEP1, SID);
			ds.send(new DatagramPacket(step_a_with_header, step_a_with_header.length));
			
			// Receive 
			byte[] rec = new byte[HEADER_LENGTH+16];
			ds.receive(new DatagramPacket(rec, rec.length));
			
			// Get num, len, udpport, and secret_a
			ByteBuffer received = ByteBuffer.wrap(rec); 
			received.position(HEADER_LENGTH);
			num = received.getInt();
			len = received.getInt();
			udp_port = received.getInt();
			received.get(secret_a, 0, 4);
			
			System.out.println("Stage A:\tsecret_a = " + Arrays.toString(secret_a) + "\tnum = " + num + " len = " + len + " udp_port = " + udp_port);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		//////////////////////////////////////////////
		/////	STAGE B1/B2
		//////////////////////////////////////////////
		
		int tcp_port = -1;
		byte[] secret_b = new byte[4];

		try {
			// Set up DatagramSocket
			DatagramSocket ds = new DatagramSocket();
			ds.setSoTimeout(500);
			ds.connect(InetAddress.getByName(Project1_Finished.HOST), udp_port);
			
			ByteBuffer buf = ByteBuffer.wrap(new byte[len+4]);	// buffer for sending
			for (int i = 0; i < num; ) {
				// Clear and fill payload
				buf.clear();
				buf.putInt(i);			// put packet id
				buf.put(new byte[len]);	// put len 0 bytes

				// Add header and word align payload
				byte[] to_send = prepend_and_align(buf.array(), secret_a, STEP1, SID);
				ds.send(new DatagramPacket(to_send, to_send.length));

				// Receive ACK -- if timeout exception, retry
				byte[] rec = new byte[HEADER_LENGTH+4];				// header plus 4 byte ack
				DatagramPacket to_rec = new DatagramPacket(rec, rec.length);
				try {
					ds.receive(to_rec);
					i++;
				} catch (SocketTimeoutException e) {
					continue;
				}
			}
			
			// Receive TCP port and secret_b
			byte[] rec = new byte[HEADER_LENGTH+8];
			ds.receive(new DatagramPacket(rec, rec.length));
			buf = ByteBuffer.wrap(rec);
			buf.position(HEADER_LENGTH);
			tcp_port = buf.getInt();
			secret_b = new byte[4];
			buf.get(secret_b, 0, 4);
			
			System.out.println("Stage B:\tsecret_b = " + Arrays.toString(secret_b) + "\ttcp_port = " + tcp_port);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//////////////////////////////////////////////
		/////	STAGE C1/C2, D1/D2
		//////////////////////////////////////////////
		
		try {
			
			/*
			 * Stage C
			 */
			
			// Open TCP socket
			Socket s = new Socket(InetAddress.getByName(HOST), tcp_port);
			
			// Read header + 13 characters for num2, len2, secretC, and char c
			byte[] input = new byte[HEADER_LENGTH + 13];
			InputStream is = s.getInputStream();
			is.read(input);

			// Parse input
			ByteBuffer buf = ByteBuffer.wrap(input);
			buf.position(HEADER_LENGTH);
			int num2 = buf.getInt();
			int len2 = buf.getInt();
			byte[] secret_c = new byte[4];
			buf.get(secret_c, 0, 4);
			byte c = buf.get();
			System.out.println("Stage C:\tsecret_c = " + Arrays.toString(secret_c) + "\tnum2 = " + num2 + " len2 = " + len2 + " c = " + c);
			
			
			/*
			 * Stage D
			 */
			
			// Fill byte array of size len2 with char c
			byte[] payload_d = new byte[len2];
			Arrays.fill(payload_d, c);
			
			// Place header and word-align the payload
			byte[] aligned_payload = prepend_and_align(payload_d, secret_c, STEP1, SID);
			
			// Write payload num2 times
			OutputStream os = s.getOutputStream();
			for (int i = 0; i < num2; i++)
				os.write(aligned_payload);

			// Receive secret_d
			byte[] secret_d = new byte[4];	// No header? (the rest is filled with zeros)
			is.read(secret_d);
			
			System.out.println("Stage D:\tsecret_d = " + Arrays.toString(secret_d));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Prepends header and word-aligns payload by filling with zeros
	 * @param input The message to send
	 * @param secret The secret, expected to be an array of bytes of the exact length
	 * @param step the step in the sequence
	 * @param digits the last 3 digits of your student id number
	 * @return a ByteBuffer consisting of a header with your message appended to it
	 */
	public static byte[] prepend_and_align(byte[] input, byte[] secret, short step, short digits){
		int alignedLen = input.length % 4 == 0 ? input.length : input.length + (4 - input.length % 4);
		//System.out.println("Msg len= "+input.length+", alignedlen= "+alignedLen);
		byte[] message = new byte[alignedLen+HEADER_LENGTH];
		ByteBuffer messageBuffer = ByteBuffer.wrap(message);
		messageBuffer.putInt(input.length);
		messageBuffer.put(secret);
		messageBuffer.putShort(step);
		messageBuffer.putShort(digits);
		messageBuffer.put(input);
		return messageBuffer.array();
	}
}
