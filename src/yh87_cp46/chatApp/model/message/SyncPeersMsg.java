package yh87_cp46.chatApp.model.message;

import java.util.Set;

import common.connector.INamedConnector;
import common.connector.messages.ISyncPeersMsg;

/**
 * @author jimmy
 * Class for the sync peer message.
 */
public class SyncPeersMsg implements ISyncPeersMsg {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -1050307218539648166L;

	/**
	 * the set of connected peers.
	 */
	private Set<INamedConnector> peers;

	/**
	 * the constructor.
	 * @param peerSet set of connected peers.
	 */
	public SyncPeersMsg(Set<INamedConnector> peerSet) {
		this.peers = peerSet;
	}

	@Override
	public Set<INamedConnector> getNewPeers() {
		return peers;
	}

}
