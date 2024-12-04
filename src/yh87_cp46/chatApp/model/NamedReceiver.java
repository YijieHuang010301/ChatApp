package yh87_cp46.chatApp.model;

import common.connector.INamedConnector;
import common.receiver.INamedReceiver;
import common.receiver.IReceiver;

/**
 * The concrete class for the named receiver.
 * @author jimmy
 *
 */

public class NamedReceiver implements INamedReceiver {

	/**
	 * the serial id.
	 */
	private static final long serialVersionUID = 4933685277091459116L;

	/**
	 * the receiver.
	 */
	private IReceiver receiver;

	/**
	 * user name.
	 */
	private String name;

	/**
	 *  wrapped connector.
	 */
	private INamedConnector namedConnector;

	/**
	 * constructor.
	 * @param receiver the receiver.
	 * @param namedConnector the namedconnector in it.
	 * @param name the user name.
	 */
	public NamedReceiver(IReceiver receiver, INamedConnector namedConnector, String name) {
		this.name = name;
		this.receiver = receiver;
		this.namedConnector = namedConnector;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public INamedConnector getConnector() {
		return this.namedConnector;
	}

	@Override
	public IReceiver getStub() {
		return this.receiver;
	}

	//Functions override for safety.
	/**
	 * Returns true if obj is an INamedReceiver with an equivalent stub, and false otherwise.
	 * 
	 * THIS METHOD MUST BE OVERRIDDEN FOR THE SYSTEM TO WORK!
	 * 
	 * @param obj An arbitrary object.
	 * @return The result of the comparison.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof INamedReceiver)) {
			return false;
		}
		IReceiver compareObj = ((INamedReceiver) obj).getStub();
		if (this.getStub().equals(compareObj)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns the hash code of the stub contained within.
	 * 
	 * THIS METHOD MUST BE OVERRIDDEN FOR THE SYSTEM TO WORK!
	 * 
	 * @return The hash code.
	 */
	@Override
	public int hashCode() {
		return this.receiver.hashCode();
	}

	/**
	 * Returns a human-friendly String representation of the named receiver.
	 * 
	 * We suggest simply delegating to this.getName().
	 * 
	 * THIS METHOD MUST BE OVERRIDDEN FOR THE SYSTEM TO WORK!
	 * 
	 * @return The String representation of the named receiver.
	 */
	@Override
	public String toString() {
		return this.getName();
	}

}
