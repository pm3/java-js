package eu.aston.javajs;

import java.util.List;

import eu.aston.javajs.types.JsFunction;

/**
 * Visitor interface for AST nodes.
 * Implements the Visitor pattern to perform operations on AST nodes without modifying the node classes.
 */
public interface AstVisitor {
    // Program and block nodes
    Object visitProgramNode(AstNodes.ProgramNode node);

    Object visitBlockNode(AstNodes.BlockNode node);

    Object visitEmptyStatementNode(AstNodes.EmptyStatementNode node);

    // Variable nodes
    Object visitVariableStatementNode(AstNodes.VariableStatementNode node);

    Object visitVariableDeclarationNode(AstNodes.VariableDeclarationNode node);

    Object visitDestructuringArrayNode(AstNodes.DestructuringArrayNode node);

    Object visitDestructuringObjectNode(AstNodes.DestructuringObjectNode node);

    // Control flow nodes
    Object visitIfStatementNode(AstNodes.IfStatementNode node);

    Object visitWhileStatementNode(AstNodes.WhileStatementNode node);

    Object visitDoWhileStatementNode(AstNodes.DoWhileStatementNode node);

    Object visitForStatementNode(AstNodes.ForStatementNode node);

    Object visitForInStatementNode(AstNodes.ForInStatementNode node);

    Object visitForOfStatementNode(AstNodes.ForOfStatementNode node);

    Object visitContinueStatementNode(AstNodes.ContinueStatementNode node);

    Object visitBreakStatementNode(AstNodes.BreakStatementNode node);

    Object visitReturnStatementNode(AstNodes.ReturnStatementNode node);

    Object visitSwitchStatementNode(AstNodes.SwitchStatementNode node);

    Object visitSwitchCaseNode(AstNodes.SwitchCaseNode node);

    Object visitSwitchDefaultNode(AstNodes.SwitchDefaultNode node);

    Object visitThrowStatementNode(AstNodes.ThrowStatementNode node);

    Object visitTryStatementNode(AstNodes.TryStatementNode node);

    Object visitCatchClauseNode(AstNodes.CatchClauseNode node);

    // Expression nodes
    Object visitBinaryExpressionNode(AstNodes.BinaryExpressionNode node);

    Object visitStringConcatExpressionNode(AstNodes.StringConcatExpressionNode node);

    Object visitAssignmentExpressionNode(AstNodes.AssignmentExpressionNode node);

    Object visitConditionalExpressionNode(AstNodes.ConditionalExpressionNode node);

    Object visitUnaryExpressionNode(AstNodes.UnaryExpressionNode node);

    Object visitIdentifierNode(AstNodes.IdentifierNode node);

    Object visitConstantNode(AstNodes.ConstantNode node);

    Object visitArrayLiteralNode(AstNodes.ArrayLiteralNode node);

    Object visitObjectLiteralNode(AstNodes.ObjectLiteralNode node);

    Object visitPropertyNode(AstNodes.PropertyNode node);

    Object visitOptionalNode(AstNodes.OptionalNode node);

    Object visitMemberExpressionNode(AstNodes.MemberExpressionNode node);

    // Function nodes
    Object visitFunctionDeclarationNode(AstNodes.FunctionDeclarationNode node);

    Object visitCallExpressionNode(AstNodes.CallExpressionNode node);

    Object executeFunction(JsFunction function, List<Object> args);

    GetSet createGetSet(AstNodes.ASTNode identifierNode);

    Scope getCurrentScope();
}