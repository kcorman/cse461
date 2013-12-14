package lobby;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListDataListener;

import lobby.LobbyState.Room;


/**
 * 
 * @author kcorman
 * This is the actual panel that contains information about the rooms in a lobby
 * The implementation of the lobby window is under LobbyWindow
 */
public class LobbyPanel extends JPanel implements LobbyConnectionListener {
	private static final int VISIBLE_ROWS = 10;
	private static final String ROOMS_AVAILABLE = "Available Rooms";
	private static final String HOST_ROOM = "Host Room";
	private static final String JOIN_GAME = "Join Room";
	private static final String ROOM_NAME = "%d: %s's Room (%d)";
	private JLabel lobbyTitle;
	private JList<String> roomList;
	private final JLabel notConnectedLabel = new JLabel("Not connected");
	private JButton joinButton;
	private JButton hostButton;
	private ConnectionBean connectionBean;
	private boolean connected = false;
	
	public LobbyPanel(ConnectionBean connectionBean) {
		this.setLayout(new BorderLayout());
		// room title
		lobbyTitle = new JLabel();
		lobbyTitle.setText(ROOMS_AVAILABLE); 	// add action listener
		lobbyTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lobbyTitle.setVerticalAlignment(SwingConstants.CENTER);
		this.add(lobbyTitle, BorderLayout.NORTH);
		this.add(notConnectedLabel, BorderLayout.CENTER);
		this.connectionBean = connectionBean;
		
	}
	
	/**
	 * Updates this panel to sync it with its remote lobby
	 */
	public void syncWithRemote(){
		if(!connected){
			throw new IllegalStateException("Panel must be connected to remote lobby before "
					+ "syncing");
		}
		/*
		 * We just need to update the roomList
		 */
		int idx = roomList.getSelectedIndex();	//store this so we can update it
		
		LobbyState state = connectionBean.getLobbyState();
		String[] roomArr = new String[state.getRooms().size()];
		for(int i = 0;i<state.getRooms().size();i++){
			Integer id = state.getRooms().get(i).roomID;
			roomArr[i] = String.format(ROOM_NAME, 
					id, state.getUserNames().get(id), 
					state.getRooms().get(i).getPlayers().size());
		}
		roomList.setListData(roomArr);
		roomList.setSelectedIndex(idx);
		
		
	}

	@Override
	public void onSuccessfulConnect() {
		this.remove(notConnectedLabel);
		// list of players
		roomList = new JList<String>();		// add action listener
		roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		roomList.setLayoutOrientation(JList.VERTICAL);
		roomList.setVisibleRowCount(VISIBLE_ROWS);
		JScrollPane listScroller = new JScrollPane(roomList);
		listScroller.setPreferredSize(new Dimension(300, 350));
		this.add(listScroller, BorderLayout.CENTER);
		
		// add exit room/ start game buttons
		JPanel buttonPanel = new JPanel();
		//set up host button
		hostButton = new JButton(HOST_ROOM);
		hostButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				onHostButtonClick();
			}
		});
		//set up join button
		joinButton = new JButton(JOIN_GAME);
		joinButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				onJoinButtonClick();
			}
		});
		buttonPanel.setLayout(new GridLayout(2, 0));
		buttonPanel.add(hostButton);
		buttonPanel.add(joinButton);
		this.add(buttonPanel, BorderLayout.SOUTH);
		connected = true;
		
	}
	
	public void onHostButtonClick(){
		//check to see if we're in a room already
		LobbyState state = connectionBean.getLobbyState();
		if(state.getRoomContainingUser(connectionBean.getUserID()) == null){
			connectionBean.hostRoom();
		}else{
			JOptionPane.showMessageDialog(this,
					"You are already in a room. You must leave before hosting a new one.");
		}
	}
	
	public void onJoinButtonClick(){
		LobbyState state = connectionBean.getLobbyState();
		//get room to join
		Integer val = Integer.parseInt(roomList.getSelectedValue().split(":")[0]);
		if(val == null){
			JOptionPane.showMessageDialog(this,
					"You have not selected a room to join.");
		}else{
			//If user is already in this room, don't let them join again
			int uid = connectionBean.getUserID();
			if(state.getRoomContainingUser(uid) != null && 
					state.getRoomContainingUser(uid).roomID == val){
				JOptionPane.showMessageDialog(this,"You are already in that room.");
				return;
			}
			//check to make sure room exists
			boolean found = false;
			for(Room r : state.getRooms()){
				found |= (r.roomID == val);
			}
			if(found){
				connectionBean.leaveRoom();
				connectionBean.joinRoom(val);
			}else{
				JOptionPane.showMessageDialog(this,
						"You have not selected a room to join.");
			}
		}
		
	}
	
}
