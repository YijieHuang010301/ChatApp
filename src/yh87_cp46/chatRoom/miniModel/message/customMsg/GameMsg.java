package yh87_cp46.chatRoom.miniModel.message.customMsg;

import java.util.HashSet;
import java.util.UUID;

import common.receiver.INamedReceiver;
import common.receiver.messages.IReceiverMsg;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * The message for sending a game UI.
 * @author Cheng Peng
 * @author Yijie Huang
 */
public class GameMsg implements IReceiverMsg {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -3848850767810182039L;

	/**
	 * Assigned team number.
	 */
	private boolean team;

	/**
	 * The INamedReceiver of the player.
	 */
	private INamedReceiver player;

	/**
	 * The ID of the game.
	 */
	private UUID gameID;

	/**
	 * The team member list.
	 */
	private HashSet<INamedReceiver> memberList;

	/**
	 * Constructor for the GameMsg.
	 * 
	 * @param team Assigned team number.
	 * @param player The INamedReceiver of the player.
	 * @param memberList  The team member list
	 * @param gameID The game ID.
	 */
	public GameMsg(boolean team, INamedReceiver player, UUID gameID, HashSet<INamedReceiver> memberList) {
		this.team = team;
		this.player = player;
		this.gameID = gameID;
		this.memberList = memberList;
	}

	/**
	 * @return Getter for the assigned team.
	 */
	public boolean getTeam() {
		return this.team;
	}

	/**
	 * @return Getter for the assigned team.
	 */
	public INamedReceiver getPlayer() {
		return this.player;
	}

	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(GameMsg.class);
	}

	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public IDataPacketID getID() {
		return GameMsg.GetID();
	}

	/**
	 * Return the game ID.
	 * @return the game ID.
	 */
	public UUID getGameID() {
		return gameID;
	}

	/**
	 * Return the team member list.
	 * @return the team member list.
	 */
	public HashSet<INamedReceiver> getMemberList() {
		return memberList;
	}

}
