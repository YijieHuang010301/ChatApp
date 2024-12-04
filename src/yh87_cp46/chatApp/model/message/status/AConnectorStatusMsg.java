package yh87_cp46.chatApp.model.message.status;

import common.connector.ConnectorDataPacket;
import common.connector.INamedConnector;
import common.connector.messages.IConnectorStatusMsg;

/**
 * @author jimmy
 * The abstract concrete class for the statusmsg.
 */
public abstract class AConnectorStatusMsg implements IConnectorStatusMsg {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = 5636341249752343564L;

	/**
	 * The input data.
	 */
	protected ConnectorDataPacket<?> dataPacket;

	/**
	 * The sender.
	 */
	protected INamedConnector sender;

	@Override
	public ConnectorDataPacket<?> getPacket() {
		return dataPacket;
	}

	@Override
	public abstract String getStatus();

}
