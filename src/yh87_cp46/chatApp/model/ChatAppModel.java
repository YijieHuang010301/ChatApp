package yh87_cp46.chatApp.model;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import yh87_cp46.chatApp.model.message.AddPeersMsg;
import yh87_cp46.chatApp.model.message.InviteMsg;
import yh87_cp46.chatApp.model.message.QuitMsg;
import yh87_cp46.chatApp.model.message.RequestInviteMsg;
import yh87_cp46.chatApp.model.message.SyncPeersMsg;
import yh87_cp46.chatApp.model.message.status.ConnectorErrMsg;
import yh87_cp46.chatApp.model.message.status.ConnectorFailMsg;
import yh87_cp46.chatApp.model.message.status.ConnectorRejectMsg;
import common.connector.AConnectorDataPacketAlgoCmd;
import common.connector.ConnectorDataPacket;
import common.connector.ConnectorDataPacketAlgo;
import common.connector.IConnector;
import common.connector.INamedConnector;
import common.connector.messages.IAddPeersMsg;
import common.connector.messages.IConnectorErrMsg;
import common.connector.messages.IConnectorFailMsg;
import common.connector.messages.IConnectorMsg;
import common.connector.messages.IConnectorRejectMsg;
import common.connector.messages.IInviteMsg;
import common.connector.messages.IQuitMsg;
import common.connector.messages.IRequestInviteMsg;
import common.connector.messages.ISyncPeersMsg;
import provided.datapacket.IDataPacketID;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.pubsubsync.IPubSubSyncManager;
import provided.pubsubsync.IPubSubSyncConnection;
import provided.rmiUtils.IRMIUtils;
import provided.rmiUtils.IRMI_Defs;
import provided.rmiUtils.RMIUtils;

/**
 * The model for the chat app
 * @author Irpan & Jimmy
 *
 */

public class ChatAppModel {
	/**
	 * The log in name.
	 */
	private String name;

	/**
	 * The adapter in use.
	 */
	private IMainModelToViewAdptr myModel2ViewAdapter;

	/**
	 * The RMI utils of the model.
	 */
	private IRMIUtils rmiUtils;
	/**
	 * The system logger to use.
	 */
	private ILogger logger = ILoggerControl.getSharedLogger();

	/**
	 * Stub to local user
	 */
	private INamedConnector myNamedConnector;

	/**
	 * Stub to local user
	 */
	private IConnector connector;

	/**
	 * Stub to local user
	 */
	private IConnector connectorStub;

	/**
	 * The registry that the model
	 */
	private Registry myRegistry;

	/**
	 * PubSubSync manager for the model.
	 */
	private IPubSubSyncManager myPubSubSyncManager;

	/**
	 * The connector algo for the model
	 */
	private ConnectorDataPacketAlgo connectAlgo;

	/**
	 * The port for the stub
	 */
	private final int myPort = IRMI_Defs.STUB_PORT_CLIENT;

	/**
	 * A set of named connector to keep track of the connected users
	 */
	private Set<INamedConnector> connectedUsersSet = new CopyOnWriteArraySet<>();

	/**
	 * A set of IMainModelToMiniMVCAdptr to keep track of all the chat room 
	 */
	private Set<IMainModelToMiniMVCAdptr> connectedChatRoomsSet = new CopyOnWriteArraySet<>();

	/**
	 * A thread pool that helps with concurrency
	 */
	private ExecutorService threadPool = Executors.newCachedThreadPool();

	/**
	 * Constructor for the model
	 * 
	 * @param logger	The system logger
	 * @param model2ViewAdpt The view adapter used by the model
	 */
	public ChatAppModel(ILogger logger, IMainModelToViewAdptr model2ViewAdpt) {
		this.logger = logger;
		this.myModel2ViewAdapter = model2ViewAdpt;
		this.rmiUtils = new RMIUtils(logger);
		this.setConnectionCmd();
		this.connector = new IConnector() {

			@Override
			public void sendMessage(ConnectorDataPacket<?> packet) throws RemoteException {
				threadPool.submit(() -> packet.execute(connectAlgo));
				//packet.execute(connectAlgo);
			}

			@Override
			public INamedConnector makeNamedConnector() throws RemoteException {
				return myNamedConnector;
			}
		};
	}

