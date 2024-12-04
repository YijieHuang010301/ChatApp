package yh87_cp46.chatRoom.miniModel.message.customMsg;

import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

import javax.swing.JComponent;

import common.adapter.ICmd2ModelAdapter;
import common.receiver.AReceiverDataPacketAlgoCmd;
import common.receiver.ReceiverDataPacket;
import provided.datapacket.IDataPacketID;
import provided.logger.ILogger;
import provided.logger.LogLevel;
import provided.mixedData.IMixedDataDictionary;
import provided.owlMaps.control.IOwlMapControl;
import provided.owlMaps.control.MapLengthUnits;
import provided.owlMaps.map.IMapOptions;
import provided.owlMaps.map.IOwlMap;
import provided.owlMaps.mouse.MapMouseEventType;
import provided.owlMaps.utils.IOwlMapUtils;

/**
 * The map command.
 * @author Cheng Peng
 *
 */
public class MapCmd extends AReceiverDataPacketAlgoCmd<MapMsg> {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 7809345857697348203L;

	/**
	 * The cmd to model adapter.
	 */
	private transient ICmd2ModelAdapter adapter;

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
	@SuppressWarnings("unused")
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

	@Override
	public Void apply(IDataPacketID index, ReceiverDataPacket<MapMsg> host, Void... params) {
		ILogger logger = adapter.getLogger();
		try {
			logger.log(LogLevel.INFO, "Starting map creation...");
			mapReadyCountDownLatch = new CountDownLatch(1);
			googleMapsApiKey = adapter.getMapKey();
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
						logger.log(LogLevel.INFO, "[onMapLoad] completed, releasing countdown latch!");
						mapReadyCountDownLatch.countDown();
					}, logger, MapLengthUnits.FEET, false);
			adapter.buildFixedComponent(mapFactory::get, "Test Map");
		} catch (Exception e) {
			logger.log(LogLevel.ERROR, "Exception = " + e);
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setCmd2ModelAdpt(ICmd2ModelAdapter cmd2ModelAdpt) {
		this.adapter = cmd2ModelAdpt;
	}

}
