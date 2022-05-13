package at.tugraz.ist.soma.cfg;

import java.util.ArrayList;

import at.tugraz.ist.soma.statements.Statement;
import at.tugraz.ist.soma.utils.Type;

/**
 * This class represents one node of the control flow graph.
 * @author Stephan Frühwirt
 */
public class CFGNode {

	private Statement statement;
	private ArrayList<CFGNode> predecessors;
	private CFGNode influencedBy;
	private Boolean afterElse;
	
	/**
	 * use this constructor if you want to create the start node.
	 * You can recognize the start node via the <i>statement</i>
	 * variable which should always be <b>null</b>.
	 */
	public CFGNode(){
		this.statement = null;
		predecessors = new ArrayList<>();
		influencedBy = null;
		afterElse = false;
	}
	
	/**
	 * use this constructor if you want to create a new node.
	 * If you want to create an entry node you should use the {@link at.tugraz.ist.soma.cfg.CFGNode#CFGNode()} constructor.
	 */
	public CFGNode(Statement statement){
		this.statement = statement;
		predecessors = new ArrayList<>();
		influencedBy = null;
		afterElse = false;
	}
	
	/**
	 * appends a new node to this node, i.e. a link between this node and a given node (successor or predecessor).
	 * <br><strong>HINT:</strong> think about in which direction you should save the linkages!
	 */
	public void appendPredecessor(CFGNode node){
		if(node != null){
		    if (getPredecessors().size() > 0) {
		        // don't add the same twice (after exiting multiple blocks)
		        CFGNode last = getPredecessors().get(getPredecessors().size() - 1);
                if (last.getStatement() != null && node.getStatement() != null &&
                        last.getStatement().getLine() == node.getStatement().getLine()
                ) {
                    return;
                }
            }
			predecessors.add(node);
		}
	}
	
	/**
	 * returns the linkages to other nodes.
	 */
	public ArrayList<CFGNode> getPredecessors(){
		return predecessors;
	}
	
	/**
	 * saves the information if this node is influenced by another one.
	 * @param node The node which influences this node, i.e. this node is in the body of an if-, else-, while- or for-statement.
	 */
	public void influencedBy(CFGNode node){
		influencedBy = node;
	}
	
	/**
	 * returns the node which influences this node.
	 */
	public CFGNode getInfluence(){
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
	
	/**
	 * returns the statement of a node.
	 */
	public Statement getStatement(){
		return statement;
	}
	
	/**
	 * returns the {@link at.tugraz.ist.soma.utils.Type} of a statement.
	 * If the statement is null (= start node), the type will be '{@link at.tugraz.ist.soma.utils.Type}'.
	 */
	public Type getType(){
		if(statement != null){
			return statement.getType();
		}
		return Type.STATEMENT;
	}
	
	/**
	 * prints information of this node and of its successor/s or predecessor/s to std. out.
	 */
	public void printPredecessors(){
		System.out.println(statement != null ? statement.getLine() + ": " + statement.getText() : "ENTRY");
		for (CFGNode pre : predecessors){
			System.out.println("--" + pre.getStatement().getText());
		}
	}
	
	
}
