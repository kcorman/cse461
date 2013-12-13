package lobby;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.concurrent.Executors;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

/**
 * 
 * @author kcorman
 * The main client side lobby window
 * The lobby window will not do much until the user clicks the connect button
 * on the connect panel
 * 
 * Each panel that the lobby window consists of is capable of interacting directly with the
 * connectionBean, 
 */
public class LobbyWindow extends JFrame implements LobbyConnectionListener, Runnable{
	private ConnectionBean connectionBean;
	static final long THREAD_SLEEP_TIME = 50;
	RoomPanel roomPanel;
	LobbyPanel lobbyPanel;
	boolean connected = false;
	
	public LobbyWindow(ConnectionBean connectionBean){
		this.setName("Democracy Pong Lobby");
		//String[] test = {"9", "12", "3", "6"};
	    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	    setPreferredSize(new Dimension(800, 600));
	    //Set up main child components
	    ConnectPanel connectPanel = new ConnectPanel(connectionBean);
	    roomPanel = new RoomPanel(connectionBean);
	    lobbyPanel = new LobbyPanel(connectionBean);
	    //Add connection listeners
	    //This will cause the roomPanel and lobbyPanel to start updating themselves
	    //once a connection is made
	    connectPanel.addLobbyConnectionListener(this);
	    connectPanel.addLobbyConnectionListener(roomPanel);
	    connectPanel.addLobbyConnectionListener(lobbyPanel);
	    
	    setLayout(new GridLayout(0, 2));
	    JPanel leftPanel = new JPanel();
	    leftPanel.setLayout(new BorderLayout());
	    leftPanel.add(connectPanel, BorderLayout.NORTH);
	    leftPanel.add(lobbyPanel, BorderLayout.CENTER);
	    leftPanel.setBorder(BorderFactory.createBevelBorder(0));
	    add(leftPanel);
	    add(roomPanel);
	    pack();
	    setVisible(true);
	    repaint();
	    Executors.newSingleThreadExecutor().execute(this);
	}
	

	@Override
	public void onSuccessfulConnect() {
		pack();
		repaint();
		connected = true;
		
	}

	/**
	 * In addition to the UI thread, this object is intended to be updated
	 * by a dedicated thread calling run
	 * This keeps it in sync with the remote lobby
	 */
	@Override
	public void run() {
		while(true){
			if(connected){
				lobbyPanel.syncWithRemote();
				roomPanel.syncWithRemote();
			}
			try {
				Thread.sleep(THREAD_SLEEP_TIME);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
	}
}
