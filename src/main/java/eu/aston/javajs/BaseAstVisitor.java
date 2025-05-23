package eu.aston.javajs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.aston.javajs.types.JsFunction;
import eu.aston.javajs.types.JsOps;
import eu.aston.javajs.types.JsTypes;
import eu.aston.javajs.types.Undefined;

/**
 * Base implementation of AstVisitor that provides execution functionality for all AST nodes.
 * This visitor implements the same logic as the exec methods in each AST node.
 */
public class BaseAstVisitor implements AstVisitor {

    public BaseAstVisitor(Scope rootScope) {
        this.currentScope = rootScope;
    }

    protected Object wrapOptionalNotFound(AstNodes.ASTNode node) {
        try {
            return node.accept(this);
        } catch (AstNodes.OptionalNotFoundException ignore) {
            return Undefined.INSTANCE;
        }
    }

    protected boolean wrapBreakBlock(AstNodes.ASTNode node) {
        try {
            node.accept(this);
        } catch (AstNodes.BreakBlockException e) {
            return !e.nextLoop();
        } catch (AstNodes.OptionalNotFoundException ignore) {
            return false;
        }
        return false;
    }

    // Current scope being used for execution
    protected Scope currentScope;

    /**
     * Set the current scope for execution
     */
    public void setScope(Scope scope) {
        this.currentScope = scope;
    }

    @Override
    public Object visitProgramNode(AstNodes.ProgramNode node) {
        Scope oldScope = currentScope;
        try {
            currentScope = new Scope(currentScope, node.scopeDef.size(), null);
            visitBlockNode(node.blockNode);
        } finally {
            currentScope = oldScope;
        }
        return null;
    }

    @Override
    public Object visitBlockNode(AstNodes.BlockNode node) {
        // Initialize functions first
        for (AstNodes.FunctionDeclarationNode functionNode : node.functions) {
            functionNode.scopeGetSet.set(currentScope, functionNode.function.initScope(currentScope));
        }

        // Execute statements
        for (AstNodes.ASTNode statement : node.statements) {
            wrapOptionalNotFound(statement);
        }

        return null;
    }

    @Override
    public Object visitEmptyStatementNode(AstNodes.EmptyStatementNode node) {
        return null;
    }

    @Override
    public Object visitVariableStatementNode(AstNodes.VariableStatementNode node) {
        for (AstNodes.VariableDeclarationNode declaration : node.declarations) {
            visitVariableDeclarationNode(declaration);
        }
        return null;
    }

    @Override
    public Object visitVariableDeclarationNode(AstNodes.VariableDeclarationNode node) {
        Object value = node.initializer != null ? wrapOptionalNotFound(node.initializer) : Undefined.INSTANCE;
        node.scopeGetSet.init(currentScope, value);
        return value;
    }

