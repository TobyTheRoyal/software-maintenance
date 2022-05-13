package at.tugraz.ist.soma;

import at.tugraz.ist.soma.FPA.FPANode;
import at.tugraz.ist.soma.cfg.CFGNode;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Handles all the data that is needed for this assignment
 * Based on the DataPool of Assignment 1
 */
public class DataPoolExtended {
    private static DataPoolExtended instance = null;
    private static boolean isDeepTest = false;
    private DataPool oldPool = DataPool.getInstance();

    private DataPoolExtended(){
        backwardSlicingTables = new HashMap<>();
        forwardSlicingTable = new ArrayList<>();
        forwardSlicingCriteria = -1;
    }

    public static DataPoolExtended getInstance(){
        if(instance == null)
            instance = new DataPoolExtended();
        return instance;
    }

    // saves the Backward-Slicing Criteria and the corresponding FPA Nodes
    private Map<BackwardSlicingCriteria, ArrayList<FPANode> /*Node-Table*/> backwardSlicingTables;
    // saves the Forward-Slicing Criteria (initially -1 if not implemented)
    int forwardSlicingCriteria;
    // saves the forward slicing nodes
    private ArrayList<FPANode> forwardSlicingTable;

    /**
     * This class simply compresses the backward slicing criteria
     */
    public class BackwardSlicingCriteria {
        public final int lineNumber;
        public final HashSet<String> variables;

        BackwardSlicingCriteria(int lineNumber, HashSet<String> variables){
            this.lineNumber = lineNumber;
            this.variables = variables;
        }

        public String toString(){
            StringBuilder str = new StringBuilder();
            str.append("(" + lineNumber + ",{");
            for(String var : variables){
                str.append(var + ",");
            }
            str.deleteCharAt(str.length() - 1);
            str.append("})");
            return str.toString();
        }
    }

    //******************************************************************************************************************
    // getters and setters for the backward slicing criteria and tables
    public void addBackwardSlicingCriteria(int lineNumber, HashSet<String> variables){
        backwardSlicingTables.put(new BackwardSlicingCriteria(lineNumber, variables), new ArrayList<>());
        backwardSlicingTables = backwardSlicingTables.entrySet().stream()
                .sorted((e1, e2) -> Integer.compare(e1.getKey().lineNumber, e2.getKey().lineNumber))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

    }

    public Set<BackwardSlicingCriteria> getBackwardSlicingCriterias(){
        return backwardSlicingTables.keySet();
    }

    public Map<BackwardSlicingCriteria, ArrayList<FPANode>> getBackwardSlicingTables() {
        return backwardSlicingTables;
    }

    public void setBackwardSlicingTable(BackwardSlicingCriteria criteria, ArrayList<FPANode> backwardSlicingTable){
        Collections.sort(backwardSlicingTable, new Comparator<FPANode>() {
            @Override
            public int compare(FPANode lhs, FPANode rhs) {
                return Integer.compare(lhs.getLineNumber(), rhs.getLineNumber());
            }
        });
        backwardSlicingTables.put(criteria, backwardSlicingTable);
    }

    //******************************************************************************************************************
    // getters and setters for the forward slicing criteria and tables
    public void setForwardSlicingCriteria(int slicingStmt){
        forwardSlicingCriteria = slicingStmt;
    }

    public int getForwardSlicingCriteria() {
        return forwardSlicingCriteria;
    }

    public ArrayList<FPANode> getForwardSlicingTable() {
        return forwardSlicingTable;
    }

    public void setForwardSlicingTable(ArrayList<FPANode> forwardSlicingTable){
        this.forwardSlicingTable = forwardSlicingTable;

        Collections.sort(this.forwardSlicingTable, new Comparator<FPANode>() {
            @Override
            public int compare(FPANode lhs, FPANode rhs) {
                return Integer.compare(lhs.getLineNumber(), rhs.getLineNumber());
            }
        });
    }

    //******************************************************************************************************************
    // functions based on the DataPool from Assignment 1
    public void setInputFileName(String name){
        oldPool.setInputFileName(name);
    }

    public String getInputFileName(){
        return oldPool.getInputFileName();
    }

    public String getOutputFileName(){
        return oldPool.getOutputFileName();
    }

    public void setDebug(){
        oldPool.setDebug();
    }

    public boolean getDebug() {
        return oldPool.getDebug();
    }

    public void setDeepTest() { isDeepTest = true; }

    public boolean getDeepTest() { return isDeepTest; }

    /**
     * Returns the control flow graph (you can assume the cfg correct)
     * Note: 'new' methods of the CFGNode:
     *  - ArrayList<CFGNode> getPredecessors() -> returns the predecessors of the node
     *  - ArrayList<CFGNode> getSuccessors() -> returns the successors of the node
     *  e.g. (2)  int a = 1;     // predecessors = {}, successors = {3}
     *       (3)  while(a < 5){  // predecessors = {2}, successors = {4,6}
     *       (4)    a = a + 1;   // predecessors = {3}, successors = {3}
     *       (5)  }
     *       (6)  int b = a + 1; // predecessors = {3}, successors = {}
     */
    public ArrayList<CFGNode> getControlFlowGraph() {
        return oldPool.getControlFlowGraph();
    }
}
