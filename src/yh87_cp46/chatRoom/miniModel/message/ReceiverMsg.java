package yh87_cp46.chatRoom.miniModel.message;

import common.receiver.AReceiverDataPacketAlgoCmd;
import common.receiver.messages.IReceiverMsg;
import provided.datapacket.IDataPacketID;

/**
 * @author jimmy
 * Class for received message.
 */
public class ReceiverMsg implements IReceiverMsg {

	/**
	 * Serial id
	 */
	private static final long serialVersionUID = -3625430366276450770L;

	/**
	 * data packet
	 */
	protected AReceiverDataPacketAlgoCmd<?> data;

	/**
	 * data id.
	 */
	protected IDataPacketID ID;

	/**
	 * Constructor for the message.
	 * @param data the data packet.
	 * @param ID the data id.
	 */
	public ReceiverMsg(AReceiverDataPacketAlgoCmd<?> data, IDataPacketID ID) {
		this.data = data;
		this.ID = ID;
	}

}
