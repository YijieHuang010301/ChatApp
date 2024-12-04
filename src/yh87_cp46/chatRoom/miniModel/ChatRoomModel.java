package yh87_cp46.chatRoom.miniModel;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.SwingUtilities;

import java.rmi.server.UnicastRemoteObject;

import yh87_cp46.chatApp.model.NamedReceiver;
import yh87_cp46.chatRoom.miniModel.message.CommandMsg;
import yh87_cp46.chatRoom.miniModel.message.CommandRequestMsg;
import yh87_cp46.chatRoom.miniModel.message.StringMsg;
import yh87_cp46.chatRoom.miniModel.message.customMsg.GameCmd;
import yh87_cp46.chatRoom.miniModel.message.customMsg.GameMsg;
import yh87_cp46.chatRoom.miniModel.message.customMsg.MapCmd;
import yh87_cp46.chatRoom.miniModel.message.customMsg.MapMsg;
import yh87_cp46.chatRoom.miniModel.message.customMsg.NewPageCmd;
import yh87_cp46.chatRoom.miniModel.message.customMsg.NewPageMsg;
import yh87_cp46.game.message.GameWinCmd;
import yh87_cp46.game.message.GameWinMsg;
import yh87_cp46.game.message.TeamTextCmd;
import yh87_cp46.game.message.TeamTextMsg;
import yh87_cp46.game.message.UpdateScoreCmd;
import yh87_cp46.game.message.UpdateScoreMsg;
import common.adapter.ICmd2ModelAdapter;
import common.connector.INamedConnector;
import common.receiver.AReceiverDataPacketAlgoCmd;
import common.receiver.INamedReceiver;
import common.receiver.IReceiver;
import common.receiver.ReceiverDataPacket;
import common.receiver.ReceiverDataPacketAlgo;
import common.receiver.messages.ICommandMsg;
import common.receiver.messages.ICommandRequestMsg;
import common.receiver.messages.IReceiverErrMsg;
import common.receiver.messages.IReceiverFailMsg;
import common.receiver.messages.IReceiverMsg;
import common.receiver.messages.IReceiverRejectMsg;
import common.receiver.messages.IReceiverStatusMsg;
import common.receiver.messages.IStringMsg;
import provided.datapacket.IDataPacketID;
import provided.logger.ILogger;
import provided.logger.LogLevel;
import provided.pubsubsync.IPubSubSyncChannelUpdate;
import provided.pubsubsync.IPubSubSyncManager;
import provided.pubsubsync.IPubSubSyncUpdater;
import provided.rmiUtils.IRMI_Defs;
import java.util.Random;

/**
 * The chat room model
 */
public class ChatRoomModel {

	/**
	 * The user name for this tab.
	 */
	private String username;

	/**
	 * The friendly name of the room.
	 */
	private String roomName;

	/**
	 * Logger for the model
	 */
	private ILogger logger;

	/**
	 * The chat room connector, i.e. the chat room object.
	 */
	private INamedConnector myNamedConnector;

	/**
	 * The chat room receiver.
	 */
	private IReceiver receiver;

	/**
	 * The chat room receiverStub.
	 */
	private IReceiver receiverStub;

	/**
	 * My named receiver.
	 */
	private INamedReceiver myNamedReceiver;
	/**
	 * The chat room model to view adapter.
	 */
	private IMiniModelToViewAdptr miniM2VAdptr;
	/**
	 * The chat room model to main model adapter.
	 */
	private IMiniModelToMainModelAdptr miniM2MainMAdptr;

	/**
	 * PSS Channel
	 */
	private IPubSubSyncChannelUpdate<HashSet<INamedReceiver>> chatroomChannel;

	/**
	 * Visitor for this model.
	 */
	private ReceiverDataPacketAlgo receiverAlgo;

	/**
	 * Chatroom roster.
	 */
	private HashSet<INamedReceiver> roster = new HashSet<>();

	/**
	 * The map for unknown command.
	 */
	private Map<IDataPacketID, List<ReceiverDataPacket<?>>> cachedMsg = new ConcurrentHashMap<>();

