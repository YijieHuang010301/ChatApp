package yh87_cp46.game;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.util.HashSet;
import java.awt.event.ActionEvent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import common.adapter.IComponentFactory;
import common.receiver.INamedReceiver;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JOptionPane;
import java.awt.FlowLayout;

/**
 * 
 * The GUI component of the Rice Questionnaire Game.
 * 
 * @author Cheng Peng
 */
public class GameView extends JPanel {
	/**
	 * The Unique UID.
	 */
	private static final long serialVersionUID = -8572274620427315123L;
	/**
	 * The panel that holds the game info.
	 */
	private final JPanel infoPnl = new JPanel();

	/**
	 * The adapter that enables the view to talk to the model.
	 */
	private IGameView2ModelAdapter adapter;

	/**
	 * The panel to hold the game content.
	 */
	private final JPanel gamePnl = new JPanel();

	/**
	 * The panel to send text message to team members only.
	 */
	private final JPanel sendTextPnl = new JPanel();

	/**
	 * The text sent to only the team members.
	 */
	private final JTextField teamText = new JTextField();

	/**
	 * The button that sends the text in the text field to team members only
	 */
	private final JButton teamTextBtn = new JButton("Send To Team");

	/**
	 * The panel that shows the current members of the team.
	 */
	@SuppressWarnings("unused")
	private final JPanel memberPane = new JPanel();

	/**
	 * The member list of the chat room
	 */
	private JTextArea memberList;
	/**
	 * The text field to display Team A's current score.
	 */
	private final JTextField myScoreText = new JTextField();
	/**
	 * The panel to display scores for both teams.
	 */
	private final JPanel scorePnl = new JPanel();

	/**
	 * The button to have the next question pop up.
	 */
	private final JButton HardQBtn = new JButton("Hard");

	/**
	 * The easy question button.
	 */
	private final JButton EasyQBtn = new JButton("Easy");

	/**
	 * The member set.
	 */
	private HashSet<INamedReceiver> memberSet;

	/**
	 * The text area for showing the result.
	 */
	private final JTextArea answerText = new JTextArea();

	/**
	 * The pane for adding the map.
	 */
	private final JPanel mapPane = new JPanel();

	/**
	 * logger for debug.
	 */
	private ILogger logger = ILoggerControl.getSharedLogger();
	
	
	/**
	 * Constructor for the GameView.
	 * 
	 * @param adapter The adapter that enables the view to talk to the model.
	 * @param memberSet The set that contains current team members.
	 */
	public GameView(IGameView2ModelAdapter adapter, HashSet<INamedReceiver> memberSet) {
		this.adapter = adapter;
		this.memberSet = memberSet;
		initGUI();
	}

	/**
	 * Initialize the GUI component.
	 */
	private void initGUI() {
		setLayout(new BorderLayout(0, 0));
		infoPnl.setToolTipText("Control pane");

		add(infoPnl, BorderLayout.NORTH);
		infoPnl.add(scorePnl);
		scorePnl.setLayout(new GridLayout(1, 4, 0, 0));
		myScoreText.setBorder(new TitledBorder(null, "Your Score", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		gamePnl.setToolTipText("Game pane");
		infoPnl.add(gamePnl);
		gamePnl.setLayout(new GridLayout(2, 2, 0, 0));
		gamePnl.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		EasyQBtn.setToolTipText("Press the button to show the easy question.");
		gamePnl.add(EasyQBtn);
		HardQBtn.setToolTipText("Press the button to show the hard question.");
		gamePnl.add(HardQBtn);
		answerText.setToolTipText("Show the result of previous question.");
		gamePnl.add(answerText);
		answerText.setEditable(false);
		answerText.setRows(2);
		answerText.setColumns(8);
		answerText.setBorder(new TitledBorder(null, "Result", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		HardQBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adapter.showHardQ();
			}
		});
		EasyQBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adapter.showEasyQ();
			}
		});

		add(sendTextPnl, BorderLayout.SOUTH);
		sendTextPnl.setLayout(new GridLayout(0, 2, 0, 0));

		JScrollPane memberListPane = new JScrollPane();
		JPanel memberPnl = new JPanel();
		memberPnl.setToolTipText("The team member pane to show the team members.");
		add(memberPnl, BorderLayout.WEST);
		memberPnl.setLayout(new BorderLayout(0, 0));

		memberListPane.setToolTipText("Members of this room.");
		memberPnl.add(memberListPane);

		memberList = new JTextArea();
		memberList.setColumns(10);
		memberList.setToolTipText("The member list");
		memberList.setEditable(false);
		memberList
				.setBorder(new TitledBorder(null, "Team Members:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		memberListPane.setViewportView(memberList);
		for (INamedReceiver teammate : memberSet) {
			memberList.append(teammate + "\n");
		}
		mapPane.setToolTipText("The pane for adding the map.");

		add(mapPane, BorderLayout.CENTER);
		mapPane.setLayout(new GridLayout(1, 0, 0, 0));
		
		myScoreText.setToolTipText("To show the score of the player");
		myScoreText.setEditable(false);
		myScoreText.setText("0");
		scorePnl.setToolTipText("The pane for showing the score");
		scorePnl.add(myScoreText);
		myScoreText.setColumns(10);
		sendTextPnl.setToolTipText("Send the text to the team");
		teamText.setText("Enter some message...");
		teamText.setToolTipText("Enter text.");
		sendTextPnl.add(teamText);
		teamText.setColumns(20);
		teamTextBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adapter.sendTeamText(teamText.getText());
				teamText.setText("");
			}
		});

		sendTextPnl.add(teamTextBtn);
	}

	/**
	 * Start the view, i.e. make it visible.
	 */
	public void start() {
		setVisible(true);
	}

	/**
	 * Show the question by pop up dialog.
	 * @param question the question string.
	 * @return The player's answer.
	 */
	public boolean showQuestion(String question) {
		return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, question, "", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
	}

	/**
	 * Show the total score.
	 * @param totalScore the score to show.
	 */
	public void updateMyScore(int totalScore) {
		myScoreText.setText(totalScore + "");
	}

	/**
	 * Show the result of the question.
	 * @param result Either correct or incorrect.
	 */
	public void showResult(boolean result) {
		if (result) {
			answerText.setText("Correct");
		} else {
			answerText.setText("Incorrect");
		}
	}

	/**
	 * Build the map component.
	 * @param mapComp The map component.
	 */
	public void buildMap(IComponentFactory mapComp) {
		SwingUtilities.invokeLater(() -> {
			Component map = mapComp.make();
			logger.log(LogLevel.DEBUG, "adding map in game pane");
			mapPane.add(map);
		});

	}
}
