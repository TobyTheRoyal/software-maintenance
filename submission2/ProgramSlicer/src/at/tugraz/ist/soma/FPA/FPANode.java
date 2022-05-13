package at.tugraz.ist.soma.FPA;

import at.tugraz.ist.soma.cfg.CFGNode;

import java.util.*;

/**
 * This class represents an FPANode. It stores the information of all the sets needed for calculating an forward slice.
 * @author Sandra Brettschuh
 *
 */
public class FPANode {
    private int lineNumber;
    private HashSet<String> def;
    private HashSet<String> ref;
    private HashSet<String> gen;
    private HashSet<String> kill;
    private HashSet<String> in;
    private HashSet<String> out;
    private HashSet<FPANode> succ;
    private HashSet<FPANode> pred;
    boolean inSlice;
    CFGNode cfgnode;

    private ArrayList<FPANode> influencedNodes;

    /**
     * Creates the FPANode
     */
    public FPANode(int lineNr){
        lineNumber = lineNr;
        def = new HashSet<>();
        ref = new HashSet<>();
        gen = new HashSet<>();
        kill = new HashSet<>();
        in = new HashSet<>();
        out = new HashSet<>();
        succ = new HashSet<>();
        pred = new HashSet<>();

        inSlice = false;
        cfgnode = null;
        influencedNodes = new ArrayList<>();
    }

    public int getLineNumber() { return lineNumber; }

    public HashSet<String> getDef() { return def; }
    public void addDef(String def) { this.def.add(def); }
    public void addDef(HashSet<String> def){ this.def.addAll(def); }

    public HashSet<String> getRef() { return ref; }
    public void addRef(String ref) { this.ref.add(ref); }
    public void addRef(HashSet<String> ref){ this.ref.addAll(ref); }

    public HashSet<String> getGen() { return gen; }
    public void addGen(String gen) { this.gen.add(gen); }
    public void addGen(HashSet<String> gen){ this.gen.addAll(gen); }

    public HashSet<String> getKill() { return kill; }
    public void addKill(String kill) { this.kill.add(kill); }
    public void addKill(HashSet<String> kill) { this.kill.addAll(kill); }

    public HashSet<String> getIn() { return in; }
    public void addIn(String in) { this.in.add(in); }
    public void addIn(HashSet<String> in) { this.in.addAll(in); }

    public HashSet<String> getOut() { return out; }
    public void addOut(String out) { this.out.add(out); }
    public void addOut(HashSet<String> out) { this.out.addAll(out); }

    public HashSet<FPANode> getSucc() { return succ; }
    public void addSucc(FPANode succ) { this.succ.add(succ); }
    public void addSucc(HashSet<FPANode> succ) { this.succ.addAll(succ); }

    public HashSet<FPANode> getPred() { return pred; }
    public void addPred(FPANode pred) { this.pred.add(pred); }
    public void addPred(HashSet<FPANode> pred) { this.pred.addAll(pred); }

    public boolean isInSlice() { return inSlice; }
    public void setInSlice(boolean inSlice) { this.inSlice = inSlice; }

    public ArrayList<FPANode> getInfluencedNodes() { return influencedNodes; }
    public void addInfluencedNode(FPANode fsNode) { influencedNodes.add(fsNode); }
    public void addInfluencedNode(ArrayList<FPANode> fsNodes) { influencedNodes.addAll(fsNodes); }

    public String getString(HashSet<String> mySet){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");

        List<String> sorted = new ArrayList<String>();
        sorted.addAll(mySet);
        Collections.sort(sorted);

        for(String str : sorted){
            stringBuilder.append(" " + str + ",");
        }

        if(mySet.size() > 0)
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);

        stringBuilder.append(" }");
        return stringBuilder.toString();
    }

    public String getString(ArrayList<FPANode> mySet){
        Collections.sort(mySet, new Comparator<FPANode>() {
            @Override
            public int compare(FPANode lhs, FPANode rhs) {
                return Integer.compare(lhs.getLineNumber(), rhs.getLineNumber());
            }
        });

        StringBuilder stringBuilder = new StringBuilder();
        for(FPANode fsNode : mySet){
            stringBuilder.append(" " + fsNode.getLineNumber() + ",");
        }

        if(mySet.size() > 0)
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    public void print() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(lineNumber + ": " + (inSlice? "true" : "false") + "\n");
        stringBuilder.append("- def: " + getString(def) + "\n");
        stringBuilder.append("- ref: " + getString(ref) + "\n");
        stringBuilder.append("- gen: " + getString(gen) + "\n");
        stringBuilder.append("- kill: " + getString(kill) + "\n");
        stringBuilder.append("- in: " + getString(in) + "\n");
        stringBuilder.append("- out: " + getString(out) + "\n");

        stringBuilder.append("- influences:" + getString(influencedNodes) + "\n");

        stringBuilder.append("\n");

        System.out.println(stringBuilder.toString());
    }
}
