package yh87_cp46.game;

import java.util.List;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;

import javax.swing.JComponent;

import common.adapter.ICmd2ModelAdapter;
import common.receiver.INamedReceiver;
import common.receiver.ReceiverDataPacket;
import provided.logger.ILogger;
import provided.logger.LogLevel;
import provided.mixedData.IMixedDataDictionary;
import provided.owlMaps.components.marker.IMarker;
import provided.owlMaps.components.marker.IMarkerOptions;
import provided.owlMaps.components.shapes.IPolyline;
import provided.owlMaps.components.shapes.IPolylineOptions;
import provided.owlMaps.control.IOwlMapControl;
import provided.owlMaps.control.MapLengthUnits;
import provided.owlMaps.general.IIconSequenceOptions;
import provided.owlMaps.general.ILatLng;
import provided.owlMaps.general.IPath;
import provided.owlMaps.general.ISymbolOptions;
import provided.owlMaps.general.SymbolPathConstants;
import provided.owlMaps.map.IMapOptions;
import provided.owlMaps.map.IOwlMap;
import provided.owlMaps.map.MapTypeId;
import provided.owlMaps.mouse.MapMouseEventType;
import provided.owlMaps.utils.IOwlMapUtils;
import yh87_cp46.game.message.TeamTextMsg;
import yh87_cp46.game.message.UpdateScoreMsg;

/**
 * The model for the game.
 * 
 * @author Cheng Peng
 *
 */
public class GameModel {

	/**
	 * The adapter that talks to the view.
	 */
	private IGameModel2ViewAdapter adapter;

	/**
	 * The server of the game to send update msg to.
	 */
	private INamedReceiver server;

	/**
	 * The team.
	 */
	private boolean team;

	/**
	 * A map that contains a set of easy questions.
	 */
	private List<String[]> easyQuestions = new ArrayList<>();

	/**
	 * A map that contains a set of hard questions.
	 */
	private List<String[]> hardQuestions = new ArrayList<>();

	/**
	 * The score of the player.
	 */
	private int myScore = 0;

	/**
	 * The namedReceiver of the player.
	 */
	private INamedReceiver player;

	/**
	 * The game's identifying ID.
	 */
	private UUID gameID;

	/**
	* Latch to prevent map operations before map is ready.  Not really used here but good general practice.
	*/
	private CountDownLatch mapReadyCountDownLatch;

	/**
	* The Google Maps API key to use.   Can hard-code it here if not available through the ICmd2ModelAdapter.
	  	*/
	private String googleMapsApiKey;

	/**
	* The map control object.  Not available until the onMapLoadFn runs.
	*/
	private IOwlMapControl owlMapControl;

	/**
	* The map object.  Not available until the onMapLoadFn runs.
	*/
	private IOwlMap owlMap;

	/**
	* The map utilities object.  Not available until the onMapLoadFn runs.
	*/
	@SuppressWarnings("unused")
	private IOwlMapUtils owlMapUtils;

	/**
	 * The cmd to model adapter.
	 */
	private ICmd2ModelAdapter cmdAdpter;

	/**
	 * A thread pool that helps with 
	 */
	private ExecutorService threadPool = Executors.newCachedThreadPool();

	/**
	 * The list of predetermined location.
	 */
	private List<ILatLng> locations = new ArrayList<>();

	//	/**
	//	 * record current location of the player. This is an index into the "locations" list.
	//	 */
	//	private int curPos = 0;

	/**
	 * The number of players in your team.
	 */
	private int teamSize = 0;

	/**
	 * Constructor for the GameModel.
	 * 
	 * @param adapter The model to view adapter.
	 * @param server  The host of the game.
	 * @param team 	  The team.
	 * @param player  The name of the player.
	 * @param gameID  The game ID.
	 * @param cmd2ModelApdt The command adapter.
	 * @param teamSize The number of players in your team.
	 */
	public GameModel(IGameModel2ViewAdapter adapter, INamedReceiver server, boolean team, INamedReceiver player,
			UUID gameID, ICmd2ModelAdapter cmd2ModelApdt, int teamSize) {
		this.adapter = adapter;
		this.server = server;
		this.team = team;
		this.player = player;
		this.gameID = gameID;
		this.cmdAdpter = cmd2ModelApdt;
		this.teamSize = teamSize;
	}

