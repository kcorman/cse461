import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;


public class Main {
	public static final int HEADER_LEN = 12;
	public static final Random RAND = new Random();
	public static final int MAX_PACKET_LEN = 255;
	public static final int BOUNDARY = 4;

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage:\n\t./Main <server> <port>");
			return;
		}

		String host = args[0];
		int port = Integer.parseInt(args[1]);

		InetAddress addr = null;
		DatagramSocket ds = null;
		// Open main socket to receive requests
		try {
			addr = InetAddress.getByName(host);
			ds = new DatagramSocket(port, addr);
			ds.setSoTimeout(3000);
		} catch (Exception e) {
			ds.close();
			e.printStackTrace();
		} 
		
		// Loop, accept client requests and spawn new thread to handle each one
		while (true) {
			byte[] received = new byte[MAX_PACKET_LEN];
			DatagramPacket packet = new DatagramPacket(received, MAX_PACKET_LEN);
			try {
				ds.receive(packet);
				System.out.println("Found client!");
				ServerThread thread = new ServerThread(packet, ds, host);
				thread.start();
			} catch (SocketTimeoutException e) {
				continue;
			} catch (Exception e) {
				ds.close();
				e.printStackTrace();
			}
		}
	}
	
	public static byte[] generatePacket(int pSecret, short step, short SID, byte[] payload, int payloadLen) {
		// Create message long enough for header + payload
		int paddingLen = payload.length % BOUNDARY == 0 ? 
				payload.length : payload.length + (BOUNDARY - payload.length % BOUNDARY);
		byte[] packet = new byte[HEADER_LEN + paddingLen];
		
		ByteBuffer packetBuffer = ByteBuffer.wrap(packet);
		packetBuffer.order(ByteOrder.BIG_ENDIAN);
		packetBuffer.putInt(payloadLen);
		packetBuffer.putInt(pSecret);
		packetBuffer.putShort(step);
		packetBuffer.putShort(SID);
		packetBuffer.put(payload);
		
		return packetBuffer.array();
	}
}
