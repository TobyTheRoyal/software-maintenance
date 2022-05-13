package at.tugraz.ist.soma.FPA;

import at.tugraz.ist.soma.DataPoolExtended;
import at.tugraz.ist.soma.FPAVisualizer;
import at.tugraz.ist.soma.cfg.CFGNode;
import at.tugraz.ist.soma.statements.Statement;
import org.antlr.v4.codegen.model.SrcOp;

import java.util.*;

/**
 * This class will handle the backward and forward slicing
 * feel free to add any additional methods but do not change the signature of existing ones otherwise it may lead to
 * fails on the testing system
 */
public class FPAlgorithms {
    private DataPoolExtended pool;

    public FPAlgorithms() {
        pool = DataPoolExtended.getInstance();
    }

    /**
     * handles the whole backward and forward slicing procedure
     */
    public void startTheSlicingParty(){
        //calculate for each input criteria the backward slice
        for(DataPoolExtended.BackwardSlicingCriteria criteria : pool.getBackwardSlicingCriterias()){
            if(pool.getDebug()) {
                System.out.println("1. Calculate Backward Slice for: " + criteria.toString());
            }
            generateBackwardSlice(criteria);
        }

        //calculate the stmt that should be used for the calculation of the forward slice
        if(pool.getDebug()) {
            System.out.println("2. Calculate Forward Slice Criteria");
        }
        calculateForwardSlicingCriteria();

        //calculate the forward slice
        if(pool.getForwardSlicingCriteria() != -1) {
            if(pool.getDebug()) {
                System.out.println("3. Calculate Forward Slice for: " + pool.getForwardSlicingCriteria());
            }
            generateForwardSlice(pool.getForwardSlicingCriteria());
        }
        else if(pool.getDebug()) {
            System.out.println("3. No Forward Slice Criteria given.");
        }

        //additionally the visualizer creates an html file with the table in it if you are in debug mode
        FPAVisualizer.outputTables();
    }


    /**
     * In this method the backward slice is calculated
     * @param slicingCriteria the slicing criteria containing
     *                        slicingCriteria.lineNumber -> the line number of the stmt for which the slice should be calculated
     *                        slicingCriteria.variables  -> the variables given in the corresponding slicing criterion
     */
    public void generateBackwardSlice(DataPoolExtended.BackwardSlicingCriteria slicingCriteria){
        //here you already got the control flow graph and an empty bwdSlicing table
        ArrayList<CFGNode> cfg = pool.getControlFlowGraph();
        ArrayList<FPANode> bwdSlicingTable = new ArrayList<>();

        /**
         * TODO: 1. Calculate the Backward Slice
         */

        if (pool.getDebug()) {
            System.out.println("## BEGIN BACKWARD SLICE ##");
            System.out.println(" # Init from CFG #");
        }

        for (CFGNode cnode : cfg) {
            Statement stmt = cnode.getStatement();
            FPANode fnode = new FPANode(stmt.getLine());

            if (pool.getDebug())
                System.out.println(String.format("%2d : %s", stmt.getLine(), stmt.getText()));

            // slicing criterion always in slice
            if (stmt.getLine() == slicingCriteria.lineNumber)
                fnode.inSlice = true;

            // transfer def/ref
            fnode.addDef(stmt.getDef());
            if (pool.getDebug())
                System.out.print(String.format("  Def:  %s      ", fnode.getString(fnode.getDef())));
            fnode.addRef(stmt.getRef());
            if (pool.getDebug())
                System.out.println(String.format("  Ref: %s", fnode.getString(fnode.getRef())));

            // add kill
            fnode.addKill(stmt.getDef());
            if (pool.getDebug())
                System.out.print(String.format("  Kill: %s      ", fnode.getString(fnode.getKill())));
            // add gen
            // all ref if in slice or intersection def/in not empty
            // in is always empty, so an intersection with def is always empty
            // only in slice if slicing criterion, so add V and ref
            if (fnode.inSlice) {
                fnode.addGen(slicingCriteria.variables);
                fnode.addGen(fnode.getRef());
            }
            if (pool.getDebug())
                System.out.println(String.format("  Gen: %s", fnode.getString(fnode.getGen())));

            // transfer influence
            fnode.cfgnode = cnode;
            addInfluences(bwdSlicingTable, fnode);

            bwdSlicingTable.add(fnode);
        }

        if (pool.getDebug())
            System.out.println();

        for (FPANode fnode : bwdSlicingTable) {
            CFGNode cnode = fnode.cfgnode;

            // transfer successors
            if (pool.getDebug())
                System.out.print(String.format("%2d succ. by:", fnode.getLineNumber()));
            for (CFGNode csucc : cnode.getSuccessors()) {
                if (pool.getDebug())
                    System.out.print(String.format(" %2d", csucc.getStatement().getLine()));
                for (FPANode node : bwdSlicingTable) {
                    if (node.getLineNumber() == csucc.getStatement().getLine()) {
                        fnode.addSucc(node);
                        break;
                    }
                }
            }
            if (pool.getDebug())
                System.out.println();
        }

        if (pool.getDebug())
            System.out.println("\n # Update slice #");

        // loop as long as there are changes in in or out
        boolean hasChanged = true;
        while (hasChanged) {
            hasChanged = false;

            for (FPANode fnode : bwdSlicingTable) {
                // update in/out
                // in is merge of all successors' out
                int inSize = fnode.getIn().size();
                for (FPANode succ : fnode.getSucc()) {
                    fnode.addIn(succ.getOut());
                }
                // in changed?
                hasChanged = hasChanged || inSize != fnode.getIn().size();
                if (pool.getDebug() && inSize != fnode.getIn().size())
                    System.out.println(String.format("%2d: in changed: %s", fnode.getLineNumber(),
                            fnode.getString(fnode.getIn())));

                // out is gen merged with in without kill
                int outSize = fnode.getOut().size();
                HashSet<String> inNoKill = new HashSet<>(fnode.getIn());
                inNoKill.removeAll(fnode.getKill());
                fnode.addOut(fnode.getGen());
                fnode.addOut(inNoKill);
                // out changed?
                hasChanged = hasChanged || outSize != fnode.getOut().size();
                if (pool.getDebug() && outSize != fnode.getOut().size())
                    System.out.println(String.format("%2d: out changed: %s", fnode.getLineNumber(),
                            fnode.getString(fnode.getOut())));

                // update inSlice
                if (!fnode.inSlice) {
                    // calculate def int in
                    HashSet<String> intersection = new HashSet<>(fnode.getDef());
                    intersection.retainAll(fnode.getIn());
                    // find influences in slice
                    boolean hasInflInSlice = false;
                    for (FPANode infl : fnode.getInfluencedNodes()) {
                        if (infl.inSlice) {
                            hasInflInSlice = true;
                            break;
                        }
                    }

                    // if intersection def/in is not empty
                    // or is control flow statement and one or more influenced statements are in slice
                    if (intersection.size() != 0 || hasInflInSlice) {
                        fnode.inSlice = true;
                        // update gen
                        fnode.addGen(fnode.getRef());
                        if (pool.getDebug())
                            System.out.println(String.format("%2d: in slice, gen: %s",
                                    fnode.getLineNumber(), fnode.getString(fnode.getGen())));
                    }
                }
            }

            if (pool.getDebug())
                System.out.println(String.format("Repeat? %s", hasChanged ? "true" : "false"));
        }

        if (pool.getDebug())
            System.out.println("## END BACKWARD SLICE ##\n");

        //now the backward slice table is handed to our data pool in order to be tested
        //additionally the visualizer creates an html file with all tables in it if you are in debug mode
        pool.setBackwardSlicingTable(slicingCriteria, bwdSlicingTable);
    }