	/**
	 * Start the model.
	 */
	public void start() {
		initEasyQ();
		initHardQ();
		initMap();
		initLoc();
	}

	/**
	 * initiate the map in the center.
	 */
	public void initMap() {
		// logger that displays messages on both the view and console
		ILogger logger = cmdAdpter.getLogger();
		try {
			logger.log(LogLevel.INFO, "Starting map creation...");
			mapReadyCountDownLatch = new CountDownLatch(1);
			googleMapsApiKey = cmdAdpter.getMapKey();
			IMixedDataDictionary mapOptionsDict = IMapOptions.makeDefault();
			Supplier<JComponent> mapFactory = IOwlMapControl.makeMapFactory(this.googleMapsApiKey, mapOptionsDict,
					(owlMapControl) -> {
						this.owlMapControl = owlMapControl;
						this.owlMap = owlMapControl.getMap();
						this.owlMapUtils = owlMapControl.getMapUtils();
						this.owlMap.setMapMouseEvent(MapMouseEventType.RIGHT_CLICK, (mouseEvent) -> {
							logger.log(LogLevel.DEBUG, "[MapMouseEvent: " + MapMouseEventType.RIGHT_CLICK.getName()
									+ "]" + " mouseEvent = " + mouseEvent);
							logger.log(LogLevel.INFO, "ILatLng = " + mouseEvent.getLatLng());
						});
						this.owlMap.setMapTypeId(MapTypeId.TERRAIN);
						this.owlMap.setZoom(15);
						logger.log(LogLevel.INFO, "[onMapLoad] completed, releasing countdown latch!");
						mapReadyCountDownLatch.countDown();
					}, logger, MapLengthUnits.FEET, false);
			adapter.buildMap(mapFactory::get);
		} catch (Exception e) {
			logger.log(LogLevel.ERROR, "Exception = " + e);
			e.printStackTrace();
		}
	}

