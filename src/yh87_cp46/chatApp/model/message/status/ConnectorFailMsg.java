package yh87_cp46.chatApp.model.message.status;

import common.connector.ConnectorDataPacket;
import common.connector.INamedConnector;
import common.connector.messages.IConnectorFailMsg;

/**
 * @author jimmy
 * The connection fail class.
 */
public class ConnectorFailMsg extends AConnectorStatusMsg implements IConnectorFailMsg {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 1788757620303599210L;

	/**
	 * Constructor for the fail message.
	 * @param dataPacket the data.
	 * @param sender the sender.
	 */
	public ConnectorFailMsg(ConnectorDataPacket<?> dataPacket, INamedConnector sender) {
		this.dataPacket = dataPacket;
		this.sender = sender;
	}

	@Override
	public String getStatus() {
		return "Connetion Fail from " + sender.getName();
	}

}
