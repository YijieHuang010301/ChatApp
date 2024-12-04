package yh87_cp46.chatRoom.miniView;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.util.HashSet;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import common.adapter.IComponentFactory;
import common.receiver.INamedReceiver;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;

/**
 * The chat room view
 * @param <TMember> The chat room member type
 */
public class ChatRoomView<TMember> extends JPanel {

	/**
	 * The serial version ID of this JPanel
	 */
	private static final long serialVersionUID = 7461608452907208228L;

	/**
	 * The chat room view to chat room model adapter
	 */
	IMiniViewToModelAdptr miniModelAdaptr;

	/**
	 * The name of the chat room 
	 */
	private String chatRoomName;

	/**
	 * The chat room id
	 */
	private UUID chatRoomID;

	/**
	 * The member list of the chat room
	 */
	private JTextArea memberList;

	/**
	 * The tabbed panel for mini view
	 */
	private JTabbedPane tabbedPane;

	/**
	 * The text display area 
	 */
	private JTextArea textDisplayArea;

	/**
	 * The file chooser
	 */
	private JFileChooser fileChooser;

	/**
	 * The constructor of the ChatRoomView
	 * @param miniModelAdaptr The chat room view to chat room model adapter
	 * @param chatRoomName The name of the chat room 
	 */
	public ChatRoomView(IMiniViewToModelAdptr miniModelAdaptr, String chatRoomName) {
		fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
		this.miniModelAdaptr = miniModelAdaptr;
		this.chatRoomName = chatRoomName;
		initGUI();
	}

	/**
	 * Set the uuid of the chat room 
	 * @param id The uuid
	 */
	public void setUUID(UUID id) {
		chatRoomID = id;
	}

