package yh87_cp46.chatApp.model.message;

import java.util.Map;
import java.util.UUID;

import common.connector.messages.IInviteMsg;

/**
 * @author jimmy
 * The invite message class.
 */
public class InviteMsg implements IInviteMsg {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 3113111964908485469L;

	/**
	 * The rooms map
	 */
	private Map<UUID, String> rooms;

	/**
	 * The constructor for the invite message.
	 * @param rooms The set of current rooms.
	 */
	public InviteMsg(Map<UUID, String> rooms) {
		this.rooms = rooms;
	}

	@Override
	public Map<UUID, String> getRooms() {
		return rooms;
	}

}
