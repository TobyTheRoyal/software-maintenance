package at.tugraz.ist.soma;

import at.tugraz.ist.soma.FPA.FPANode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * This class handles the parsing of the console arguments and outputs the final results
 * @author Sandra Brettschuh
 */
public class IOHandler {

    /**
     * Parses the console inputs:
     * Should look like: java -jar Slicer.jar input.java "(13,{z});(14,{x})"
     * @return the filename
     */
    public static String parseInput(String[] args){
        DataPoolExtended pool = DataPoolExtended.getInstance();

        if(args.length < 2) {
            System.err.println("Error: Please specify an input file and a slicing criterion, e.g. (13,{z});(14,{x})");
            System.exit(-1);
        }

        if(args.length == 3 && args[2].equals("debug")){
            pool.setDebug();
        }
        else if(args.length == 3 && args[2].equals("deeptest")){
            pool.setDeepTest();
        }
        else if(args.length == 3){
            System.err.println("Error: You can only use 'debug' (for debug output) or 'deeptest' (for getting the whole table printed) as third parameter.");
            System.exit(-1);
        }

        pool.setInputFileName(args[0]);

        //parse the input-backward slicing criterion
        String[] criterions = args[1].split(";");

        for(String criteria : criterions) {
            criteria = criteria.replaceAll("[() {}]", "");
            String[] arr = criteria.split(",");

            if(arr.length < 1) {
                System.err.println("Error: Please specify an input file and a slicing criterion, e.g. (13,{z});(14,{x})");
                System.exit(-1);
            }

            try{
                int lineNr = Integer.parseInt(arr[0]);
                HashSet<String> tmp = new HashSet<>();
                tmp.addAll(Arrays.asList(arr).subList(1, arr.length));
                pool.addBackwardSlicingCriteria(lineNr, tmp);
            }catch (NumberFormatException e){
                System.err.println("Error: Please specify an input file and a slicing criterion, e.g. (13,{z});(14,{x})");
                System.exit(-1);
            }
        }

        return args[0];
    }

    /**
     * Creates the output file that will be tested on the test server
     * DO NOT CHANGE THIS METHOD !!!
     */
    public static void printToOutputFile(){
        DataPoolExtended pool = DataPoolExtended.getInstance();

        StringBuilder output = new StringBuilder();
        for(Map.Entry<DataPoolExtended.BackwardSlicingCriteria, ArrayList<FPANode>> entry : pool.getBackwardSlicingTables().entrySet()){
            output.append("BwdSlice: " + entry.getKey().toString() + " -> " + printTableSlice(entry.getValue()) + "\n");
        }

        output.append("FwdSlice: " + pool.getForwardSlicingCriteria() + " -> " + printTableSlice(pool.getForwardSlicingTable()) + "\n");

        if(!pool.getDeepTest()) {
            System.out.print(output.toString());
        }

        String file = pool.getOutputFileName();
        File outputFile = new File(file.replace(".java", "-slice.log"));

        FileWriter fw;
        try {
            fw = new FileWriter(outputFile);
            fw.write(output.toString());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the output file that may be tested on the test server ;)
     * DO NOT CHANGE THIS METHOD !!!
     */
    public static void printToOutputFileDeepTest(){
        DataPoolExtended pool = DataPoolExtended.getInstance();

        StringBuilder output = new StringBuilder();
        for(Map.Entry<DataPoolExtended.BackwardSlicingCriteria, ArrayList<FPANode>> entry : pool.getBackwardSlicingTables().entrySet()){
            output.append("BwdSlice: " + entry.getKey().toString() + " -> " + printTableSlice(entry.getValue()) + "\n");
            output.append(printTable(entry.getValue()));
            output.append("--------------------------------------------------\n");
        }

        output.append("FwdSlice: " + pool.getForwardSlicingCriteria() + " -> " + printTableSlice(pool.getForwardSlicingTable()) + "\n");
        output.append(printTable(pool.getForwardSlicingTable()));

        if(pool.getDeepTest()){
            System.out.print(output.toString());
        }

        String file = pool.getOutputFileName();
        File outputFile = new File(file.replace(".java", "-tables.log"));

        FileWriter fw;
        try {
            fw = new FileWriter(outputFile);
            fw.write(output.toString());
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates the slice string
     * DO NOT CHANGE THIS METHOD !!!
     */
    private static String printTableSlice(ArrayList<FPANode> table){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");
        for(FPANode node : table){
            if(node.isInSlice())
                stringBuilder.append(node.getLineNumber() + ",");
        }
        if(!table.isEmpty())
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("}");
        return stringBuilder.toString();
    }

    /**
     * Prints the table
     * DO NOT CHANGE THIS METHOD !!!
     */
    private static String printTable(ArrayList<FPANode> table){
        StringBuilder stringBuilder = new StringBuilder();
        for(FPANode node : table){
            stringBuilder.append(node.getLineNumber() + ": " + node.getString(node.getDef())
                    + node.getString(node.getRef())
                    + node.getString(node.getGen())
                    + node.getString(node.getKill())
                    + node.getString(node.getIn())
                    + node.getString(node.getOut())
                    + " -> " + (node.isInSlice()?"T":"F") + "\n");
        }
        return stringBuilder.toString();
    }
}
