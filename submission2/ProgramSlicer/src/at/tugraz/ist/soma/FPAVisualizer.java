package at.tugraz.ist.soma;

import at.tugraz.ist.soma.FPA.FPANode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

/**
 * This class outputs the calculated tables into html files if you are in debug mode
 */
public class FPAVisualizer {
    private static DataPoolExtended pool = DataPoolExtended.getInstance();
    FPAVisualizer() {}

    /**
     * Outputs all backward slicing and the forward slicing table into an html file for easier debugging and testing
     */
    public static void outputTables(){
        if(!pool.getDebug())
            return;

        StringBuilder outstr = new StringBuilder();
        outstr.append("<html><body>");

        for(Map.Entry<DataPoolExtended.BackwardSlicingCriteria, ArrayList<FPANode>> bwdTables : pool.getBackwardSlicingTables().entrySet()){
            outstr.append(
                    "<h1 style=\"color: blue; font-size: 1em;\">Backward Slice: " + bwdTables.getKey() + "</h1>\n"
            );
            outstr.append(outputTable(bwdTables.getValue()));
        }

        outstr.append(
                "<h1 style=\"color: blue; font-size: 1em;\">Forward Slice: " + pool.getForwardSlicingCriteria() + "</h1>\n"
        );
        outstr.append(outputTable(pool.getForwardSlicingTable()));

        outstr.append("</body></html>");

        String outputName = pool.getOutputFileName();
        File file = new File(outputName.replace(".java", ".html"));

        try {
            FileWriter fw = new FileWriter(file);
            fw.write(outstr.toString());
            fw.flush();
            fw.close();
        } catch (IOException var8) {
            var8.printStackTrace();
        }
    }

    /**
     * Outputs one table
     */
    private static String outputTable(ArrayList<FPANode> table){
        StringBuilder outstr = new StringBuilder();
        outstr.append(
                "<table style=\"width:100%\">\n" +
                        "  <tr>" +
                        "    <th>n</th>" +
                        "    <th>def</th>" +
                        "    <th>ref</th>" +
                        "    <th>gen</th>" +
                        "    <th>kill</th>" +
                        "    <th>in</th>" +
                        "    <th>out</th>" +
                        "    <th>INFL</th>" +
                        "    <th>inSlice</th>" +
                        "  </tr>\n"
        );

        for(FPANode node : table){
            outstr.append(
                    "<tr>" +
                            "<td align=\"center\">" + node.getLineNumber() + "</td>" +
                            "<td align=\"center\">" + node.getString(node.getDef()) + "</td>" +
                            "<td align=\"center\">" + node.getString(node.getRef()) + "</td>" +
                            "<td align=\"center\">" + node.getString(node.getGen()) + "</td>" +
                            "<td align=\"center\">" + node.getString(node.getKill()) + "</td>" +
                            "<td align=\"center\">" + node.getString(node.getIn()) + "</td>" +
                            "<td align=\"center\">" + node.getString(node.getOut()) + "</td>" +
                            "<td align=\"center\">" + node.getString(node.getInfluencedNodes()) + "</td>" +
                            "<td align=\"center\">" + (node.isInSlice()? "true" : "false") + "</td>" +
                            "</tr>\n"
            );
        }

        outstr.append(
                "</table>"
        );

        return outstr.toString();
    }

}
