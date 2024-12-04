package yh87_cp46.chatRoom.miniModel.message;

import common.receiver.AReceiverDataPacketAlgoCmd;
import common.receiver.messages.ICommandMsg;
import provided.datapacket.IDataPacketID;

/**
 * @author jimmy
 * The command message.
 */
public class CommandMsg implements ICommandMsg {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 3820088575476032200L;

	/**
	 * data packet.
	 */
	private AReceiverDataPacketAlgoCmd<?> data;

	/**
	 * packet id.
	 */
	private IDataPacketID ID;

	/**
	 * The constructor for the command message.
	 * @param data the data
	 * @param ID the id for the data packet.
	 */
	public CommandMsg(AReceiverDataPacketAlgoCmd<?> data, IDataPacketID ID) {
		this.ID = ID;
		this.data = data;
	}

	@Override
	public AReceiverDataPacketAlgoCmd<?> getCmd() {
		return this.data;
	}

	@Override
	public IDataPacketID getCmdID() {
		return this.ID;
	}

}
