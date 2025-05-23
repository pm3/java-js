package eu.aston.javajs;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import eu.aston.javajs.types.JsFunction;
import eu.aston.javajs.types.JsOps;
import eu.aston.javajs.types.JsTypes;
import eu.aston.javajs.types.Undefined;

@SuppressWarnings({"rawtypes"})
public class AstNodes {

    public static int INFINITE_LOOP_LIMIT = 8 * 1024;

    // Abstract Syntax Tree node classes
    public static abstract class ASTNode {
        /**
         * Accept method for the visitor pattern
         *
         * @param visitor the visitor to accept
         * @return the result of the visitor's visit method
         */
        public abstract Object accept(AstVisitor visitor);
    }

    public interface ExecuteWithReturn {
    }

    public static Object wrapOptionalNotFound(ASTNode node, AstVisitor visitor) {
        try {
            return node.accept(visitor);
        } catch (OptionalNotFoundException ignore) {
            return Undefined.INSTANCE;
        }
    }
    // block nodes

    public static class ProgramNode extends BlockNode {
        public final BlockNode blockNode;
        public final Scope.ScopeDef scopeDef;

        public ProgramNode(BlockNode blockNode, Scope.ScopeDef scopeDef) {
            this.blockNode = blockNode;
            this.scopeDef = scopeDef;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitProgramNode(this);
        }
    }

    public static class BlockNode extends ASTNode {
        public final List<ASTNode> statements = new ArrayList<>();
        public final List<FunctionDeclarationNode> functions = new ArrayList<>();

        public void addStatement(ASTNode statement) {
            if (statement instanceof FunctionDeclarationNode functionDeclarationNode &&
                    functionDeclarationNode.name != null) {
                functions.add(functionDeclarationNode);
            } else {
                statements.add(statement);
            }
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitBlockNode(this);
        }
    }

