package yh87_cp46.chatRoom.miniController;

import java.io.File;
import java.util.HashSet;
import java.util.UUID;

import yh87_cp46.chatApp.model.ChatAppModel;
import yh87_cp46.chatApp.view.ChatAppView;
import yh87_cp46.chatRoom.miniModel.ChatRoomModel;
import yh87_cp46.chatRoom.miniModel.IMiniModelToMainModelAdptr;
import yh87_cp46.chatRoom.miniModel.IMiniModelToViewAdptr;
import yh87_cp46.chatRoom.miniView.ChatRoomView;
import yh87_cp46.chatRoom.miniView.IMiniViewToModelAdptr;
import common.adapter.ICmd2ModelAdapter;
import common.adapter.IComponentFactory;
import common.connector.INamedConnector;
import common.receiver.INamedReceiver;
import common.receiver.messages.IReceiverMsg;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;
import provided.mixedData.MixedDataDictionary;
import provided.mixedData.MixedDataKey;
import provided.pubsubsync.IPubSubSyncManager;

//TODO: add the unimplemented functions in cmd adapter.

/**
 * The controller for the chat room
 * 
 * @author Jimmy and PC
 */
public class ChatRoomController {

	/**
	 *  The system logger
	 */
	private ILogger sysLogger = ILoggerControl.getSharedLogger();

	/**
	 * The model in use
	 */
	private ChatRoomModel model;

	/**
	 * The view in use
	 */
	private ChatRoomView<INamedConnector> view;

	/**
	 * Constructor for the chat room controller.
	 * @param chatRoomName The name of the chat room
	 * @param pssManager The PubSubSyncManager of the chat room
	 * @param connectorStub The stub for the current member of the room 
	 * @param chatRoomID The chat room uuid
	 * @param mainModel The main model that the chat room will be on
	 * @param mainView The main view that the chat room will be displayed
	 */
	public ChatRoomController(IPubSubSyncManager pssManager, INamedConnector connectorStub, String chatRoomName,
			UUID chatRoomID, ChatAppModel mainModel, ChatAppView<INamedConnector> mainView) {
		sysLogger.setLogLevel(LogLevel.DEBUG);
		view = new ChatRoomView<INamedConnector>(new IMiniViewToModelAdptr() {

			@Override
			public void exitChatRoom() {
				model.leaveChatroom();
			}

			@Override
			public void sendText(String text) {
				model.sendTextMsg(text);
			}

			@Override
			public void sendNewPage() {
				model.sendPage();
			}

			@Override
			public void sendMap() {
				model.sendMap();
			}

			@Override
			public void sendGameToAll() {
				model.sendGameToAll();
			}

		}, chatRoomName);
		model = new ChatRoomModel(pssManager, connectorStub, chatRoomName, chatRoomID, sysLogger,
				new IMiniModelToViewAdptr() {
					@Override
					public void showText(String text) {
						view.showText(text);
					}

					@Override
					public void updateUserList(HashSet<INamedReceiver> roster) {
						view.updateUserList(roster);
					}

				},

				new IMiniModelToMainModelAdptr() {

					@Override
					public void leaveChatroom(UUID roomID) {
						mainModel.leaveChatroom(roomID);

					}

				},

				new ICmd2ModelAdapter() {
					MixedDataDictionary mixedDD = new MixedDataDictionary();

					@Override
					public void buildFixedComponent(IComponentFactory fac, String name) {
						sysLogger.log(LogLevel.INFO, "Adding fixed component to view...");
						view.buildFixedComponent(fac, name);
					}

					@Override
					public void buildScrollingComponent(IComponentFactory fac, String name) {
						sysLogger.log(LogLevel.INFO, "Adding Scrolling component to view...");
						view.buildScrollingComponent(fac, name);

					}

					@Override
					public <T> T put(MixedDataKey<T> key, T value) {
						return mixedDD.put(key, value);
					}

					@Override
					public <T> T get(MixedDataKey<T> key) {
						return mixedDD.get(key);
					}

					@Override
					public boolean containsKey(MixedDataKey<?> key) {
						return mixedDD.containsKey(key);
					}

					@Override
					public String getRoomName() {
						return model.getRoomName();
					}

					@Override
					public String getInstanceName() {
						// TODO check what name we want.
						return model.getInstanceName();

					}

					@Override
					public String getReceiverName() {
						// TODO check what name we want. also the user name
						return model.getReceiver().getName();
					}

					@Override
					public <T extends IReceiverMsg> void broadcast(T msg) {
						model.broadcast(msg);
					}

					@Override
					public <T extends IReceiverMsg> void send(T data, INamedReceiver recv) {
						model.sendDataPacketTo(data, recv);

					}

					@Override
					public File getFile() {
						return view.getFile();
					}

					@Override
					public File saveFile(String defaultName) {
						return view.saveFile(defaultName);
					}

					@Override
					public ILogger getLogger() {
						return model.getLogger();
					}

					@Override
					public String getMapKey() {
						return "AIzaSyAHhtSinAIsD1dCq8Jzgh0wA-H3AY-9WWo";
					}
				});

		//set the either generated uuid or join uuid after initiate the model.
		view.setUUID(model.getUUID());
		// Add the mini view to main view once everything in mini mvc is set
		mainView.addChatRoomView(view);
	}

	/**
	 * start the controller.
	 */
	public void start() {
		view.start();
		model.start();
	}

	/**
	 * Get the uuid of the chat room
	 * @return The uuid of the chat room
	 */
	public UUID getUUID() {
		return model.getUUID();
	}

	/**
	 * Exit the chat room 
	 */
	public void exitChatRoom() {
		model.leaveChatroom();

	}
}
