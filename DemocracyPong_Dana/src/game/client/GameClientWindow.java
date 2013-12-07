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
		//Draw scores
		g.drawString("Score: "+s.getLeftScore(), 25, 45);
		g.drawString("Score: "+s.getRightScore(), getWidth()-75, 45);
		//Draw real paddles
		g.fillRect(0, s.getLeftPaddleY(), s.getPaddleWidth(), s.getPaddleHeight());
		g.fillRect(s.getRightPaddleX(), s.getRightPaddleY(), 
				s.getPaddleWidth(), s.getPaddleHeight());
		g.setColor(Color.yellow);
		//draw local paddle
		int localPaddleX = model.isOnLeftSide() ? s.leftPaddleX : s.rightPaddleX;
		g.drawRect(localPaddleX, mouseY, s.getPaddleWidth(), s.getPaddleHeight());
		//draw ball
		g.setColor(Color.white);
		g.fillRect(s.ballX, s.ballY, GameState.BALL_SIZE, GameState.BALL_SIZE);
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
