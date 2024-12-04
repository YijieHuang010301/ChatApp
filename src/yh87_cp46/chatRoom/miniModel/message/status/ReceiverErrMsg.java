package yh87_cp46.chatRoom.miniModel.message.status;

import common.receiver.ReceiverDataPacket;
import common.receiver.messages.IReceiverErrMsg;

/**
 * Error message.
 * @author jimmy
 *
 */
public class ReceiverErrMsg extends ReceiverStatusMsg implements IReceiverErrMsg {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = -3814285044577549694L;

	/**
	 * The constructor for error message.
	 * @param data the data packet.
	 */
	public ReceiverErrMsg(ReceiverDataPacket<?> data) {
		super(data);
	}

	@Override
	public String getStatus() {
		return "Error status";
	}

}