    @Override
    public Object visitDestructuringArrayNode(AstNodes.DestructuringArrayNode node) {
        Object rightValue = wrapOptionalNotFound(node.right);
        if (rightValue instanceof List<?> l) {
            for (int i = 0; i < node.variables.size(); i++) {
                AstNodes.VariableDeclarationNode v = node.variables.get(i);
                if (v != null) {
                    Object value = i < l.size() ? l.get(i) : Undefined.INSTANCE;
                    v.setValue(currentScope, value);
                }
            }
            if (node.restVariable != null) {
                Object value = node.variables.size() < l.size() ? l.subList(node.variables.size(), l.size())
                                                                : Undefined.INSTANCE;
                node.restVariable.setValue(currentScope, value);
            }
        } else {
            for (AstNodes.VariableDeclarationNode v : node.variables) {
                if (v != null) {
                    v.setValue(currentScope, Undefined.INSTANCE);
                }
            }
            if (node.restVariable != null) {
                node.restVariable.setValue(currentScope, Undefined.INSTANCE);
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object visitDestructuringObjectNode(AstNodes.DestructuringObjectNode node) {
        Object rightValue = wrapOptionalNotFound(node.right);
        if (rightValue instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) rightValue;
            for (AstNodes.VariableDeclarationNode v : node.variables) {
                Object value = map.getOrDefault(v.identifier, Undefined.INSTANCE);
                v.setValue(currentScope, value);
            }
            if (node.restVariable != null) {
                Map<String, Object> restMap = new HashMap<>();
                List<String> names = node.variables.stream().map(v -> v.identifier).toList();
                for (Map.Entry<String, Object> e : map.entrySet()) {
                    if (!names.contains(e.getKey())) {
                        restMap.put(e.getKey(), e.getValue());
                    }
                }
                node.restVariable.setValue(currentScope, restMap);
            }
        } else {
            for (AstNodes.VariableDeclarationNode v : node.variables) {
                v.setValue(currentScope, Undefined.INSTANCE);
            }
            if (node.restVariable != null) {
                node.restVariable.setValue(currentScope, Undefined.INSTANCE);
            }
        }
        return null;
    }

    @Override
    public Object visitIfStatementNode(AstNodes.IfStatementNode node) {
        Object conditionValue = wrapOptionalNotFound(node.condition);
        if (JsTypes.toBoolean(conditionValue)) {
            return node.thenStatement.accept(this);
        } else if (node.elseStatement != null) {
            return node.elseStatement.accept(this);
        }
        return null;
    }

    @Override
    public Object visitWhileStatementNode(AstNodes.WhileStatementNode node) {
        int step = 0;
        while (true) {
            Object conditionValue = wrapOptionalNotFound(node.condition);
            if (!JsTypes.toBoolean(conditionValue)) {
                break;
            }
            if (wrapBreakBlock(node.body)) {
                break;
            }
            if (step++ > AstNodes.INFINITE_LOOP_LIMIT) {
                throw new RuntimeException("Infinite loop detected - while statement");
            }
        }
        return null;
    }

    @Override
    public Object visitDoWhileStatementNode(AstNodes.DoWhileStatementNode node) {
        int step = 0;
        while (true) {
            if (wrapBreakBlock(node.body)) {
                break;
            }
            Object conditionValue = wrapOptionalNotFound(node.condition);
            if (!JsTypes.toBoolean(conditionValue)) {
                break;
            }
            if (step++ > AstNodes.INFINITE_LOOP_LIMIT) {
                throw new RuntimeException("Infinite loop detected - do-while statement");
            }
        }
        return null;
    }

    @Override
    public Object visitForStatementNode(AstNodes.ForStatementNode node) {
        int step = 0;
        if (node.initialization != null) {
            wrapOptionalNotFound(node.initialization);
        }
        while (true) {
            if (node.condition != null) {
                Object conditionValue = wrapOptionalNotFound(node.condition);
                if (!JsTypes.toBoolean(conditionValue)) {
                    break;
                }
            }
            if (wrapBreakBlock(node.body)) {
                break;
            }
            if (node.update != null) {
                wrapOptionalNotFound(node.update);
            }
            if (step++ > AstNodes.INFINITE_LOOP_LIMIT) {
                throw new RuntimeException("Infinite loop detected - for statement");
            }
        }
        return null;
    }

    @Override
    public Object visitForInStatementNode(AstNodes.ForInStatementNode node) {
        Object value = wrapOptionalNotFound(node.expression);
        if (value instanceof Map map) {
            int step = 0;
            for (Object key : map.keySet()) {
                node.variableName.setValue(currentScope, key);
                if (wrapBreakBlock(node.body)) {
                    break;
                }
                if (step++ > AstNodes.INFINITE_LOOP_LIMIT) {
                    throw new RuntimeException("Infinite loop detected - for-in statement");
                }
            }
        } else if (value instanceof List list) {
            int step = 0;
            for (int i = 0; i < list.size(); i++) {
                node.variableName.setValue(currentScope, i);
                if (wrapBreakBlock(node.body)) {
                    break;
                }
                if (step++ > AstNodes.INFINITE_LOOP_LIMIT) {
                    throw new RuntimeException("Infinite loop detected - for-in statement");
                }
            }
        }
        return null;
    }

    @Override
    public Object visitForOfStatementNode(AstNodes.ForOfStatementNode node) {
        Object value = wrapOptionalNotFound(node.expression);
        if (value instanceof List list) {
            int step = 0;
            for (Object o : list) {
                node.variableName.setValue(currentScope, o);
                if (wrapBreakBlock(node.body)) {
                    break;
                }
                if (step++ > AstNodes.INFINITE_LOOP_LIMIT) {
                    throw new RuntimeException("Infinite loop detected - for-of statement");
                }
            }
        }
        return null;
    }

    @Override
    public Object visitContinueStatementNode(AstNodes.ContinueStatementNode node) {
        throw new AstNodes.BreakBlockException(true);
    }

    @Override
    public Object visitBreakStatementNode(AstNodes.BreakStatementNode node) {
        throw new AstNodes.BreakBlockException(false);
    }

    @Override
    public Object visitReturnStatementNode(AstNodes.ReturnStatementNode node) {
        Object val = wrapOptionalNotFound(node.expression);
        throw new AstNodes.ReturnException(val);
    }

    @Override
    public Object visitSwitchStatementNode(AstNodes.SwitchStatementNode node) {
        Object discriminantValue = wrapOptionalNotFound(node.discriminant);
        boolean switched = false;
        for (AstNodes.SwitchCaseNode caseNode : node.cases) {
            Object caseValue = wrapOptionalNotFound(caseNode.test);
            if (switched || JsOps.strictEqual(discriminantValue, caseValue)) {
                switched = true;
                if (wrapBreakBlock(caseNode)) {
                    return null;
                }
            }
        }
        if (node.defaultCase != null) {
            wrapBreakBlock(node.defaultCase);
        }
        return null;
    }

    @Override
    public Object visitSwitchCaseNode(AstNodes.SwitchCaseNode node) {
        for (AstNodes.ASTNode statement : node.consequent) {
            wrapOptionalNotFound(statement);
        }
        return null;
    }

    @Override
    public Object visitSwitchDefaultNode(AstNodes.SwitchDefaultNode node) {
        for (AstNodes.ASTNode statement : node.consequent) {
            wrapOptionalNotFound(statement);
        }
        return null;
    }

    @Override
    public Object visitThrowStatementNode(AstNodes.ThrowStatementNode node) {
        Object value = wrapOptionalNotFound(node.expression);
        throw new AstNodes.ExecuteScriptException("throw", value, node.tokenPos);
    }

    @Override
    public Object visitTryStatementNode(AstNodes.TryStatementNode node) {
        try {
            node.block.accept(this);
        } catch (AstNodes.BreakBlockException e) {
            throw e;
        } catch (AstNodes.ExecuteScriptException e) {
            Object throwValue = e.throwValue() != null ? e.throwValue() : e.getMessage();
            if (node.catchClause != null) {
                if (node.catchClause.param != null) {
                    node.catchClause.param.setValue(currentScope, throwValue);
                }
                visitCatchClauseNode(node.catchClause);
            }
        } finally {
            if (node.finallyBlock != null) {
                node.finallyBlock.accept(this);
            }
        }
        return null;
    }

    @Override
    public Object visitCatchClauseNode(AstNodes.CatchClauseNode node) {
        node.body.accept(this);
        return null;
    }

    @Override
    public Object visitBinaryExpressionNode(AstNodes.BinaryExpressionNode node) {
        return node.operatorFunction.apply(this);
    }

    @Override
    public Object visitStringConcatExpressionNode(AstNodes.StringConcatExpressionNode node) {
        StringBuilder sb = new StringBuilder();
        for (AstNodes.ASTNode item : node.items) {
            Object value = wrapOptionalNotFound(item);
            sb.append(JsTypes.toString(value));
        }
        return sb.toString();
    }

    @Override
    public Object visitAssignmentExpressionNode(AstNodes.AssignmentExpressionNode node) {
        try {
            GetSet leftGetSet = createGetSet(node.left);
            Object rightValue = wrapOptionalNotFound(node.right);
            return node.assignmentFunction.apply(leftGetSet, rightValue);
        } catch (AstNodes.OptionalNotFoundException ignore) {
            return Undefined.INSTANCE;
        } catch (AstNodes.ExecuteScriptException e) {
            throw e;
        } catch (Exception e) {
            throw new AstNodes.ExecuteScriptException("Error in assignment " + e.getMessage(), null);
        }
    }

    @Override
    public Object visitConditionalExpressionNode(AstNodes.ConditionalExpressionNode node) {
        Object conditionValue = wrapOptionalNotFound(node.condition);
        if (JsTypes.toBoolean(conditionValue)) {
            return node.trueExpression.accept(this);
        } else {
            return node.falseExpression.accept(this);
        }
    }

    @Override
    public Object visitUnaryExpressionNode(AstNodes.UnaryExpressionNode node) {
        return node.unaryFunction.apply(this);
    }

    @Override
    public Object visitIdentifierNode(AstNodes.IdentifierNode node) {
        try {
            return node.scopeGetSet.get(currentScope);
        } catch (RuntimeException e) {
            throw new AstNodes.ExecuteScriptException(e.getMessage(), node.tokenPos);
        }
    }

    @Override
    public Object visitConstantNode(AstNodes.ConstantNode node) {
        return node.value;
    }

    @Override
    public Object visitArrayLiteralNode(AstNodes.ArrayLiteralNode node) {
        List<Object> array = new ArrayList<>();
        for (AstNodes.ASTNode element : node.elements) {
            array.add(wrapOptionalNotFound(element));
        }
        return array;
    }

    @Override
    public Object visitObjectLiteralNode(AstNodes.ObjectLiteralNode node) {
        Map<String, Object> object = new java.util.HashMap<>();
        for (AstNodes.PropertyNode property : node.properties) {
            Object value = wrapOptionalNotFound(property.value);
            if (value instanceof JsFunction functionValue) {
                value = functionValue.setParent(object);
            }
            object.put(property.key, value);
        }
        return object;
    }

    @Override
    public Object visitPropertyNode(AstNodes.PropertyNode node) {
        return node.value.accept(this);
    }

    @Override
    public Object visitOptionalNode(AstNodes.OptionalNode node) {
        try {
            Object val = wrapOptionalNotFound(node.object);
            if (val == null || val == Undefined.INSTANCE) {
                throw new AstNodes.OptionalNotFoundException("undefined optional");
            }
            return val;
        } catch (AstNodes.NotFoundException e) {
            throw new AstNodes.OptionalNotFoundException(e.getMessage());
        }
    }

    @Override
    public Object visitMemberExpressionNode(AstNodes.MemberExpressionNode node) {
        return createGetSet(node).value();
    }

    @Override
    public Object visitFunctionDeclarationNode(AstNodes.FunctionDeclarationNode node) {
        return node.function.initScope(currentScope);
    }

    @Override
    public Object visitCallExpressionNode(AstNodes.CallExpressionNode node) {
        Object functionRaw = node.callee.accept(this);
        if (!(functionRaw instanceof JsFunction function)) {
            throw new AstNodes.ExecuteScriptException(JsTypes.typeof(functionRaw) + " is not function", node.tokenPos);
        }

        // Prepare arguments
        List<Object> args = new ArrayList<>();
        for (int i = 0; i < Math.max(node.arguments.size(), function.params().size()); i++) {
            Object argValue = Undefined.INSTANCE;
            if (i < node.arguments.size()) {
                argValue = wrapOptionalNotFound(node.arguments.get(i));
            }
            args.add(argValue);
        }
        return executeFunction(function, args);
    }

    @Override
    public Object executeFunction(JsFunction function, List<Object> args) {
        Scope oldScope = currentScope;
        try {
            this.currentScope = function.createNewScope(oldScope, args);
            return function.exec.exec(this, args);
        } finally {
            this.currentScope = oldScope;
        }
    }

    // GetSet creation methods implementation

    @Override
    public GetSet createGetSet(AstNodes.ASTNode node) {
        if (node instanceof AstNodes.IdentifierNode identifierNode) {
            try {
                return new GetSet(identifierNode.scopeGetSet.get(currentScope),
                                  val -> identifierNode.scopeGetSet.set(currentScope, val));
            } catch (RuntimeException e) {
                throw new AstNodes.ExecuteScriptException(e.getMessage(), identifierNode.tokenPos);
            }
        } else if (node instanceof AstNodes.MemberExpressionNode memberExpressionNode) {
            Object parent = memberExpressionNode.object.accept(this);
            Object property = memberExpressionNode.staticProperty != null ? memberExpressionNode.staticProperty
                                                                          : memberExpressionNode.dynamicProperty != null
                                                                            ? memberExpressionNode.dynamicProperty.accept(
                                                                                  this) : null;
            return GetSet.createGetSet(parent, property, currentScope, memberExpressionNode.tokenPos);
        } else {
            throw new IllegalArgumentException("Unsupported node type for GetSet creation: " + node.getClass());
        }
    }

    @Override
    public Scope getCurrentScope() {
        return currentScope;
    }
} 