	/**
	 * Update the score in the lobby.
	 * @param score the score need to add.
	 */
	public void updateScore(int score) {
		try {
			// Only update the score if it's less than 75.
			if (myScore < 75) {
				myScore += score;
				if (myScore > 75) {
					score = 5;
					myScore = 75;
				}
				if (myScore < 0) {
					score = 0;
					myScore = 0;
				}

				cmdAdpter.getLogger().log(LogLevel.DEBUG, player.getName());

				server.sendMessage(new ReceiverDataPacket<UpdateScoreMsg>(
						new UpdateScoreMsg(score, team, gameID, teamSize), player));
				adapter.updateMyScore(myScore);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Show the hard question and compare the answer.
	 */
	public void showHardQ() {
		//ILogger logger = cmdAdpter.getLogger();
		Random rand = new Random();
		if (hardQuestions.isEmpty()) {
			initHardQ();
		}
		int questionIndex = rand.nextInt(hardQuestions.size());
		String a = hardQuestions.get(questionIndex)[0];
		boolean answer = adapter.showQuestion(a);

		String compare = "No";
		if (answer) {
			compare = "Yes";
		}

		if (compare.equals(hardQuestions.get(questionIndex)[1])) {
			int prevPos = myScore / 5;
			updateScore(10);
			int curPos = myScore / 5;
			displayPos(locations.get(curPos));
			drawLine(locations.get(prevPos), locations.get(curPos), "Blue");
		} else {
			int prevPos = myScore / 5;
			updateScore(-10);
			int curPos = myScore / 5;
			displayPos(locations.get(curPos));
			drawLine(locations.get(prevPos), locations.get(curPos), "Red");
		}
		adapter.showResult(compare.equals(hardQuestions.get(questionIndex)[1]));
		hardQuestions.remove(questionIndex);
	}

	/**
	 * Show the easy question and compare the answer.
	 */
	public void showEasyQ() {
		Random rand = new Random();
		if (easyQuestions.isEmpty()) {
			initEasyQ();
		}
		int questionIndex = rand.nextInt(easyQuestions.size());
		String a = easyQuestions.get(questionIndex)[0];
		boolean answer = adapter.showQuestion(a);
		String compare = "No";
		if (answer) {
			compare = "Yes";
		}

		if (compare.equals(easyQuestions.get(questionIndex)[1])) {
			int prevPos = myScore / 5;
			updateScore(5);
			int curPos = myScore / 5;
			displayPos(locations.get(curPos));
			drawLine(locations.get(prevPos), locations.get(curPos), "Blue");
		} else {
			int prevPos = myScore / 5;
			updateScore(-5);
			int curPos = myScore / 5;
			displayPos(locations.get(curPos));
			drawLine(locations.get(prevPos), locations.get(curPos), "Red");
		}
		adapter.showResult(compare.equals(easyQuestions.get(questionIndex)[1]));
		easyQuestions.remove(questionIndex);
	}

	/**
	 * Display a location in the map.
	 * 
	 * @param location The location to display.
	 */
	public void displayPos(ILatLng location) {
		//ILogger logger = cmdAdpter.getLogger();
		threadPool.submit(() -> {
			try {
				mapReadyCountDownLatch.await();
				try {
					IMixedDataDictionary markerOptions = IMarkerOptions.makeDefault();
					IMarker marker = owlMapControl.getMapComponentFactory().makeMarker(markerOptions);
					marker.setPosition(location);
					marker.setIcon("https://www.clear.rice.edu/comp310/course/samples/images/Rice_OwlBlue_50x58.png");
					//					owlMap.setCenter(location);
					owlMap.panTo(location);
					marker.setVisible(true);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		});
	}

	/**
	 * Draw the line between two points.
	 * @param start the start location.
	 * @param end	the end location.
	 * @param color	the color of the arrow.
	 */
	public void drawLine(ILatLng start, ILatLng end, String color) {
		threadPool.submit(() -> {
			IMixedDataDictionary options = IPolylineOptions.makeDefault();
			options.put(IPolylineOptions.PATH, IPath.make(start, end));
			options.put(IPolylineOptions.ICONS, new ArrayList<IMixedDataDictionary>() {

				/**
				 * Unique UID.
				 */
				private static final long serialVersionUID = -4473237614325145948L;
				{
					IMixedDataDictionary symbolOptions = ISymbolOptions.makeDefault();
					symbolOptions.put(ISymbolOptions.PATH, SymbolPathConstants.FORWARD_CLOSED_ARROW);
					symbolOptions.put(ISymbolOptions.STROKE_COLOR, color);
					symbolOptions.put(ISymbolOptions.SCALE, 2.0);
					IMixedDataDictionary iconSequenceOptions = IIconSequenceOptions.makeDefault();
					iconSequenceOptions.put(IIconSequenceOptions.ICON, symbolOptions);
					iconSequenceOptions.put(IIconSequenceOptions.REPEAT, "15%");
					add(iconSequenceOptions);
				}
			});

			try {
				IPolyline polyline = owlMapControl.getMapComponentFactory().makePolyline(options);
				polyline.setVisible(true);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
	}

	/**
	 * Send team members text message.
	 * 
	 * @param msg The text message to send.
	 */
	public void sendTeamText(String msg) {
		try {
			server.sendMessage(new ReceiverDataPacket<TeamTextMsg>(new TeamTextMsg(msg, team), player));
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Set up the easy questions.
	 */
	public void initEasyQ() {

		easyQuestions.add(new String[] { "Is COMP 421 a required course for CS major?", "Yes" });
		easyQuestions.add(new String[] { "Is Luay the Chair of the Computer Science Department?", "No" });
		easyQuestions.add(new String[] { "Reginald DesRoches will become the next Rice president.", "Yes" });
		easyQuestions.add(new String[] { "Luay is currently the Dean of Engineering at Rice.", "Yes" });
		easyQuestions.add(new String[] { "According to Niche, Rice has the best campus food in the U.S.", "No" });
		easyQuestions.add(new String[] { "Rice has 7 residential colleges.", "No" });
		easyQuestions.add(new String[] { "Rice has 11 residential colleges.", "Yes" });
		easyQuestions.add(new String[] { "'Unconventional Wisdom' is Rice's motto.", "Yes" });
		easyQuestions.add(new String[] { "David Leebron is Rice's current president.", "Yes" });
		easyQuestions.add(new String[] { "COMP 321 is taught by Prof. Cox", "Yes" });
		easyQuestions.add(new String[] { "Rice has over 1000 full-time academic staffs.", "No" });
		easyQuestions
				.add(new String[] { "Computer Science Department is one of the largest departments at Rice.", "Yes" });
		easyQuestions.add(new String[] { "Rice is strong in sports management.", "Yes" });
		easyQuestions.add(new String[] { "Rice has an undergrduate Business major.", "No" });
		easyQuestions.add(new String[] { "Rice has a pre-Med major.", "No" });
		easyQuestions.add(new String[] { "Rice has a 4-year graduation rate of over 95%.", "No" });
		easyQuestions.add(new String[] { "Rice is a non-profit institution.", "No" });
		easyQuestions.add(new String[] { "Rice has the lowest acceptance rate in Texas.", "Yes" });
		easyQuestions.add(new String[] { "Rice has the lowest acceptance rate in the Southern United States.", "No" });
		easyQuestions.add(new String[] { "COMP 411 is usually offered in spring.", "Yes" });

	}

	/**
	 * Set up the hard questions.
	 */
	public void initHardQ() {

		hardQuestions
				.add(new String[] { "Rice is ranked #16 in U.S. News National Universities Ranking (2022)", "No" });
		hardQuestions
				.add(new String[] { "Rice is ranked #17 in U.S. News National Universities Ranking (2022)", "Yes" });
		hardQuestions.add(new String[] { "As of fall 2020, Rice has about 3000 undergraduate students", "No" });
		hardQuestions.add(new String[] { "As of fall 2020, Rice has about 4000 undergraduate students", "Yes" });
		hardQuestions.add(new String[] { "Swong is a Swarthmore alumnus.", "Yes" });
		hardQuestions.add(new String[] { "Rice has a 6:1 Student-Faculty ratio.", "Yes" });
		hardQuestions.add(new String[] { "Rice was founded in 1912", "Yes" });
		hardQuestions.add(new String[] {
				"Duncan College is named after Charles Duncan Jr., Secretary of Energy under Pres. Clinton.", "No" });
		hardQuestions.add(new String[] { "The first female mayor of Houston graduated from Rice.", "No" });
		hardQuestions.add(new String[] { "The second female mayor of Houston graduated from Rice.", "Yes" });
		hardQuestions.add(new String[] {
				"Alberto Gonzales, a Rice alumnus, served as U.S. Attorney General under President Obama.", "No" });
		hardQuestions.add(new String[] { "According to Princeton Review, Rice ranks No.1 in quality of life.", "Yes" });
		hardQuestions.add(new String[] { "Rice has two alumni who won the Nobel Prize", "Yes" });
		hardQuestions.add(new String[] { "Swong got his Ph.D. degree at MIT.", "Yes" });
		hardQuestions.add(new String[] { "Swong got his Ph.D. degree at Stanford.", "No" });
		hardQuestions.add(new String[] { "Swong's cell phone number is 713-348-3814.", "Yes" });
		hardQuestions.add(new String[] { "Kraft Hall is home to the Social Science Department.", "Yes" });
		hardQuestions.add(new String[] { "Rice Statistics starts to offer a B.S. program in 2020.", "Yes" });
		hardQuestions.add(new String[] { "Rice campus spans 400 acres.", "No" });
		hardQuestions.add(new String[] { "Swong started to teach COMP 310 since 2012.", "No" });
	}

	/**
	 * Initialize the list of locations.
	 */
	public void initLoc() {
		locations.add(ILatLng.make(29.7174, -95.4018)); // Rice (Houston)
		locations.add(ILatLng.make(32.7767, -96.7970)); // Dallas
		locations.add(ILatLng.make(33.4251, -94.0477)); // Texarkana
		locations.add(ILatLng.make(34.7465, -92.2896)); // Little Rock
		locations.add(ILatLng.make(35.1495, -90.0490)); // Memphis
		locations.add(ILatLng.make(36.1627, -86.7816)); // Nashville
		locations.add(ILatLng.make(38.2527, -85.7585)); // Louisville
		locations.add(ILatLng.make(39.1031, -84.5120)); // Cincinnati
		locations.add(ILatLng.make(39.9612, -82.9988)); // Columbus
		locations.add(ILatLng.make(40.4406, -79.9959)); // Pittsburgh
		locations.add(ILatLng.make(40.2732, -76.8867)); // Harrisburg
		locations.add(ILatLng.make(39.9526, -75.1652)); // Philadelphia
		locations.add(ILatLng.make(40.7128, -74.0060)); // New York
		locations.add(ILatLng.make(41.7658, -72.6734)); // Hartford
		locations.add(ILatLng.make(41.8240, -71.4128)); // Providence
		locations.add(ILatLng.make(42.3601, -71.0589)); // Boston

	}

}