	/**
	 * To start the RMI 
	 */
	public void start() {
		try {
			rmiUtils.startRMI(IRMI_Defs.CLASS_SERVER_PORT_CLIENT);
			myRegistry = rmiUtils.getLocalRegistry();
		} catch (Exception e) {
			logger.log(LogLevel.ERROR, "Exception in starting rmi and getting local registry");
			e.printStackTrace();
		}
	}

	/**
	 * Export the connector stub and binding to registry
	 * @param userName The name of the user of this chat app
	 */
	public void startServer(String userName) {
		this.name = userName;

		try {
			this.connectorStub = (IConnector) UnicastRemoteObject.exportObject(connector, IRMI_Defs.STUB_PORT_SERVER);

		} catch (Exception e) {
			logger.log(LogLevel.ERROR, "Exception in exporting connector stub");
			e.printStackTrace();
		}

		try {
			myNamedConnector = new NamedConnector(this.connectorStub, name);
			myRegistry.rebind(name, myNamedConnector.getStub());
			connectedUsersSet.add(myNamedConnector);
		} catch (Exception e) {
			logger.log(LogLevel.ERROR, "Exception in binding to registry");
			e.printStackTrace();
		}

		try {
			myPubSubSyncManager = IPubSubSyncConnection.getPubSubSyncManager(logger, rmiUtils, myPort);
		} catch (Exception e) {
			logger.log(LogLevel.ERROR, "Exception of PubSubSyncManager");
			e.printStackTrace();
		}
	}

	/**
	 * Connect button with remote IP and boundname.
	 * @param remoteIP The remote host ip.
	 * @param boundName The boundname of the host.
	 */
	public void connectTo(String remoteIP, String boundName) {
		try {
			logger.log(LogLevel.INFO, "Locating registry at " + remoteIP + "...");
			Registry registry = rmiUtils.getRemoteRegistry(remoteIP);
			IConnector remoteStub = (IConnector) registry.lookup(boundName);
			connectToStub(remoteStub);
		} catch (Exception e) {
			logger.log(LogLevel.ERROR, "Exception connecting to " + remoteIP + ": " + e);
			e.printStackTrace();
		}
	}

