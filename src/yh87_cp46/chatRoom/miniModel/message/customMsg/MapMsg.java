package yh87_cp46.chatRoom.miniModel.message.customMsg;

import common.receiver.messages.IReceiverMsg;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * Sending a map message.
 * @author Cheng Peng
 *
 */
public class MapMsg implements IReceiverMsg {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -5251877576414029352L;

	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(MapMsg.class);
	}

	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public IDataPacketID getID() {
		return MapMsg.GetID();
	}
}
