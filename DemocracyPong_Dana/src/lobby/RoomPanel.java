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
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import lobby.LobbyState.Room;


public class RoomPanel extends JPanel implements LobbyConnectionListener{
	private static final int VISIBLE_ROWS = 10;
	private static final String PLAYERS_IN_ROOM = "Players in Room ";
	private static final String LEAVE_ROOM = "Leave Room";
	private static final String START_GAME = "Start Game";
	private boolean connected = false;
	private ConnectionBean connectionBean;
	private boolean isInRoom = false;
	
	private String uid;
	private JLabel roomTitle;
	private String roomNum;
	private JList<Integer> playerList;
	JButton leaveButton = new JButton(LEAVE_ROOM);
	JButton startButton = new JButton(START_GAME);
	//Label used for displaying that we're not connected
	private final JLabel notConnectedLabel = new JLabel("Not connected");
	private final JLabel notInRoom = new JLabel("You are not in a room");
	
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
				this.remove(playerList);
				this.add(notInRoom, BorderLayout.CENTER);
				leaveButton.setEnabled(false);
				startButton.setEnabled(false);
				isInRoom = false;
			}
		}else{
			if(!isInRoom){
				this.remove(notInRoom);
				this.add(playerList, BorderLayout.CENTER);
				leaveButton.setEnabled(true);
				if(currentRoom.roomID == connectionBean.getUserID()){
					startButton.setEnabled(true);
				}
				isInRoom = true;
			}
			//get players
			Integer[] playerArr = new Integer[currentRoom.players.size()];
			int i = 0;
			for(Integer playerId : currentRoom.getPlayers()){
				playerArr[i++] = playerId;
			}
			playerList.setListData(playerArr);
			//Update selected index?
		}
	}

	@Override
	public void onSuccessfulConnect() {
		// list of players
		this.remove(notConnectedLabel);
		playerList = new JList<Integer>();		// add action listener
		playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		playerList.setLayoutOrientation(JList.VERTICAL);
		playerList.setVisibleRowCount(VISIBLE_ROWS);

		JScrollPane listScroller = new JScrollPane(playerList);
		listScroller.setPreferredSize(new Dimension(300, 350));
		this.add(listScroller, BorderLayout.CENTER);
		
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
		this.add(buttonPanel, BorderLayout.SOUTH);
		connected = true;

	}
	
}
