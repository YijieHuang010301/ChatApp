package yh87_cp46.game.message;

import common.receiver.messages.IReceiverMsg;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * 
 * @author Cheng Peng
 *
 */
public class TeamTextMsg implements IReceiverMsg {

	/**
	 * Unique UID.
	 */
	private static final long serialVersionUID = 3429226095562944500L;

	/**
	 * The message sent to team members.
	 */
	private String msg;

	/**
	 * The team.
	 */
	private boolean team;

	/**
	 * constructor of the class
	 * @param msg The msg sent to team members.
	 * @param team The team.
	 */
	public TeamTextMsg(String msg, boolean team) {
		this.msg = msg;
		this.team = team;
	}

	/**
	 * @return The message sent to team members.
	 */
	public String getMsg() {
		return msg;
	}

	/**
	 * @return The team.
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
		return DataPacketIDFactory.Singleton.makeID(TeamTextMsg.class);
	}

	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public IDataPacketID getID() {
		return TeamTextMsg.GetID();
	}

}