    public static class EmptyStatementNode extends ASTNode {
        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitEmptyStatementNode(this);
        }
    }

    // variable nodes

    public static class VariableStatementNode extends ASTNode {
        public final List<VariableDeclarationNode> declarations = new ArrayList<>();

        public void addDeclaration(VariableDeclarationNode declaration) {
            declarations.add(declaration);
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitVariableStatementNode(this);
        }
    }

    public static class VariableDeclarationNode extends ASTNode implements ExecuteWithReturn {
        public final String access;
        public final String identifier;
        public final ASTNode initializer;
        public final TokenPos tokenPos;
        public Scope.IGetSet scopeGetSet;

        public VariableDeclarationNode(String access, String identifier, TokenPos tokenPos) {
            this.access = access;
            this.identifier = identifier;
            this.initializer = null;
            this.tokenPos = tokenPos;
        }

        public VariableDeclarationNode(String access, String identifier, ASTNode initializer, TokenPos tokenPos) {
            this.access = access;
            this.identifier = identifier;
            this.initializer = initializer;
            this.tokenPos = tokenPos;
        }

        public void setValue(Scope scope, Object value) {
            scopeGetSet.set(scope, value);
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitVariableDeclarationNode(this);
        }
    }

    public static class DestructuringArrayNode extends ASTNode {
        public final List<VariableDeclarationNode> variables;
        public final VariableDeclarationNode restVariable;
        public final ASTNode right;

        public DestructuringArrayNode(List<VariableDeclarationNode> variables, VariableDeclarationNode restVariable,
                                      ASTNode right) {
            this.variables = variables;
            this.restVariable = restVariable;
            this.right = right;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitDestructuringArrayNode(this);
        }
    }

    public static class DestructuringObjectNode extends ASTNode {
        public final List<VariableDeclarationNode> variables;
        public final VariableDeclarationNode restVariable;
        public final ASTNode right;
        public final List<String> names;

        public DestructuringObjectNode(List<VariableDeclarationNode> variables, VariableDeclarationNode restVariable,
                                       ASTNode right) {
            this.variables = variables;
            this.restVariable = restVariable;
            this.right = right;
            this.names = variables.stream().map(v -> v.identifier).toList();
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitDestructuringObjectNode(this);
        }
    }

    // control flow nodes

    public static class IfStatementNode extends ASTNode {
        public final ASTNode condition;
        public final ASTNode thenStatement;
        public final ASTNode elseStatement;

        public IfStatementNode(ASTNode condition, ASTNode thenStatement, ASTNode elseStatement) {
            this.condition = condition;
            this.thenStatement = thenStatement;
            this.elseStatement = elseStatement;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitIfStatementNode(this);
        }
    }

    public static class WhileStatementNode extends ASTNode {
        public final ASTNode condition;
        public final ASTNode body;

        public WhileStatementNode(ASTNode condition, ASTNode body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitWhileStatementNode(this);
        }
    }

    public static class DoWhileStatementNode extends ASTNode {
        public final ASTNode condition;
        public final ASTNode body;

        public DoWhileStatementNode(ASTNode condition, ASTNode body) {
            this.condition = condition;
            this.body = body;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitDoWhileStatementNode(this);
        }
    }

    public static class ForStatementNode extends ASTNode {
        public final ASTNode initialization;
        public final ASTNode condition;
        public final ASTNode update;
        public final ASTNode body;

        public ForStatementNode(ASTNode initialization, ASTNode condition, ASTNode update, ASTNode body) {
            this.initialization = initialization;
            this.condition = condition;
            this.update = update;
            this.body = body;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitForStatementNode(this);
        }
    }

    public static class ForInStatementNode extends ASTNode {
        public final VariableDeclarationNode variableName;
        public final ASTNode expression;
        public final ASTNode body;

        public ForInStatementNode(VariableDeclarationNode variableName, ASTNode expression, ASTNode body) {
            this.variableName = variableName;
            this.expression = expression;
            this.body = body;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitForInStatementNode(this);
        }
    }

    public static class ForOfStatementNode extends ASTNode {
        public final VariableDeclarationNode variableName;
        public final ASTNode expression;
        public final ASTNode body;

        public ForOfStatementNode(VariableDeclarationNode variableName, ASTNode expression, ASTNode body) {
            this.variableName = variableName;
            this.expression = expression;
            this.body = body;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitForOfStatementNode(this);
        }
    }

    public static class ContinueStatementNode extends ASTNode {
        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitContinueStatementNode(this);
        }
    }

    public static class BreakStatementNode extends ASTNode {
        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitBreakStatementNode(this);
        }
    }

    public static class ReturnStatementNode extends ASTNode {
        public final ASTNode expression;

        public ReturnStatementNode(ASTNode expression) {
            this.expression = expression;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitReturnStatementNode(this);
        }
    }

    public static class SwitchStatementNode extends ASTNode {
        public final ASTNode discriminant;
        public final List<SwitchCaseNode> cases = new ArrayList<>();
        public ASTNode defaultCase;

        public SwitchStatementNode(ASTNode discriminant) {
            this.discriminant = discriminant;
        }

        public void addCase(SwitchCaseNode node) {
            cases.add(node);
        }

        public void setDefaultCase(ASTNode defaultCase) {
            this.defaultCase = defaultCase;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitSwitchStatementNode(this);
        }
    }

    public static class SwitchCaseNode extends ASTNode {
        public final ASTNode test;
        public final List<ASTNode> consequent;

        public SwitchCaseNode(ASTNode test, List<ASTNode> consequent) {
            this.test = test;
            this.consequent = consequent;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitSwitchCaseNode(this);
        }
    }

    public static class SwitchDefaultNode extends ASTNode {
        public final List<ASTNode> consequent;

        public SwitchDefaultNode(List<ASTNode> consequent) {
            this.consequent = consequent;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitSwitchDefaultNode(this);
        }
    }

    public static class ThrowStatementNode extends ASTNode {
        public final ASTNode expression;
        public final TokenPos tokenPos;

        public ThrowStatementNode(ASTNode expression, TokenPos tokenPos) {
            this.expression = expression;
            this.tokenPos = tokenPos;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitThrowStatementNode(this);
        }
    }

    public static class TryStatementNode extends ASTNode {
        public final ASTNode block;
        public final CatchClauseNode catchClause;
        public final ASTNode finallyBlock;

        public TryStatementNode(ASTNode block, CatchClauseNode catchClause, ASTNode finallyBlock) {
            this.block = block;
            this.catchClause = catchClause;
            this.finallyBlock = finallyBlock;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitTryStatementNode(this);
        }
    }

    public static class CatchClauseNode extends ASTNode {
        public final VariableDeclarationNode param;
        public final ASTNode body;

        public CatchClauseNode(VariableDeclarationNode param, ASTNode body) {
            this.param = param;
            this.body = body;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitCatchClauseNode(this);
        }
    }

    // expression nodes

    public static class BinaryExpressionNode extends ASTNode implements ExecuteWithReturn {
        public final ASTNode left;
        public final String operator;
        public final Function<AstVisitor, Object> operatorFunction;
        public final ASTNode right;

        public BinaryExpressionNode(ASTNode left, String operator, ASTNode right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
            if (operator.equals("||")) {
                this.operatorFunction = (visitor) -> {
                    Object leftValue = wrapOptionalNotFound(left, visitor);
                    if (JsTypes.toBoolean(leftValue)) {
                        return leftValue;
                    }
                    return wrapOptionalNotFound(right, visitor);
                };
            } else if (operator.equals("&&")) {
                this.operatorFunction = (visitor) -> {
                    Object leftValue = wrapOptionalNotFound(left, visitor);
                    if (!JsTypes.toBoolean(leftValue)) {
                        return leftValue;
                    }
                    return wrapOptionalNotFound(right, visitor);
                };
            } else if (operator.equals("??")) {
                this.operatorFunction = (visitor) -> {
                    Object leftValue = wrapOptionalNotFound(left, visitor);
                    if (leftValue != null && leftValue != Undefined.INSTANCE) {
                        return leftValue;
                    }
                    return wrapOptionalNotFound(right, visitor);
                };
            } else {
                BiFunction<Object, Object, Object> operand = JsOps.operation(operator);
                if (operand == null) {
                    throw new JsParser.SyntaxError("Invalid operator " + operator);
                }
                this.operatorFunction = (visitor) -> {
                    Object leftValue = wrapOptionalNotFound(left, visitor);
                    Object rightValue = wrapOptionalNotFound(right, visitor);
                    return operand.apply(leftValue, rightValue);
                };
            }
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitBinaryExpressionNode(this);
        }
    }

    public static class StringConcatExpressionNode extends ASTNode implements ExecuteWithReturn {
        public final List<ASTNode> items;

        public StringConcatExpressionNode(List<ASTNode> items) {
            this.items = items;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitStringConcatExpressionNode(this);
        }
    }

    public static class AssignmentExpressionNode extends ASTNode implements ExecuteWithReturn {
        public final ASTNode left;
        public final String operator;
        public final ASTNode right;
        public final BiFunction<GetSet, Object, Object> assignmentFunction;

        public AssignmentExpressionNode(ASTNode left, String operator, ASTNode right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
            if (operator.equals("=")) {
                assignmentFunction = (leftGetSet, rightValue) -> {
                    leftGetSet.setter().accept(rightValue);
                    return rightValue;
                };
            } else {
                String operator2 = operator.substring(0, operator.length() - 1);
                BiFunction<Object, Object, Object> operand = JsOps.operation(operator2);
                if (operand == null) {
                    throw new JsParser.SyntaxError("Invalid operator " + operator);
                }
                assignmentFunction = (leftGetSet, rightValue) -> {
                    Object leftValue = leftGetSet.value();
                    Object resp = operand.apply(leftValue, rightValue);
                    leftGetSet.setter().accept(resp);
                    return resp;
                };
            }
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitAssignmentExpressionNode(this);
        }
    }

    public static class ConditionalExpressionNode extends ASTNode implements ExecuteWithReturn {
        public final ASTNode condition;
        public final ASTNode trueExpression;
        public final ASTNode falseExpression;

        public ConditionalExpressionNode(ASTNode condition, ASTNode trueExpression, ASTNode falseExpression) {
            this.condition = condition;
            this.trueExpression = trueExpression;
            this.falseExpression = falseExpression;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitConditionalExpressionNode(this);
        }
    }

    public static class UnaryExpressionNode extends ASTNode implements ExecuteWithReturn {
        public final String operator;
        public final ASTNode operand;
        public final Function<AstVisitor, Object> unaryFunction;

        public UnaryExpressionNode(String operator, ASTNode operand) {
            this.operator = operator;
            this.operand = operand;
            if (operator.equals("var++")) {
                this.unaryFunction = createIncrementFn(operand, +1, true);
            } else if (operator.equals("var--")) {
                this.unaryFunction = createIncrementFn(operand, -1, true);
            } else if (operator.equals("++var")) {
                this.unaryFunction = createIncrementFn(operand, +1, false);
            } else if (operator.equals("--var")) {
                this.unaryFunction = createIncrementFn(operand, -1, false);
            } else {
                Function<Object, Object> unaryFunction2 = switch (operator) {
                    case "+" -> JsTypes::toNumber;
                    case "-" -> JsTypes::unaryMinus;
                    case "!" -> (v) -> !JsTypes.toBoolean(v);
                    case "typeof" -> JsTypes::typeof;
                    default -> throw new JsParser.SyntaxError("Invalid operator " + operator);
                };
                this.unaryFunction = (visitor) -> {
                    Object value = wrapOptionalNotFound(operand, visitor);
                    return unaryFunction2.apply(value);
                };
            }
        }

        private Function<AstVisitor, Object> createIncrementFn(ASTNode operand, int inc, boolean returnLeft) {
            BiFunction<Object, Object, Object> plus = JsOps.numberPlus();
            return (visitor) -> {
                try {
                    GetSet getSet = visitor.createGetSet(operand);
                    Number right = JsTypes.toNumber(getSet.value());
                    Object resp = plus.apply(right, inc);
                    getSet.setter().accept(resp);
                    return returnLeft ? resp : right;
                } catch (OptionalNotFoundException ignore) {
                    return Undefined.INSTANCE;
                }
            };
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitUnaryExpressionNode(this);
        }
    }

    public static class IdentifierNode extends ASTNode implements ExecuteWithReturn {
        public final String name;
        public final TokenPos tokenPos;
        public Scope.IGetSet scopeGetSet;
        public boolean wasAssigned;

        public IdentifierNode(String name, TokenPos tokenPos) {
            this.name = name;
            this.tokenPos = tokenPos;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitIdentifierNode(this);
        }
    }

    public static class ConstantNode extends ASTNode implements ExecuteWithReturn {
        public final Object value;

        public ConstantNode(Object value) {
            this.value = value;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitConstantNode(this);
        }
    }

    public static class ArrayLiteralNode extends ASTNode implements ExecuteWithReturn {
        public final List<ASTNode> elements;

        public ArrayLiteralNode(List<ASTNode> elements) {
            this.elements = elements;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitArrayLiteralNode(this);
        }
    }

    public static class ObjectLiteralNode extends ASTNode implements ExecuteWithReturn {
        public final List<PropertyNode> properties;

        public ObjectLiteralNode(List<PropertyNode> properties) {
            this.properties = properties;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitObjectLiteralNode(this);
        }
    }

    public static class PropertyNode extends ASTNode {
        public final String key;
        public final ASTNode value;

        public PropertyNode(String key, ASTNode value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitPropertyNode(this);
        }
    }

    public static class OptionalNode extends ASTNode implements ExecuteWithReturn {
        public final ASTNode object;

        public OptionalNode(ASTNode object) {
            this.object = object;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitOptionalNode(this);
        }
    }

    public static class MemberExpressionNode extends ASTNode implements ExecuteWithReturn {
        public final ASTNode object;
        public final String staticProperty;
        public final ASTNode dynamicProperty;
        public final TokenPos tokenPos;

        public MemberExpressionNode(ASTNode object, ASTNode dynamicProperty, TokenPos tokenPos) {
            this.object = object;
            this.tokenPos = tokenPos;
            this.staticProperty = null;
            this.dynamicProperty = dynamicProperty;
        }

        public MemberExpressionNode(ASTNode object, String staticProperty, TokenPos tokenPos) {
            this.object = object;
            this.staticProperty = staticProperty;
            this.tokenPos = tokenPos;
            this.dynamicProperty = null;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitMemberExpressionNode(this);
        }
    }

    // function nodes

    public static class FunctionDeclarationNode extends ASTNode implements ExecuteWithReturn {
        public final JsFunction function;
        public final String name;
        public final TokenPos tokenPos;
        public Scope.IGetSet scopeGetSet;

        public FunctionDeclarationNode(String name, TokenPos tokenPos, List<String> params, ASTNode body,
                                       Scope.ScopeDef scopeDef, boolean inlineThis) {
            this.function = new JsFunction(name, params, new JsFunction.LocalFunctionExec(body), inlineThis, scopeDef);
            this.name = name;
            this.tokenPos = tokenPos;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitFunctionDeclarationNode(this);
        }
    }

    public static class CallExpressionNode extends ASTNode implements ExecuteWithReturn {
        public final ASTNode callee;
        public final List<ASTNode> arguments;
        public final TokenPos tokenPos;

        public CallExpressionNode(ASTNode callee, List<ASTNode> arguments, TokenPos tokenPos) {
            this.callee = callee;
            this.arguments = arguments;
            this.tokenPos = tokenPos;
        }

        @Override
        public Object accept(AstVisitor visitor) {
            return visitor.visitCallExpressionNode(this);
        }
    }

    public static class ExecuteScriptException extends RuntimeException {
        private final Object throwValue;

        public ExecuteScriptException(String message, Object throwValue, TokenPos tokenPos) {
            super(tokenPos != null ? message + " at line " + tokenPos.line() + ", column " + tokenPos.column()
                                   : message);
            this.throwValue = throwValue;
        }

        public ExecuteScriptException(String message, TokenPos tokenPos) {
            this(message, null, tokenPos);
        }

        public Object throwValue() {
            return throwValue;
        }
    }

    public static class BreakBlockException extends ExecuteScriptException {
        private final boolean nextLoop;

        public BreakBlockException(boolean nextLoop) {
            super(nextLoop ? "continue" : "break", null);
            this.nextLoop = nextLoop;
        }

        public boolean nextLoop() {
            return nextLoop;
        }
    }

    public static class ReturnException extends ExecuteScriptException {
        public ReturnException(Object value) {
            super("return", value, null);
        }
    }

    public static class NotFoundException extends ExecuteScriptException {
        public NotFoundException(String message, TokenPos tokenPos) {
            super(message, null, tokenPos);
        }
    }

    public static class OptionalNotFoundException extends ExecuteScriptException {
        public OptionalNotFoundException(String message) {
            super(message, null, null);
        }
    }

}

