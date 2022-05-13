package at.tugraz.ist.soma.statements;

import at.tugraz.ist.soma.utils.Type;

/**
 * This class represents an <b>if</b> condition.
 * @author Stephan Frühwirt
 */
public class IfStatement extends Statement {
	
	public IfStatement(int number, String text) {
		super(number, text);
		type = Type.IF_STMT;
	}

	/**
	 * returns the last line of the if block body. Keep in mind that this may be a brace ('}')
	 * and therefore the line will not show up in the list of statements!
	 */
	public int getLastLine() {
		return lastLine;
	}

	public void setLastLine(int lastLine) {
		this.lastLine = lastLine;
	}
}
