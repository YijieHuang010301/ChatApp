package yh87_cp46.game.message;

import common.receiver.messages.IReceiverMsg;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * message for winning the game.
 * @author jimmy
 *
 */
public class GameWinMsg implements IReceiverMsg {

	/**
	 * Unique ID
	 */
	private static final long serialVersionUID = 7400021657719513474L;

	/**
	 * The team that win the game.
	 */
	private boolean team;

	/**
	 * Constructor for the win game message.
	 * @param team The team that win the game.
	 */
	public GameWinMsg(boolean team) {
		this.team = team;
	}

	/**
	 * Get the team.
	 * @return get the win game.
	 */
	public boolean getTeam() {
		return team;
	}

	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(GameWinMsg.class);
	}

	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public IDataPacketID getID() {
		return GameWinMsg.GetID();
	}

}
