package at.tugraz.ist.soma;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import at.tugraz.ist.soma.cfg.CFGNode;
import at.tugraz.ist.soma.statements.Statement;

/**
 * This class holds a list of all statements, the information about the file name, the slicing criterion
 * and the generated control flow graph.
 * @author Stephan Frühwirt
 */

/*
 * This class does more than one could imagine;
 * this is because I thought it is wise to pass all information as parameters -
 * God I was wrong...
 * So that' why you have to use this super mega fancy singleton class!
 */
public class DataPool {

	private static DataPool instance = null;
	private HashMap<Integer, Statement> statements;
	private String inputFileName;
	private String slicingCriterion;
	private ArrayList<CFGNode> controlFlowGraph;
	private boolean debug;
	
	private DataPool(){
		// LinkedHashMap to keep ordering
		statements = new LinkedHashMap<Integer, Statement>();
		debug = false;
	}
	
	/**
	 * returns one instance of the singleton class.
	 */
	public static DataPool getInstance(){
		if(instance == null){
			instance = new DataPool();
		}
		return instance;
	}
	
	public void addStatement(Statement stmt){
		statements.put(stmt.getLine(), stmt);
	}

	/**
	 * returns a statement at a specific position of the list.
	 */
	public Statement getStatement(int index){
		if(statements.containsKey(index)) {
			return statements.get(index);
		}
		return null;
	}
	
	/**
	 * returns the hashmap of statements.
	 */
	public HashMap<Integer, Statement> getStatements() {
		return statements;
	}

	public void updateStatement(int index, Statement stmt){
		statements.put(index, stmt);
	}
	
	/**
	 * returns the input file name.
	 */
	public String getInputFileName() {
		return inputFileName;
	}

	public void setInputFileName(String inputFileName) {
		this.inputFileName = inputFileName;
	}

	/**
	 * returns the output file name
	 */
	public String getOutputFileName() {
		return inputFileName.replace("input", "output");
	}

	public void setSlicingCriterion(String slicingCriterion_) {
		this.slicingCriterion = slicingCriterion_;
	}
	
	/**
	 * returns the slicing criterion (extracted from the console arguments).
	 */
	public String getSlicingCriterion() {
		return slicingCriterion;
	}
	
	/**
	 * if debug flag is set the dot-files will be created.
	 */
	public void setDebug() {
		debug = true;
	}
	
	public boolean getDebug() {
		return debug;
	}

	/**
	 * returns the control flow graph.
	 */
	public ArrayList<CFGNode> getControlFlowGraph() {
		return controlFlowGraph;
	}

	public void setControlFlowGraph(ArrayList<CFGNode> controlFlowGraph) {
		this.controlFlowGraph = controlFlowGraph;
	}

	/**
	 * calls the {@link at.tugraz.ist.soma.statements.Statement#toString()} method of each statement.
	 */
	public void printStatements(){
		statements.forEach((k,v) -> System.out.print(v.toString()));
	}
	
}
