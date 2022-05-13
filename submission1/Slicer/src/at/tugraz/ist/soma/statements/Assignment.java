package at.tugraz.ist.soma.statements;

import at.tugraz.ist.soma.utils.Type;

/**
 * This class represents an <b>assignment</b>. <br>
 * An <b>assignment</b> is e.g.
 * <ul>
 * 		<li> a = b; </li>
 * 		<li> a = b + c; </li>
 * </ul>
 * <b>but in our case</b>
 * <ul>
 * 		<li>int a;</li> 
 * 		<li>int a = b;</li>
 * 		<li>int a = b + c;</li>
 * </ul>
 * is <b>not</b> an assignment. See {@link at.tugraz.ist.soma.statements.VariableDeclaration} instead.
 * @author Stephan Frühwirt
 */
public class Assignment extends Statement {

	public Assignment(int number, String text) {
		super(number, text);
		type = Type.ASSIGNMENT;
	}

	@Override
	public void setLastLine(int lastLine) {
	}

	/**
	 * will return -1 by default for assignments and variable declarations.
	 */
	@Override
	public int getLastLine() {
		return lastLine;
	}
}