    /**
     * This method calculates the criteria for the forward slice
     * -> lowest number of the intersection of all backward slices
     */
    public void calculateForwardSlicingCriteria(){
        Map<DataPoolExtended.BackwardSlicingCriteria /*bwd-slicing-criteria*/, ArrayList<FPANode> /*bwd-slicing-table*/>
                bwdSlicingTables = pool.getBackwardSlicingTables();
        int fwdSlicingStmt = -1;

        /**
         * TODO: 2. Calculate the Forward FPA Criteria
         *          The Line Number that occurs in the most slices (if lines have the same number of occurrences choose the smallest)
         */
        if (bwdSlicingTables.size() > 1) {
            Hashtable<Integer, Integer> criteriaMap = new Hashtable<Integer, Integer>();
            for (ArrayList<FPANode> Nodes : bwdSlicingTables.values()) {
                for (FPANode Node : Nodes) {
                    if (Node.isInSlice()) {
                        criteriaMap.put(Node.getLineNumber(), criteriaMap.getOrDefault(Node.getLineNumber(), 0) + 1);
                    }
                }
            }
            int max = 0;
            for (Map.Entry mapElement : criteriaMap.entrySet()) {
                if ((int)mapElement.getValue() > max) {
                    max = (int)mapElement.getValue();
                    fwdSlicingStmt = (int) mapElement.getKey();
                } else if ((int)mapElement.getValue() == max && (int) mapElement.getKey() < fwdSlicingStmt) {
                    fwdSlicingStmt = (int) mapElement.getKey();
                }
            }
        } else {
            Map.Entry<DataPoolExtended.BackwardSlicingCriteria, ArrayList<FPANode>> element = bwdSlicingTables.entrySet().iterator().next();
            for(FPANode nd : element.getValue()) {
                if (nd.isInSlice()) {
                    if (pool.getDebug())
                        System.out.println(nd.getLineNumber());
                    fwdSlicingStmt = nd.getLineNumber();
                    break;
                }
            }
        }
        //Sets the forward Slicing Criteria (=line number)
        pool.setForwardSlicingCriteria(fwdSlicingStmt);
    }


