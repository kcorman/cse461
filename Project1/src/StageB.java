import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StageB{
	public static JunkDrawer stageB(JunkDrawer prev) {
		DatagramSocket ds = prev.d;
		try {
			System.out.println("Trying to receive..");
			ByteBuffer received = Project1.buildBuf(ds,64);
			System.out.println("stage 2a received: "+Arrays.toString(received.array()));
			ByteBuffer buf = received;
			buf.order(ByteOrder.BIG_ENDIAN);
			int offset = buf.getInt();
			buf.position(Project1.HEADER_LENGTH);
			int num = buf.getInt();
			int len = buf.getInt();
			if(len % 4 > 0) len += 4-(len % 4);
			int udp_port = buf.getInt();
			System.out.println("UDP port= "+udp_port);
			byte[] secretA = new byte[4];
			buf.get(secretA);
			ds.close();
			ds = new DatagramSocket();
			ds.connect(InetAddress.getByName(Project1.HOST), udp_port);
			Set<Integer> packetIds = new HashSet<Integer>();
			for(int i = 0;i<num;i++){
				int id = -i-1;
				packetIds.add(id);
				byte[] packet = new byte[4+len];
				buf = ByteBuffer.wrap(packet);
				buf.order(ByteOrder.BIG_ENDIAN);
				buf.putInt(id);
				DatagramPacket toSend = new DatagramPacket(packet, packet.length);
				ds.send(toSend);
			}
			byte[] rec = new byte[4];
			while(!packetIds.isEmpty()){
				ds.receive(new DatagramPacket(rec,rec.length));
				//populates received with data from ack
				buf = ByteBuffer.wrap(rec);
				buf.order(ByteOrder.BIG_ENDIAN);
				int ack = buf.getInt();
				packetIds.remove(ack);
			}
			byte[] secretPacket = new byte[8];
			ds.receive(new DatagramPacket(secretPacket, secretPacket.length));
			buf = ByteBuffer.wrap(secretPacket);
			buf.order(ByteOrder.BIG_ENDIAN);
			int port = buf.getInt();
			byte[] secret = new byte[4];
			buf.get(secret);
			
			System.out.println("Secret= "+new String(secret));
			ds.close();
			//Read info, now need to send num udp packets
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
