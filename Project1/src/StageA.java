import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class StageA{
	public static JunkDrawer stageA(short studentNum){
		try {
			int port = 12235;
			DatagramSocket ds = new DatagramSocket();
			short step = 1;
			byte[] message = "hello world\0".getBytes();
			byte[] withHeader = Project1.prependHeader(message, new byte[4], step, studentNum);
			ds.connect(InetAddress.getByName(Project1.HOST), port);
			DatagramPacket p = new DatagramPacket(withHeader, withHeader.length);
			ds.send(p);
			
			JunkDrawer result = new JunkDrawer();
			result.d = ds;
			return result;
			//System.out.println(Arrays.toString(received));
			//ds.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