    /**
     * In this method the forward slice is calculated
     * @param fwdSlicingStmt the line number of the stmt for which the slice should be calculated
     */
    public void generateForwardSlice(int fwdSlicingStmt) {
        //here you already got the control flow graph and an empty fwdSlicing table
        ArrayList<CFGNode> cfg = pool.getControlFlowGraph();
        ArrayList<FPANode> fwdSlicingTable = new ArrayList<>();

        /**
         * TODO: 3. Calculate the Forward Slice
         */

        //init the fwdSlicingTable
        for (CFGNode cfgNode : cfg) {
            FPANode fpaNode = new FPANode(cfgNode.getStatement().getLine());
            // set inSlice = true for the fwSlicing statement
            fpaNode.setInSlice(cfgNode.getStatement().getLine() == fwdSlicingStmt);
            // set kills for each stmt
            fpaNode.addKill(cfgNode.getStatement().getDef());
            // set Defs for each stmt
            fpaNode.addDef(cfgNode.getStatement().getDef());
            // set Refs for each stmt
            fpaNode.addRef(cfgNode.getStatement().getRef());
            // set Successors of each stmt
            for (CFGNode succ : cfgNode.getSuccessors()) {
                fpaNode.addSucc(new FPANode(succ.getStatement().getLine()));
            }
            // set influences
            fpaNode.cfgnode = cfgNode;
            addInfluences(fwdSlicingTable, fpaNode);

            fwdSlicingTable.add(fpaNode);
        }

        boolean hasChanged = true;

        while (hasChanged) {
            hasChanged = false;
            // compute relevant => FPA
            for (FPANode fpaNode : fwdSlicingTable) {
                // calc gen: if v is defined in the statement n and the statement n is already in the forward slice
                // or v is defined in the statement n and (v ∈ REF(n) ∧ v ∈ in(n)).
                if(fpaNode.getDef().size() > 0 && fpaNode.isInSlice()){
                    fpaNode.addGen(fpaNode.getDef());
                } else {
                    HashSet<String> gen = new HashSet<>(fpaNode.getIn());
                    gen.retainAll(fpaNode.getRef());
                    gen.retainAll(fpaNode.getDef());
                    fpaNode.addGen(gen);
                }
                // calc out: out(n) = gen(n) ∪ (in(n) \ kill(n))
                HashSet<String> out = new HashSet<>(fpaNode.getIn());
                out.removeAll(fpaNode.getKill());
                out.addAll(fpaNode.getGen());
                fpaNode.addOut(out);
                // calc in for the successors: in(n) is the out(n) of the predecessor
                for (FPANode succ : fpaNode.getSucc()) {
                    for (FPANode tableNode : fwdSlicingTable) {
                        if (tableNode.getLineNumber() == succ.getLineNumber() && !tableNode.getIn().containsAll(fpaNode.getOut())) {
                            tableNode.addIn(fpaNode.getOut());
                            hasChanged = true;
                        }
                    }
                }
            }
            // set inSlice: if ref(n) already intersects out(n) or if the line is one of the influences of a stmt that is in the slice
            for (FPANode fpaNode: fwdSlicingTable) {
                HashSet<String> ref_out_intersect = new HashSet<>(fpaNode.getRef());
                ref_out_intersect.retainAll(fpaNode.getOut());
                if(ref_out_intersect.size() > 0 && !fpaNode.isInSlice()) {
                    fpaNode.setInSlice(true);
                    hasChanged = true;
                } else if (fpaNode.isInSlice() && fpaNode.getInfluencedNodes().size() > 0) {
                    for (FPANode influenced : fpaNode.getInfluencedNodes()) {
                        if (!influenced.isInSlice()) {
                            influenced.setInSlice(true);
                            hasChanged = true;
                        }
                    }
                }
            }
        }

        //now the forward slice table is handed to our data pool in order to be tested
        //additionally the visualizer creates an html file with all tables in it if you are in debug mode
        pool.setForwardSlicingTable(fwdSlicingTable);
    }


    private void addInfluences(ArrayList<FPANode> table, FPANode node) {
        addInfluences(table, node, node);
    }


    private void addInfluences(ArrayList<FPANode> table, FPANode node, FPANode influence) {
        CFGNode cnode = influence.cfgnode;
        if (cnode.getInfluence() != null) {
            if (pool.getDebug())
                System.out.println(String.format("  inf. by %2d", cnode.getInfluence().getStatement().getLine()));
            // can only be before, so check and link
            CFGNode infl = cnode.getInfluence();
            for (FPANode inflf : table) {
                if (inflf.getLineNumber() == infl.getStatement().getLine()) {
                    inflf.addInfluencedNode(node);
                    addInfluences(table, node, inflf);
                    break;
                }
            }
        }
    }
}
