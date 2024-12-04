package yh87_cp46.chatRoom.miniModel.message.status;

import common.receiver.ReceiverDataPacket;
import common.receiver.messages.IReceiverRejectMsg;

/**
 * @author jimmy
 * Rejection message.
 */
public class ReceiverRejectMsg extends ReceiverStatusMsg implements IReceiverRejectMsg {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -1953833386283738808L;

	/**
	 * Constructor for the rejection message.
	 * @param data the data packet.
	 */
	public ReceiverRejectMsg(ReceiverDataPacket<?> data) {
		super(data);
	}

	@Override
	public String getStatus() {
		return "Reject status";
	}
}
