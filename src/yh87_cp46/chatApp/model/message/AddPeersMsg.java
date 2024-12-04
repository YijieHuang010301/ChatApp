package yh87_cp46.chatApp.model.message;

import java.util.Set;

import common.connector.INamedConnector;
import common.connector.messages.IAddPeersMsg;

/**
 * @author jimmy
 * The add peer message.
 */
public class AddPeersMsg implements IAddPeersMsg {

	/**
	 * The serial id.
	 */
	private static final long serialVersionUID = 73409886991426996L;

	/**
	 * The set of connected peers.
	 */
	private Set<INamedConnector> peersConnector;

	/**
	 * Constructor that takes the set of peers.
	 * @param peersConnector the set of connected peers.
	 */
	public AddPeersMsg(Set<INamedConnector> peersConnector) {
		this.peersConnector = peersConnector;
	}

	@Override
	public Set<INamedConnector> getNewPeers() {
		return this.peersConnector;
	}

}
