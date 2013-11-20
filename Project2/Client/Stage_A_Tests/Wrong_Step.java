/* Jonathan Ellington,
 * Kenny Corman,
 * Dana Van Aken
 * CSE 461 Project 1
 */

import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class Wrong_Step {
	static final int HEADER_LENGTH = 12;	// header length in bytes
	static final String HOST = "localhost";
	
	static final String step_a_msg = "hello world\0";
	static final int step_a_port = 12236;
	
	static final short STEP1 = 1;
	static final short STEP2 = 2;
	static final short SID = 83;
	
	public static void main(String[] args) {
		
		//////////////////////////////////////////////
		/////	STAGE A1/A2
		//////////////////////////////////////////////
		
		int num, len, udp_port, secret_a;
		num = len = udp_port = secret_a = -1;
		//byte[] secret_a = new byte[4];
		
		try {
			// Create socket and connect to port
			DatagramSocket ds = new DatagramSocket(12235, InetAddress.getByName(HOST));
			ds.connect(InetAddress.getByName(Project1_Finished.HOST), step_a_port);
			
			// Append header & byte-align message and send packet
			byte[] step_a_with_header = prepend_and_align(step_a_msg.getBytes(), 0, STEP2, SID);
			ds.send(new DatagramPacket(step_a_with_header, step_a_with_header.length));
      ds.setSoTimeout(5000);
			
			// Receive 
			byte[] rec = new byte[HEADER_LENGTH+16];
			ds.receive(new DatagramPacket(rec, rec.length));
			ds.close();
			
			// Get num, len, udpport, and secret_a
			ByteBuffer received = ByteBuffer.wrap(rec); 
			received.position(HEADER_LENGTH);
			num = received.getInt();
			len = received.getInt();
			udp_port = received.getInt();
			secret_a = received.getInt();
			System.out.println("Stage A:\tsecret_a = " + secret_a);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		//////////////////////////////////////////////
		/////	STAGE B1/B2
		//////////////////////////////////////////////
		
		int tcp_port, secret_b;
		secret_b = tcp_port = -1;

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
			ds.close();
			
			buf = ByteBuffer.wrap(rec);
			buf.position(HEADER_LENGTH);
			tcp_port = buf.getInt();
			secret_b = buf.getInt();
			
			System.out.println("Stage B:\tsecret_b = " + secret_b);
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
			byte[] input = new byte[HEADER_LENGTH+16];
			InputStream is = s.getInputStream();
			is.read(input);

			// Parse input
			ByteBuffer buf = ByteBuffer.wrap(input);
			buf.position(HEADER_LENGTH);
			int num2 = buf.getInt();
			int len2 = buf.getInt();
			int secret_c = buf.getInt();
			byte c = buf.get();
			System.out.println("Stage C:\tsecret_c = " + secret_c);
			
			
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
			for (int i = 0; i < num2; i++) {
                os.write(aligned_payload);
			    os.flush();
            }

			// Receive secret_d
			byte[] received = new byte[HEADER_LENGTH + 4];
			is.read(received);
			buf.clear();
			buf = ByteBuffer.wrap(received);
			buf.position(HEADER_LENGTH);
			int secret_d = buf.getInt();
			System.out.println("Stage D:\tsecret_d = " + secret_d);
			s.close();
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
	public static byte[] prepend_and_align(byte[] input, int secret, short step, short digits){
		int alignedLen = input.length % 4 == 0 ? input.length : input.length + (4 - input.length % 4);
		byte[] message = new byte[alignedLen+HEADER_LENGTH];
		ByteBuffer messageBuffer = ByteBuffer.wrap(message);
		messageBuffer.putInt(input.length);
		messageBuffer.putInt(secret);
		messageBuffer.putShort(step);
		messageBuffer.putShort(digits);
		messageBuffer.put(input);
		return messageBuffer.array();
	}
}
