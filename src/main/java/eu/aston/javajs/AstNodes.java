package eu.aston.javajs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        public Object exec(Scope scope) {
            return Undefined.INSTANCE;
        }
    }

    public interface ExecuteWithReturn {
    }

    public interface GetSetReturn {
        GetSet createGetSet(Scope scope);
    }

    public static Object wrapOptionalNotFound(ASTNode node, Scope scope) {
        try {
            return node.exec(scope);
        } catch (OptionalNotFoundException ignore) {
            return Undefined.INSTANCE;
        }
    }

    public static boolean wrapBreakBlock(ASTNode node, Scope scope) {
        try {
            node.exec(scope);
        } catch (BreakBlockException e) {
            return !e.nextLoop();
        } catch (OptionalNotFoundException ignore) {
            return false;
        }
        return false;
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
        public Object exec(Scope scope) {
            Scope newScope = new Scope(scope, scopeDef.size());
            return blockNode.exec(newScope);
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
        public Object exec(Scope scope) {
            for (FunctionDeclarationNode functionNode : functions) {
                scope.setStackValue(functionNode.index, functionNode.name, functionNode.function.initScope(scope));
            }
            for (ASTNode statement : statements) {
                wrapOptionalNotFound(statement, scope);
            }
            return null;
        }
    }

    public static class EmptyStatementNode extends ASTNode {
    }

    // variable nodes

    public static class VariableStatementNode extends ASTNode {
        public final List<VariableDeclarationNode> declarations = new ArrayList<>();

        public void addDeclaration(VariableDeclarationNode declaration) {
            declarations.add(declaration);
        }

        @Override
        public Object exec(Scope scope) {
            for (VariableDeclarationNode declaration : declarations) {
                declaration.exec(scope);
            }
            return null;
        }
    }

    public static class VariableDeclarationNode extends ASTNode implements ExecuteWithReturn {
        public final String access;
        public final String identifier;
        public final ASTNode initializer;
        public final TokenPos tokenPos;
        public int index = -1;

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

        @Override
        public Object exec(Scope scope) {
            Object value = initializer != null ? wrapOptionalNotFound(initializer, scope) : Undefined.INSTANCE;
            setValue(scope, value);
            return value;
        }

        public void setValue(Scope scope, Object value) {
            scope.setStackValue(index, identifier, value);
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
        public Object exec(Scope scope) {
            Object rightValue = wrapOptionalNotFound(right, scope);
            if (rightValue instanceof List<?> l) {
                for (int i = 0; i < variables.size(); i++) {
                    VariableDeclarationNode v = variables.get(i);
                    if (v != null) {
                        Object value = i < l.size() ? l.get(i) : Undefined.INSTANCE;
                        v.setValue(scope, value);
                    }
                }
                if (restVariable != null) {
                    Object value =
                            variables.size() < l.size() ? l.subList(variables.size(), l.size()) : Undefined.INSTANCE;
                    restVariable.setValue(scope, value);
                }
            } else {
                for (VariableDeclarationNode v : variables) {
                    if (v != null) {
                        v.setValue(scope, Undefined.INSTANCE);
                    }
                }
                if (restVariable != null) {
                    restVariable.setValue(scope, Undefined.INSTANCE);
                }
            }
            return null;
        }
    }

    public static class DestructuringObjectNode extends ASTNode {
        public final List<VariableDeclarationNode> variables;
        public final VariableDeclarationNode restVariable;
        public final ASTNode right;
        private final List<String> names;

        public DestructuringObjectNode(List<VariableDeclarationNode> variables, VariableDeclarationNode restVariable,
                                       ASTNode right) {
            this.variables = variables;
            this.restVariable = restVariable;
            this.right = right;
            this.names = variables.stream().map(v -> v.identifier).toList();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object exec(Scope scope) {
            Object rightValue = wrapOptionalNotFound(right, scope);
            if (rightValue instanceof Map) {
                Map<String, Object> map = (Map<String, Object>) rightValue;
                for (VariableDeclarationNode v : variables) {
                    Object value = map.getOrDefault(v.identifier, Undefined.INSTANCE);
                    v.setValue(scope, value);
                }
                if (restVariable != null) {
                    Map<String, Object> restMap = new HashMap<>();
                    for (Map.Entry<String, Object> e : map.entrySet()) {
                        if (!names.contains(e.getKey())) {
                            restMap.put(e.getKey(), e.getValue());
                        }
                    }
                    restVariable.setValue(scope, restMap);
                }
            } else {
                for (VariableDeclarationNode v : variables) {
                    v.setValue(scope, Undefined.INSTANCE);
                }
                if (restVariable != null) {
                    restVariable.setValue(scope, Undefined.INSTANCE);
                }
            }
            return null;
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
        public Object exec(Scope scope) {
            Object conditionValue = wrapOptionalNotFound(condition, scope);
            if (JsTypes.toBoolean(conditionValue)) {
                return wrapOptionalNotFound(thenStatement, scope);
            } else if (elseStatement != null) {
                return wrapOptionalNotFound(elseStatement, scope);
            }
            return null;
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
        public Object exec(Scope scope) {
            int step = 0;
            while (true) {
                Object conditionValue = wrapOptionalNotFound(condition, scope);
                if (!JsTypes.toBoolean(conditionValue)) {
                    break;
                }
                if (wrapBreakBlock(body, scope)) {
                    break;
                }
                if (step++ > INFINITE_LOOP_LIMIT) {
                    throw new RuntimeException("Infinite loop detected - while statement");
                }
            }
            return null;
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
        public Object exec(Scope scope) {
            int step = 0;
            while (true) {
                if (wrapBreakBlock(body, scope)) {
                    break;
                }
                Object conditionValue = wrapOptionalNotFound(condition, scope);
                if (!JsTypes.toBoolean(conditionValue)) {
                    break;
                }
                if (step++ > INFINITE_LOOP_LIMIT) {
                    throw new RuntimeException("Infinite loop detected - while statement");
                }
            }
            return null;
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
        public Object exec(Scope scope) {
            int step = 0;
            if (initialization != null) {
                wrapOptionalNotFound(initialization, scope);
            }
            while (true) {
                Object conditionValue = wrapOptionalNotFound(condition, scope);
                if (!JsTypes.toBoolean(conditionValue)) {
                    break;
                }
                if (wrapBreakBlock(body, scope)) {
                    break;
                }
                if (update != null) {
                    wrapOptionalNotFound(update, scope);
                }
                if (step++ > INFINITE_LOOP_LIMIT) {
                    throw new RuntimeException("Infinite loop detected - for statement");
                }
            }
            return null;
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
        public Object exec(Scope scope) {
            Object value = wrapOptionalNotFound(expression, scope);
            if (value instanceof Map map) {
                int step = 0;
                for (Object key : map.keySet()) {
                    variableName.setValue(scope, key);
                    if (wrapBreakBlock(body, scope)) {
                        break;
                    }
                    if (step++ > INFINITE_LOOP_LIMIT) {
                        throw new RuntimeException("Infinite loop detected - for statement");
                    }
                }
            } else if (value instanceof List list) {
                int step = 0;
                for (int i = 0; i < list.size(); i++) {
                    variableName.setValue(scope, i);
                    if (wrapBreakBlock(body, scope)) {
                        break;
                    }
                    if (step++ > INFINITE_LOOP_LIMIT) {
                        throw new RuntimeException("Infinite loop detected - for statement");
                    }
                }
            }
            return null;
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
        public Object exec(Scope scope) {
            Object value = wrapOptionalNotFound(expression, scope);
            if (value instanceof List list) {
                int step = 0;
                for (Object o : list) {
                    variableName.setValue(scope, o);
                    if (wrapBreakBlock(body, scope)) {
                        break;
                    }
                    if (step++ > INFINITE_LOOP_LIMIT) {
                        throw new RuntimeException("Infinite loop detected - for statement");
                    }
                }
            }
            return null;
        }
    }

    public static class ContinueStatementNode extends ASTNode {
        @Override
        public Object exec(Scope scope) {
            throw new BreakBlockException(true);
        }
    }

    public static class BreakStatementNode extends ASTNode {
        @Override
        public Object exec(Scope scope) {
            throw new BreakBlockException(false);
        }
    }

    public static class ReturnStatementNode extends ASTNode {
        public final ASTNode expression;

        public ReturnStatementNode(ASTNode expression) {
            this.expression = expression;
        }

        @Override
        public Object exec(Scope scope) {
            Object val = wrapOptionalNotFound(expression, scope);
            throw new ReturnException(val);
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
        public Object exec(Scope scope) {
            Object discriminantValue = wrapOptionalNotFound(discriminant, scope);
            boolean switched = false;
            for (SwitchCaseNode caseNode : cases) {
                Object caseValue = caseNode.test.exec(scope);
                if (switched || JsOps.strictEqual(discriminantValue, caseValue)) {
                    switched = true;
                    if (wrapBreakBlock(caseNode, scope)) {
                        return null;
                    }
                }
            }
            if (defaultCase != null) {
                wrapBreakBlock(defaultCase, scope);
            }
            return null;
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
        public Object exec(Scope scope) {
            for (ASTNode statement : consequent) {
                statement.exec(scope);
            }
            return null;
        }
    }

    public static class SwitchDefaultNode extends ASTNode {
        public final List<ASTNode> consequent;

        public SwitchDefaultNode(List<ASTNode> consequent) {
            this.consequent = consequent;
        }

        @Override
        public Object exec(Scope scope) {
            for (ASTNode statement : consequent) {
                statement.exec(scope);
            }
            return null;
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
        public Object exec(Scope scope) {
            Object value = expression.exec(scope);
            throw new ExecuteScriptException("throw", value, tokenPos);
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
        public Object exec(Scope scope) {
            try {
                block.exec(scope);
            } catch (BreakBlockException e) {
                throw e;
            } catch (ExecuteScriptException e) {
                Object throwValue = e.throwValue() != null ? e.throwValue() : e.getMessage();
                if (catchClause != null) {
                    if (catchClause.param != null) {
                        catchClause.param.setValue(scope, throwValue);
                    }
                    catchClause.exec(scope);
                }
            } finally {
                if (finallyBlock != null) {
                    finallyBlock.exec(scope);
                }
            }
            return null;
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
        public Object exec(Scope scope) {
            body.exec(scope);
            return null;
        }
    }

    // expression nodes

    public static class BinaryExpressionNode extends ASTNode implements ExecuteWithReturn {
        public final ASTNode left;
        public final String operator;
        public final Function<Scope, Object> operatorFunction;
        public final ASTNode right;

        public BinaryExpressionNode(ASTNode left, String operator, ASTNode right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
            if (operator.equals("||")) {
                this.operatorFunction = (scope) -> {
                    Object leftValue = wrapOptionalNotFound(left, scope);
                    if (JsTypes.toBoolean(leftValue)) {
                        return leftValue;
                    }
                    return wrapOptionalNotFound(right, scope);
                };
            } else if (operator.equals("&&")) {
                this.operatorFunction = (scope) -> {
                    Object leftValue = wrapOptionalNotFound(left, scope);
                    if (!JsTypes.toBoolean(leftValue)) {
                        return leftValue;
                    }
                    return wrapOptionalNotFound(right, scope);
                };
            } else if (operator.equals("??")) {
                this.operatorFunction = (scope) -> {
                    Object leftValue = wrapOptionalNotFound(left, scope);
                    if (leftValue != null && leftValue != Undefined.INSTANCE) {
                        return leftValue;
                    }
                    return wrapOptionalNotFound(right, scope);
                };
            } else {
                BiFunction<Object, Object, Object> operand = JsOps.operation(operator);
                if (operand == null) {
                    throw new JsParser.SyntaxError("Invalid operator " + operator);
                }
                this.operatorFunction = (scope) -> {
                    Object leftValue = wrapOptionalNotFound(left, scope);
                    Object rightValue = wrapOptionalNotFound(right, scope);
                    return operand.apply(leftValue, rightValue);
                };
            }
        }

        @Override
        public Object exec(Scope scope) {
            return operatorFunction.apply(scope);
        }
    }

    public static class StringConcatExpressionNode extends ASTNode implements ExecuteWithReturn {
        public final List<ASTNode> items;

        public StringConcatExpressionNode(List<ASTNode> items) {
            this.items = items;
        }

        @Override
        public Object exec(Scope scope) {
            StringBuilder sb = new StringBuilder();
            for (ASTNode item : items) {
                Object value = item.exec(scope);
                sb.append(JsTypes.toString(value));
            }
            return sb.toString();
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
        public Object exec(Scope scope) {
            try {
                GetSet leftGetSet = ((GetSetReturn) left).createGetSet(scope);
                Object rightValue = right.exec(scope);
                return assignmentFunction.apply(leftGetSet, rightValue);
            } catch (OptionalNotFoundException ignore) {
                return Undefined.INSTANCE;
            } catch (ExecuteScriptException e) {
                throw e;
            } catch (Exception e) {
                throw new ExecuteScriptException("Error in assignment " + e.getMessage(), null);
            }
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
        public Object exec(Scope scope) {
            Object conditionValue = wrapOptionalNotFound(condition, scope);
            if (JsTypes.toBoolean(conditionValue)) {
                return wrapOptionalNotFound(trueExpression, scope);
            } else {
                return wrapOptionalNotFound(falseExpression, scope);
            }
        }
    }

    public static class UnaryExpressionNode extends ASTNode implements ExecuteWithReturn {
        public final String operator;
        public final ASTNode operand;
        public final Function<Scope, Object> unaryFunction;

        public UnaryExpressionNode(String operator, ASTNode operand) {
            this.operator = operator;
            this.operand = operand;
            if (operator.equals("typeof") && operand instanceof IdentifierNode identifierNode) {
                this.unaryFunction = (scope) -> typeofScopeVar(identifierNode, scope);
            } else if (operator.equals("var++")) {
                this.unaryFunction = createIncrementFn(operand, +1, true);
            } else if (operator.equals("var--")) {
                this.unaryFunction = createIncrementFn(operand, -1, true);
            } else if (operator.equals("++var")) {
                this.unaryFunction = createIncrementFn(operand, +1, false);
            } else if (operator.equals("--var")) {
                this.unaryFunction = createIncrementFn(operand, -1, false);
            } else {
                Function<Object, Object> unaryFunction = switch (operator) {
                    case "+" -> JsTypes::toNumber;
                    case "-" -> JsTypes::unaryMinus;
                    case "!" -> (v) -> !JsTypes.toBoolean(v);
                    case "typeof" -> JsTypes::typeof;
                    default -> throw new JsParser.SyntaxError("Invalid operator " + operator);
                };
                this.unaryFunction = (scope) -> {
                    Object value = wrapOptionalNotFound(operand, scope);
                    return unaryFunction.apply(value);
                };
            }
        }

        private Function<Scope, Object> createIncrementFn(ASTNode operand, int inc, boolean returnLeft) {
            BiFunction<Object, Object, Object> plus = JsOps.numberPlus();
            return (scope) -> {
                try {
                    GetSet getSet = ((GetSetReturn) operand).createGetSet(scope);
                    Number right = JsTypes.toNumber(getSet.value());
                    Object resp = plus.apply(right, inc);
                    getSet.setter().accept(resp);
                    return returnLeft ? resp : right;
                } catch (OptionalNotFoundException ignore) {
                    return Undefined.INSTANCE;
                }
            };
        }

        private Object typeofScopeVar(IdentifierNode identifierNode, Scope scope) {
            try {
                Object value = identifierNode.get(scope);
                return JsTypes.typeof(value);
            } catch (NotFoundException e) {
                return Undefined.INSTANCE.toString();
            }
        }

        @Override
        public Object exec(Scope scope) {
            return unaryFunction.apply(scope);
        }
    }

    public static class IdentifierNode extends ASTNode implements ExecuteWithReturn, GetSetReturn {
        public final String name;
        public final TokenPos tokenPos;
        public int index = -1;
        public boolean wasAssigned;

        public IdentifierNode(String name, TokenPos tokenPos) {
            this.name = name;
            this.tokenPos = tokenPos;
        }

        @Override
        public Object exec(Scope scope) {
            return get(scope);
        }

        @Override
        public GetSet createGetSet(Scope scope) {
            return new GetSet(get(scope), val -> set(scope, val));
        }

        public Object get(Scope scope) {
            try {
                return scope.getValue(index, name);
            } catch (RuntimeException e) {
                throw new ExecuteScriptException(e.getMessage(), tokenPos);
            }
        }

        public void set(Scope scope, Object value) {
            scope.setStackValue(index, name, value);
        }
    }

    public static class ConstantNode extends ASTNode implements ExecuteWithReturn {
        public final Object value;

        public ConstantNode(Object value) {
            this.value = value;
        }

        @Override
        public Object exec(Scope scope) {
            return value;
        }
    }

    public static class ArrayLiteralNode extends ASTNode implements ExecuteWithReturn {
        public final List<ASTNode> elements;

        public ArrayLiteralNode(List<ASTNode> elements) {
            this.elements = elements;
        }

        @Override
        public Object exec(Scope scope) {
            List<Object> array = new ArrayList<>();
            for (ASTNode element : elements) {
                array.add(wrapOptionalNotFound(element, scope));
            }
            return array;
        }
    }

    public static class ObjectLiteralNode extends ASTNode implements ExecuteWithReturn {
        public final List<PropertyNode> properties;

        public ObjectLiteralNode(List<PropertyNode> properties) {
            this.properties = properties;
        }

        @Override
        public Object exec(Scope scope) {
            Map<String, Object> object = new java.util.HashMap<>();
            for (PropertyNode property : properties) {
                Object value = wrapOptionalNotFound(property.value, scope);
                if (value instanceof JsFunction functionValue) {
                    value = functionValue.setParent(object);
                }
                object.put(property.key, value);
            }
            return object;
        }
    }

    public static class PropertyNode extends ASTNode {
        public final String key;
        public final ASTNode value;

        public PropertyNode(String key, ASTNode value) {
            this.key = key;
            this.value = value;
        }
    }

    public static class OptionalNode extends ASTNode implements ExecuteWithReturn {
        public final ASTNode object;

        public OptionalNode(ASTNode object) {
            this.object = object;
        }

        @Override
        public Object exec(Scope scope) {
            try {
                Object val = object.exec(scope);
                if (val == null || val == Undefined.INSTANCE) {
                    throw new OptionalNotFoundException("undefined optional");
                }
                return val;
            } catch (NotFoundException e) {
                throw new OptionalNotFoundException(e.getMessage());
            }
        }
    }

    public static class MemberExpressionNode extends ASTNode implements ExecuteWithReturn, GetSetReturn {
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
        public Object exec(Scope scope) {
            return createGetSet(scope).value();
        }

        @Override
        public GetSet createGetSet(Scope scope) {
            Object parent = object.exec(scope);
            Object property = staticProperty != null ? staticProperty
                                                     : dynamicProperty != null ? dynamicProperty.exec(scope) : null;
            return GetSet.createGetSet(parent, property, scope, tokenPos);
        }
    }

    // function nodes

    public static class FunctionDeclarationNode extends ASTNode implements ExecuteWithReturn {
        public final JsFunction function;
        public final String name;
        public final TokenPos tokenPos;
        public int index = -1;

        public FunctionDeclarationNode(String name, TokenPos tokenPos, List<String> params, ASTNode body,
                                       Scope.ScopeDef scopeDef, boolean inlineThis) {
            this.function = new JsFunction(name, params, new JsFunction.LocalFunctionExec(body), inlineThis, scopeDef);
            this.name = name;
            this.tokenPos = tokenPos;
        }

        @Override
        public Object exec(Scope scope) {
            return function.initScope(scope);
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
        public Object exec(Scope scope) {
            Object functionRaw = callee.exec(scope);
            if (!(functionRaw instanceof JsFunction function)) {
                throw new AstNodes.ExecuteScriptException(JsTypes.typeof(functionRaw) + " is not function", tokenPos);
            }
            // Prepare arguments
            List<Object> args = new ArrayList<>();
            for (int i = 0; i < Math.max(arguments.size(), function.params().size()); i++) {
                Object argValue = Undefined.INSTANCE;
                if (i < arguments.size()) {
                    argValue = wrapOptionalNotFound(arguments.get(i), scope);
                }
                args.add(argValue);
            }
            return function.exec(scope, args);
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

