package yh87_cp46.game.message;

import java.util.HashMap;
import java.util.UUID;

import common.adapter.ICmd2ModelAdapter;
import common.receiver.AReceiverDataPacketAlgoCmd;
import common.receiver.ReceiverDataPacket;
import provided.datapacket.IDataPacketID;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.mixedData.MixedDataKey;
import yh87_cp46.chatRoom.miniModel.message.StringMsg;

/**
 * A command that updates the score of a team.
 * 
 * @author jimmy
 *
 */
public class UpdateScoreCmd extends AReceiverDataPacketAlgoCmd<UpdateScoreMsg> {

	/**
	 * The unique id
	 */
	private static final long serialVersionUID = -7023836263716400538L;

	/**
	 * The cmd to model adapter.
	 */
	private transient ICmd2ModelAdapter adapter;

	/**
	 * logger for debug.
	 */
	private ILogger logger = ILoggerControl.getSharedLogger();

	@Override
	public Void apply(IDataPacketID index, ReceiverDataPacket<UpdateScoreMsg> host, Void... params) {
		String team;
		if (host.getData().getTeam()) {
			team = "teamA";
		} else {
			team = "teamB";
		}

		logger.log(LogLevel.DEBUG, "HERE------------------->" + host.getSender().getName());
		adapter.broadcast(new StringMsg(
				host.getSender().getName() + " has added " + host.getData().getScore() + " points to " + team));
		UUID gameID = host.getData().getGameID();
		@SuppressWarnings("rawtypes")
		MixedDataKey<HashMap> key = new MixedDataKey<HashMap>(gameID, "game id", HashMap.class);
		@SuppressWarnings("unchecked")
		HashMap<String, Integer> valueMap = adapter.get(key);

		// The team's new score
		int score = ((int) valueMap.get(team)) + host.getData().getScore();
		adapter.broadcast(new StringMsg(team + " now has a score of " + score));

		// Broadcast the game win message if every team member has reached Boston.
		if (score >= (75 * host.getData().getTeamSize())) {
			adapter.broadcast(new GameWinMsg(host.getData().getTeam()));
		}

		valueMap.put(team, score);
		adapter.put(key, valueMap);
		return null;
	}

	@Override
	public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
		this.adapter = cmd2ModelAdpt;

	}

}
