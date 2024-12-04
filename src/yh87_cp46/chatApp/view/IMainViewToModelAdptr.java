package yh87_cp46.chatApp.view;

/**
 * @author jimmy
 * Adapter for the view to model.
 * @param <H> The remoteHost in the connected drop list.
 */
public interface IMainViewToModelAdptr<H> {

	/**
	 * Login to the chatapp with a name.
	 * @param inputName the input name for the user.
	 */
	void login(String inputName);

	/**
	 * Create a new chatroom with name.
	 * @param chatRoomName input string for creating chatroom.
	 */
	void createChatroom(String chatRoomName);

	/**
	 * Invite a host to join the chatroom.
	 * 
	 * @param remoteHost  the host we want to invite.
	 */
	void invite(H remoteHost);

	/**
	 * connect to the host with ip and bound name.
	 * @param RemoteIP the remote ip.
	 * @param BoundName the bound name.
	 */
	void connect(String RemoteIP, String BoundName);

	/**
	 * quit the chatApp.
	 */
	void quit();

	/**
	 * Request to join a game room.
	 * 
	 * @param remoteHost The remote game server.
	 */
	void request(H remoteHost);
}
