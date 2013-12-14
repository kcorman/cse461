package lobby;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import lobby.LobbyState.Room;


@SuppressWarnings("serial")
public class RoomPanel extends JPanel implements LobbyConnectionListener{
	private static final int VISIBLE_ROWS = 10;
	private static final String PLAYERS_IN_ROOM = "Players in Room ";
	private static final String LEAVE_ROOM = "Leave Room";
	private static final String START_GAME = "Start Game";
	private boolean connected = false;
	private ConnectionBean connectionBean;
	private boolean isInRoom = false;
	
	private JLabel roomTitle;
	private JList<String> playerList;
	JButton leaveButton = new JButton(LEAVE_ROOM);
	JButton startButton = new JButton(START_GAME);
	//Label used for displaying that we're not connected
	private final JLabel notConnectedLabel = new JLabel("Not connected");
	private final JLabel notInRoom = new JLabel("You are not in a room");
	private JScrollPane listPane;
	
	public RoomPanel(ConnectionBean connectionBean) {
		this.setLayout(new BorderLayout());
		
		// room title
		roomTitle = new JLabel();
		roomTitle.setText(PLAYERS_IN_ROOM); 	// add action listener
		roomTitle.setHorizontalAlignment(SwingConstants.CENTER);
		roomTitle.setVerticalAlignment(SwingConstants.CENTER);
		this.add(roomTitle, BorderLayout.NORTH);
		
		/* Don't add the rest of the components until successful connection */
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
		
		//Just need to update room title and players in room
		LobbyState state = connectionBean.getLobbyState();
		Room currentRoom = state.getRoomContainingUser(connectionBean.getUserID());
		if(currentRoom == null){
			//Only update to the "No room state" if we haven't already
			if(isInRoom){
				this.remove(listPane);
				//this.remove(roomNameLabel);
				this.add(notInRoom, BorderLayout.CENTER);
				leaveButton.setEnabled(false);
				startButton.setEnabled(false);
				isInRoom = false;
			}
		}else{
			if(!isInRoom){
				this.remove(notInRoom);
				//roomNameLabel.setText("Room #: "+currentRoom.roomID);
				//this.add(roomNameLabel, BorderLayout.CENTER);
				this.add(listPane, BorderLayout.CENTER);
				leaveButton.setEnabled(true);
				if(currentRoom.roomID == connectionBean.getUserID()){
					startButton.setEnabled(true);
				}
				isInRoom = true;
			}
			//get players
			int idx = playerList.getSelectedIndex();
			String[] playerArr = new String[currentRoom.getPlayers().size()];
			int i = 0;
			for(Integer playerId : currentRoom.getPlayers()){
				String playerName = "Player: "+playerId;
				String userName = state.getUserNames().get(playerId);
				if(userName != null) playerName = userName;
				playerArr[i++] = playerName;
			}
			playerList.setListData(playerArr);
			playerList.setSelectedIndex(idx);
			//Update selected index?
		}
	}

	@Override
	public void onSuccessfulConnect() {
		// list of players
		this.remove(notConnectedLabel);
		playerList = new JList<String>();		// add action listener
		playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		playerList.setLayoutOrientation(JList.VERTICAL);
		playerList.setVisibleRowCount(VISIBLE_ROWS);

		listPane = new JScrollPane(playerList);
		listPane.setPreferredSize(new Dimension(300, 350));
		this.add(listPane, BorderLayout.CENTER);
		
		// add exit room/ start game buttons
		startButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				connectionBean.startGame();
			}
		});
		leaveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				connectionBean.leaveRoom();
			}
		});
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2, 0));
		buttonPanel.add(leaveButton);
		buttonPanel.add(startButton);
		leaveButton.setEnabled(false);
		startButton.setEnabled(false);
		this.add(buttonPanel, BorderLayout.SOUTH);
		connected = true;

	}
	
}
