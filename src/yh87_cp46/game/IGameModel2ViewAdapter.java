package yh87_cp46.game;

import common.adapter.IComponentFactory;

/**
 * 
 * The adapter that enables the model to talk to the view.
 * 
 * @author Yijie Huang
 * @author Cheng Peng
 *
 */
public interface IGameModel2ViewAdapter {

	/**
	 * Show the questions.
	 * @param question the string of the question.
	 * @return The answer of the question.
	 */
	boolean showQuestion(String question);

	/**
	 * Update the score of one player.
	 * @param myScore The score.
	 */
	void updateMyScore(int myScore);

	/**
	 * Show the result of the answer.
	 * @param result The result to show.
	 */
	void showResult(boolean result);

	/**
	 * Build the map component.
	 * @param mapComp the map component.
	 */
	void buildMap(IComponentFactory mapComp);

}
