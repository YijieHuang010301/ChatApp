package yh87_cp46.game.message;

import java.util.UUID;

import common.receiver.messages.IReceiverMsg;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * The update score message.
 * 
 * @author jimmy
 *
 */
public class UpdateScoreMsg implements IReceiverMsg {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -3848850767810182039L;
	/**
	 * The score.
	 */
	private int score;

	/**
	 * The team.
	 */
	private boolean team;

	/**
	 * The game ID.
	 */
	private UUID gameID;

	/**
	 * The number of people in your team.
	 */
	private int teamSize = 0;

	/**
	 * constructor of the class
	 * @param score the score
	 * @param team the team.
	 * @param gameID The game ID.
	 * @param teamSize The number of people in your team.
	 */
	public UpdateScoreMsg(int score, boolean team, UUID gameID, int teamSize) {
		this.score = score;
		this.team = team;
		this.gameID = gameID;
		this.teamSize = teamSize;
	}

	/**
	 * get the score.
	 * @return the score.
	 */
	public int getScore() {
		return this.score;
	}

	/**
	 * get the team.
	 * @return the team.
	 */
	public boolean getTeam() {
		return this.team;
	}

	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(UpdateScoreMsg.class);
	}

	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public IDataPacketID getID() {
		return UpdateScoreMsg.GetID();
	}

	/**
	 * @return the game id.
	 */
	public UUID getGameID() {
		return gameID;
	}

	/**
	 * @return The number of people in your team.
	 */
	public int getTeamSize() {
		return teamSize;
	}
}
