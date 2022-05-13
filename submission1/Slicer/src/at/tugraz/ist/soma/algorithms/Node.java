package at.tugraz.ist.soma.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import at.tugraz.ist.soma.statements.Statement;

/**
 * This class represents one node of the PDG.
 * @author Stephan Frühwirt
 */
public class Node {

	private Statement statement;
	private TreeMap<Integer, Node> controlDependencies;
	private TreeMap<Integer, Node> dataDependencies;
	private Node influencedBy;
	private Boolean afterElse;
	
	/**
	 * use this constructor if you want to create the ENTRY node.
	 * You can recognize the ENTRY node via the <i>statement</i>
	 * variable which should always be <b>null</b>.
	 */
	public Node(){
		controlDependencies = new TreeMap<Integer, Node>(Collections.reverseOrder());
		dataDependencies = new TreeMap<Integer, Node>(Collections.reverseOrder());
		statement = null;
		influencedBy = null;
		afterElse = false;
	}
	
	/**
	 * use this constructor if you want to create a new node.
	 * If you want to create the ENTRY node you should use the {@link at.tugraz.ist.soma.algorithms.Node#Node()} constructor.
	 */
	public Node(Statement statement){
		this.statement = statement;
		controlDependencies = new TreeMap<Integer, Node>(Collections.reverseOrder());
		dataDependencies = new TreeMap<Integer, Node>(Collections.reverseOrder());
		influencedBy = null;
		afterElse = false;
	}
	
	/**
	 * appends a control dependency to this node, i.e. a link between this node and a given node.
	 * <br><strong>HINT:</strong> think about in which direction you should save the linkages!
	 */
	public void appendControlDependency(Node node){
	    // node must exist and should not be self
		if (node != null && (node.getStatement() == null || statement == null ||
		    node.getStatement().getLine() != statement.getLine()
        )) {
			controlDependencies.put(node.getStatement().getLine(), node);			
		}
	}
	
	/**
	 * returns the control dependencies of this node.
	 */
	public TreeMap<Integer, Node> getControlDependencies() {
		return controlDependencies;
	}
	
	/**
	 * appends a data dependency to this node, i.e. a link between this node and a given node.
	 * <br><strong>HINT:</strong> think about in which direction you should save the linkages!
	 */
	public void appendDataDependency(Node node){
        // node must exist and should not be self
        if (node != null && (node.getStatement() == null || statement == null ||
            node.getStatement().getLine() != statement.getLine()
        )) {
			dataDependencies.put(node.getStatement().getLine(), node);			
		}
	}
	
	/**
	 * returns the data dependencies of this node.
	 */
	public TreeMap<Integer, Node> getDataDependencies() {
		return dataDependencies;
	}
	
	/**
	 * returns an array list of all dependencies (control + data).
	 * Might be useful when you calculate the slice.
	 */
	public ArrayList<Node> getAllDependencies(){
		ArrayList<Node> all = new ArrayList<>();
		
		controlDependencies.forEach((k,v) -> all.add(v));
		dataDependencies.forEach((k,v) -> all.add(v));
		return all;
	}
	
	/**
	 * returns the {@link at.tugraz.ist.soma.statements.Statement} of a node.
	 */
	public Statement getStatement(){
		return statement;
	}


    /**
     * saves the information if this node is influenced by another one.
     * @param node The node which influences this node, i.e. this node is in the body of an if-, else-, while- or for-statement.
     */
    public void influencedBy(Node node){
        influencedBy = node;
    }

    /**
     * returns the node which influences this node.
     */
    public Node getInfluence(){
        return influencedBy;
    }

    /**
     * Sets this statement to be the first of an else branch
     */
    public void afterElse() { afterElse = true; }
    public void afterElse(Boolean afterElse) { this.afterElse = afterElse; }

    /**
     * Whether this statement is the first of an else branch
     */
    public Boolean isAfterElse() { return afterElse; }
}
