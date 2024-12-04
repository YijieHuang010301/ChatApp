package yh87_cp46.chatApp.model;

import common.connector.IConnector;
import common.connector.INamedConnector;

/**
 * Concrete class for the INamedConnector.
 * @author jimmy
 *
 */
public class NamedConnector implements INamedConnector {

	/**
	 * Serial ID.
	 */
	private static final long serialVersionUID = -5920990897915616818L;

	/**
	 * IConnector instance.
	 */
	private IConnector connector;

	/**
	 * input user name.
	 */
	private String userName;

	/**
	 * constructor for this class.
	 * @param connector the input IConnector.
	 * @param userName 	the user name.
	 */
	public NamedConnector(IConnector connector, String userName) {
		this.connector = connector;
		this.userName = userName;
	}

	@Override
	public String getName() {
		return userName;
	}

	@Override
	public IConnector getStub() {
		return connector;
	}

	//Functions that need to override.

	/**
	 * Returns true if obj is an INamedConnector with an equivalent stub, and false otherwise.
	 * 
	 * THIS METHOD MUST BE OVERRIDDEN FOR THE SYSTEM TO WORK!
	 * 
	 * @param obj An arbitrary object.
	 * @return The result of the comparison.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof INamedConnector)) {
			return false;
		}
		IConnector compareObj = ((INamedConnector) obj).getStub();
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
		return connector.hashCode();
	}

	/**
	 * Returns a human-friendly String representation of the named connector.
	 * 
	 * We suggest simply delegating to this.getName().
	 * 
	 * THIS METHOD MUST BE OVERRIDDEN FOR THE SYSTEM TO WORK!
	 * 
	 * @return The String representation of the named connector.
	 */
	@Override
	public String toString() {
		return getName();
	}

}
