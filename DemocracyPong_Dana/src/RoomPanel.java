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
	//private String[] players;
	private JList<String> playerList;
	
	public RoomPanel() {
		this.setLayout(new BorderLayout());
		
		// room title
		roomTitle = new JLabel();
		roomTitle.setText(PLAYERS_IN_ROOM); 	// add action listener
		roomTitle.setHorizontalAlignment(SwingConstants.CENTER);
		roomTitle.setVerticalAlignment(SwingConstants.CENTER);
		this.add(roomTitle, BorderLayout.NORTH);
		
		// list of players
		playerList = new JList<String>();		// add action listener
		playerList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		playerList.setLayoutOrientation(JList.VERTICAL);
		playerList.setVisibleRowCount(VISIBLE_ROWS);
		JScrollPane listScroller = new JScrollPane(playerList);
		listScroller.setPreferredSize(new Dimension(300, 350));
		this.add(listScroller, BorderLayout.CENTER);
		
		// add exit room/ start game buttons
		JButton leaveButton = new JButton(LEAVE_ROOM);
		JButton startButton = new JButton(START_GAME);
		GridLayout grid = new GridLayout(0, 2);
		grid.add();
	}
	
	public void notifyViewer(List<String> playerList, String uid) {
		//this.playerList = playerList;
		//this.uid = uid;
		//repaint();
	}
	
//	@Override
//	public void paintComponent(Graphics g) {
//		super.paintComponent(g);
//		
//		// Clear panel
//		Rectangle bounds = getBounds();
//	    g.clearRect(0, 0, bounds.width, bounds.height);
//	    this.setLayout(new GridLayout(10, 1));
//	    this.add(new JLabel("Welcome to Democracy Pong!"));
//	    if (playerList != null) {
//	    	// First player on list is always host
//	    	g.setColor(Color.BLUE);
//	    	for (int i = 0; i < playerList.size(); ++i) {
//	    		String player = playerList.get(i);
//	    		if (uid.equals(player)) {
//	    			g.setColor(Color.GREEN);
//	    			this.add(new JLabel(i + ". Player " + player));
//	    			g.setColor(Color.BLUE);
//	    		} else {
//	    			this.add(new JLabel(i + ". Player " + player));
//	    		}
//	    	}
//	    }
//	}
}
