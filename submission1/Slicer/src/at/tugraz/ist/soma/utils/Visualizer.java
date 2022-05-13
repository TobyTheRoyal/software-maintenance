package at.tugraz.ist.soma.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import at.tugraz.ist.soma.DataPool;
import at.tugraz.ist.soma.algorithms.Node;
import at.tugraz.ist.soma.cfg.CFGNode;
import at.tugraz.ist.soma.statements.Statement;

/**
 * This class is responsible for the visualisation of your graphs.
 * The generated .dot files can be opened using a program called 'graphviz'.
 * @author Stephan Frühwirt
 */
public class Visualizer {

	private static DataPool pool = DataPool.getInstance();
	
	/**
	 * Creates a .dot file of your CFG if you pass "debug" as third console parameter.
	 * <br>The graph is saved at ../output/'input filename'-cfg.dot
	 * 
	 * @param tree An Arraylist of {@link at.tugraz.ist.soma.cfg.CFGNode}s, each node knows its successor/predecessor.
	 * It might be necessary to adapt the direction of the arrows in the marked line below!
	 */
	public static void createCFGdotFile(ArrayList<CFGNode> tree){
		if(!pool.getDebug()){
			return;
		}
		
		String cfgString = "digraph {\n";
		
		for(CFGNode node : tree){
			if(node.getStatement() == null){
				continue;
			}
			if(node.getType() != Type.ASSIGNMENT && node.getType() != Type.VARIABLE_DECLARATION && node.getPredecessors().size() > 0){
				cfgString += node.getStatement().getLine() + "[shape=box];\n";					
			}
		}
		
		for(CFGNode node : tree){
			if(node.getStatement() == null){
				continue;
			}
			for(CFGNode edge : node.getPredecessors()){
                if(edge.getStatement() == null){
                    continue;
                }
                
				// TODO: Depending on your implementation you may change the direction of the arrows by exchanging the following lines
				cfgString += edge.getStatement().getLine() + " -> " + node.getStatement().getLine() + ";\n";
				//cfgString += node.getStatement().getLine() + " -> " + edge.getStatement().getLine() + ";\n";
			}
		}
		
		String file = pool.getOutputFileName();
		File outputFile = new File(file.replace(".java", "-cfg.dot"));
		FileWriter fw;
		try {
			fw = new FileWriter(outputFile);
			fw.write(cfgString +  "}" );
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates a .dot file of your PDG if you pass "debug" as third console parameter.
	 * <br>The graph is saved at ../output/'input filename'.dot
	 * 
	 * @param tree An Arraylist of {@link at.tugraz.ist.soma.algorithms.Node}s, each node knows its successor/predecessor.
	 * It might be necessary to adapt the direction of the arrows in the marked line below!
	 * @param slice The Arraylist containing the slice. These nodes will be marked in the graph.
	 */
	public static void createPDGdotFile(HashMap<Integer, Node> tree, ArrayList<Integer> slice, Boolean doData){
		if(!pool.getDebug()){
			return;
		}
		
		String outString = "digraph {\n";
		String dataDependenicies = "";
		
		for(Statement stmt : pool.getStatements().values()){
			// then- and else-branch are arranged side-by-side
			if(stmt.getType() != Type.ELSE_STMT){
				outString += stmt.getLine() + "[";
				if(slice.contains(stmt.getLine())){
					outString += "style=filled, fillcolor=yellow; ";
				}
				outString += "label=\"" + stmt.getLine() + ". " + stmt.getText() + "\"];\n";
			}
		}
		
		for(Node n : tree.values()){

            if (doData) for(Node data : n.getDataDependencies().values()){
				// TODO: Depending on your implementation you may change the direction of the arrows by exchanging the following lines
				// dataDependenicies += n.getStatement().getLine() + " -> " + data.getStatement().getLine() + "[style=dashed, color=red];\n";
				dataDependenicies += data.getStatement().getLine() + " -> " + n.getStatement().getLine() + "[style=dashed, color=red];\n";
			}
			
			for(Node controlDep : n.getControlDependencies().values()){
				if(n.getStatement() == null){
					outString +=  "ENTRY -> " + controlDep.getStatement().getLine() + ";\n";
				}
				else{
					// TODO: Depending on your implementation you may change the direction of the arrows by exchanging the following lines
					outString += n.getStatement().getLine() + " -> " + controlDep.getStatement().getLine() + ";\n";
					// outString += controlDep.getStatement().getLine() + " -> " + n.getStatement().getLine() + ";\n";
				}

			}
			
		}
		
		String file = pool.getOutputFileName();
		if (doData) {
            file = file.replace(".java", ".dot");
        } else {
            file = file.replace(".java", "-ctrl.dot");
        }
		File outputFile = new File(file);
		FileWriter fw;
		try {
			fw = new FileWriter(outputFile);
			fw.write(outString + dataDependenicies + "}" );
			fw.flush();
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    public static void createPDGdotFile(HashMap<Integer, Node> tree, ArrayList<Integer> slice){
	    createPDGdotFile(tree, slice, false); // just control dependencies
        createPDGdotFile(tree, slice, true); // all dependencies
    }
	
}
