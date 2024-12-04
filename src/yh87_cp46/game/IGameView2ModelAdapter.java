package yh87_cp46.game;

/**
 * 
 * The adapter that enables the view to talk to the model.
 * @author Yijie Huang
 * @author Cheng Peng
 *
 */
public interface IGameView2ModelAdapter {

	/**
	 * Show the hard question/
	 */
	void showHardQ();

	/**
	 * Show the easy question/
	 */
	void showEasyQ();

	/**
	 * Send the team message to only team members.
	 * @param msg the message string.
	 */
	void sendTeamText(String msg);

}
