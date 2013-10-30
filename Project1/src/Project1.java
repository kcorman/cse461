import java.net.DatagramSocket;
import java.net.Socket;

public class Project1{
	public static void main(String[] args){

	}
	
	public static void messWithobject(Object o){
		o = new Object();
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
