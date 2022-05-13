package at.tugraz.ist.soma.algorithms;

import java.util.*;

import at.tugraz.ist.soma.DataPool;
import at.tugraz.ist.soma.cfg.CFGNode;
import at.tugraz.ist.soma.statements.Statement;
import at.tugraz.ist.soma.utils.DataIO;
import at.tugraz.ist.soma.utils.Type;
import at.tugraz.ist.soma.utils.Visualizer;

/**
 * This is the class where you should implement static slicing using a PDG.
 * @author Stephan Frühwirt
 */
public class StaticSlicer {

    private DataPool pool = DataPool.getInstance();
    ArrayList<Integer> slice = new ArrayList<>();

    /**
     * calls all necessary methods for calculating the slice.
     * You can use the {@link at.tugraz.ist.soma.utils.Visualizer#createPDGdotFile(HashMap, ArrayList)}
     * to get a graphical representation of your graph!
     */
    public void calculateSlice() {
        HashMap<Integer, Node> tree = generateControlDependencies();
        generateDataDependencies(tree);
        startTheSlicingParty(tree);

        Visualizer.createPDGdotFile(tree, slice);
        DataIO.printSlice(slice);
    }

    /**
     * generates the control dependencies of the PDG.
     *
     * @return the generated PDG
     */
    private HashMap<Integer, Node> generateControlDependencies() {
        ArrayList<CFGNode> cfg = pool.getControlFlowGraph();

        HashMap<Integer, Node> tree = new LinkedHashMap<Integer, Node>();

        // TODO: calculate the control dependencies

        if (pool.getDebug()) {
            System.out.println("## Begin Control Dependencies ##");
            System.out.println("Adding to tree");
            System.out.println("0 - root");
        }

        Node root = new Node();
        tree.put(0, root);

        // convert all CFGNodes to Nodes and add them to the tree
        // the line numbers are the "keys"
        // tree start, end statement == NULL (Ignore)
        Node node;
        for (CFGNode element : cfg) {
            if (element.getStatement() != null) {
                node = new Node(element.getStatement());
                if (element.isAfterElse()) node.afterElse(); // copy afterElse
                tree.put(node.getStatement().getLine(), node);
                if (pool.getDebug()) System.out.println(String.format("%d - %s",
                    node.getStatement().getLine(), node.getStatement().getText()));
            }
        }

        if (pool.getDebug()) System.out.println("Adding control dependencies");

        // go through the cfg once more, for every node check influencedBy
        // find the node of influencedBy in the tree (by line number) and add it as control dependency
        // if you encounter influencedBy = null, add it to the tree root
        Node elementNode;
        CFGNode influence;
        Node influenceNode;
        for (CFGNode element : cfg)
        {
            if (element.getStatement() == null)
                continue;

            elementNode = tree.get(element.getStatement().getLine());
            influence = element.getInfluence();
            if (influence != null && influence.getStatement() != null) {
                influenceNode = tree.get(influence.getStatement().getLine());
                influenceNode.appendControlDependency(elementNode);
                elementNode.influencedBy(influenceNode);
                if (pool.getDebug()) System.out.println(String.format("Connect %d with %d",
                    influenceNode.getStatement().getLine(), elementNode.getStatement().getLine()));
            } else {
                root.appendControlDependency(elementNode);
                if (pool.getDebug()) System.out.println(String.format("Connect root with %d",
                    elementNode.getStatement().getLine()));
            }
        }

        if (pool.getDebug()) System.out.println("## End Control Dependencies ##\n");

        return tree;
    }

    /**
     * adds the data dependencies to the generated PDG.
     * @param tree the PDG which was created in {@link at.tugraz.ist.soma.algorithms.StaticSlicer#generateControlDependencies()}.
     */
    private void generateDataDependencies(HashMap<Integer, Node> tree) {
        // TODO: calculate the data dependencies

        if (pool.getDebug()) {
            System.out.println("## Begin Data Dependencies ##");
        }

        for (Node node : tree.values()) {
            if (node.getStatement() != null)
                findDependencies(tree, node);
        }

        if (pool.getDebug()) {
            System.out.println("## End Data Dependencies ##\n");
        }

    }


