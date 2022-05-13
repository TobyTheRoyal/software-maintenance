package at.tugraz.ist.soma.cfg;

import java.util.ArrayList;
import java.util.LinkedList;
import at.tugraz.ist.soma.DataPool;
import at.tugraz.ist.soma.statements.Statement;
import at.tugraz.ist.soma.utils.Type;
import at.tugraz.ist.soma.utils.Visualizer;

import javax.swing.plaf.nimbus.State;

/**
 * This class creates a control flow graph using the list of {@link at.tugraz.ist.soma.statements.Statement}s.
 * @author Stephan Frühwirt
 */
public class CFGgenerator {

	private DataPool pool;
	private LinkedList<CFGNode> stack;
	
	public CFGgenerator(){
		pool = DataPool.getInstance();
		stack = new LinkedList<>();
	}
	
	/**
	 * generates the CFG. You can use the {@link at.tugraz.ist.soma.utils.Visualizer#createCFGdotFile(ArrayList)}
	 * to get a graphical representation of your graph!
	 */
	public void generate(){
		ArrayList<CFGNode> cfg = new ArrayList<>();
		stack.clear();
		
		CFGNode node = new CFGNode();
		cfg.add(node);
		stack.push(node);

		CFGNode pre = node;

        if (pool.getDebug()) {
            System.out.println("## Begin CFG Generate ##");
        }
		
		// TODO: calculate the CFG
		//		hint: this will be the most difficult part of this exercise.
		//		Prepare a coffee supply...
		for(Statement stmt : pool.getStatements().values()){
            node = new CFGNode(stmt);

            if (stmt.getType() != Type.ELSE_STMT)
                cfg.add(node);

            // handle block close and dependencies
            closeBlocks(node, pre);
            pre = node;

            // set influence
            if (stack.getFirst().getStatement() == null || stack.getFirst().getStatement().getType() != Type.ELSE_STMT) {
                node.influencedBy(stack.getFirst());
            } else {
                node.influencedBy(stack.get(2));
            }

            // open block
		    switch (stmt.getType()) {
                case IF_STMT:
                case ELSE_STMT:
                case LOOP_FOR:
                case LOOP_WHILE:
                    stack.push(node);
                    break;
            }
		}

		node = new CFGNode();
		cfg.add(node);
		closeBlocks(node, pre);

        if (pool.getDebug()) {
            System.out.println("## End CFG Generate");
            System.out.println("## Begin CFG Printout ##");

            for (CFGNode nod : cfg) {
                if (nod.getStatement() != null) {
                    System.out.print(String.format("%d: %s  - ", nod.getStatement().getLine(), nod.getStatement().getText()));
                } else {
                    System.out.print("Empty - ");
                }

                for (CFGNode pred : nod.getPredecessors()) {
                    if (pred.getStatement() != null) {
                        System.out.print(String.format("%d ", pred.getStatement().getLine()));
                    } else {
                        System.out.print("0 ");
                    }
                }
                System.out.println();
            }

            System.out.println("## End CFG Printout ##\n");
        }

		Visualizer.createCFGdotFile(cfg);
		pool.setControlFlowGraph(cfg);
	}

    /**
     * Closes all previously opened blocks if statement of node prev is after closing braces
     * @param node Current node
     * @param prev Previous node
     */
	private void closeBlocks(CFGNode node, CFGNode prev) {
	    Statement stackTop = stack.getFirst().getStatement();
	    CFGNode beginBlock = null;
	    Boolean addPre = true;

        // first statement of else block needs link to if block
        if (prev.getStatement() != null && prev.getStatement().getType() == Type.ELSE_STMT) {
            node.appendPredecessor(stack.get(2)); // if
            node.afterElse();
            addPre = false;
        }

        // end block
        while (stack.size() != 0) {
            if (node.getStatement() != null && node.getStatement().getType() == Type.ELSE_STMT) {
                stack.push(prev); // add last node of if block
                break; // if-else-blocks are handled at the end of else
            }

            if (stackTop != null && stackTop.getLastLine() != -1 &&
                (
                    stackTop.getLastLine() <= node.getStatement().getLine() ||
                        node.getStatement().getLine() == -1
                ) // either statement after block or end node
            ) {
                addPre = false; // predecessors are handled here

                beginBlock = stack.pop(); // if, else, while, for
                prev = leaveBlock(beginBlock, node, prev);

                stackTop = stack.getFirst().getStatement();

            } else {
                // if no blocks were closed, simply add a connection to the previous statement
                if (addPre) node.appendPredecessor(prev);
                break;
            }
        }
    }

    /**
     * Leaves a block and correctly connects the nodes at any depth
     * @param beginBlock    control statement node
     * @param node          node after block(s)
     * @param prev          last node of block (or previous block)
     * @return              new prev
     */
    private CFGNode leaveBlock(CFGNode beginBlock, CFGNode node, CFGNode prev) {
        // higher blocks that close here?
        Statement stackTop = beginBlock.getStatement();
        if (stackTop == null) return null;
        if (pool.getDebug()) {
            System.out.println(String.format("%d: %s", stackTop.getLine(), stackTop.getText()));
        }
        
        CFGNode endif = null;
        if (stackTop.getType() == Type.ELSE_STMT) {
            endif = stack.pop(); // last of if
            stack.pop(); // if
        }
        CFGNode next = getNext(stackTop, node);
        
        // connect condition to next
        if (stackTop.getType() != Type.ELSE_STMT) {
            next.appendPredecessor(beginBlock); // condition false - skip block
        } else {
            next.appendPredecessor(endif);
        }
        
        if (prev != null) {
            if (
                stackTop.getType() == Type.LOOP_FOR ||
                    stackTop.getType() == Type.LOOP_WHILE
            ) { // end of loops connect only to control statement, not previous
                beginBlock.appendPredecessor(prev); // loops back to condition
                if (pool.getDebug()) {
                    System.out.println(String.format("while %d, %d: next %d", beginBlock.getStatement().getLine(),
                        prev.getStatement().getLine(), next.getStatement().getLine()));
                }
                return beginBlock;
            } else {
                next.appendPredecessor(prev); // leave block
                if (pool.getDebug()) {
                    System.out.println(String.format("if%s %d, %d: next %d",
                        (stackTop.getType() == Type.ELSE_STMT ? "/else" : ""),
                        beginBlock.getStatement().getLine(),
                        prev.getStatement().getLine(), next.getStatement().getLine()));
                }
                return prev;
            }
        } else if (pool.getDebug()) {
            System.out.println(String.format("%d: next %d", beginBlock.getStatement().getLine(),
                next.getStatement().getLine()));
        }
        return null;
    }

    /**
     * Recursively returns the node that will be executed after a block
     * @param stackTop  current block begin statement
     * @param node      node after block end
     * @param depth     depth in stack
     * @return          node to connect
     */
    private CFGNode getNext(Statement stackTop, CFGNode node, int depth) {
        if (
            stackTop.getLastLine() + 1 == node.getStatement().getLine() || // statement directly after }
                (node.getStatement() == null && stack.size() == 1) // or end of program and last block
        ) {
            // continue on
            return node;
        } else {
            // one higher
            stackTop = stack.get(depth).getStatement();
            if (
                stackTop.getType() == Type.LOOP_FOR ||
                    stackTop.getType() == Type.LOOP_WHILE
            ) {
                // is loop? connect to here (control statement)
                return stack.get(depth);
            } else {
                // other block? got deeper
                return getNext(stackTop, node, ++depth);
            }
        }
    }
    private CFGNode getNext(Statement stackTop, CFGNode node) {
        return getNext(stackTop, node, 0);
    }
}
