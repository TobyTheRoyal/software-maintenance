package at.tugraz.ist.soma.statements;

import at.tugraz.ist.soma.utils.Type;

/**
 * This class represents a <b>while</b>-loop.
 * @author Stephan Frühwirt
 *
 */
public class WhileLoop extends Statement {

	public WhileLoop(int number, String text) {
		super(number, text);
		type = Type.LOOP_WHILE;
	}

	@Override
	public void setLastLine(int lastLine) {
		this.lastLine = lastLine;
	}

	/**
	 * returns the last line of the loop body. Keep in mind that this may be a brace ('}')
	 * and therefore the line will not show up in the list of statements!
	 */
	@Override
	public int getLastLine() {
		return lastLine;
	}

}
