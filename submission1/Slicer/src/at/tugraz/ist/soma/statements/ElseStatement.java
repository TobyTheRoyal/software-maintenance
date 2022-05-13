package at.tugraz.ist.soma.statements;

import at.tugraz.ist.soma.utils.Type;

/**
 * This class represents an <b>else</b> branch of an if-condition.
 * @author Stephan Frühwirt
 */
public class ElseStatement extends Statement {

	public ElseStatement(int number, String text) {
		super(number, text);
		type = Type.ELSE_STMT;
	}
	
	@Override
	public void setLastLine(int lastLine) {
		this.lastLine = lastLine;
	}

	/**
	 * returns the last line of the else block body. Keep in mind that this may be a brace ('}')
	 * and therefore the line will not show up in the list of statements!
	 */
	@Override
	public int getLastLine() {
		return lastLine;
	}
}
