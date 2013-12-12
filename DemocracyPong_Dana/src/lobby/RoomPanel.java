package lobby;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;


public class RoomPanel extends JPanel {
	private static final int VISIBLE_ROWS = 10;
	private static final String PLAYERS_IN_ROOM = "Players in Room ";
	private static final String LEAVE_ROOM = "Leave Room";
	private static final String START_GAME = "Start Game";
	
	private String uid;
	private JLabel roomTitle;
	private String roomNum;
	private JList<String> playerList;
	
	public RoomPanel(String[] test) {
		this.setLayout(new BorderLayout());
		
		// room title
		roomTitle = new JLabel();
		roomTitle.setText(PLAYERS_IN_ROOM); 	// add action listener
		roomTitle.setHorizontalAlignment(SwingConstants.CENTER);
		roomTitle.setVerticalAlignment(SwingConstants.CENTER);
		this.add(roomTitle, BorderLayout.NORTH);
		
		// list of players
		playerList = new JList<String>(test);		// add action listener
		playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		playerList.setLayoutOrientation(JList.VERTICAL);
		playerList.setVisibleRowCount(VISIBLE_ROWS);
		JScrollPane listScroller = new JScrollPane(playerList);
		listScroller.setPreferredSize(new Dimension(300, 350));
		this.add(listScroller, BorderLayout.CENTER);
		
		// add exit room/ start game buttons
		JPanel buttonPanel = new JPanel();
		JButton leaveButton = new JButton(LEAVE_ROOM);
		JButton startButton = new JButton(START_GAME);
		buttonPanel.setLayout(new GridLayout(2, 0));
		buttonPanel.add(leaveButton);
		buttonPanel.add(startButton);
		this.add(buttonPanel, BorderLayout.SOUTH);
		
	}
	
	public void notifyViewer(List<String> playerList, String uid) {

	}
}
