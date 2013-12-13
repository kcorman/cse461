package game.server;

import game.entities.ClientState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class MockClient implements Runnable {
	static final int REG_TIMEOUT = 100;

	private int userid;
	private DatagramSocket ds;
	private int team;
	private ClientState cs;

	public MockClient(String host, int port, int userid) {
		this.userid = userid;
		try {
			ds = new DatagramSocket();
			ds.connect(InetAddress.getByName(host), port);
		} catch (SocketException | UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		cs = new ClientState(userid);
		cs.yVote = 50;
	}

	@Override
	public void run() {
		getAndSendInitialInfo();
		System.out.println("CLIENT " + userid + ": registered on team " + team);
		
		while (true) {
			int plen = ClientState.getMaxSize();
			byte[] buf = cs.toBytes();
			DatagramPacket cp = new DatagramPacket(buf, plen);
			try {
				ds.send(cp);
				Thread.sleep(50);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// send client state
		// receive client data
	}

	private void getAndSendInitialInfo(){
		byte[] idBuf = new byte[4];
		ByteBuffer buf = ByteBuffer.wrap(idBuf);
		buf.putInt(userid);
		DatagramPacket idPacket = new DatagramPacket(idBuf,idBuf.length);
		
		byte[] serverAckBuf = new byte[1];
		DatagramPacket serverAckPacket = new DatagramPacket(serverAckBuf, serverAckBuf.length);
		boolean receivedAck = false;
		while(!receivedAck){
			try {
				ds.setSoTimeout(REG_TIMEOUT);
				System.out.println("CLIENT " + userid + ": attempting to register");
				ds.send(idPacket);
				ds.receive(serverAckPacket); 
				receivedAck = true;
				byte indicator = serverAckBuf[0];
				System.out.println("CLIENT " + userid + ": received ack: " + indicator);
				if(indicator == 0 || indicator == 1){
					team = indicator;
				}else{
					//server error, must exit
					throw new RuntimeException("Server responded with code: "+indicator+", will not start.");
				}
			} catch (SocketTimeoutException e) {
				continue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("CLIENT:" + userid + " Done registering.");
		//throw new UnsupportedOperationException();
	}
}
