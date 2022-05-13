package at.tugraz.ist.soma;

import at.tugraz.ist.soma.antlr.gen.JavaLexer;
import at.tugraz.ist.soma.antlr.gen.JavaParser;
import at.tugraz.ist.soma.cfg.CFGgenerator;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import at.tugraz.ist.soma.DataPoolExtended;
import at.tugraz.ist.soma.FPA.FPAlgorithms;
import at.tugraz.ist.soma.IOHandler;

import java.util.ArrayList;

/**
 * This is the main class.
 * In this assignment you have to implement backward and forward slicing.
 * @author Sandra Brettschuh
 */

/*
 * life motto:
 * if(sad() == true){
 * 		sad().stop();
 * 		beAwesome();
 * }
 */

public class Main {

    public static void main(String[] args) throws Exception {
        IOHandler.parseInput(args);
        ANTLRInputStream input = new ANTLRFileStream(DataPool.getInstance().getInputFileName());
        JavaLexer lexer = new JavaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);

        ParseTree tree = parser.compilationUnit();
        new FirstPassVisitor().visit(tree);
        new SecondPassVisitor().visit(tree);

        CFGgenerator cfgGenerator = new CFGgenerator();
        cfgGenerator.generate();

        DataPoolExtended pool = DataPoolExtended.getInstance();

        FPAlgorithms fs = new FPAlgorithms();
        fs.startTheSlicingParty();

        IOHandler.printToOutputFile();
        IOHandler.printToOutputFileDeepTest();
    }
}
