package at.tugraz.ist.soma;

import at.tugraz.ist.soma.antlr.gen.JavaBaseVisitor;
import at.tugraz.ist.soma.antlr.gen.JavaParser.ElseStmtContext;
import at.tugraz.ist.soma.antlr.gen.JavaParser.ExpressionContext;
import at.tugraz.ist.soma.antlr.gen.JavaParser.ForStmtContext;
import at.tugraz.ist.soma.antlr.gen.JavaParser.IfStmtContext;
import at.tugraz.ist.soma.antlr.gen.JavaParser.PrimaryContext;
import at.tugraz.ist.soma.antlr.gen.JavaParser.StatementExpressionContext;
import at.tugraz.ist.soma.antlr.gen.JavaParser.VariableDeclaratorContext;
import at.tugraz.ist.soma.antlr.gen.JavaParser.WhileStmtContext;
import at.tugraz.ist.soma.statements.Statement;

/**
 * This class adds information (line number, last line etc.) to a given {@link at.tugraz.ist.soma.statements.Statement}.
 * This is done by using the auto generated visitor methods from the ANTLR-framework.
 * @author Stephan Frühwirt
 */

public class SecondPassVisitor extends JavaBaseVisitor<String>{
	
	private DataPool pool = DataPool.getInstance();
	private Statement statement = null;
	
	@Override
	public String visitVariableDeclarator(VariableDeclaratorContext ctx) {
		int line = ctx.getStart().getLine();
		statement = pool.getStatement(line);
		
		String left = ctx.variableDeclaratorId().getText();
		visit(ctx.variableInitializer());
		
		statement.addDef(left);
		pool.updateStatement(line, statement);
		
		return "";
	}
	
	@Override
	public String visitPrimary(PrimaryContext ctx) {
		if(ctx.varName != null){
			statement.addRef(ctx.varName.getText());
		}
		
		visitChildren(ctx);
		return ctx.getText();
	}
	
	@Override
	public String visitStatementExpression(StatementExpressionContext ctx) {
		int line = ctx.getStart().getLine();
		statement = pool.getStatement(line);
		
		visit(ctx.expression());
		
		pool.updateStatement(line, statement);
		
		return "";
	}
	
	@Override
	public String visitWhileStmt(WhileStmtContext ctx) {
		int line = ctx.getStart().getLine();
		statement = pool.getStatement(line);
		statement.setLastLine(ctx.getStop().getLine());
		
		visit(ctx.parExpression());
		pool.updateStatement(line, statement);
		visit(ctx.statement());
		
		return "";
	}
	
	@Override
	public String visitForStmt(ForStmtContext ctx) {
		int line = ctx.getStart().getLine();
		statement = pool.getStatement(line);
		statement.setLastLine(ctx.getStop().getLine());

		visit(ctx.forControl());
		pool.updateStatement(line, statement);
		visit(ctx.statement());
		
		return "";
	}
	
	@Override
	public String visitIfStmt(IfStmtContext ctx) {
		int line = ctx.getStart().getLine();
		statement = pool.getStatement(line);
		statement.setLastLine(ctx.getStop().getLine());
		
		visit(ctx.parExpression());
		pool.updateStatement(line, statement);
		visit(ctx.statement());
		

		return "";
	}
	
	@Override
	public String visitElseStmt(ElseStmtContext ctx) {
		int line = ctx.getStart().getLine();
		statement = pool.getStatement(line);
		statement.setLastLine(ctx.getStop().getLine());

		pool.updateStatement(line, statement);
		visit(ctx.statement());
		
		return "";
	}
	
	@Override
	public String visitExpression(ExpressionContext ctx) {

		if(ctx.unary != null){
			statement.addDef(ctx.unary.getText());
			statement.addRef(ctx.unary.getText());
			return "";
		}
		if(ctx.lhs != null){
			visit(ctx.rhs);
			statement.addDef(ctx.lhs.getText());
			return "";
		}
		visitChildren(ctx);
		return "";
	}
}
