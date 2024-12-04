package yh87_cp46.game.message;

import java.awt.Component;
import javax.swing.JTextArea;

import common.adapter.ICmd2ModelAdapter;
import common.adapter.IComponentFactory;
import common.receiver.AReceiverDataPacketAlgoCmd;
import common.receiver.ReceiverDataPacket;
import provided.datapacket.IDataPacketID;

/**
 * The win message.
 * @author Cheng Peng
 *
 */
public class GameWinCmd extends AReceiverDataPacketAlgoCmd<GameWinMsg> {

	/**
	 * Unique UID.
	 */
	private static final long serialVersionUID = -6070743229886283979L;

	/**
	 * The cmd to model adapter.
	 */
	private transient ICmd2ModelAdapter adapter;

	@Override
	public Void apply(IDataPacketID index, ReceiverDataPacket<GameWinMsg> host, Void... params) {
		String team;
		if (host.getData().getTeam()) {
			team = "team A";
		} else {
			team = "team B";
		}

		adapter.buildFixedComponent(new IComponentFactory() {
			@Override
			public Component make() {
				JTextArea textArea = new JTextArea();
				textArea.append("Congratulations! " + team + " wins the game");
				return textArea;
			}
		}, "Result");
		return null;
	}

	@Override
	public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
		this.adapter = cmd2ModelAdpt;
	}

}
