package yh87_cp46.chatApp.model.message.status;

import common.connector.ConnectorDataPacket;
import common.connector.INamedConnector;
import common.connector.messages.IConnectorErrMsg;

/**
 * @author jimmy
 * The connection error message.
 */
public class ConnectorErrMsg extends AConnectorStatusMsg implements IConnectorErrMsg {

	/**
	 * Serial Id.
	 */
	private static final long serialVersionUID = 6878755681143402550L;

	/**
	 * The constructor.
	 * @param dataPacket the data.
	 * @param sender  the sender.
	 */
	public ConnectorErrMsg(ConnectorDataPacket<?> dataPacket, INamedConnector sender) {
		this.dataPacket = dataPacket;
		this.sender = sender;
	}

	public String getStatus() {
		return "Connection Error from " + sender.getName();
	}
}
