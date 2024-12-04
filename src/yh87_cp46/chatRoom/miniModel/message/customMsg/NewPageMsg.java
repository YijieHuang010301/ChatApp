package yh87_cp46.chatRoom.miniModel.message.customMsg;

import java.awt.Component;

import javax.swing.JTextArea;

import common.adapter.ICmd2ModelAdapter;
import common.adapter.IComponentFactory;
import common.receiver.messages.IReceiverMsg;
import provided.datapacket.DataPacketIDFactory;
import provided.datapacket.IDataPacketID;

/**
 * Sending a new page to the receivers in the rooms.
 * @author jimmy
 *
 */
public class NewPageMsg implements IReceiverMsg {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -8099425304843013409L;

	/**
	 * @param cmdAdatper 
	 * Start the algorithm.
	 */
	public void start(ICmd2ModelAdapter cmdAdatper) {
		cmdAdatper.buildFixedComponent(new IComponentFactory() {

			@Override
			public Component make() {
				JTextArea textArea = new JTextArea();
				textArea.append("You get a new page in " + cmdAdatper.getRoomName() + " room! \n");
				textArea.append("You could write some notes here: \n");
				return textArea;
			}

		}, "Notes");
	}

	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public static IDataPacketID GetID() {
		return DataPacketIDFactory.Singleton.makeID(NewPageMsg.class);
	}

	/**
	 * Get the data packet ID associated with this class.
	 * 
	 * @return The data packet ID.
	 */
	public IDataPacketID getID() {
		return NewPageMsg.GetID();
	}
}
