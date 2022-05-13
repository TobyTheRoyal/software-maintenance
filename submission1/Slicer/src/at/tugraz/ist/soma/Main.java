package at.tugraz.ist.soma;

import org.antlr.v4.runtime.ANTLRFileStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import at.tugraz.ist.soma.algorithms.StaticSlicer;
import at.tugraz.ist.soma.antlr.gen.JavaLexer;
import at.tugraz.ist.soma.antlr.gen.JavaParser;
import at.tugraz.ist.soma.cfg.CFGgenerator;
import at.tugraz.ist.soma.utils.DataIO;

/**
 * This is the main class. The slicing algorithm can be chosen in this class.
 * In this assignment you have to implement static slicing using a PDG.
 * @author Stephan Frühwirt
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
		DataIO.parseInput(args);
		ANTLRInputStream input = new ANTLRFileStream(DataPool.getInstance().getInputFileName());
		JavaLexer lexer = new JavaLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		JavaParser parser = new JavaParser(tokens);
		
		ParseTree tree = parser.compilationUnit();
		
		new FirstPassVisitor().visit(tree);
		
		new SecondPassVisitor().visit(tree);
		//DataPool.getInstance().printStatements();
		
		CFGgenerator cfgGenerator = new CFGgenerator();
		cfgGenerator.generate();
		
		StaticSlicer slicer = new StaticSlicer();
		slicer.calculateSlice();
		
	}

}
