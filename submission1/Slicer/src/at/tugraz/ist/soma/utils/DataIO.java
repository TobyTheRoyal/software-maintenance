package at.tugraz.ist.soma.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import at.tugraz.ist.soma.DataPool;

/**
 * This class handles the parsing of the console arguments and the output of the end result (slice).
 * @author Stephan Frühwirt
 */
public class DataIO {

	/**
	 * parses the console arguments. There should be exactly two arguments:
	 * the path to an input (.java) file and a slicing criterion.
	 */
	public static String parseInput(String[] args){
		
		DataPool pool = DataPool.getInstance();
		
		if(args.length < 2){
			System.err.println("Error: Please specify an input file and a slicing criterion, e.g. \"(13,{z})\"");
			System.exit(-1);
		}
		
		if(args.length == 3 && args[2].equals("debug")){
			pool.setDebug();
		}
		
		pool.setInputFileName(args[0]);
		pool.setSlicingCriterion(args[1]);
		
		return args[0];
	}
	
	/**
	 * prints the slice to the console. The formatting is done for you!
	 */
	public static void printSlice(ArrayList<Integer> slice){
		System.out.println(slice);
	}

}
