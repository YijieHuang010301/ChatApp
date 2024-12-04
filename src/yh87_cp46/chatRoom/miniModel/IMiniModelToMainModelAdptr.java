package yh87_cp46.chatRoom.miniModel;

import java.util.UUID;

/**
 * adapter in from mini to main. will be constructed in the mini controller.
 * @author jimmy
 *
 */
public interface IMiniModelToMainModelAdptr {

	/**
	 * Let the main model to know to leave room 
	 * @param roomID THe uuid of the room to leave
	 */
	void leaveChatroom(UUID roomID);

	// Invite

}
