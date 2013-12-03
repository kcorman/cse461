import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;


public class RoomPanel extends JPanel {
	private List<String> playerList;
	private String uid;
	
	public RoomPanel() {
	}
	
	public void notifyViewer(List<String> playerList, String uid) {
		this.playerList = playerList;
		this.uid = uid;
		repaint();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Clear panel
		Rectangle bounds = getBounds();
	    g.clearRect(0, 0, bounds.width, bounds.height);
	    this.setLayout(new GridLayout(10, 1));
	    this.add(new JLabel("Welcome to Democracy Pong!"));
	    if (playerList != null) {
	    	// First player on list is always host
	    	g.setColor(Color.BLUE);
	    	for (int i = 0; i < playerList.size(); ++i) {
	    		String player = playerList.get(i);
	    		if (uid.equals(player)) {
	    			g.setColor(Color.GREEN);
	    			this.add(new JLabel(i + ". Player " + player));
	    			g.setColor(Color.BLUE);
	    		} else {
	    			this.add(new JLabel(i + ". Player " + player));
	    		}
	    	}
	    }
	}
}