	/**
	 * Connect to the remote host's stub.
	 * @param otherConnector The other user's connector stub
	 */
	public void connectToStub(IConnector otherConnector) {
		logger.log(LogLevel.INFO, "Connecting...");
		try {
			otherConnector.sendMessage(
					new ConnectorDataPacket<ISyncPeersMsg>(new SyncPeersMsg(connectedUsersSet), myNamedConnector));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the ChatRoom.
	 * @param chatRoomName The name of the chat room to create
	 */
	public void createChatroom(String chatRoomName) {
		threadPool.submit(() -> {
			IMainModelToMiniMVCAdptr chatroom = this.myModel2ViewAdapter.makeMiniMVC(chatRoomName, myPubSubSyncManager,
					myNamedConnector, null);
			connectedChatRoomsSet.add(chatroom);
		});
	}

	/**
	 * Join the ChatRoom.
	 * @param chatRoomName The name of the chat room to join
	 * @param chatroomID The uuid of the chat room to join
	 */
	public void joinChatroom(String chatRoomName, UUID chatroomID) {
		this.logger.log(LogLevel.DEBUG, "main model join " + chatroomID.toString());
		IMainModelToMiniMVCAdptr chatroom = this.myModel2ViewAdapter.makeMiniMVC(chatRoomName, myPubSubSyncManager,
				myNamedConnector, chatroomID);
		this.myModel2ViewAdapter.selectMiniMVC(chatroomID);
		connectedChatRoomsSet.add(chatroom);
	}

	/**
	 * Invite other to join the chat room.\
	 * @param remoteHostStub THe remote host stub of the chat room to invite to
	 */
	public void invite(INamedConnector remoteHostStub) {
		new Thread(() -> {
			try {
				remoteHostStub.sendMessage(new ConnectorDataPacket<IInviteMsg>(
						new InviteMsg(this.myModel2ViewAdapter.getRooms()), myNamedConnector));
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}).start();
	}

	/**
	 * Leave the ChatRoom.
	 * @param roomID The id of the chat room to leave
	 */
	public void leaveChatroom(UUID roomID) {
		if (connectedChatRoomsSet == null) {
			return;
		}
		for (IMainModelToMiniMVCAdptr adapter : connectedChatRoomsSet) {
			if (adapter.getUUID().equals(roomID)) {
				connectedChatRoomsSet.remove(adapter);
			}
		}
		logger.log(LogLevel.INFO, "remove in main model" + roomID.toString());
		this.myModel2ViewAdapter.removeMiniMVC(roomID);
	}

	/**
	 * Visitor for dealing with connection command.
	 */
	public void setConnectionCmd() {

		//default
		connectAlgo = new ConnectorDataPacketAlgo(new AConnectorDataPacketAlgoCmd<IConnectorMsg>() {
			/**
			 * Serial ID.
			 */
			private static final long serialVersionUID = 6067989989662917827L;

			@Override
			public Void apply(IDataPacketID index, ConnectorDataPacket<IConnectorMsg> host, Void... params) {
				INamedConnector sender = host.getSender();
				try {
					sender.sendMessage(new ConnectorDataPacket<IConnectorErrMsg>(new ConnectorErrMsg(host, sender),
							myNamedConnector));
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				logger.log(LogLevel.ERROR, "Received unknown data packet type from " + sender);
				return null;
			}
		});

		connectAlgo.setCmd(ISyncPeersMsg.GetID(), new AConnectorDataPacketAlgoCmd<ISyncPeersMsg>() {
			/**
			 * Serial ID.
			 */
			private static final long serialVersionUID = 6500821099523345562L;

			@Override
			public Void apply(IDataPacketID index, ConnectorDataPacket<ISyncPeersMsg> host, Void... params) {
				try {
					for (INamedConnector myPeer : connectedUsersSet) {
						myPeer.sendMessage(new ConnectorDataPacket<IAddPeersMsg>(
								new AddPeersMsg(host.getData().getNewPeers()), myNamedConnector));
					}
					for (INamedConnector otherPeer : host.getData().getNewPeers()) {
						otherPeer.sendMessage(new ConnectorDataPacket<IAddPeersMsg>(new AddPeersMsg(connectedUsersSet),
								myNamedConnector));
					}
				} catch (RemoteException e) {
					try {
						host.getSender().sendMessage(new ConnectorDataPacket<IConnectorErrMsg>(
								new ConnectorErrMsg(host, host.getSender()), myNamedConnector));
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
					e.printStackTrace();
				}
				return null;
			}

		});

		connectAlgo.setCmd(IAddPeersMsg.GetID(), new AConnectorDataPacketAlgoCmd<IAddPeersMsg>() {
			/**
			 * Serial ID.
			 */
			private static final long serialVersionUID = -7247522809287838629L;

			@Override
			public Void apply(IDataPacketID index, ConnectorDataPacket<IAddPeersMsg> host, Void... params) {
				for (INamedConnector eachNamedConnector : host.getData().getNewPeers()) {
					connectedUsersSet.add(eachNamedConnector);
				}
				//update the list without self.
				Set<INamedConnector> tempSet = new CopyOnWriteArraySet<>();
				for (INamedConnector a : connectedUsersSet) {
					if (!a.equals(myNamedConnector)) {
						tempSet.add(a);
					}
				}
				myModel2ViewAdapter.updateConnectorList(tempSet);
				return null;
			}
		});

		connectAlgo.setCmd(IQuitMsg.GetID(), new AConnectorDataPacketAlgoCmd<IQuitMsg>() {
			/**
			 * Serial ID.
			 */
			private static final long serialVersionUID = -3320470647452126721L;

			@Override
			public Void apply(IDataPacketID index, ConnectorDataPacket<IQuitMsg> host, Void... params) {
				try {
					connectedUsersSet.remove(host.getSender());
					//update the list without self.
					Set<INamedConnector> tempSet = new CopyOnWriteArraySet<>();
					for (INamedConnector a : connectedUsersSet) {
						if (!a.equals(myNamedConnector)) {
							tempSet.add(a);
						}
					}
					myModel2ViewAdapter.updateConnectorList(tempSet);

				} catch (Exception e) {
					try {
						host.getSender().sendMessage(new ConnectorDataPacket<IConnectorFailMsg>(
								new ConnectorFailMsg(host, host.getSender()), myNamedConnector));
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
				}
				return null;
			}
		});

		connectAlgo.setCmd(IInviteMsg.GetID(), new AConnectorDataPacketAlgoCmd<IInviteMsg>() {
			/**
			 * Serial ID.
			 */
			private static final long serialVersionUID = -3320470647452126721L;

			@Override
			public Void apply(IDataPacketID index, ConnectorDataPacket<IInviteMsg> host, Void... params) {
				Map<UUID, String> rooms = host.getData().getRooms();
				String roomName = myModel2ViewAdapter
						.confirmToJoin(host.getSender().getName() + " invites you to join ", rooms);
				if (roomName != "Error") {
					for (Entry<UUID, String> entry : rooms.entrySet()) {
						if (entry.getValue() == roomName) {
							joinChatroom(entry.getValue(), entry.getKey());
						}
					}
				} else {
					try {
						host.getSender().sendMessage(new ConnectorDataPacket<IConnectorRejectMsg>(
								new ConnectorRejectMsg(host, host.getSender()), myNamedConnector));
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				return null;
			}
		});

		connectAlgo.setCmd(IRequestInviteMsg.GetID(), new AConnectorDataPacketAlgoCmd<IRequestInviteMsg>() {

			/**
			 * Unique ID.
			 */
			private static final long serialVersionUID = -4596618750945912853L;

			@Override
			public Void apply(IDataPacketID index, ConnectorDataPacket<IRequestInviteMsg> host, Void... params) {
				new Thread(() -> {
					try {
						host.getSender().sendMessage(new ConnectorDataPacket<IInviteMsg>(
								new InviteMsg(myModel2ViewAdapter.getRooms()), myNamedConnector));
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}).start();
				return null;
			}
		});

		connectAlgo.setCmd(IConnectorErrMsg.GetID(), new AConnectorDataPacketAlgoCmd<IConnectorErrMsg>() {

			/**
			 * Serial ID.
			 */
			private static final long serialVersionUID = 2482820133214869252L;

			@Override
			public Void apply(IDataPacketID index, ConnectorDataPacket<IConnectorErrMsg> host, Void... params) {
				logger.log(LogLevel.ERROR, name + "had an error when connecting with you!!");
				return null;
			}
		});

		connectAlgo.setCmd(IConnectorRejectMsg.GetID(), new AConnectorDataPacketAlgoCmd<IConnectorRejectMsg>() {

			/**
			 * Serial ID.
			 */
			private static final long serialVersionUID = 2482820133214869252L;

			@Override
			public Void apply(IDataPacketID index, ConnectorDataPacket<IConnectorRejectMsg> host, Void... params) {
				logger.log(LogLevel.ERROR, name + " rejected your request to join the chatroom!!");
				return null;
			}
		});

		connectAlgo.setCmd(IConnectorFailMsg.GetID(), new AConnectorDataPacketAlgoCmd<IConnectorFailMsg>() {

			/**
			 * Serial ID.
			 */
			private static final long serialVersionUID = 2482820133214869252L;

			@Override
			public Void apply(IDataPacketID index, ConnectorDataPacket<IConnectorFailMsg> host, Void... params) {
				logger.log(LogLevel.ERROR, name + " had a FAIL status with you");
				return null;
			}
		});

	}

	/**
	 * Quit the APP.
	 */
	public void quit() {
		try {
			for (INamedConnector eachConnector : connectedUsersSet)
				eachConnector.sendMessage(new ConnectorDataPacket<IQuitMsg>(new QuitMsg(), myNamedConnector));
			// quit all joined rooms
			for (IMainModelToMiniMVCAdptr mainToMiniAdptr : connectedChatRoomsSet)
				mainToMiniAdptr.exitChatRoom();
			rmiUtils.stopRMI();
			System.exit(0);
		} catch (Exception e) {
			logger.log(LogLevel.ERROR, "Error stopping client: " + e + "\n");
			e.printStackTrace();
		}
	}

	/**
	 * Get the internal IRMIUtils instance being used.
	 * The discovery model start method needs the main model's IRMIUtils.
	 * ONLY call the method AFTER the model, i.e. the internal IRMIUtils, has been started!
	 * @return The internal IRMIUtils instance
	 */
	public IRMIUtils getRMIUtils() {
		return this.rmiUtils;
	}

	/**
	 * Send a RequestInviteMsg to the selected host. Asked the game server
	 * to be invited to the game room.
	 * 
	 * @param remoteHost The remote Game Server.
	 */
	public void request(INamedConnector remoteHost) {
		try {
			remoteHost
					.sendMessage(new ConnectorDataPacket<IRequestInviteMsg>(new RequestInviteMsg(), myNamedConnector));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
}
