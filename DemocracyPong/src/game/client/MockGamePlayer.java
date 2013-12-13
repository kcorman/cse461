package game.client;

/**
 * 
 * @author kcorman
 * A simple implementation of a GamePlayer that is intended to be controlled from the outside
 * via calls to setVote and setTeam
 */
public class MockGamePlayer implements game.server.GamePlayer{
	private int vote, team;
	@Override
	public int getVote() {
		return vote;
	}

	@Override
	public void setVote(int yVote) {
		vote = yVote;
		
	}

	@Override
	public int getTeam() {
		return team;
	}

	@Override
	public void setTeam(int team) {
		this.team = team;
	}

}
