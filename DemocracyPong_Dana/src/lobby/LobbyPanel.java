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


public class LobbyPanel extends JPanel {
	private static final int VISIBLE_ROWS = 10;
	private static final String ROOMS_AVAILABLE = "Available Rooms";
	private static final String HOST_ROOM = "Host Room";
	private static final String JOIN_GAME = "Join Room";
	
	private JLabel lobbyTitle;
	private JList<String> playerList;
	
	public LobbyPanel(String[] test) {
		this.setLayout(new BorderLayout());
		
		// room title
		lobbyTitle = new JLabel();
		lobbyTitle.setText(ROOMS_AVAILABLE); 	// add action listener
		lobbyTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lobbyTitle.setVerticalAlignment(SwingConstants.CENTER);
		this.add(lobbyTitle, BorderLayout.NORTH);
		
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
		JButton hostButton = new JButton(HOST_ROOM);
		JButton joinButton = new JButton(JOIN_GAME);
		buttonPanel.setLayout(new GridLayout(2, 0));
		buttonPanel.add(hostButton);
		buttonPanel.add(joinButton);
		this.add(buttonPanel, BorderLayout.SOUTH);
		
	}
	
	public void notifyViewer(List<String> playerList, String uid) {

	}
}
