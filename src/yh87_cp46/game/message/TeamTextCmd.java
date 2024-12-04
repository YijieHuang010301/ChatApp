package yh87_cp46.game.message;

import java.util.HashMap;
import java.util.HashSet;

import common.adapter.ICmd2ModelAdapter;
import common.receiver.AReceiverDataPacketAlgoCmd;
import common.receiver.INamedReceiver;
import common.receiver.ReceiverDataPacket;
import provided.datapacket.IDataPacketID;
import yh87_cp46.chatRoom.miniModel.message.StringMsg;

/**
 * 
 * @author Cheng Peng
 *
 */
public class TeamTextCmd extends AReceiverDataPacketAlgoCmd<TeamTextMsg> {

	/**
	 * Unique UID.
	 */
	private static final long serialVersionUID = -7561990117258505450L;

	/**
	 * The cmd to model adapter.
	 */
	private transient ICmd2ModelAdapter adapter;

	/**
	 * The map tells the info about team assignment
	 */
	private transient HashMap<Boolean, HashSet<INamedReceiver>> teamMap;

	/**
	 * Constructor for TeamTextCmd.
	 * @param teamMap The map tells the info about team assignment
	 */
	public TeamTextCmd(HashMap<Boolean, HashSet<INamedReceiver>> teamMap) {
		this.teamMap = teamMap;
	}

	@Override
	public Void apply(IDataPacketID index, ReceiverDataPacket<TeamTextMsg> host, Void... params) {
		for (INamedReceiver member : teamMap.get(host.getData().getTeam())) {
			adapter.send(
					new StringMsg(
							"(Team Message from " + host.getSender().getName() + "): " + host.getData().getMsg()),
					member);
		}
		return null;
	}

	@Override
	public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
		this.adapter = cmd2ModelAdpt;
	}

}
