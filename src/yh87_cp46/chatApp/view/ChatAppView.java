package yh87_cp46.chatApp.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import yh87_cp46.chatRoom.miniView.ChatRoomView;
import provided.logger.ILogger;
import provided.logger.ILoggerControl;
import provided.logger.LogLevel;

import java.awt.Font;
import java.awt.Component;
import javax.swing.JComboBox;
import java.awt.FlowLayout;
import java.awt.Color;

/**
 * The chat app main view
 * @param <TMember> The chat room member type
 * 
 */
public class ChatAppView<TMember> extends JFrame {
	/**
	 * The serial version ID of this JFrame
	 */
	private static final long serialVersionUID = 1086054677642236593L;

	/**
	 * The logger for this view
	 */
	private ILogger logger = ILoggerControl.getSharedLogger();

	/**
	 * A view to model adaptor
	 */
	private IMainViewToModelAdptr<TMember> viewToModelAdptr;

	/**
	 * A map of chat room uuid to chat room view use the select which chat room view to display
	 */
	private Map<UUID, Component> chatRoomViewMap = new HashMap<>();

	/**
	 * The content panel
	 */
	private JPanel contentPane;

	/**
	 * Drop down for hosts
	 */
	private JComboBox<TMember> hostDropdown;

	/**
	 * Discovery panel 
	 */
	private JPanel discoveryPane;

	/**
	 * The chat room tab panel
	 */
	private JTabbedPane chatRoomTabPane = new JTabbedPane(JTabbedPane.TOP);

	/**
	 * The chat room info panel
	 */
	private JPanel chatRoominfoPane = new JPanel();

	/**
	 * The text area for chat room info
	 */
	private JTextArea infoTextArea = new JTextArea();

