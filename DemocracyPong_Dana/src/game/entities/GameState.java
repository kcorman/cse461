package game.entities;

import java.nio.ByteBuffer;

/**
 * @author Kenny Corman
 * A GameState which can be converted to and from a byte array
 * This is the ideal way to transfer state from the server to the client
 * This can be modified as long as toBytes and fromBytes are properly updated as well (if necessary)
 * Also the getMaxSize must always reflect the upper bound of bytes required by a game state object
 * 
 * @Jon you can modify this class any way you see fit to implement game logic, just follow the
 * reccomendations above and it shouldn't break any code
 * You can decide if you want to include the ball logic in this class or if you want
 * to take a functional approach and use another class that operates on a GameState
 * 
 */
public class GameState {
	private static final int NUM_INTS = 10;		//should store the number of ints in an instance,
	// Ball related fields
	int ballX, ballY, ballDx, ballDy;
	//Paddle related fields
	int leftPaddleY, rightPaddleY;
	int paddleHeight, paddleWidth;
	//Score related fields
	int leftScore, rightScore;
	
	
	/**
	 * Initializes a new GameState object
	 * this should only be called to create a state at the start of a game
	 */
	public GameState(){
		/* all values initialized to zero, this should probably be changed */
		
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
		s.rightPaddleY = buf.getInt();
		s.paddleHeight = buf.getInt();
		s.paddleWidth = buf.getInt();
		s.leftScore = buf.getInt();
		s.rightScore = buf.getInt();
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
		buf.putInt(rightPaddleY);
		buf.putInt(paddleHeight);
		buf.putInt(paddleWidth);
		buf.putInt(leftScore);
		buf.putInt(rightScore);
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
	
}
