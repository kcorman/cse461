package client;
import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.util.Arrays;

import state.GameState;


public class ClientMain {
	public static final String HOST = "localhost";
	private static final int PORT = 14001;
	private static final int CONNECTION_PACKET_LEN = 8;
	
	public static void main(String[] args){
		try {
			// Create socket and connect to port
			int user_id = 0;
			DatagramSocket ds = new DatagramSocket();
			ds.setSoTimeout(100000);
			ds.connect(InetAddress.getByName(HOST), PORT);
			
			byte[] helloPacket = "hello\0\0\0".getBytes();
			ds.send(new DatagramPacket(helloPacket, helloPacket.length));
			byte[] rec = new byte[8];
			DatagramPacket server_resp = new DatagramPacket(rec,rec.length);
			ds.receive(server_resp);
			System.out.println("Received from server: "+Arrays.toString(rec));
			ByteBuffer buf = ByteBuffer.wrap(rec);
			user_id = buf.getInt();
			
			byte[] to_send = new byte[8];
			ByteBuffer sendBuffer = ByteBuffer.wrap(to_send);
			sendBuffer.putInt(user_id);
			sendBuffer.put((byte)0);
			ds.send(new DatagramPacket(to_send, to_send.length));
			
			//reuse server_resp
			ds.receive(server_resp);
			System.out.println("Received from server: "+Arrays.toString(rec));
			buf.rewind();
			byte[] possible_start_str = new byte[5];
			buf.get(possible_start_str, 0, 5);
			if(!new String(possible_start_str).equals("start")){
				ds.close();
				throw new Exception("Did not receive start string");
			}
			System.out.println("Received start");
			
			sendBuffer.rewind();
			sendBuffer.putInt(user_id);
			sendBuffer.put((byte)1);
			//Send start ack
			ds.send(new DatagramPacket(to_send, to_send.length));
			
			//Next packet should be a new port number
			ds.receive(server_resp);
			buf.rewind();
			int game_port = buf.getInt();
			int max_receive_buffer_size = buf.getInt();
			System.out.println("Received port: "+game_port+", max buf size= "+max_receive_buffer_size);
			ds.close();
			ds = new DatagramSocket();
			ds.connect(InetAddress.getByName(HOST), game_port);
			ds.setSoTimeout(10000);
			
			sendBuffer.rewind();
			sendBuffer.putInt(user_id);
			sendBuffer.putInt(0);
			//Send start ack
			ds.send(new DatagramPacket(to_send, to_send.length));
			
			while(true){
				System.out.println("Waiting for state data...");
				byte[] stateData = new byte[max_receive_buffer_size];
				DatagramPacket p = new DatagramPacket(stateData,max_receive_buffer_size);
				try{
					ds.receive(p);
				}catch(SocketTimeoutException e){
					System.out.println("Timed out waitng for state data.");
					continue;
				}
				GameState state = deserializeState(stateData);
				System.out.println("Received state: "+state);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static GameState deserializeState(byte[] stateData){
		ByteArrayInputStream bis = new ByteArrayInputStream(stateData);
		ObjectInput in = null;
		try {
		  in = new ObjectInputStream(bis);
		  Object o = in.readObject(); 
		  return (GameState) o;
		  
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try{
			  bis.close();
			  in.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return null;
		
	}
}
