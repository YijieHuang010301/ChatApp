package yh87_cp46.game;

import java.util.HashSet;
import java.util.UUID;

import javax.swing.JComponent;

import common.adapter.ICmd2ModelAdapter;
import common.adapter.IComponentFactory;
import common.receiver.INamedReceiver;
import provided.logger.LogLevel;

/**
 * 
 * The controller for the game.
 * 
 * @author jimmy
 * @author Cheng Peng
 *
 */
public class GameController {
	/**
	 * The game model
	 */
	private GameModel model;

	/**
	 * The game view.
	 */
	private GameView view;

	/**
	 * The adapter for command
	 */
	private ICmd2ModelAdapter cmd2ModelApdt;

	/**
	 *  The team string.
	 */
	private String teamString;

	/**
	 * The game server.
	 */
	@SuppressWarnings("unused")
	private INamedReceiver server;

	/**
	 * constructor of the game controller
	 * @param cmd2ModelApdt The cmd adapter.
	 * @param team The team of the game.
	 * @param server The game host.
	 * @param player The name of the player.
	 * @param gameID The game id.
	 * @param memberList The team member list.
	 */
	public GameController(ICmd2ModelAdapter cmd2ModelApdt, boolean team, INamedReceiver server, INamedReceiver player,
			UUID gameID, HashSet<INamedReceiver> memberList) {
		this.cmd2ModelApdt = cmd2ModelApdt;
		if (team) {
			teamString = "Team A";
		} else {
			teamString = "Team B";
		}

		cmd2ModelApdt.getLogger().log(LogLevel.DEBUG, player.getName());

		this.server = server;
		model = new GameModel(new IGameModel2ViewAdapter() {

			@Override
			public boolean showQuestion(String question) {
				return view.showQuestion(question);
			}

			@Override
			public void updateMyScore(int myScore) {
				view.updateMyScore(myScore);
			}

			@Override
			public void showResult(boolean result) {
				view.showResult(result);

			}

			@Override
			public void buildMap(IComponentFactory mapCompFac) {
				view.buildMap(mapCompFac);

			}

		}, server, team, player, gameID, cmd2ModelApdt, memberList.size());
		view = new GameView(new IGameView2ModelAdapter() {

			@Override
			public void showHardQ() {
				model.showHardQ();
			}

			@Override
			public void showEasyQ() {
				model.showEasyQ();
			}

			@Override
			public void sendTeamText(String string) {
				model.sendTeamText(string);
			}
		}, memberList);
	}

	/**
	 * start the controller.
	 */
	public void start() {
		view.start();
		model.start();
		this.cmd2ModelApdt.buildFixedComponent(new IComponentFactory() {
			@Override
			public JComponent make() {
				return view;
			}
		}, teamString);
	}
}
