package yh87_cp46.chatRoom.miniModel;

import java.util.HashSet;

import common.receiver.INamedReceiver;

/**
 * @author jimmy
 * The mini model to view adapter.
 */
public interface IMiniModelToViewAdptr {

	/**
	 * show the text in the view.
	 * @param text input text.
	 */
	public void showText(String text);

	/**
	 * Update the roster showed in the view.
	 * @param roster the list of users in a given chatroom.
	 */
	public void updateUserList(HashSet<INamedReceiver> roster);
}
