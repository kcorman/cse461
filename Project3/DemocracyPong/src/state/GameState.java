package state;

import java.io.Serializable;

/**
 * 
 * @author kenny
 * A serializable game state object that represents the state of the actual game
 * This object is sent to clients to keep them in sync with the game
 */
public class GameState implements Serializable{

	private static final long serialVersionUID = -4907974329167344219L;
	private int leftSideScore, rightSideScore;
	private Ball ball;
	private Paddle left, right;
	
	public GameState(int stageWidth, int stageHeight){
		ball = new Ball();
		left = new Paddle();
		left.setX(stageWidth - left.getWidth());
		right = new Paddle();
		leftSideScore = 0;
		rightSideScore = 0;
	}
	
	class Ball implements Serializable{
		private static final long serialVersionUID = 8505641802314884600L;
		private int x,y,dx,dy;

		public int getX() {
			return x;
		}

		public void setX(int x) {
			this.x = x;
		}

		public int getY() {
			return y;
		}

		public void setY(int y) {
			this.y = y;
		}

		public int getDx() {
			return dx;
		}

		public void setDx(int dx) {
			this.dx = dx;
		}

		public int getDy() {
			return dy;
		}

		public void setDy(int dy) {
			this.dy = dy;
		}

		@Override
		public String toString() {
			return "Ball [x=" + x + ", y=" + y + ", dx=" + dx + ", dy=" + dy
					+ "]";
		}
	}
	
	class Paddle implements Serializable{
		private static final long serialVersionUID = 6222520323198455934L;
		int x,y;
		int width = 12;
		int height = 32;
		public int getX() {
			return x;
		}
		public void setX(int x) {
			this.x = x;
		}
		public int getY() {
			return y;
		}
		public void setY(int y) {
			this.y = y;
		}
		
		public int getWidth(){
			return width;
		}
		
		public int getHeight(){
			return height;
		}
		@Override
		public String toString() {
			return "Paddle [x=" + x + ", y=" + y + ", width=" + width
					+ ", height=" + height + "]";
		}
		
	}

	@Override
	public String toString() {
		return "GameState [leftSideScore=" + leftSideScore
				+ ", rightSideScore=" + rightSideScore + ", ball=" + ball
				+ ", left=" + left + ", right=" + right + "]";
	}
}
