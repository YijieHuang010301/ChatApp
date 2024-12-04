package yh87_cp46.chatRoom.miniModel.message.customMsg;

import java.util.HashMap;
import java.util.UUID;

import common.adapter.ICmd2ModelAdapter;
import common.receiver.AReceiverDataPacketAlgoCmd;
import common.receiver.ReceiverDataPacket;
import provided.datapacket.IDataPacketID;
import provided.mixedData.MixedDataKey;
import yh87_cp46.game.GameController;

/**
 * The sending game UI cmd.
 * @author Cheng Peng
 *
 */
public class GameCmd extends AReceiverDataPacketAlgoCmd<GameMsg> {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 4417143744890707976L;

	/**
	 * The cmd to model adapter.
	 */
	private transient ICmd2ModelAdapter adapter;

	@Override
	public Void apply(IDataPacketID index, ReceiverDataPacket<GameMsg> host, Void... params) {

		UUID gameID = host.getData().getGameID();

		@SuppressWarnings("rawtypes")
		MixedDataKey<HashMap> gameKey = new MixedDataKey<HashMap>(gameID, "game id", HashMap.class);

		HashMap<String, Integer> teamScoreMap = new HashMap<>();
		teamScoreMap.put("teamA", 0);
		teamScoreMap.put("teamB", 0);
		adapter.put(gameKey, teamScoreMap);

		GameController gameController = new GameController(adapter, host.getData().getTeam(), host.getSender(),
				host.getData().getPlayer(), host.getData().getGameID(), host.getData().getMemberList());
		gameController.start();
		return null;
	}

	@Override
	public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
		this.adapter = cmd2ModelAdpt;
	}

}
