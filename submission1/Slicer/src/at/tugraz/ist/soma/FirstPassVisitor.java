package at.tugraz.ist.soma;

import at.tugraz.ist.soma.antlr.gen.JavaBaseVisitor;
import at.tugraz.ist.soma.antlr.gen.JavaParser.ElseStmtContext;
import at.tugraz.ist.soma.antlr.gen.JavaParser.ExpressionContext;
import at.tugraz.ist.soma.antlr.gen.JavaParser.ForControlContext;
import at.tugraz.ist.soma.antlr.gen.JavaParser.ForStmtContext;
import at.tugraz.ist.soma.antlr.gen.JavaParser.IfStmtContext;
import at.tugraz.ist.soma.antlr.gen.JavaParser.LocalVariableDeclarationContext;
import at.tugraz.ist.soma.antlr.gen.JavaParser.ParExpressionContext;
import at.tugraz.ist.soma.antlr.gen.JavaParser.StatementExpressionContext;
import at.tugraz.ist.soma.antlr.gen.JavaParser.TypeContext;
import at.tugraz.ist.soma.antlr.gen.JavaParser.VariableDeclaratorsContext;
import at.tugraz.ist.soma.antlr.gen.JavaParser.WhileStmtContext;
import at.tugraz.ist.soma.statements.Assignment;
import at.tugraz.ist.soma.statements.ElseStatement;
import at.tugraz.ist.soma.statements.ForLoop;
import at.tugraz.ist.soma.statements.IfStatement;
import at.tugraz.ist.soma.statements.Statement;
import at.tugraz.ist.soma.statements.VariableDeclaration;
import at.tugraz.ist.soma.statements.WhileLoop;

/**
 * This class creates a {@link at.tugraz.ist.soma.statements.Statement} for each line of the input file.
 * This is done by using the auto generated visitor methods from the ANTLR-framework.
 * @author Stephan Frühwirt
 *
 */

/*
 * This code works.
 * I don't know why.
 * It's kinda magic...
 */

public class FirstPassVisitor extends JavaBaseVisitor<String>{
	
	private DataPool pool = DataPool.getInstance();
	
	@Override
	public String visitLocalVariableDeclaration(LocalVariableDeclarationContext ctx) {
		String type = visit(ctx.type());
		String declaration = visit(ctx.variableDeclarators());
		
		Statement s = new VariableDeclaration(ctx.getStart().getLine(), type + " " + declaration);
		pool.addStatement(s);
		
		return type + " " + declaration;
	}
	
	@Override
	public String visitType(TypeContext ctx) {
		return ctx.getText();
	}
	
	@Override
	public String visitVariableDeclarators(VariableDeclaratorsContext ctx) {
		return ctx.getText();
	}
	
	@Override
	public String visitStatementExpression(StatementExpressionContext ctx) {
		String expr = visit(ctx.expression());

		Statement s = new Assignment(ctx.getStart().getLine(), expr);
		pool.addStatement(s);
		
		return "";
	}
	
	@Override
	public String visitExpression(ExpressionContext ctx) {
		return ctx.getText();
	}
	
	@Override
	public String visitIfStmt(IfStmtContext ctx) {
		String expr = visit(ctx.parExpression());
		
		Statement s = new IfStatement(ctx.getStart().getLine(), "if" + expr);
		pool.addStatement(s);
		
		visit(ctx.statement());
		
		return "";
	}
	
	@Override
	public String visitParExpression(ParExpressionContext ctx) {
		return ctx.getText();
	}
	
	@Override
	public String visitElseStmt(ElseStmtContext ctx) {
		Statement s = new ElseStatement(ctx.getStart().getLine(), "else");
		pool.addStatement(s);
		
		visit(ctx.statement());
		
		return "";
	}
	
	@Override
	public String visitWhileStmt(WhileStmtContext ctx) {
		String expr = visit(ctx.parExpression());
		
		Statement s = new WhileLoop(ctx.getStart().getLine(), "while" + expr);
		pool.addStatement(s);
		
		visit(ctx.statement());
		
		return "";
	}
	
	@Override
	public String visitForStmt(ForStmtContext ctx) {
		String expr = visit(ctx.forControl());
		
		Statement s = new ForLoop(ctx.getStart().getLine(), "for(" + expr + ")");
		pool.addStatement(s);
		
		visit(ctx.statement());
		
		return "";
	}
	
	@Override
	public String visitForControl(ForControlContext ctx) {
		return ctx.getText();
	}
	
}