	/**
	 * Create the frame.
	 * @param adptr A view to model adapter
	 */
	public ChatAppView(IMainViewToModelAdptr<TMember> adptr) {
		this.viewToModelAdptr = adptr;
		this.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				viewToModelAdptr.quit();
			}
		});
		initGUI();
	}

	/**
	 * Initialize the GUI components
	 */
	private void initGUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 600);
		this.setPreferredSize(new Dimension(1300, 1000));
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel controlPane = new JPanel();
		controlPane.setToolTipText("control pane");
		contentPane.add(controlPane, BorderLayout.NORTH);
		controlPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel startPane = new JPanel();
		startPane.setToolTipText("log/exit pane");
		startPane.setBorder(new TitledBorder(null, "App Startup", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		controlPane.add(startPane);
		startPane.setLayout(new GridLayout(2, 0, 0, 0));

		JPanel userNamePane = new JPanel();
		userNamePane.setToolTipText("Please enter the user name");
		userNamePane.setBorder(new TitledBorder(null, "Username:", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		startPane.add(userNamePane);
		userNamePane.setLayout(new GridLayout(0, 1, 0, 0));

		JTextField userNameInput = new JTextField();
		userNameInput.setToolTipText("Enter your user name to start.");
		userNamePane.add(userNameInput);
		userNameInput.setColumns(10);

		JPanel startBtnPane = new JPanel();
		startBtnPane.setToolTipText("start/exit pane");
		startPane.add(startBtnPane);
		startBtnPane.setLayout(new GridLayout(0, 2, 0, 0));

		//Log in
		JButton startBtn = new JButton("LOG IN");
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewToModelAdptr.login(userNameInput.getText());
			}
		});
		startBtn.setMinimumSize(new Dimension(110, 15));
		startBtn.setPreferredSize(new Dimension(110, 15));
		startBtn.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		startBtn.setToolTipText("Click to log in and start the app.");
		startBtnPane.add(startBtn);

		//Quit
		JButton quitBtn = new JButton("EXIT ALL AND QUIT");
		quitBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewToModelAdptr.quit();
			}
		});

		quitBtn.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		quitBtn.setMinimumSize(new Dimension(110, 15));
		quitBtn.setPreferredSize(new Dimension(110, 15));
		quitBtn.setToolTipText("Click to exit all activities and quit the app.");
		startBtnPane.add(quitBtn);
		chatRoomTabPane.setToolTipText("The tab pane for adding the chatrooms");

		contentPane.add(chatRoomTabPane, BorderLayout.CENTER);
		chatRoominfoPane.setToolTipText("adding chatrooms in the pane");
		chatRoomTabPane.addTab("Home", chatRoominfoPane);
		chatRoominfoPane.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setToolTipText("The pane for adding chatrooms");
		chatRoominfoPane.add(scrollPane, BorderLayout.CENTER);
		infoTextArea.setBackground(new Color(175, 238, 238));
		infoTextArea.setEditable(false);
		infoTextArea.setText("\t\t\t\t\t             Welcome to Rice Questionnaire!! \n");
		infoTextArea.append("To login: \n");
		infoTextArea.append("    1. Enter a user name under the Username text field \n");
		infoTextArea.append("    2. Click LOG IN button \n");
		infoTextArea.append("To make a room: \n");
		infoTextArea.append("    1. Enter a friendly chat room name in the Make Chat Room text field \n");
		infoTextArea.append("    2. Click Make Game button (you must be logged in to make a game room) \n");
		infoTextArea.append("To connect with another chat app user via discovery server: \n");
		infoTextArea.append(
				"    1. Enter the discovery server category that the other user is on in the Category text field \n");
		infoTextArea.append("    2. Click Connect button to connect to the discovery server \n");
		infoTextArea.append("    3. Select the desired user from the Registred EndPoints \n");
		infoTextArea.append("    4. Click Get Selected Endpoint button to connect with the other user \n");
		infoTextArea.append("To connect with another chat app user via host address and bound name: \n");
		infoTextArea.append("    1. Enter the other user's IP address under the Host text field \n");
		infoTextArea.append("    2. Enter the other user's bound name under the Bound Name text field \n");
		infoTextArea.append("    3. Click Connect button to connect with the other user \n");
		infoTextArea.append("To invite a connected host to a chat room: \n");
		infoTextArea.append("    1. Click on the tab for the desired chat room \n");
		infoTextArea.append("    2. Select the desired connected user under the Connected Hosts dropdown list \n");
		infoTextArea.append(
				"    3. Click Invite button to invite the connected user to the room (this will promte a window to the connected user where they will select if they accept the invitation or not) \n");
		infoTextArea.append("To send a text message to a chat room: \n");
		infoTextArea.append("    1. Click on the tab for the desired chat room \n");
		infoTextArea.append(
				"    2. Enter your message in the chat room message box located at the bottom on the chat room GUI \n");
		infoTextArea.append("    3. Click Send Text button to send your message \n");
		infoTextArea.append("To send a new page message to a chat room: \n");
		infoTextArea.append("    1. Click on the tab for the desired chat room \n");
		infoTextArea.append(
				"    2. Click Send New Page button to send a new note page to the chat room (now you can click on the Notes tab to write any text notes you want) \n");
		infoTextArea.append("To exit a chat room: \n");
		infoTextArea.append("    1. Click on the tab for the desired chat room \n");
		infoTextArea.append("    2. Click on the Exit Room button \n");
		infoTextArea.append("To exit the app: \n");
		infoTextArea.append(
				"    1. Click on the EXIT ALL AND QUIT button (this will result in you getting removed from all the chat rooms you are in and removed from the connected host list of all the other chat app users that you have connected with) \n");
		infoTextArea.setToolTipText("Info pane for showing the infomations");
		scrollPane.setViewportView(infoTextArea);

		JPanel makeCRPane = new JPanel();
		makeCRPane.setToolTipText("ChatRoom name pane");
		makeCRPane.setBorder(
				new TitledBorder(null, "Make Game Lobby", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		controlPane.add(makeCRPane);
		makeCRPane.setLayout(new GridLayout(2, 1, 0, 0));

		JTextField chatRoomInput = new JTextField();
		chatRoomInput.setToolTipText("Enter the name of the chat room you want to make.");
		makeCRPane.add(chatRoomInput);
		chatRoomInput.setColumns(10);

		//make a chatroom
		JButton makeRoomBtn = new JButton("Make Game");
		makeRoomBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewToModelAdptr.createChatroom(chatRoomInput.getText());
			}
		});

		makeRoomBtn.setToolTipText("Click to make the chat room.");
		makeCRPane.add(makeRoomBtn);

		JPanel remoteHostsPane = new JPanel();
		remoteHostsPane.setToolTipText("connect to remote pane");
		controlPane.add(remoteHostsPane);
		remoteHostsPane.setLayout(new GridLayout(0, 2, 0, 0));

		discoveryPane = new JPanel();
		discoveryPane.setToolTipText("pane for discovery server");
		remoteHostsPane.add(discoveryPane);

		JPanel connectPane = new JPanel();
		connectPane.setToolTipText("pane for connect to remote host");
		remoteHostsPane.add(connectPane);
		connectPane.setLayout(new GridLayout(2, 0, 0, 0));

		JPanel ipPane = new JPanel();
		ipPane.setToolTipText("pane for the info of remote host");
		ipPane.setBorder(new TitledBorder(null, "Connect To...", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		connectPane.add(ipPane);
		ipPane.setLayout(new GridLayout(2, 1, 0, 0));

		JTextField ipInput = new JTextField();
		ipInput.setBorder(new TitledBorder(null, "Host", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		ipInput.setToolTipText("Enter the IP address of the remote host you want to connect.");
		ipPane.add(ipInput);
		ipInput.setColumns(10);

		JTextField boundName = new JTextField();
		boundName.setToolTipText("The bound name for the stub");
		boundName.setBorder(new TitledBorder(null, "Bound Name", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		ipPane.add(boundName);
		boundName.setColumns(10);

		// Connect to the remote host manually.
		JButton connectBtn = new JButton("Connect");
		connectBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewToModelAdptr.connect(ipInput.getText(), boundName.getText());
			}
		});

		connectBtn.setToolTipText("Click to connect to remote host.");
		connectBtn.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		ipPane.add(connectBtn);

		JPanel connectHostPane = new JPanel();
		connectHostPane.setToolTipText("pane to invite the remote host");
		connectHostPane.setBorder(
				new TitledBorder(null, "Connected Hosts", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		connectPane.add(connectHostPane);
		connectHostPane.setLayout(new GridLayout(2, 0, 0, 0));

		hostDropdown = new JComboBox<TMember>();
		hostDropdown.setToolTipText("Select from connected hosts.");
		connectHostPane.add(hostDropdown);

		JPanel connectHostBtnPane = new JPanel();
		connectHostBtnPane.setToolTipText("pane for inivte the host");
		connectHostPane.add(connectHostBtnPane);
		connectHostBtnPane.setLayout(new GridLayout(1, 0, 0, 0));

		//Invite
		JButton inviteBtn = new JButton("Invite");
		inviteBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewToModelAdptr.invite(hostDropdown.getItemAt(hostDropdown.getSelectedIndex()));
			}
		});
		inviteBtn.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		inviteBtn.setToolTipText("Click to invite selected host.");
		connectHostBtnPane.add(inviteBtn);

		JButton requestBtn = new JButton("Request");
		requestBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				viewToModelAdptr.request(hostDropdown.getItemAt(hostDropdown.getSelectedIndex()));
			}
		});
		requestBtn.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		connectHostBtnPane.add(requestBtn);
	}

	/**
	 * Start the view, i.e. make it visible.
	 */
	public void start() {
		setVisible(true);
	}

	/**
	 * Append the given string to the textArea to display
	 * @param s the string to display
	 */
	public void append(String s) {
		infoTextArea.append(s);
		infoTextArea.setCaretPosition(infoTextArea.getText().length());
	}

	/**
	 * Ask the user whether they want to be invited to the new chatroom.
	 * @param msg	The message to display.
	 * @param rooms The rooms.
	 * @return True if they confirmed, false otherwise.
	 */
	public String inquiry(String msg, Map<UUID, String> rooms) {

		//return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(null, msg, "", JOptionPane.YES_NO_OPTION,
		//JOptionPane.QUESTION_MESSAGE);

		//		JPanel panel = new JPanel();
		//        panel.setBackground(new Color(0, 0, 0));
		//        panel.setSize(new Dimension(250, 32));
		//        panel.setLayout(null);
		//
		//        JLabel label = new JLabel("It's your choice! :)");
		//        label.setForeground(new Color(255, 255, 0));
		//        label.setHorizontalAlignment(SwingConstants.CENTER);
		//        label.setFont(new Font("Arial", Font.BOLD, 11));
		//        label.setBounds(0, 0, 250, 32);
		//        panel.add(label);
		//        UIManager.put("OptionPane.minimumSize",new Dimension(270, 120));
		String[] options = new String[rooms.size()];
		int index = 0;
		for (Entry<UUID, String> entry : rooms.entrySet()) {
			logger.log(LogLevel.INFO, entry.getKey() + " id");
			//logger.log(LogLevel.INFO, ((ChatRoomView<TMember>) entry.getValue()).getChatRoomName() + " name");
			options[index] = entry.getValue();
			index++;
		}
		String choice = (String) JOptionPane.showInputDialog(null, msg, "", JOptionPane.PLAIN_MESSAGE, null, options,
				options[0]);
		logger.log(LogLevel.INFO, "bugs maybe");
		if ((choice != null) && (choice.length() > 0)) {
			logger.log(LogLevel.INFO, choice);
			return choice;
		} else {
			return "Error";
		}
	}

	/**
	 * Add the given component to the control panel,  then revalidating and packing the frame.
	 * @param comp The component to add..
	 */
	public void addCtrlComponent(JComponent comp) {
		discoveryPane.add(comp); // Add the component to the control panel
		validate(); // re-runs the frame's layout manager to account for the newly added component 
		pack(); // resizes the frame and panels to make sure the newly added component is visible.  Note that this may adversely affect empty text displays without a preferred size setting.
	}

	/**
	 * update the connected user.
	 * @param connectors the connected user set.
	 */
	public void updateConnectorList(Set<TMember> connectors) {
		this.hostDropdown.removeAllItems();
		for (TMember each : connectors) {
			this.hostDropdown.addItem(each);
		}
	}

	/**
	 * Add a chat room view in the JTabbedPane. 
	 * @param chatRoomView the chat room view to add.
	 */
	public void addChatRoomView(ChatRoomView<TMember> chatRoomView) {
		chatRoomViewMap.put(chatRoomView.getChatRoomID(), chatRoomView);
		chatRoomTabPane.addTab(chatRoomView.getChatRoomName(), chatRoomView);
		chatRoomTabPane.setSelectedComponent(chatRoomView);
	}

	/**
	 * Remove a chat room view in the JTabbedPane.
	 * @param chatRoomID the UUID of the chat room to remove.
	 */
	public void removeChatRoomView(UUID chatRoomID) {
		chatRoomTabPane.remove(chatRoomViewMap.get(chatRoomID));
		chatRoomViewMap.remove(chatRoomID);
	}

	/**
	 * Select a chat room view that the user already joined to display.
	 * @param chatRoomID the UUID of the chat room.
	 */
	public void seletChatRoom(UUID chatRoomID) {
		chatRoomTabPane.setSelectedComponent(chatRoomViewMap.get(chatRoomID));
	}

	/**
	 * Return the map of rooms.
	 * @return the map of rooms.
	 */
	@SuppressWarnings("unchecked")
	public Map<UUID, String> getRooms() {
		Map<UUID, String> rooms = new HashMap<>();
		for (Entry<UUID, Component> entry : chatRoomViewMap.entrySet()) {
			rooms.put(entry.getKey(), ((ChatRoomView<TMember>) entry.getValue()).getChatRoomName());
			logger.log(LogLevel.INFO, entry.getKey() + " id");
			logger.log(LogLevel.INFO, ((ChatRoomView<TMember>) entry.getValue()).getChatRoomName() + " name");
		}
		return rooms;
	}
}
