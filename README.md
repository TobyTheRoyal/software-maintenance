# Project Title: Program Slicer

## Overview

This project involves implementing static and dynamic program slicing techniques in Java. The primary objective is to analyze and extract relevant parts of a program based on slicing criteria using both backward and forward slicing methods.

## Features

- **Static Slicing**: Calculates the static slice for a given Java program using a Program Dependency Graph (PDG).
- **Backward Slicing**: Implements backward slicing to determine the set of program statements that affect the values at a particular program point.
- **Forward Slicing**: Calculates the forward slicing criteria and implements forward slicing to identify the impact of a specific statement on the program.
- **Control Flow Graph (CFG) Extension**: Extends the CFG to include all necessary nodes and edges for accurate slicing.
- **Automated Testing**: Utilizes a set of predefined public and private test cases to ensure the correctness and performance of the implemented slicers.

## Project Structure

- **src/**: Contains the source code for the slicing algorithms.
  - `FPAlgorithms.java`: Core slicing algorithm implementations.
  - `CFGNode.java`: Represents nodes in the Control Flow Graph.
  - `IOHandler.java`: Handles input and output operations.
- **testdata/**: Includes test cases for verifying the slicing implementations.
- **lib/**: Contains required libraries such as `cfgGenerator.jar` and `antlr-4.5-complete.jar`.
- **build.xml**: Ant build file for compiling the project and generating the executable JAR.

## Compilation and Execution

To build the project, run the following command:

```sh
ant jar

This will generate an executable JAR file named `ProgramSlicer_<GroupNr>.jar`.

To execute the program, use the command:

```sh
java -jar ProgramSlicer_<GroupNr>.jar <input-file> <slicing-criterias>

For detailed testing, you can add the `deeptest` keyword to output the calculation tables:
```sh
java -jar ProgramSlicer_<GroupNr>.jar <input-file> <slicing-criterias> deeptest

## Testing
To test your implementation, use the provided test cases in the testdata/ folder. Ensure that your implementation meets the output specifications by using the methods in IOHandler.java and FPANode.java.

