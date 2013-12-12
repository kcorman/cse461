package game.server;
/**
 * 
 * @author kcorman
 * This interface seperates out the game-specific specification of a user from the lobby.User class,
 * which contains a lot of fields that aren't necessary to the function of a game
 * This allows for easier testing and understanding
 */
public interface GamePlayer {
	public int getVote();
	
	public void setVote(int yVote);
	
	public int getTeam();
	
	public void setTeam(int team);
}