	/**
	 * Room unique id.
	 */
	private UUID roomID;

	/**
	 * The Cmd2ModelAdapter in use
	 */
	private ICmd2ModelAdapter cmd2ModelAdpt;

	/**
	 * Random number generator.
	 */
	private Random rand = new Random();

	/**
	 * A map that has the team assignment.
	 */
	private HashMap<Boolean, HashSet<INamedReceiver>> teamMap = new HashMap<Boolean, HashSet<INamedReceiver>>();

	/**
	 * Constructor of the chat room model.
	 * @param pssManager 		The manager for the class
	 * @param myNamedConnector  The NamedConnector which should be wrapped in the INamedReceiver.
	 * @param roomName 	        The room name.
	 * @param roomID            The room unique id.
	 * @param logger            The logger for the log.
	 * @param adapter 			The mini model to mini view adapter.
	 * @param miniM2MainMAdptr 	The mini model to main model adapter. 
	 * @param cmd2ModelAdpt 	The command to model adapter.
	 * 
	 */
	public ChatRoomModel(IPubSubSyncManager pssManager, INamedConnector myNamedConnector, String roomName, UUID roomID,
			ILogger logger, IMiniModelToViewAdptr adapter, IMiniModelToMainModelAdptr miniM2MainMAdptr,
			ICmd2ModelAdapter cmd2ModelAdpt) {
		this.miniM2VAdptr = adapter;
		this.miniM2MainMAdptr = miniM2MainMAdptr;
		this.cmd2ModelAdpt = cmd2ModelAdpt;
		this.myNamedConnector = myNamedConnector;
		this.username = myNamedConnector.getName();
		this.roomID = roomID;
		this.logger = logger;
		this.roomName = roomName;
		this.setCommand();

		this.receiver = new IReceiver() {
			@Override
			public void sendMessage(ReceiverDataPacket<?> packet) throws RemoteException {
				packet.execute(receiverAlgo);
			}
		};

		try {
			this.receiverStub = (IReceiver) UnicastRemoteObject.exportObject(this.receiver, IRMI_Defs.STUB_PORT_CLIENT);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		this.myNamedReceiver = new NamedReceiver(receiverStub, this.myNamedConnector, this.username);

		//create
		if (roomID == null) {
			this.chatroomChannel = pssManager.createChannel(roomName, new HashSet<INamedReceiver>(),
					(pubSubSyncData) -> {
						roster.clear();
						roster.addAll(pubSubSyncData.getData());
						SwingUtilities.invokeLater(() -> miniM2VAdptr.updateUserList(roster));
						logger.log(LogLevel.INFO, "Update the rosters.");
					}, (statusMsg) -> {
						logger.log(LogLevel.INFO, "[" + roomName + "] " + statusMsg);
					});
			chatroomChannel.update(IPubSubSyncUpdater.makeSetAddFn(myNamedReceiver));
			this.roomID = chatroomChannel.getChannelID();

		} else { // join with id
			this.chatroomChannel = pssManager.subscribeToUpdateChannel(roomID, (pubSubSyncData) -> {
				roster.clear();
				roster.addAll(pubSubSyncData.getData());
				//SwingUtilities.invokeLater(() -> miniM2VAdptr.updateUserList(roster));
				miniM2VAdptr.updateUserList(roster);
				logger.log(LogLevel.INFO, "Update the rosters.");
			}, (statusMsg) -> {
				logger.log(LogLevel.INFO, "[" + roomID + "] " + statusMsg);
			});
			chatroomChannel.update(IPubSubSyncUpdater.makeSetAddFn(myNamedReceiver));
		}
	}

	/**
	 * Start.
	 */
	public void start() {
	}

	/**
	 * To leave the chat room
	 */
	public void leaveChatroom() {
		chatroomChannel.update(IPubSubSyncUpdater.makeSetRemoveFn(myNamedReceiver));
		this.chatroomChannel.unsubscribe();
		this.miniM2MainMAdptr.leaveChatroom(this.roomID);
		miniM2VAdptr.updateUserList(roster);
	}

	/**
	 * Send a text message to the chatroom
	 * 
	 * @param textMsg The text to be sent
	 */
	public void sendTextMsg(String textMsg) {
		for (INamedReceiver member : roster) {
			try {
				member.getStub()
						.sendMessage(new ReceiverDataPacket<IStringMsg>(new StringMsg(textMsg), myNamedReceiver));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 */

	/**
	 * visitor
	 */
	public void setCommand() {

		receiverAlgo = new ReceiverDataPacketAlgo(new AReceiverDataPacketAlgoCmd<IReceiverMsg>() {

			/**
			 * Serial ID.
			 */
			private static final long serialVersionUID = -6248942648814275526L;

			@Override
			public Void apply(IDataPacketID index, ReceiverDataPacket<IReceiverMsg> host, Void... params) {
				logger.log(LogLevel.INFO, "Received unknown command data. Put it into cached message list...");
				cachedMsg.putIfAbsent(index, new ArrayList<>());
				cachedMsg.get(index).add(host);
				try {
					logger.log(LogLevel.INFO, "Sending a request command to the sender...");
					host.getSender().sendMessage(
							new ReceiverDataPacket<ICommandRequestMsg>(new CommandRequestMsg(index), myNamedReceiver));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				//no-op
			}
		});

		receiverAlgo.setCmd(IStringMsg.GetID(), new AReceiverDataPacketAlgoCmd<IStringMsg>() {

			/**
			 * Serial ID.
			 */
			private static final long serialVersionUID = -7776861230954818394L;

			@Override
			public Void apply(IDataPacketID index, ReceiverDataPacket<IStringMsg> host, Void... params) {
				INamedReceiver sender = host.getSender();
				miniM2VAdptr.showText("[" + sender + "]: " + host.getData().getString());
				return null;
			}

			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				//no-op
			}
		});

		receiverAlgo.setCmd(ICommandMsg.GetID(), new AReceiverDataPacketAlgoCmd<ICommandMsg>() {

			/**
			 * Serial ID.
			 */
			private static final long serialVersionUID = 7106878944341568313L;

			@Override
			public Void apply(IDataPacketID index, ReceiverDataPacket<ICommandMsg> host, Void... params) {
				IDataPacketID unknownID = null;
				try {
					logger.log(LogLevel.DEBUG, host.getData().getCmdID() + "----h1111ere-----");
					logger.log(LogLevel.DEBUG, host.getData().getCmd() + "----h2222ere-----");
					unknownID = ((ICommandMsg) (host.getData())).getCmdID();
					AReceiverDataPacketAlgoCmd<?> cmd = ((ICommandMsg) host.getData()).getCmd();
					cmd.setCmd2ModelAdpt(cmd2ModelAdpt);
					receiverAlgo.setCmd(unknownID, cmd);
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (!cachedMsg.containsKey(unknownID)) {
					return null;
				}

				try {
					cachedMsg.get(unknownID).forEach(data -> data.execute(receiverAlgo));
					cachedMsg.remove(unknownID);
					logger.log(LogLevel.INFO, "Processed cached data packets of ID: " + unknownID);
				} catch (Exception e) {
					logger.log(LogLevel.ERROR, "Cannot use received command to process data: " + e);
					e.printStackTrace();
				}

				return null;
			}

			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				//no-op
			}
		});

		receiverAlgo.setCmd(ICommandRequestMsg.GetID(), new AReceiverDataPacketAlgoCmd<ICommandRequestMsg>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -8956323378109031651L;

			@Override
			public Void apply(IDataPacketID index, ReceiverDataPacket<ICommandRequestMsg> host, Void... params) {

				IDataPacketID unknownID = ((ICommandRequestMsg) (host.getData())).getCmdID();
				logger.log(LogLevel.INFO, "Received command request message with ID: " + unknownID);
				logger.log(LogLevel.DEBUG, "HERE_____----" + receiverAlgo.getCmd(unknownID).toString());
				try {
					logger.log(LogLevel.INFO, "Received command request message with ID: " + unknownID);
					host.getSender().sendMessage(new ReceiverDataPacket<ICommandMsg>(
							// first parameter is null.
							new CommandMsg((AReceiverDataPacketAlgoCmd<?>) receiverAlgo.getCmd(unknownID), unknownID),
							myNamedReceiver));
				} catch (RemoteException e) {
					logger.log(LogLevel.ERROR, "Error while responding to CommandRequestMsg: " + e);
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				//no-op
			}

		});

		receiverAlgo.setCmd(IReceiverStatusMsg.GetID(), new AReceiverDataPacketAlgoCmd<IReceiverStatusMsg>() {

			/**
			 * Serial ID.
			 */
			private static final long serialVersionUID = -8956323378109031651L;

			@Override
			public Void apply(IDataPacketID index, ReceiverDataPacket<IReceiverStatusMsg> host, Void... params) {
				logger.log(LogLevel.ERROR,
						"Received status message from " + host.getSender().getName() + host.getData());
				return null;
			}

			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				//no-op
			}

		});

		//Error message
		receiverAlgo.setCmd(IReceiverErrMsg.GetID(), new AReceiverDataPacketAlgoCmd<IReceiverErrMsg>() {
			/**
			 * Serial ID.
			 */
			private static final long serialVersionUID = 8720295831850711066L;

			@Override
			public Void apply(IDataPacketID index, ReceiverDataPacket<IReceiverErrMsg> host, Void... params) {
				logger.log(LogLevel.ERROR,
						"Received error message from " + host.getSender().getName() + host.getData());
				return null;
			}

			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {

			}

		});

		receiverAlgo.setCmd(IReceiverFailMsg.GetID(), new AReceiverDataPacketAlgoCmd<IReceiverFailMsg>() {

			/**
			 * Serial ID.
			 */
			private static final long serialVersionUID = -4286353973727557452L;

			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				//no-op
			}

			@Override
			public Void apply(IDataPacketID index, ReceiverDataPacket<IReceiverFailMsg> host, Void... params) {
				logger.log(LogLevel.ERROR, "Received fail message from " + host.getSender().getName() + host.getData());
				return null;
			}
		});

		receiverAlgo.setCmd(IReceiverRejectMsg.GetID(), new AReceiverDataPacketAlgoCmd<IReceiverRejectMsg>() {

			/**
			 * Serial ID.
			 */
			private static final long serialVersionUID = -7860618723787158374L;

			@Override
			public Void apply(IDataPacketID index, ReceiverDataPacket<IReceiverRejectMsg> host, Void... params) {
				logger.log(LogLevel.ERROR,
						"Received reject message from " + host.getSender().getName() + host.getData());
				return null;
			}

			@Override
			public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
				//no-op
			}
		});

		// Implement custom command.
		NewPageCmd pageCmd = new NewPageCmd();
		pageCmd.setCmd2ModelAdpt(cmd2ModelAdpt);
		receiverAlgo.setCmd(NewPageMsg.GetID(), pageCmd);

		MapCmd googleMapCmd = new MapCmd();
		googleMapCmd.setCmd2ModelAdpt(cmd2ModelAdpt);
		receiverAlgo.setCmd(MapMsg.GetID(), googleMapCmd);

		GameCmd gameCmd = new GameCmd();
		gameCmd.setCmd2ModelAdpt(cmd2ModelAdpt);
		receiverAlgo.setCmd(GameMsg.GetID(), gameCmd);

		UpdateScoreCmd updateScoreCmd = new UpdateScoreCmd();
		updateScoreCmd.setCmd2ModelAdpt(cmd2ModelAdpt);
		receiverAlgo.setCmd(UpdateScoreMsg.GetID(), updateScoreCmd);

		TeamTextCmd teamTextCmd = new TeamTextCmd(teamMap);
		teamTextCmd.setCmd2ModelAdpt(cmd2ModelAdpt);
		receiverAlgo.setCmd(TeamTextMsg.GetID(), teamTextCmd);

		GameWinCmd gameWinCmd = new GameWinCmd();
		gameWinCmd.setCmd2ModelAdpt(cmd2ModelAdpt);
		receiverAlgo.setCmd(GameWinMsg.GetID(), gameWinCmd);
	}

	/**
	 * Get the room name.
	 * @return the Room name.
	 */
	public String getRoomName() {
		return roomName;
	}

	/**
	 * Get the UUID of the room.
	 * @return the UUID of the room.
	 */
	public UUID getUUID() {
		return this.roomID;
	}

	/**
	 * Get the user name.
	 * @return the user name.
	 */
	public String getInstanceName() {
		return username;
	}

	/**
	 * Return the receiver in this tab.
	 * @return the named receiver in this tab.
	 */
	public INamedReceiver getReceiver() {
		return this.myNamedReceiver;
	}

	/**
	 * BroadCast the message.
	 * @param msg the message we want to send.
	 */
	public void broadcast(IReceiverMsg msg) {
		//		roster.parallelStream().forEach((member) -> {
		//			new Thread(() -> {
		//				try {
		//					member.getStub().sendMessage(new ReceiverDataPacket<IReceiverMsg>(msg, myNamedReceiver));
		//				} catch (RemoteException e) {
		//					logger.log(LogLevel.ERROR, "Exception while broadcasting: " + e);
		//					e.printStackTrace();
		//				}
		//			}).start();
		//		});
		for (INamedReceiver receiver : roster) {
			sendDataPacketTo(msg, receiver);
		}
	}

	/**
	 * Send the data to a specific sender.
	 * @param msg The message we want to send.
	 * @param namedReceiver The receiver for this receiving the message.
	 */
	public void sendDataPacketTo(IReceiverMsg msg, INamedReceiver namedReceiver) {
		try {
			namedReceiver.sendMessage(new ReceiverDataPacket<IReceiverMsg>(msg, myNamedReceiver));
		} catch (RemoteException e) {
			logger.log(LogLevel.ERROR, "Exception while send to one: " + e);
			e.printStackTrace();
		}
	}

	/**
	 * Get the logger.
	 * @return The logger in this class.
	 */
	public ILogger getLogger() {
		return this.logger;
	}

	/**
	 * The function for sending a new page.
	 */
	public void sendPage() {
		for (INamedReceiver member : roster) {
			try {
				logger.log(LogLevel.INFO, "Sending a new page to members...");
				member.getStub().sendMessage(new ReceiverDataPacket<NewPageMsg>(new NewPageMsg(), myNamedReceiver));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * The function for sending a new page.
	 */
	public void sendMap() {
		for (INamedReceiver member : roster) {
			try {
				logger.log(LogLevel.INFO, "Sending a new page to members...");
				member.getStub().sendMessage(new ReceiverDataPacket<MapMsg>(new MapMsg(), myNamedReceiver));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Send Game MVC to all while assigning each member a team randomly.
	 */
	public void sendGameToAll() {

		teamMap.clear();

		HashSet<INamedReceiver> teamA = new HashSet<INamedReceiver>();
		HashSet<INamedReceiver> teamB = new HashSet<INamedReceiver>();

		List<Integer> randIntList = new ArrayList<>();
		for (int i = 0; i < roster.size(); i++) {
			randIntList.add(i);
		}
		Collections.shuffle(randIntList, rand);

		int counter = 0;
		UUID gameID = UUID.randomUUID();

		for (INamedReceiver member : roster) {

			boolean team = false; // Temporary change HERE!!! Turn back to false after test!!!

			if (randIntList.get(counter) >= roster.size() / 2) {
				team = true;
			}

			if (team) {
				teamA.add(member);
			} else {
				teamB.add(member);
			}

			counter++;
		}

		for (INamedReceiver member : teamA) {
			try {
				logger.log(LogLevel.INFO, "Sending a new game to team A...");
				member.sendMessage(
						new ReceiverDataPacket<GameMsg>(new GameMsg(true, member, gameID, teamA), myNamedReceiver));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		for (INamedReceiver member : teamB) {
			try {
				logger.log(LogLevel.INFO, "Sending a new game to team B...");
				member.sendMessage(
						new ReceiverDataPacket<GameMsg>(new GameMsg(false, member, gameID, teamB), myNamedReceiver));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		teamMap.put(true, teamA);
		teamMap.put(false, teamB);
	}

}
