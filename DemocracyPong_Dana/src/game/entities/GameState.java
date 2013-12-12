package game.entities;

import java.nio.ByteBuffer;

/**
 * @author Kenny Corman
 * A GameState which can be converted to and from a byte array
 * This is the ideal way to transfer state from the server to the client
 * This can be modified as long as toBytes and fromBytes are properly updated as well (if necessary)
 * Also the getMaxSize must always reflect the upper bound of bytes required by a game state object
 * 
 * The max number of points to play to has a default value, but otherwise is left up to the server to set
 * A max_points of 0 or less indicates an endless game that only ends when all players have left
 * 
 * Displaying GameOver/Win messages and exiting is the responsibility of the client (and thus it 
 * should check every state to see if either side has won)
 */
public class GameState {
	private static final int NUM_INTS = 17;		//should store the number of ints in an instance,
	public static final int BALL_SIZE = 24;		//ball width and height
	//This is ideally how often the state should be updated
	//Storing this in a central location allows clients to implement graphics smoothing
	//using dy/dx values
	public static final int TIME_BETWEEN_UPDATES_MS = 50;
	// Ball related fields
	public int ballX, ballY, ballDx, ballDy;
	//Paddle related fields
	public int leftPaddleY, leftPaddleX, rightPaddleY, rightPaddleX;
	public int paddleHeight, paddleWidth;
	//Score related fields
	public int leftScore, rightScore;
	public int maxPoints;
	//Boundary Y coordinates (X boundaries are behind respective paddles)
	public int upperBoundsY, lowerBoundsY;
	//other
	//This should be updated every time the state is updated
	public long timeUpdated = System.currentTimeMillis();
	
	
	/**
	 * Initializes a new GameState object
	 * this should only be called to create a state at the start of a game
	 */
	public GameState(){
		/* all values initialized to zero, this should probably be changed */
		paddleWidth = 20;
		paddleHeight = 128;
		leftPaddleX = 10;	//arbitrary
		rightPaddleX = 750;	//arbitrary, but based on the size of the game board
		lowerBoundsY = 50;
		upperBoundsY = 550;
		maxPoints = 40;
	}
	
	
	/**
	 * Deserializes a game state object from a byte array
	 * @param state the bytes that contain the serialized gamestate object
	 * @return
	 */
	public static GameState fromBytes(byte[] state){
		GameState s = new GameState();
		ByteBuffer buf = ByteBuffer.wrap(state);
		s.ballX = buf.getInt();
		s.ballY = buf.getInt();
		s.ballDx = buf.getInt();
		s.ballDy = buf.getInt();
		s.leftPaddleY = buf.getInt();
		s.leftPaddleX = buf.getInt();
		s.rightPaddleY = buf.getInt();
		s.rightPaddleX = buf.getInt();
		s.paddleHeight = buf.getInt();
		s.paddleWidth = buf.getInt();
		s.leftScore = buf.getInt();
		s.rightScore = buf.getInt();
		s.maxPoints = buf.getInt();
		s.upperBoundsY = buf.getInt();
		s.lowerBoundsY = buf.getInt();
		s.timeUpdated = buf.getLong();
		return s;
	}
	
	/**
	 * Serializes a game state object into a byte array
	 * @return
	 */
	public byte[] toBytes(){
		byte[] array = new byte[getMaxSize()];
		ByteBuffer buf = ByteBuffer.wrap(array);
		//Order is very important here
		buf.putInt(ballX);
		buf.putInt(ballY);
		buf.putInt(ballDx);
		buf.putInt(ballDy);
		buf.putInt(leftPaddleY);
		buf.putInt(leftPaddleX);
		buf.putInt(rightPaddleY);
		buf.putInt(rightPaddleX);
		buf.putInt(paddleHeight);
		buf.putInt(paddleWidth);
		buf.putInt(leftScore);
		buf.putInt(rightScore);
		buf.putInt(maxPoints);
		buf.putInt(upperBoundsY);
		buf.putInt(lowerBoundsY);
		buf.putLong(timeUpdated);
		return array;
	}
	
	/**
	 * Returns the max game state size in bytes
	 * @return
	 */
	public static int getMaxSize(){
		return NUM_INTS * 4;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ballDx;
		result = prime * result + ballDy;
		result = prime * result + ballX;
		result = prime * result + ballY;
		result = prime * result + leftPaddleY;
		result = prime * result + leftScore;
		result = prime * result + paddleHeight;
		result = prime * result + paddleWidth;
		result = prime * result + rightPaddleY;
		result = prime * result + rightScore;
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GameState other = (GameState) obj;
		if (ballDx != other.ballDx)
			return false;
		if (ballDy != other.ballDy)
			return false;
		if (ballX != other.ballX)
			return false;
		if (ballY != other.ballY)
			return false;
		if (leftPaddleY != other.leftPaddleY)
			return false;
		if (leftScore != other.leftScore)
			return false;
		if (paddleHeight != other.paddleHeight)
			return false;
		if (paddleWidth != other.paddleWidth)
			return false;
		if (rightPaddleY != other.rightPaddleY)
			return false;
		if (rightScore != other.rightScore)
			return false;
		return true;
	}


	@Override
	public String toString() {
		return "GameState [ballX=" + ballX + ", ballY=" + ballY + ", ballDx="
				+ ballDx + ", ballDy=" + ballDy + ", leftPaddleY="
				+ leftPaddleY + ", rightPaddleY=" + rightPaddleY
				+ ", paddleHeight=" + paddleHeight + ", paddleWidth="
				+ paddleWidth + ", leftScore=" + leftScore + ", rightScore="
				+ rightScore + "]";
	}


	public static int getNumInts() {
		return NUM_INTS;
	}


	public int getBallX() {
		return ballX;
	}


	public int getBallY() {
		return ballY;
	}


	public int getBallDx() {
		return ballDx;
	}


	public int getBallDy() {
		return ballDy;
	}


	public int getLeftPaddleY() {
		return leftPaddleY;
	}


	public int getRightPaddleY() {
		return rightPaddleY;
	}
	
	public int getLeftPaddleX() {
		return leftPaddleX;
	}


	public int getRightPaddleX() {
		return rightPaddleX;
	}


	public int getPaddleHeight() {
		return paddleHeight;
	}


	public int getPaddleWidth() {
		return paddleWidth;
	}


	public int getLeftScore() {
		return leftScore;
	}


	public int getRightScore() {
		return rightScore;
	}
	
}
