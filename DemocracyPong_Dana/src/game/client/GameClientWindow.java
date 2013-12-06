package game.client;

import game.entities.GameState;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

public class GameClientWindow extends JFrame implements MouseMotionListener, GameMousePositionSource{
	private static final Dimension DEFAULT_SIZE = new Dimension(800,600);
	private GameClientModel model;
	int mouseX, mouseY;
	
	public GameClientWindow(GameClientModel m){
		this.setPreferredSize(DEFAULT_SIZE);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		this.pack();
		this.addMouseMotionListener(this);
		model = m;
	}
	
	@Override
	public void paint(Graphics g){
		super.paint(g);
		//if(model == null) return;
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.white);
		GameState s = model.getState();
		g.drawString("Score: "+s.getLeftScore(), 25, 45);
		g.drawString("Score: "+s.getRightScore(), getWidth()-75, 45);
		g.fillRect(0, s.getLeftPaddleY(), s.getPaddleWidth(), s.getPaddleHeight());
		g.fillRect(getWidth()-s.getPaddleWidth(), s.getRightPaddleY(), 
				s.getPaddleWidth(), s.getPaddleHeight());
		g.setColor(Color.yellow);
		g.drawRect(0, mouseY, s.getPaddleWidth(), s.getPaddleHeight());
		//g.fillRect((int)(Math.random()*1000), 200, 64, 64);
	}
	@Override
	public void mouseDragged(MouseEvent arg0) {
		mouseX = arg0.getX();
		mouseY = arg0.getY();
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		mouseX = arg0.getX();
		mouseY = arg0.getY();
	}
	
	public void run(){
		while(true){
			repaint();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getMouseY() {
		return mouseY;
	}

	@Override
	public int getMouseX() {
		return mouseX;
	}

}
