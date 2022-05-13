package at.tugraz.ist.soma.statements;

import at.tugraz.ist.soma.utils.Type;

/**
 * This class represents a <b>variable definition.</b> <br>
 * A <b>variable declaration</b> is e.g. 
 * <ul>
 * 		<li>int a;</li> 
 * 		<li>int a = b;</li>
 * 		<li>int a = b + c; </li>
 * </ul>
 * <b>but in our case</b>
 * <ul>
 * 		<li> a = b; </li>
 * 		<li> a = b + c; </li>
 * </ul>
 * is <b>not</b> a variable declaration. See {@link at.tugraz.ist.soma.statements.Assignment} instead.
 * @author Stephan Frühwirt
 *
 */
public class VariableDeclaration extends Statement {

	public VariableDeclaration(int number, String text) {
		super(number, text);
		type = Type.VARIABLE_DECLARATION;
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
