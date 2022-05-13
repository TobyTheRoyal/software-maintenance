package at.tugraz.ist.soma.statements;

import java.util.HashSet;

import at.tugraz.ist.soma.utils.Type;

/**
 * This class represents a statement. It is the base class for 
 * {@link at.tugraz.ist.soma.statements.Assignment}, {@link at.tugraz.ist.soma.statements.VariableDeclaration}, 
 * {@link at.tugraz.ist.soma.statements.IfStatement}, {@link at.tugraz.ist.soma.statements.ElseStatement}, 
 * {@link at.tugraz.ist.soma.statements.ForLoop} and {@link at.tugraz.ist.soma.statements.WhileLoop}.
 * @author Stephan Frühwirt
 *
 */
public abstract class Statement {

	protected Type type;
	protected HashSet<String> def;
	protected HashSet<String> ref;
	private int line;
	private String text;
	protected int lastLine;
	
	public Statement(int line, String text){
		type = Type.STATEMENT;
		
		this.line = line;
		this.text = text;
		
		def = new HashSet<String>();
		ref = new HashSet<String>();
		
		lastLine = -1;
	}
	
	public abstract void setLastLine(int lastLine);
	
	/**
	 * returns the last line of the statement. If the statement is an {@link at.tugraz.ist.soma.statements.Assignment} or {@link at.tugraz.ist.soma.statements.VariableDeclaration} the value will be -1.
	 */
	public abstract int getLastLine();
	
	/**
	 * returns the {@link at.tugraz.ist.soma.utils.Type} of the statement.
	 */
	public Type getType(){
		return type;
	}
	
	/**
	 * returns the line number of the statement.
	 */
	public int getLine() {
		return line;
	}

	/**
	 * returns the code text of the statement.
	 */
	public String getText() {
		return text;
	}

	/**
	 * returns a set of defined variables of the statement.
	 */
	public HashSet<String> getDef() {
		return def;
	}
	
	public void addDef(String var){
		this.def.add(var);
	}

	/**
	 * returns a set of referenced variables of the statement.
	 */
	public HashSet<String> getRef() {
		return ref;
	}
	
	public void addRef(String var){
		this.ref.add(var);
	}
	
	/**
	 * returns the number of defined variables in this line
	 */
	public int getDefSize(){
		return def.size();
	}
	
	/**
	 * returns the number of referenced variables in this line
	 */
	public int getRefSize(){
		return ref.size();
	}

	/**
	 * prints the information of a statement to std.out.
	 */
	public String toString() {
		String stmt = line + ": " + text + ", " + type.toString() + System.lineSeparator();
		stmt += !def.isEmpty() ? "Def: " + def  + System.lineSeparator() : "";
		stmt += !ref.isEmpty() ? "Ref: " + ref  + System.lineSeparator() : "";
		stmt += lastLine != -1 ? lastLine + System.lineSeparator() : "";
		return stmt;
	}
}
