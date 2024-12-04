/**
 * 
 */
package yh87_cp46.chatRoom.miniModel.message;

import common.receiver.messages.IStringMsg;

/**
 * @author jimmy
 * Message for sending a string.
 */
public class StringMsg implements IStringMsg {

	/**
	 * Serial id.
	 */
	private static final long serialVersionUID = 3815308945259383396L;

	/**
	 * input text.
	 */
	private String inputString;

	/**
	 * Constructor for the string message.
	 * @param inputString input text.
	 */
	public StringMsg(String inputString) {
		this.inputString = inputString;
	}

	@Override
	public String getString() {
		return this.inputString;
	}

}