	/**
	 * Initialize the GUI components
	 */
	private void initGUI() {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));

		JScrollPane scrollChatRoom = new JScrollPane();
		scrollChatRoom.setToolTipText("The pane for chat");
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setToolTipText("The pane for the chatroom");

		add(tabbedPane, BorderLayout.CENTER);
		tabbedPane.addTab("Chat Room", scrollChatRoom);

		textDisplayArea = new JTextArea();
		textDisplayArea.setToolTipText("the text area");
		textDisplayArea.setEditable(false);
		scrollChatRoom.setViewportView(textDisplayArea);

		JPanel chatRoomMemberListPane = new JPanel();
		chatRoomMemberListPane.setToolTipText("The list for showing the members in the room");
		add(chatRoomMemberListPane, BorderLayout.WEST);
		chatRoomMemberListPane.setLayout(new BorderLayout(0, 0));

		JScrollPane memberPane = new JScrollPane();
		memberPane.setToolTipText("Members of this room.");
		chatRoomMemberListPane.add(memberPane, BorderLayout.CENTER);

		memberList = new JTextArea();
		memberList.setToolTipText("The member list");
		memberList.setEditable(false);
		memberList.setBorder(new TitledBorder(null, "Members:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		memberPane.setViewportView(memberList);

		JButton exitRoomBtn = new JButton("Exit Room");
		exitRoomBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				miniModelAdaptr.exitChatRoom();
			}
		});
		exitRoomBtn.setToolTipText("Click to exit this room.");
		chatRoomMemberListPane.add(exitRoomBtn, BorderLayout.SOUTH);

		JSplitPane inputPane = new JSplitPane();
		inputPane.setToolTipText("Input the message to send.");
		add(inputPane, BorderLayout.SOUTH);

		JScrollPane textInputPane = new JScrollPane();
		textInputPane.setToolTipText("pane for input text");
		inputPane.setLeftComponent(textInputPane);

		JTextArea textInputField = new JTextArea();
		textInputField.setToolTipText("input text to send.");
		textInputPane.setViewportView(textInputField);

		JPanel btnsPane = new JPanel();
		btnsPane.setToolTipText("Controll tabs");
		inputPane.setRightComponent(btnsPane);
		inputPane.setResizeWeight(1);
		btnsPane.setLayout(new GridLayout(0, 4, 0, 0));

		JButton sendTextBtn = new JButton("Send Text");
		sendTextBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				miniModelAdaptr.sendText(textInputField.getText());
				textInputField.setText("");
			}
		});
		sendTextBtn.setToolTipText("Click to send text.");
		btnsPane.add(sendTextBtn);

		JButton sendCmdBtn = new JButton("Send Tab");
		sendCmdBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				miniModelAdaptr.sendNewPage();
			}
		});
		sendCmdBtn.setToolTipText("Click to send file.");
		btnsPane.add(sendCmdBtn);

		JButton MapButton = new JButton("Send Map");
		MapButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				miniModelAdaptr.sendMap();
			}
		});
		btnsPane.add(MapButton);

		JButton btnGame = new JButton("Make Game");
		btnGame.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				miniModelAdaptr.sendGameToAll();
			}
		});
		btnsPane.add(btnGame);
	}

	/**
	 * Start the view, i.e. make it visible.
	 */
	public void start() {
		setVisible(true);
	}

	/**
	 * Display text to the textDisplayArea
	 * @param text The text to display
	 */
	public void showText(String text) {
		textDisplayArea.append(text + "\n");
	}

	/**
	 * Add the user to the list.
	 * @param roster The new set of users.
	 */
	public void updateUserList(HashSet<INamedReceiver> roster) {
		SwingUtilities.invokeLater(() -> {
			memberList.setText("");
			for (INamedReceiver user : roster) {
				memberList.append(user.getName() + "\n");
			}
		});
	}

	/**
	 * Add a new chatroom tab to the chatrooms
	 * 
	 * @param roomName	The new Chatroom's name
	 * @param newTab Add new tab to the room
	 */
	public void addChatroomTab(String roomName, Component newTab) {
		tabbedPane.addTab(roomName, newTab);
	}

	/**
	 * Add a new chatroom scroll tab to the chatrooms
	 * 
	 * @param roomName	The new Chatroom's name
	 * @param newScrollTab Add new tab to the room
	 */
	public void addChatroomScrollTab(String roomName, Component newScrollTab) {
		JScrollPane scrollTab = new JScrollPane();
		scrollTab.setViewportView(newScrollTab);
		tabbedPane.addTab(roomName, scrollTab);
	}

	/**
	 * @return The chat room name 
	 */
	public String getChatRoomName() {
		return chatRoomName;
	}

	/**
	 * @return THe chat room ID
	 */
	public UUID getChatRoomID() {
		return chatRoomID;
	}

	/**
	 * Build fixed component 
	 * @param fac Component factory
	 * @param name The name of the fixed component 
	 */
	public void buildFixedComponent(IComponentFactory fac, String name) {
		SwingUtilities.invokeLater(() -> {
			Component component = fac.make();
			tabbedPane.addTab(name, component);
			tabbedPane.setSelectedComponent(component);
		});
	}

	/**
	 * Build scrolling component 
	 * @param fac Component factory
	 * @param name The name of the scrolling component 
	 */
	public void buildScrollingComponent(IComponentFactory fac, String name) {
		SwingUtilities.invokeLater(() -> {
			Component component = fac.make();
			textDisplayArea.add(component);
			showText(name);
		});
	}

	/**
	 * Get the file
	 * @return The file that the user choose
	 */
	public File getFile() {
		fileChooser.setDialogTitle("Please Choose a File");
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			return selectedFile;
		}
		return null;
	}

	/**
	 * Save the file 
	 * @param defaultName The name of the file
	 * @return The file saved
	 */
	public File saveFile(String defaultName) {
		fileChooser.setDialogTitle("Please Save a File");
		fileChooser.setCurrentDirectory(new File(defaultName));
		int userSelection = fileChooser.showSaveDialog(this);
		if (userSelection == JFileChooser.APPROVE_OPTION) {
			File fileToSave = fileChooser.getSelectedFile();
			return fileToSave;
		}
		return null;
	}

}
