package yh87_cp46.chatRoom.miniModel.message.status;

import common.receiver.ReceiverDataPacket;
import common.receiver.messages.IReceiverFailMsg;

/**
 * Fail message class.
 * @author jimmy
 *
 */
public class ReceiverFailMsg implements IReceiverFailMsg {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 3467674534172772171L;

	/**
	 * data packet.
	 */
	private ReceiverDataPacket<?> data;

	/**
	 * Constructor for fail message.
	 * @param data the data packet.
	 */
	public ReceiverFailMsg(ReceiverDataPacket<?> data) {
		this.data = data;
	}

	@Override
	public String getStatus() {
		return "Fail status";
	}

	@Override
	public ReceiverDataPacket<?> getPacket() {
		return this.data;
	}

}