    /**
     * calculates the slice of a given PDG.
     */
    private void startTheSlicingParty(HashMap<Integer, Node> tree) {
        slice.clear();

        String criterion = pool.getSlicingCriterion();

        ArrayList<String>criterionVariables = new ArrayList<>();

        if (pool.getDebug()) {
            System.out.println("## Begin Slicing ##");
            System.out.println(String.format("Criterion: %s", criterion));
        }

        criterion = criterion.replaceAll("[() {}]", "");
        String[] arr = criterion.split(",");
        int criterionLine = Integer.parseInt(arr[0]);

        for(int i = 1; i < arr.length; i++){
            criterionVariables.add(arr[i]);
        }

        // TODO: calculate the slice using the criterionLine variable and
        //			the ArrayList criterionVariables containing all variables
        //			from the slicing criterion (as used in public2.java)

        Node init = tree.get(criterionLine);
        if (init != null) {
            slice.add(criterionLine);
            if (pool.getDebug()) System.out.println("Add criterion");
        } else {
            if (pool.getDebug()) System.out.println("Criterion not available");
        }

        Node child;
        Iterator iter = tree.get(0).getControlDependencies().entrySet().iterator(); // root children
        // going from bottom to top over all statements
        // statements in first layer are all executed, only add the last definition of the slicing variables
        while (iter.hasNext()) {
            child = (Node) ((Map.Entry) iter.next()).getValue();

            prepareSlice(child, criterionVariables);

            // first layer
            if (contains(child.getStatement().getDef(), criterionVariables)) {
                criterionVariables.removeAll(child.getStatement().getDef());
                if (!slice.contains(child.getStatement().getLine())) {
                    slice.add(child.getStatement().getLine());
                    if (pool.getDebug())
                        System.out.println(String.format("Add top %d", child.getStatement().getLine()));
                }
            }
        }

        Node line;
        int ctrl;
        int data;
        // expands!
        // loop through the slice and for every line in it add all data and control dependencies
        // because it expands this automatically follows all the dependencies (if needed)
        for (int i = 0; i < slice.size(); i++) {
            line = tree.get(slice.get(i));

            if (line.getInfluence() != null && line.getInfluence().getStatement() != null) {
                // add parent (control dependency)
                ctrl = line.getInfluence().getStatement().getLine();
                if (!slice.contains(ctrl)) {
                    slice.add(ctrl);
                    if (pool.getDebug())
                        System.out.println(String.format("Add from control %d", ctrl));
                }
            }

            // data dependencies
            iter = line.getDataDependencies().entrySet().iterator();
            while (iter.hasNext()) {
                child = (Node) ((Map.Entry) iter.next()).getValue();
                data = child.getStatement().getLine();
                if (!slice.contains(data)) {
                    slice.add(data);
                    if (pool.getDebug())
                        System.out.println(String.format("Add from data %d", data));
                }
            }
        }

        if (pool.getDebug()) System.out.println("## End Slicing ##\n");

        Collections.sort(slice);
    }

    /**
     * checks if a set contains at least one variable of a second set.
     */
    private boolean contains(HashSet<String> set1, HashSet<String> set2){
        for(String string : set1){
            if(set2.contains(string)){
                return true;
            }
        }
        return false;
    }
    private boolean contains(HashSet<String> set1, ArrayList<String> vars) {
        for(String string : set1){
            if(vars.contains(string)){
                return true;
            }
        }
        return false;
    }

