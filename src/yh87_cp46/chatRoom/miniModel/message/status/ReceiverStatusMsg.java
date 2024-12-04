package yh87_cp46.chatRoom.miniModel.message.status;

import common.receiver.ReceiverDataPacket;
import common.receiver.messages.IReceiverStatusMsg;

/**
 * @author jimmy
 * The status message.
 */
public class ReceiverStatusMsg implements IReceiverStatusMsg {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 4899353642964665975L;

	/**
	 * Data packet.
	 */
	protected ReceiverDataPacket<?> data;

	/**
	 * Constructor for the status message.
	 * @param data the data packet.
	 */
	public ReceiverStatusMsg(ReceiverDataPacket<?> data) {
		this.data = data;
	}

	@Override
	public ReceiverDataPacket<?> getPacket() {
		return this.data;
	}

	@Override
	public String getStatus() {
		return "Unknown status";
	}

}
