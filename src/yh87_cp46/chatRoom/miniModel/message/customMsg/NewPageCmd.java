package yh87_cp46.chatRoom.miniModel.message.customMsg;

import common.adapter.ICmd2ModelAdapter;
import common.receiver.AReceiverDataPacketAlgoCmd;
import common.receiver.ReceiverDataPacket;
import provided.datapacket.IDataPacketID;

/**
 * @author jimmy
 * The cmd class for the new page message.
 */
public class NewPageCmd extends AReceiverDataPacketAlgoCmd<NewPageMsg> {

	/**
	 * The cmd to model adapter.
	 */
	private transient ICmd2ModelAdapter adapter;

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -6464668870275315035L;

	@Override
	public Void apply(IDataPacketID index, ReceiverDataPacket<NewPageMsg> host, Void... params) {
		host.getData().start(adapter);
		return null;
	}

	@Override
	public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
		this.adapter = cmd2ModelAdpt;

	}

}