    /**
     * Travel up the tree to find the dependencies of a node
     * Also travels down for each node that wasn't just visited
     * When called without the last two arguments inits cur with node and ref with the referenced variables of node
     * @param tree full tree of nodes
     * @param node node to add dependency to
     * @param cur current node
     * @param ref set of referenced variables
     */
    private void findDependencies(HashMap<Integer, Node> tree, Node node, Node cur, HashSet<String> ref) {
        // no more referenced variables to check
        // cannot go higher than root
        if (ref.isEmpty() || cur.getStatement() == null)
            return;

        // go up and check all subnodes
        Node parent;
        Node child;
        Boolean isLoop = false;
        if (cur.getInfluence() == null) {
            parent = tree.get(0); // root
            isLoop = false;
        }  else {
            parent = tree.get(cur.getInfluence().getStatement().getLine()); // parent node
            isLoop = (parent.getStatement().getType() == Type.LOOP_FOR ||
                parent.getStatement().getType() == Type.LOOP_WHILE);
        }
        // if parent is a loop, also add the ones before the current statement
        Boolean doAdd = isLoop;

        Iterator iter = parent.getControlDependencies().entrySet().iterator();
        while (iter.hasNext()) {
            child = (Node) ((Map.Entry) iter.next()).getValue();
            //iter.remove();

            // all nodes after this one come before in the code and this node can therefore be data dependent on them
            // exception: loops, where doAdd is already set to true
            if (child.getStatement().getLine() == cur.getStatement().getLine()) {
                doAdd = true;
                // if it's a loop we need to recheck the dependencies
                // some statements might not have been considered (nested)
                if (isLoop) {
                    if (pool.getDebug()) System.out.println("Rechecking children (is loop)");
                    checkDependencies(node, child, ref, true, true);
                } else if (child.isAfterElse()) break;

                continue;
            }

            // add all dependencies through this child node
            if (doAdd) checkDependencies(node, child, ref, isLoop, false);

            // don't consider if-branch if this is the else branch and not in a loop
            if (doAdd && !isLoop && child.isAfterElse())
                break;
        }

        // go higher until we're at the root
        if (parent.getStatement() != null && !ref.isEmpty()) {
            findDependencies(tree, node, parent, ref);
        }
    }
    private void findDependencies(HashMap<Integer, Node> tree, Node node) {
        if (node.getStatement().getRef().isEmpty())
            return;

        // copy ref
        HashSet<String> ref = new HashSet<String>();
        ref.addAll(node.getStatement().getRef());

        if (pool.getDebug()) {
            System.out.println(String.format("Examining %d: %s", node.getStatement().getLine(),
                node.getStatement().getText()));
        }

        // check possible previous iterations
        if (node.getStatement().getType() == Type.LOOP_WHILE ||
            node.getStatement().getType() == Type.LOOP_FOR
        ) {
            if (pool.getDebug()) System.out.println("is loop: true");
            checkDependencies(node, node, ref);
        }

        findDependencies(tree, node, node, ref);
    }

    /**
     * Checks if node is data dependent on cur
     * If cur is a block it travels deeper
     * @param node node to add dependencies to
     * @param cur node to check
     * @param ref list of referenced variables
     * @param inBlock currently in a block? if so, doesn't remove defined variables from set (only conditionally defd)
     */
    private void checkDependencies(Node node, Node cur, HashSet<String> ref, Boolean inBlock, Boolean stopOnLoop) {
        if (ref.isEmpty()) return;

        if (pool.getDebug()) {
            System.out.print(String.format("checking %d with ", cur.getStatement().getLine()));
            Iterator iter = ref.iterator();
            while (iter.hasNext()) {
                System.out.print(String.format("%s ", iter.next()));
            }
            System.out.println();
        }

        // add dependencies
        // at least one of the referenced variables was defined here,
        // remove the defined variables and connect the nodes
        if (contains(cur.getStatement().getDef(), ref)) {
            if (!inBlock) ref.removeAll(cur.getStatement().getDef());
            node.appendDataDependency(cur);
            if (pool.getDebug()) {
                System.out.println(String.format("Connect %d with %d", node.getStatement().getLine(),
                    cur.getStatement().getLine()));
            }
        }

        // don't go deeper if stopOnLoop
        // is only set if loop has already been considered (ie when rechecking branches)
        if (stopOnLoop &&
            (cur.getStatement().getType() == Type.LOOP_WHILE || cur.getStatement().getType() == Type.LOOP_FOR))
            return;

        // go deeper into blocks
        Node child;
        Iterator iter = cur.getControlDependencies().entrySet().iterator();
        while (iter.hasNext()) {
            child = (Node) ((Map.Entry) iter.next()).getValue();
            //iter.remove();

            // add all dependencies through this child node
            checkDependencies(node, child, ref, true, stopOnLoop);
        }
    }
    private void checkDependencies(Node node, Node cur, HashSet<String> ref) {
        checkDependencies(node, cur, ref, true, false);
    }

    /**
     * Adds all statements that define one or more of the criterion variables to the slice recursively
     * Only from second layer (conditional) onwards
     * @param block current block (condition)
     * @param criterionVariables list of variables
     */
    private void prepareSlice(Node block, ArrayList<String> criterionVariables) {
        Node child;
        Iterator iter = block.getControlDependencies().entrySet().iterator(); // block children
        // going from bottom to top over all statements
        // statements in first layer are all executed, only add the last definition of the slicing variables
        while (iter.hasNext()) {
            child = (Node) ((Map.Entry) iter.next()).getValue();

            // subsequent layers get added but don't remove from the list since they might but don't have to be executed
            if (contains(child.getStatement().getDef(), criterionVariables)) {
                if (!slice.contains(child.getStatement().getLine())) {
                    slice.add(child.getStatement().getLine());
                    if (pool.getDebug())
                        System.out.println(String.format("Add lower %d", child.getStatement().getLine()));
                }
            }

            // go deeper
            prepareSlice(child, criterionVariables);
        }
    }
}
