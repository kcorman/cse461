package game.client;

import game.entities.ClientState;
import game.entities.GameState;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

public class GameClientNetworkConnection implements GameClientConnection, Runnable{
	static final boolean DEBUG_ENABLED = true;
	static final int MAX_FAILURES = 20;
	static final int SOCKET_TIMEOUT_MS = 500;
	String host;
	int port;
	int userId;
	DatagramSocket dgSocket;
	GameClientModel model;
	GameMousePositionSource mouseSource;
	
	/**
	 * Constructs a new GameClientNetworkConnection that listens to the given host on
	 * the given port. It will try to communicate as the userId given.
	 * src is used to get mouse coordinates for "voting", and the model will be modified
	 * as new states are received
	 * Constructing this will not actually cause a connection to happen. To do that, call connect()
	 * @param host the host to connect to
	 * @param udpPort the remote port to connect to
	 * @param userId the current user's id (provided by the server from the lobby)
	 * @param src the game window to play in
	 * @param m the model that the game window displays
	 */
	public GameClientNetworkConnection(String host, int udpPort, int userId,
			GameMousePositionSource src, GameClientModel m){
		if(m == null || src == null) throw new IllegalArgumentException("m and src cannot be null");
		this.host = host;
		this.port = udpPort;
		this.userId = userId;
		this.mouseSource = src;
		this.model = m;
	}
	@Override
	public boolean connect() {
		try {
			dgSocket = new DatagramSocket();
			dgSocket.connect(InetAddress.getByName(host), port);
			try {
				dgSocket.setSoTimeout(500);
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			//start running this in a new thread
			Executors.newSingleThreadExecutor().execute(this);
			return true;
			
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	@Override
	public void run(){
		if(dgSocket == null) throw new IllegalStateException("dgSocket was not properly initialized");
		getAndSendInitialInfo();
		int failures = 0;
		byte[] buffer = new byte[GameState.getMaxSize()];
		while(!dgSocket.isClosed()){
			DatagramPacket dp = new DatagramPacket(buffer,GameState.getMaxSize());
			try {
				dgSocket.receive(dp);
				GameState s = GameState.fromBytes(buffer);
				model.setState(s);
				if(DEBUG_ENABLED) System.out.println("Read state: "+s);
				failures = 0;
			} catch (IOException e) {
				failures++;
				if(failures < MAX_FAILURES) continue;
				// TODO Auto-generated catch block
				e.printStackTrace();
				System.err.println("Too many errors. Game exited.");
				break;
			}
			//try to write coordinates
			ClientState cls = new ClientState(userId);
			cls.yVote = mouseSource.getMouseY();
			byte[] clientBuffer = cls.toBytes();
			DatagramPacket dpSend = new DatagramPacket(clientBuffer, clientBuffer.length);
			try {
				dgSocket.send(dpSend);
			} catch (IOException e) {
				/* ignore write failures */
			}
			
			if (model.isGameOver())
				dgSocket.close();
		}
	}
	
	/**
	 * Sends initial 'I'm here' to server and reads which side of the game we're on
	 * The protocol here is as follows:
	 * We send a single integer (4 bytes) to the server containing our UserID
	 * The server replies with a single 1 byte ack with the following meaning
	 * 0 : Game started, we're on left side
	 * 1 : Game started, we're on right side
	 * 2 : Server error, game will not start
	 */
	private void getAndSendInitialInfo(){
		byte[] idBuf = new byte[4];
		ByteBuffer buf = ByteBuffer.wrap(idBuf);
		buf.putInt(userId);
		DatagramPacket idPacket = new DatagramPacket(idBuf,idBuf.length);
		byte[] serverAckBuf = new byte[1];
		DatagramPacket serverAckPacket = new DatagramPacket(serverAckBuf, serverAckBuf.length);
		boolean receivedAck = false;
		while(!receivedAck){
			try {
				dgSocket.send(idPacket);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				dgSocket.receive(serverAckPacket);
				receivedAck = true;
				byte indicator = serverAckBuf[0];
				if(indicator == 0 || indicator == 1){
					model.setOnLeftSide(indicator == 0);
				}else{
					//server error, must exit
					throw new RuntimeException("Server responded with code: "+indicator+", will not start.");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
