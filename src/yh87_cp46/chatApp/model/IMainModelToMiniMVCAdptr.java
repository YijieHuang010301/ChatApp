package yh87_cp46.chatApp.model;

import java.util.UUID;

/**
 * @author jimmy
 * Main model to miniMVC adapter. Will be constructed in the main controller after the mini
 * controller is set.
 */
public interface IMainModelToMiniMVCAdptr {

	/**
	 * Get uuid from the minicontroller after the mini controller is set.
	 * @return the uuid of the chatroom.
	 */
	UUID getUUID();

	/**
	 * Exit the chat room from main to mini when quitting the app
	 */
	void exitChatRoom();

}
