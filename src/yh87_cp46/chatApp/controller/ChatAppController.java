package yh87_cp46.chatApp.controller;

import java.awt.EventQueue;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

import yh87_cp46.chatApp.model.ChatAppModel;
import yh87_cp46.chatApp.model.IMainModelToMiniMVCAdptr;
import yh87_cp46.chatApp.model.IMainModelToViewAdptr;
import yh87_cp46.chatApp.view.ChatAppView;
import yh87_cp46.chatApp.view.IMainViewToModelAdptr;
import yh87_cp46.chatRoom.miniController.ChatRoomController;
import common.connector.IConnector;
import common.connector.INamedConnector;
import provided.discovery.IEndPointData;
import provided.discovery.impl.model.DiscoveryModel;
import provided.discovery.impl.model.IDiscoveryModelToViewAdapter;
import provided.discovery.impl.view.DiscoveryPanel;
import provided.discovery.impl.view.IDiscoveryPanelAdapter;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.pubsubsync.IPubSubSyncManager;

/**
 * The controller for chat app.
 *
 */
public class ChatAppController {
	/**
	 * The system logger to use. Change and/or customize this logger as desired.
	 */
	private ILogger sysLogger = ILoggerControl.getSharedLogger();

	/**
	 * The model in use
	 */
	private ChatAppModel model;

	/**
	 * The view in use
	 */
	private ChatAppView<INamedConnector> view;

	/**
	 * The Discovery server UI panel for the view
	 */
	private DiscoveryPanel<IEndPointData> discPnl;

	/**
	 * A self-contained model to handle the discovery server.   MUST be started AFTER the main model as it needs the IRMIUtils from the main model! 
	 */
	private DiscoveryModel<IConnector> discModel;

	/**
	 * Constructor for the chat app.
	 */
	public ChatAppController() {
		sysLogger.setLogLevel(LogLevel.DEBUG);
		discPnl = new DiscoveryPanel<IEndPointData>(new IDiscoveryPanelAdapter<IEndPointData>() {
			/**
			 * watchOnly is ignored b/c discovery model configured for watchOnly = true
			 */
			@Override
			public void connectToDiscoveryServer(String category, boolean watchOnly,
					Consumer<Iterable<IEndPointData>> endPtsUpdateFn) {
				// Ask the discovery model to connect to the discovery server on the given category and use the given updateFn to update the endpoints list in the discovery panel.
				discModel.connectToDiscoveryServer(category, endPtsUpdateFn);
			}

			@Override
			public void connectToEndPoint(IEndPointData selectedEndPt) {
				// Ask the discovery model to obtain a stub from a remote Registry using the info from the given endpoint 
				discModel.connectToEndPoint(selectedEndPt);
			}
		}, true, true); // "Client" usage mode

		discModel = new DiscoveryModel<IConnector>(sysLogger, new IDiscoveryModelToViewAdapter<IConnector>() {

			@Override
			public void addStub(IConnector connector) {
				model.connectToStub(connector);
			}
		});
		view = new ChatAppView<INamedConnector>(new IMainViewToModelAdptr<INamedConnector>() {

			@Override
			public void login(String inputName) {
				model.startServer(inputName);
				discModel.start(model.getRMIUtils(), inputName, inputName);
			}

			@Override
			public void createChatroom(String chatRoomName) {
				model.createChatroom(chatRoomName);

			}

			@Override
			public void quit() {
				model.quit();
				discModel.disconnectFromDiscoveryServer();
			}

			@Override
			public void connect(String RemoteIP, String boundName) {
				model.connectTo(RemoteIP, boundName);

			}

			@Override
			public void invite(INamedConnector remoteHost) {
				model.invite(remoteHost);
			}

			@Override
			public void request(INamedConnector remoteHost) {
				model.request(remoteHost);
			}

		});

		model = new ChatAppModel(sysLogger, new IMainModelToViewAdptr() {

			@Override
			public void updateConnectorList(Set<INamedConnector> connectors) {
				view.updateConnectorList(connectors);

			}

			@Override
			public String confirmToJoin(String msg, Map<UUID, String> rooms) {
				return view.inquiry(msg, rooms);
			}

			@Override
			public void selectMiniMVC(UUID roomID) {
				view.seletChatRoom(roomID);

			}

			@Override
			public void removeMiniMVC(UUID roomID) {
				view.removeChatRoomView(roomID);

			}

			@Override
			public IMainModelToMiniMVCAdptr makeMiniMVC(String chatRoomName, IPubSubSyncManager pssManager,
					INamedConnector myNamedConnector, UUID chatRoomID) {
				ChatRoomController miniControl = new ChatRoomController(pssManager, myNamedConnector, chatRoomName,
						chatRoomID, model, view);
				miniControl.start();
				IMainModelToMiniMVCAdptr adapter = new IMainModelToMiniMVCAdptr() {

					@Override
					public UUID getUUID() {
						return miniControl.getUUID();
					}

					@Override
					public void exitChatRoom() {
						miniControl.exitChatRoom();
					}

				};
				return adapter;
			}

			@Override
			public Map<UUID, String> getRooms() {
				return view.getRooms();
			}

		});
	}

	/**
	 * Starting the model.
	 */
	public void start() {
		model.start();
		view.start();

		// starts the internal IRMIUtils instance too.
		discPnl.start();
		view.addCtrlComponent(discPnl);
	}

	/**
	 * Main function for enter the program.
	 * @param args the args from the cmd line
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					(new ChatAppController()).start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

	}

}
