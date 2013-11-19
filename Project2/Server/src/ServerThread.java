import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;


public class ServerThread extends Thread {
	private static final int P_SECRET = 0;
	private static final short STEP1 = 1;
	private static final short STEP2 = 2;
	private static final int MAX_PORT = 65535;
	private static final int MIN_PORT = 256;
	private static final String PAYLOAD_A = "hello world\0";
	
	private short sid;	// student ID
	private DatagramSocket udpSocket;
	private ServerSocket tcpSocket;
	private int num;
	private int num2;
	private int len;
	private int len2;
	private int secretA;
	private int secretB;
	private int secretC;
	
	public ServerThread(DatagramPacket packet, DatagramSocket ds) {
		udpSocket = null;
		tcpSocket = null;
		
		////////////////////////////// Step a1 /////////////////////////////////////////////
		
		// Verify total packet length
		if (packet.getLength() != (Main.HEADER_LEN + PAYLOAD_A.length())) {
			return;
		}
		
		// Verify header
		byte[] received = 
				Arrays.copyOfRange(packet.getData(), 0, Main.HEADER_LEN + PAYLOAD_A.length());
		ByteBuffer receivedBuf = ByteBuffer.wrap(received);
		if (receivedBuf.getInt() != PAYLOAD_A.length() || 
			receivedBuf.getInt() != P_SECRET           ||
			receivedBuf.getShort() != STEP1) {
			return;
		}
		sid = receivedBuf.getShort();
		if (!Arrays.equals(Arrays.copyOfRange(received, received.length - PAYLOAD_A.length(), 
				received.length), PAYLOAD_A.getBytes())) {
			return;
		}
		
		////////////////////////////// Step a2 /////////////////////////////////////////////
		
		// Generate server payload
		int port = Main.RAND.nextInt(MAX_PORT - MIN_PORT) + MIN_PORT;
		num = Main.RAND.nextInt(30);
		len = Main.RAND.nextInt(30);
		secretA = Main.RAND.nextInt(100);
		
		// Stuff server payload
		byte[] payload = new byte[16];
		ByteBuffer buf = ByteBuffer.wrap(payload);
		buf.order(ByteOrder.BIG_ENDIAN);
		buf.putInt(num);
		buf.putInt(len);
		buf.putInt(port);
		buf.putInt(secretA);
		
		// Send server payload
		byte[] toSend = Main.generatePacket(P_SECRET, STEP2, sid, payload);
		DatagramPacket response = 
				new DatagramPacket(toSend, toSend.length, packet.getAddress(), packet.getPort());
		try {
			ds.send(response);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Open socket on port "port" that client will send to in part b
		try {
			udpSocket = new DatagramSocket(port, InetAddress.getByName(Main.HOST));
			udpSocket.setSoTimeout(3000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		if (udpSocket == null)
			return;
		
		////////////////////////////// Step b1 /////////////////////////////////////////////
		
		// Generate header to use for stage B
		byte[] ack = new byte[Main.HEADER_LEN + 4];
		ByteBuffer buf = ByteBuffer.wrap(ack);
		buf.order(ByteOrder.BIG_ENDIAN);
		buf.putInt(4);
		buf.putInt(secretA);
		buf.putShort(STEP1);
		buf.putShort(sid);
		System.out.println("num = " + num);
		System.out.println("len = " + len);
		System.out.println("SecretA = " + secretA);
		System.out.println("part b header = " + Arrays.toString(ack));
		
		int acked = 0;
		boolean droppedAck = false;
		DatagramPacket packet =  null;
		while (acked < num) {
			try {
				byte[] received = new byte[Main.MAX_PACKET_LEN];
				packet = new DatagramPacket(received, Main.MAX_PACKET_LEN);
				udpSocket.receive(packet);
				if (!verifyPacket(packet, acked)) {
					System.out.println("Verify packet returned false");
					udpSocket.close();
					return;
				}
				
				// If random number is even, ack packet (33.3% chance of NOT acking)
				boolean needToDrop = !droppedAck && acked == (num - 1);
				if (Main.RAND.nextInt(3) % 2 == 0 && !needToDrop) {
					System.out.println("Acking! ack = " + acked);
					buf.position(Main.HEADER_LEN);
					buf.putInt(acked++);
					DatagramPacket toSend = 
							new DatagramPacket(ack, ack.length, packet.getAddress(), packet.getPort());
					udpSocket.send(toSend);
				} else {
					System.out.println("Dropping ack: " + acked);
					droppedAck = true;
				}
			} catch (Exception e) {
				udpSocket.close();
				e.printStackTrace();
			}
		}
		
		// Set up TCP socket for part C
		// TODO: if port is busy or something then switch to different port #
		int tcpPort = Main.RAND.nextInt(MAX_PORT - MIN_PORT) + MIN_PORT;
		try {
			tcpSocket = new ServerSocket(tcpPort);
			tcpSocket.setSoTimeout(3000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		////////////////////////////// Step b2 /////////////////////////////////////////////
		secretB = Main.RAND.nextInt(100);
		byte[] payload = new byte[8];
		buf.clear();
		buf = ByteBuffer.wrap(payload);
		buf.order(ByteOrder.BIG_ENDIAN);
		buf.putInt(tcpPort);
		buf.putInt(secretB);
		byte[] finalB = Main.generatePacket(secretA, STEP2, sid, payload);
		try {
			DatagramPacket finalPacketB = 
					new DatagramPacket(finalB, finalB.length, packet.getAddress(), packet.getPort());
			udpSocket.send(finalPacketB);
		} catch (Exception e) {
			udpSocket.close();
			e.printStackTrace();
		}
		udpSocket.close();

		////////////////////////////// Step c2 /////////////////////////////////////////////
		try {
			System.out.println("calling accept()");
			Socket client = tcpSocket.accept();
			System.out.println("done calling accept()");
			BufferedOutputStream output = new BufferedOutputStream(client.getOutputStream());
			BufferedInputStream input = new BufferedInputStream(client.getInputStream());
			byte[] c1Payload = new byte[16];
			buf.clear();
			buf = ByteBuffer.wrap(c1Payload);
			buf.order(ByteOrder.BIG_ENDIAN);
			num2 = Main.RAND.nextInt(30);
			System.out.println("num2 = " + num2);
			len2 = Main.RAND.nextInt(30);
			System.out.println("len2 = " + len2);
			secretC = Main.RAND.nextInt(100);
			System.out.println("secretC = " + secretC);
			byte c = (byte) (Main.RAND.nextInt(94) + 33);	// includes punctuation, numbers, letters
			System.out.println("c = " + (char)c);
			buf.putInt(num2);
			buf.putInt(len2);
			buf.putInt(secretC);
			buf.put(c);
			System.out.println("c payload = " + Arrays.toString(c1Payload));
			byte[] c1Packet = Main.generatePacket(secretB, STEP2, sid, c1Payload);
			System.out.println("c packet = " + Arrays.toString(c1Packet));
			output.write(c1Packet);
			output.flush();
			
			//////////////////////////////Step d1 /////////////////////////////////////////////
			int paddedPayloadLen = len2 % Main.BOUNDARY == 0 ? 
					len2 : len2 + (Main.BOUNDARY - len2 % Main.BOUNDARY);
			
			for (int i = 0; i < num2; ++i) {
				byte[] received = new byte[Main.HEADER_LEN + paddedPayloadLen];
				int bytesRead = input.read(received);
				System.out.println("i = " + i + ", byte[] = " + Arrays.toString(received));
				
				// Check total packet length
				if (bytesRead != Main.HEADER_LEN + paddedPayloadLen)
					return;
				buf.clear();
				buf = ByteBuffer.wrap(received);
				
				// Check payload length (should not include padding)
				if (buf.getInt() != len2)
					return;
				
				// Check that all byte in payload are equal to char c
				buf.position(Main.HEADER_LEN);
				for (int j = 0; j < len2; ++j) {
					if (buf.get() != c)
						return;
				}
			}
			
			////////////////////////////// Step d2 /////////////////////////////////////////////
			int secretD = Main.RAND.nextInt(100);
			byte[] finalD = new byte[4];
			buf.clear();
			buf = ByteBuffer.wrap(finalD);
			buf.order(ByteOrder.BIG_ENDIAN);
			buf.putInt(secretD);
			byte[] finalPacketD = Main.generatePacket(secretC, STEP2, sid, finalD);
			output.write(finalPacketD);
			output.flush();
			
			tcpSocket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Finished!");
	}
	
	private boolean verifyPacket(DatagramPacket packet, int packetID) {
		int size = packet.getLength();
		int paddingLen = len % Main.BOUNDARY == 0 ? 
				len : len + (Main.BOUNDARY - len % Main.BOUNDARY);

		// Check total packet size
		if (size != (Main.HEADER_LEN + 4 + paddingLen)) {
			return false;
		}
		ByteBuffer buf = ByteBuffer.wrap(packet.getData());
		
		// Verify payload size
		if (buf.getInt() != (len + 4)) {
			return false;
		}
		
		// Check packet ID and payload bytes
		buf.position(Main.HEADER_LEN);
		if (buf.getInt() != packetID) {
			return false;
		}
		
		for (int i = 0; i < len; ++i) {
			if (buf.get() != 0) {
				return false;
			}
		}
		return true;
	}
}
