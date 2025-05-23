package eu.aston.javajs;

import java.util.List;

import eu.aston.javajs.types.JsFunction;

/**
 * A visitor implementation that prints AST nodes as a string.
 * This is a simple example of the Visitor pattern.
 */
public class AstPrinterVisitor implements AstVisitor {
    private final StringBuilder sb = new StringBuilder();
    private int indentLevel = 0;

    private void indent() {
        indentLevel++;
    }

    private void dedent() {
        indentLevel--;
    }

    private String getIndent() {
        return "  ".repeat(indentLevel);
    }

    private void appendLine(String line) {
        sb.append(getIndent()).append(line).append("\n");
    }

    public String getResult() {
        return sb.toString();
    }

    @Override
    public Object visitProgramNode(AstNodes.ProgramNode node) {
        appendLine("Program");
        indent();
        node.blockNode.accept(this);
        dedent();
        return null;
    }

    @Override
    public Object visitBlockNode(AstNodes.BlockNode node) {
        appendLine("Block {");
        indent();

        for (AstNodes.FunctionDeclarationNode func : node.functions) {
            func.accept(this);
        }

        for (AstNodes.ASTNode stmt : node.statements) {
            stmt.accept(this);
        }

        dedent();
        appendLine("}");
        return null;
    }

    @Override
    public Object visitEmptyStatementNode(AstNodes.EmptyStatementNode node) {
        appendLine("EmptyStatement");
        return null;
    }

    @Override
    public Object visitVariableStatementNode(AstNodes.VariableStatementNode node) {
        appendLine("VariableStatement {");
        indent();

        for (AstNodes.VariableDeclarationNode decl : node.declarations) {
            decl.accept(this);
        }

        dedent();
        appendLine("}");
        return null;
    }

    @Override
    public Object visitVariableDeclarationNode(AstNodes.VariableDeclarationNode node) {
        appendLine("VariableDeclaration: " + node.access + " " + node.identifier);
        if (node.initializer != null) {
            indent();
            appendLine("Initializer:");
            indent();
            node.initializer.accept(this);
            dedent();
            dedent();
        }
        return null;
    }

    // Implement the remaining visitor methods similar to above
    // For brevity, I'm only implementing a few key methods

    @Override
    public Object visitIfStatementNode(AstNodes.IfStatementNode node) {
        appendLine("IfStatement {");
        indent();

        appendLine("Condition:");
        indent();
        node.condition.accept(this);
        dedent();

        appendLine("Then:");
        indent();
        node.thenStatement.accept(this);
        dedent();

        if (node.elseStatement != null) {
            appendLine("Else:");
            indent();
            node.elseStatement.accept(this);
            dedent();
        }

        dedent();
        appendLine("}");
        return null;
    }

    @Override
    public Object visitIdentifierNode(AstNodes.IdentifierNode node) {
        appendLine("Identifier: " + node.name);
        return null;
    }

    @Override
    public Object visitConstantNode(AstNodes.ConstantNode node) {
        appendLine("Constant: " + node.value);
        return null;
    }

    @Override
    public Object visitBinaryExpressionNode(AstNodes.BinaryExpressionNode node) {
        appendLine("BinaryExpression: " + node.operator);
        indent();

        appendLine("Left:");
        indent();
        node.left.accept(this);
        dedent();

        appendLine("Right:");
        indent();
        node.right.accept(this);
        dedent();

        dedent();
        return null;
    }

    // Implement remaining methods with default behavior
    @Override
    public Object visitDestructuringArrayNode(AstNodes.DestructuringArrayNode node) {
        appendLine("DestructuringArray");
        return null;
    }

    @Override
    public Object visitDestructuringObjectNode(AstNodes.DestructuringObjectNode node) {
        appendLine("DestructuringObject");
        return null;
    }

    @Override
    public Object visitWhileStatementNode(AstNodes.WhileStatementNode node) {
        appendLine("WhileStatement");
        return null;
    }

