package yh87_cp46.chatApp.model.message.status;

import common.connector.ConnectorDataPacket;
import common.connector.INamedConnector;
import common.connector.messages.IConnectorRejectMsg;

/**
 * @author jimmy
 * The connection reject message.
 */
public class ConnectorRejectMsg extends AConnectorStatusMsg implements IConnectorRejectMsg {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -2379605039639705622L;

	/**
	 * The constructor for the class.
	 * @param dataPacket the data.
	 * @param sender The sender.
	 */
	public ConnectorRejectMsg(ConnectorDataPacket<?> dataPacket, INamedConnector sender) {
		this.dataPacket = dataPacket;
		this.sender = sender;
	}

	@Override
	public String getStatus() {
		return "Connection Reject from " + this.sender.getName();
	}

}
