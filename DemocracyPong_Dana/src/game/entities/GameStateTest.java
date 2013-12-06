package game.entities;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * 
 * @author kenny
 * Simple test for game state serialization and deserialization
 */
public class GameStateTest {
	@Test
	public void testGameState(){
		GameState a = new GameState();
		a.ballDx = 34;
		a.paddleHeight = -34;
		a.rightScore = 39994;
		byte[] srs = a.toBytes();
		GameState b = GameState.fromBytes(srs);
		assertEquals(a,b);
	}
}
