package game.client;

import game.client.SoundPlayer.Sound;
import game.entities.GameState;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

public class GameClientWindow extends JFrame implements MouseMotionListener, GameMousePositionSource{
	private static final Dimension DEFAULT_SIZE = new Dimension(800,600);
	private static final Font WIN_FONT = new Font("Ariel", Font.BOLD, 64);
	private static final Font LOSE_FONT = new Font("Ariel", Font.BOLD, 64);
	private GameClientModel model;
	//booleans used to ensure that the ball bounces off of the opposite
	//side before playing a specified sound again
	//Ideally sound handling would not be done in the window, but because of the way
	//we abstracted the game info to the server, the window is the most convenient place
	private enum BounceDirection {LEFT, TOP, RIGHT, BOTTOM};
	private BounceDirection lastBounce = null;
	int mouseX, mouseY;
	
	Image dbImage;
	
	public GameClientWindow(GameClientModel m){
		this.setPreferredSize(DEFAULT_SIZE);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setVisible(true);
		this.pack();
		this.addMouseMotionListener(this);
		dbImage = this.createImage(getWidth(), getHeight());
		model = m;
	}
	
	@Override
	public void paint(Graphics g){
		if(dbImage != null){
			dbPaint(dbImage.getGraphics());
			g.drawImage(dbImage,0,0,null);
		}
	}
	
	public void dbPaint(Graphics g){
		super.paint(g);
		//if(model == null) return;
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.white);
		GameState s = model.getState();
		if(model.isGameOver()){
			if(model.hasWon()){
				g.setColor(Color.GREEN);
				g.setFont(WIN_FONT);
				g.drawString("You win!", getWidth()/2 - 32*7, getHeight()/2);
			}else{
				g.setColor(Color.RED);
				g.setFont(LOSE_FONT);
				g.drawString("You lose!", getWidth()/2 - 32*7, getHeight()/2);
			}
			return;
		}
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
		//plays sounds if appropriate
		playSounds(s);
	}
	
	/*
	 * Checks the state to see if sounds should be played
	 */
	private void playSounds(GameState state){
		if(state.ballX-4 <= state.leftPaddleX+state.paddleWidth){
			if(lastBounce != BounceDirection.LEFT){
				SoundPlayer.getInstance().playSound(Sound.LEFT_PADDLE);
				lastBounce = BounceDirection.LEFT;
			}
		}else if(state.ballX+GameState.BALL_SIZE+4 >= state.rightPaddleX){
			if(lastBounce != BounceDirection.RIGHT){
				SoundPlayer.getInstance().playSound(Sound.RIGHT_PADDLE);
				lastBounce = BounceDirection.RIGHT;
			}
		}else if(state.ballY+GameState.BALL_SIZE+4 >= state.upperBoundsY){
			if(lastBounce != BounceDirection.TOP){
				SoundPlayer.getInstance().playSound(Sound.HIT_WALL);
				lastBounce = BounceDirection.TOP;
			}
		}else if(state.ballY - 4 <= state.lowerBoundsY){
			if(lastBounce != BounceDirection.BOTTOM){
				SoundPlayer.getInstance().playSound(Sound.HIT_WALL);
				lastBounce = BounceDirection.BOTTOM;
			}
		}
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
			StateSmoother.smoothState(model.getState());
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
