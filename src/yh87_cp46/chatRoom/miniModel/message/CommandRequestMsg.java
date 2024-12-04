package yh87_cp46.chatRoom.miniModel.message;

import common.receiver.messages.ICommandRequestMsg;
import provided.datapacket.IDataPacketID;

/**
 * @author jimmy
 * The command request message.
 */
public class CommandRequestMsg implements ICommandRequestMsg {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 6901379478076613042L;

	/**
	 * data id.
	 */
	public IDataPacketID ID;

	/**
	 * Data id.
	 * @param ID the data id.
	 */
	public CommandRequestMsg(IDataPacketID ID) {
		this.ID = ID;
	}

	@Override
	public IDataPacketID getCmdID() {
		return this.ID;
	}

}