    @Override
    public Object visitDoWhileStatementNode(AstNodes.DoWhileStatementNode node) {
        appendLine("DoWhileStatement");
        return null;
    }

    @Override
    public Object visitForStatementNode(AstNodes.ForStatementNode node) {
        appendLine("ForStatement");
        return null;
    }

    @Override
    public Object visitForInStatementNode(AstNodes.ForInStatementNode node) {
        appendLine("ForInStatement");
        return null;
    }

    @Override
    public Object visitForOfStatementNode(AstNodes.ForOfStatementNode node) {
        appendLine("ForOfStatement");
        return null;
    }

    @Override
    public Object visitContinueStatementNode(AstNodes.ContinueStatementNode node) {
        appendLine("ContinueStatement");
        return null;
    }

    @Override
    public Object visitBreakStatementNode(AstNodes.BreakStatementNode node) {
        appendLine("BreakStatement");
        return null;
    }

    @Override
    public Object visitReturnStatementNode(AstNodes.ReturnStatementNode node) {
        appendLine("ReturnStatement");
        return null;
    }

    @Override
    public Object visitSwitchStatementNode(AstNodes.SwitchStatementNode node) {
        appendLine("SwitchStatement");
        return null;
    }

    @Override
    public Object visitSwitchCaseNode(AstNodes.SwitchCaseNode node) {
        appendLine("SwitchCase");
        return null;
    }

    @Override
    public Object visitSwitchDefaultNode(AstNodes.SwitchDefaultNode node) {
        appendLine("SwitchDefault");
        return null;
    }

    @Override
    public Object visitThrowStatementNode(AstNodes.ThrowStatementNode node) {
        appendLine("ThrowStatement");
        return null;
    }

    @Override
    public Object visitTryStatementNode(AstNodes.TryStatementNode node) {
        appendLine("TryStatement");
        return null;
    }

    @Override
    public Object visitCatchClauseNode(AstNodes.CatchClauseNode node) {
        appendLine("CatchClause");
        return null;
    }

    @Override
    public Object visitStringConcatExpressionNode(AstNodes.StringConcatExpressionNode node) {
        appendLine("StringConcatExpression");
        return null;
    }

    @Override
    public Object visitAssignmentExpressionNode(AstNodes.AssignmentExpressionNode node) {
        appendLine("AssignmentExpression");
        return null;
    }

    @Override
    public Object visitConditionalExpressionNode(AstNodes.ConditionalExpressionNode node) {
        appendLine("ConditionalExpression");
        return null;
    }

    @Override
    public Object visitUnaryExpressionNode(AstNodes.UnaryExpressionNode node) {
        appendLine("UnaryExpression");
        return null;
    }

    @Override
    public Object visitArrayLiteralNode(AstNodes.ArrayLiteralNode node) {
        appendLine("ArrayLiteral");
        return null;
    }

    @Override
    public Object visitObjectLiteralNode(AstNodes.ObjectLiteralNode node) {
        appendLine("ObjectLiteral");
        return null;
    }

    @Override
    public Object visitPropertyNode(AstNodes.PropertyNode node) {
        appendLine("Property");
        return null;
    }

    @Override
    public Object visitOptionalNode(AstNodes.OptionalNode node) {
        appendLine("OptionalNode");
        return null;
    }

    @Override
    public Object visitMemberExpressionNode(AstNodes.MemberExpressionNode node) {
        appendLine("MemberExpression");
        return null;
    }

    @Override
    public Object visitFunctionDeclarationNode(AstNodes.FunctionDeclarationNode node) {
        appendLine("FunctionDeclaration: " + node.name);
        return null;
    }

    @Override
    public Object visitCallExpressionNode(AstNodes.CallExpressionNode node) {
        appendLine("CallExpression");
        return null;
    }

    @Override
    public Object executeFunction(JsFunction function, List<Object> args) {
        appendLine("Executing function: " + function.name());
        return null;
    }

    @Override
    public GetSet createGetSet(AstNodes.ASTNode identifierNode) {
        return null;
    }

    @Override
    public Scope getCurrentScope() {
        return null;
    }
}