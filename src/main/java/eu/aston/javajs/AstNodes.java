package eu.aston.javajs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@SuppressWarnings({"rawtypes"})
public class AstNodes {

    public static int INFINITE_LOOP_LIMIT = 8*1024;

    // Abstract Syntax Tree node classes
    public static abstract class ASTNode {
        public Object exec(Scope scope){
            return Undefined.INSTANCE;
        }
    }

    public interface ExecuteWithReturn {
    }

    public interface GetSetReturn {
        GetSet createGetSet(Scope scope);
    }

    public static Object wrapOptionalNotFound(ASTNode node, Scope scope){
        try{
            return node.exec(scope);
        }catch (OptionalNotFoundException ignore){
            return Undefined.INSTANCE;
        }
    }

    public static boolean wrapBreakBlock(ASTNode node, Scope scope){
        try {
            node.exec(scope);
        } catch (BreakBlockException e){
            return !e.nextLoop();
        } catch (OptionalNotFoundException ignore){
            return false;
        }
        return false;
    }

    // block nodes

    public static class ProgramNode extends BlockNode {
    }

    public static class BlockNode extends ASTNode {
        protected List<ASTNode> statements = new ArrayList<>();
        protected List<JsFunction> functions = new ArrayList<>();

        public void addStatement(ASTNode statement) {
            if (statement instanceof FunctionDeclarationNode functionDeclarationNode) {
                functions.add(functionDeclarationNode.function);
            } else {
                statements.add(statement);
            }
        }

        @Override
        public Object exec(Scope scope) {
            try(Scope blockScope = scope.newBlock()) {
                for (JsFunction function : functions) {
                    blockScope.putVariable(function.name, function);
                }
                for (ASTNode statement : statements) {
                    wrapOptionalNotFound(statement, blockScope);
                }
            }
            return null;
        }
    }

    public static class EmptyStatementNode extends ASTNode {
    }

    // variable nodes

    public static class VariableStatementNode extends ASTNode {
        protected String access;
        protected List<VariableDeclarationNode> declarations = new ArrayList<>();

        public VariableStatementNode(String access) {
            this.access = access;
        }

        public void addDeclaration(VariableDeclarationNode declaration) {
            declaration.setAccess(access);
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
        protected String identifier;
        protected ASTNode initializer;
        protected String access = "let";

        public VariableDeclarationNode(String identifier, ASTNode initializer) {
            this.identifier = identifier;
            this.initializer = initializer;
        }
        public void setAccess(String access) {
            this.access = access;
        }
        @Override
        public Object exec(Scope scope) {
            Object value = initializer!=null ? wrapOptionalNotFound(initializer, scope) : Undefined.INSTANCE;
            switch (access) {
                case "var" -> scope.defineVar(identifier, value);
                case "let" -> scope.defineLocalVar(false, identifier, value);
                case "const" -> scope.defineLocalVar(true, identifier, value);
                default -> throw new RuntimeException("Invalid variable type: " + access);
            }
            return value;
        }
    }

    // control flow nodes

    public static class IfStatementNode extends ASTNode {
        protected ASTNode condition;
        protected ASTNode thenStatement;
        protected ASTNode elseStatement;

        public IfStatementNode(ASTNode condition, ASTNode thenStatement, ASTNode elseStatement) {
            this.condition = condition;
            this.thenStatement = thenStatement;
            this.elseStatement = elseStatement;
        }
        @Override
        public Object exec(Scope scope) {
            Object conditionValue = wrapOptionalNotFound(condition, scope);
            if (JsTypes.toBoolean(conditionValue)) {
                return wrapOptionalNotFound(thenStatement,scope);
            } else if (elseStatement != null) {
                return wrapOptionalNotFound(elseStatement, scope);
            }
            return null;
        }
    }

    public static class WhileStatementNode extends ASTNode {
        protected ASTNode condition;
        protected ASTNode body;

        public WhileStatementNode(ASTNode condition, ASTNode body) {
            this.condition = condition;
            this.body = body;
        }
        @Override
        public Object exec(Scope scope) {
            int step = 0;
            try(Scope blockScope = scope.newBlock()) {
                while (true) {
                    Object conditionValue = wrapOptionalNotFound(condition, scope);
                    if (!JsTypes.toBoolean(conditionValue)) break;
                    if(wrapBreakBlock(body, blockScope)) break;
                    if (step++ > INFINITE_LOOP_LIMIT) {
                        throw new RuntimeException("Infinite loop detected - while statement");
                    }
                }
            }
            return null;
        }
    }

    public static class DoWhileStatementNode extends ASTNode {
        protected ASTNode condition;
        protected ASTNode body;

        public DoWhileStatementNode(ASTNode condition, ASTNode body) {
            this.condition = condition;
            this.body = body;
        }
        @Override
        public Object exec(Scope scope) {
            int step = 0;
            try(Scope blockScope = scope.newBlock()) {
                while(true) {
                    if(wrapBreakBlock(body, blockScope)) break;
                    Object conditionValue = wrapOptionalNotFound(condition, scope);
                    if (!JsTypes.toBoolean(conditionValue)) break;
                    if (step++ > INFINITE_LOOP_LIMIT) {
                        throw new RuntimeException("Infinite loop detected - while statement");
                    }
                }
            }
            return null;
        }
    }

    public static class ForStatementNode extends ASTNode {
        protected ASTNode initialization;
        protected ASTNode condition;
        protected ASTNode update;
        protected ASTNode body;

        public ForStatementNode(ASTNode initialization, ASTNode condition, ASTNode update, ASTNode body) {
            this.initialization = initialization;
            this.condition = condition;
            this.update = update;
            this.body = body;
        }
        @Override
        public Object exec(Scope scope) {
            int step = 0;
            try(Scope blockScope = scope.newBlock()){
                if(initialization!=null) {
                    wrapOptionalNotFound(initialization, scope);
                }
                while (true) {
                    Object conditionValue = wrapOptionalNotFound(condition, scope);
                    if (!JsTypes.toBoolean(conditionValue)) break;
                    if(wrapBreakBlock(body, blockScope)) break;
                    if(update!=null){
                        wrapOptionalNotFound(update,scope);
                    }
                    if(step++ > INFINITE_LOOP_LIMIT) {
                        throw new RuntimeException("Infinite loop detected - for statement");
                    }
                }
            }
            return null;
        }
    }

    public static class ForInStatementNode extends ASTNode {
        protected String variableName;
        protected ASTNode expression;
        protected ASTNode body;

        public ForInStatementNode(String variableName, ASTNode expression, ASTNode body) {
            this.variableName = variableName;
            this.expression = expression;
            this.body = body;
        }
        @Override
        public Object exec(Scope scope) {
            Object value = wrapOptionalNotFound(expression,scope);
            if(value instanceof Map map){
                int step = 0;
                try(Scope blockScope = scope.newBlock()) {
                    for (Object key : map.keySet()) {
                        blockScope.putVariable(variableName, key);
                        if(wrapBreakBlock(body, blockScope)) break;
                        if (step++ > INFINITE_LOOP_LIMIT) {
                            throw new RuntimeException("Infinite loop detected - for statement");
                        }
                    }
                }
            } else if(value instanceof List list){
                int step = 0;
                try(Scope blockScope = scope.newBlock()) {
                    for (int i = 0; i < list.size(); i++) {
                        blockScope.putVariable(variableName, i);
                        if(wrapBreakBlock(body, blockScope)) break;
                        if (step++ > INFINITE_LOOP_LIMIT) {
                            throw new RuntimeException("Infinite loop detected - for statement");
                        }
                    }
                }
            }
            return null;
        }
    }

    public static class ForOfStatementNode extends ASTNode {
        protected String variableName;
        protected ASTNode expression;
        protected ASTNode body;

        public ForOfStatementNode(String variableName, ASTNode expression, ASTNode body) {
            this.variableName = variableName;
            this.expression = expression;
            this.body = body;
        }
        @Override
        public Object exec(Scope scope) {
            Object value = wrapOptionalNotFound(expression,scope);
            if(value instanceof List list){
                int step = 0;
                try(Scope blockScope = scope.newBlock()) {
                    for (Object o : list) {
                        blockScope.putVariable(variableName, o);
                        if(wrapBreakBlock(body, blockScope)) break;
                        if (step++ > INFINITE_LOOP_LIMIT) {
                            throw new RuntimeException("Infinite loop detected - for statement");
                        }
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
        protected ASTNode expression;

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
        protected ASTNode discriminant;
        protected List<SwitchCaseNode> cases = new ArrayList<>();
        protected ASTNode defaultCase;

        public SwitchStatementNode(ASTNode discriminant) {
            this.discriminant = discriminant;
        }

        public void addCase(ASTNode node) {
            if (node instanceof SwitchCaseNode caseNode) {
                cases.add(caseNode);
            } else {
                throw new RuntimeException("Invalid case node");
            }
        }

        public void setDefaultCase(ASTNode defaultCase) {
            this.defaultCase = defaultCase;
        }
        @Override
        public Object exec(Scope scope) {
            Object discriminantValue = wrapOptionalNotFound(discriminant,scope);
            try(Scope blockScope = scope.newBlock()) {
                boolean switched = false;
                for (SwitchCaseNode caseNode : cases) {
                    Object caseValue = caseNode.test.exec(blockScope);
                    if (switched || JsOps.strictEqual(discriminantValue, caseValue)) {
                        switched = true;
                        if(wrapBreakBlock(caseNode, blockScope)) return null;
                    }
                }
                if (defaultCase != null) {
                    wrapBreakBlock(defaultCase,blockScope);
                }
            }
            return null;
        }
    }

    public static class SwitchCaseNode extends ASTNode {
        protected ASTNode test;
        protected List<ASTNode> consequent;

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
        protected List<ASTNode> consequent;

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
        protected ASTNode expression;

        public ThrowStatementNode(ASTNode expression) {
            this.expression = expression;
        }
        @Override
        public Object exec(Scope scope) {
            Object value = expression.exec(scope);
            throw new ExecuteScriptException("throw", value);
        }
    }

    public static class TryStatementNode extends ASTNode {
        protected ASTNode block;
        protected CatchClauseNode catchClause;
        protected ASTNode finallyBlock;

        public TryStatementNode(ASTNode block, CatchClauseNode catchClause, ASTNode finallyBlock) {
            this.block = block;
            this.catchClause = catchClause;
            this.finallyBlock = finallyBlock;
        }
        @Override
        public Object exec(Scope scope) {
            try(Scope blockScope = scope.newBlock()) {
                block.exec(blockScope);
            }catch (BreakBlockException e){
                throw e;
            }catch (Exception e){
                Object throwValue = e instanceof ExecuteScriptException e2 ? (e2.throwValue()!=null ? e2.throwValue : e2.getMessage()) : e.getMessage();
                if (catchClause != null) {
                    try(Scope catchScope = scope.newBlock()) {
                        if (catchClause.param != null) {
                            catchScope.defineLocalVar(false, catchClause.param, throwValue);
                        }
                        catchClause.exec(catchScope);
                    }
                }
            }finally {
                if (finallyBlock != null) {
                    try(Scope finallyScope = scope.newBlock()) {
                        finallyBlock.exec(finallyScope);
                    }
                }
            }
            return null;
        }
    }

    public static class CatchClauseNode extends ASTNode {
        protected String param;
        protected ASTNode body;

        public CatchClauseNode(String param, ASTNode body) {
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
        protected ASTNode left;
        protected String operator;
        protected Function<Scope, Object> operatorFunction;
        protected ASTNode right;

        public BinaryExpressionNode(ASTNode left, String operator, ASTNode right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
            if(operator.equals("||")){
                this.operatorFunction = (scope) -> {
                    Object leftValue = wrapOptionalNotFound(left,scope);
                    if(JsTypes.toBoolean(leftValue)){
                        return leftValue;
                    }
                    return wrapOptionalNotFound(right,scope);
                };
            } else if(operator.equals("&&")) {
                this.operatorFunction = (scope) -> {
                    Object leftValue = wrapOptionalNotFound(left,scope);
                    if (!JsTypes.toBoolean(leftValue)) {
                        return leftValue;
                    }
                    return wrapOptionalNotFound(right,scope);
                };
            } else if(operator.equals("??")) {
                this.operatorFunction = (scope) -> {
                    Object leftValue = wrapOptionalNotFound(left,scope);
                    if (leftValue!=null && leftValue!=Undefined.INSTANCE) {
                        return leftValue;
                    }
                    return wrapOptionalNotFound(right,scope);
                };
            } else {
                BiFunction<Object,Object,Object> operand = JsOps.operation(operator);
                if(operand==null){
                    throw new RuntimeException("Invalid operator " + operator);
                }
                this.operatorFunction = (scope) -> {
                    Object leftValue = wrapOptionalNotFound(left,scope);
                    Object rightValue = wrapOptionalNotFound(right,scope);
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
        protected List<ASTNode> items;

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
        protected ASTNode left;
        protected String operator;
        protected ASTNode right;
        protected BiFunction<GetSet, Object, Object> assignmentFunction;

        public AssignmentExpressionNode(ASTNode left, String operator, ASTNode right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
            if(operator.equals("=")){
                assignmentFunction = (leftGetSet,rightValue) -> {
                    leftGetSet.setter().accept(rightValue);
                    return rightValue;
                };
            } else {
                String operator2 = operator.substring(0, operator.length() - 1);
                BiFunction<Object,Object,Object> operand = JsOps.operation(operator2);
                if(operand==null){
                    throw new RuntimeException("Invalid operator " + operator);
                }
                assignmentFunction = (leftGetSet,rightValue) -> {
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
            } catch (OptionalNotFoundException ignore){
                return Undefined.INSTANCE;
            }catch (ExecuteScriptException e){
                throw e;
            }catch (Exception e){
                throw new ExecuteScriptException("Error in assignment "+e.getMessage(), null);
            }
        }
    }

    public static class ConditionalExpressionNode extends ASTNode implements ExecuteWithReturn {
        protected ASTNode condition;
        protected ASTNode trueExpression;
        protected ASTNode falseExpression;

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
        protected String operator;
        protected ASTNode operand;
        protected Function<Scope,Object> unaryFunction;

        public UnaryExpressionNode(String operator, ASTNode operand) {
            this.operator = operator;
            this.operand = operand;
            if(operator.equals("typeof") && operand instanceof IdentifierNode identifierNode) {
                this.unaryFunction = (scope) -> typeofScopeVar(identifierNode, scope);
            } else if(operator.equals("var++")){
                this.unaryFunction = createIncrementFn(operand, +1, true);
            } else if(operator.equals("var--")){
                this.unaryFunction = createIncrementFn(operand, -1, true);
            } else if(operator.equals("++var")){
                this.unaryFunction = createIncrementFn(operand, +1, false);
            } else if(operator.equals("--var")){
                this.unaryFunction = createIncrementFn(operand, -1, false);
            } else {
                Function<Object, Object> unaryFunction = switch (operator) {
                    case "+" -> JsTypes::toNumber;
                    case "-" -> JsTypes::unaryMinus;
                    case "!" -> (v)->!JsTypes.toBoolean(v);
                    case "typeof" -> JsTypes::typeof;
                    default -> throw new RuntimeException("Invalid operator " + operator);
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
                try{
                    GetSet getSet = ((GetSetReturn)operand).createGetSet(scope);
                    Number right = JsTypes.toNumber(getSet.value());
                    Object resp = plus.apply(right, inc);
                    getSet.setter().accept(resp);
                    return returnLeft ? resp : right;
                }catch (OptionalNotFoundException ignore){
                    return Undefined.INSTANCE;
                }
            };
        }

        private Object typeofScopeVar(IdentifierNode identifierNode, Scope scope) {
            Scope.VarAccess varAccess = scope.getVar(identifierNode.name);
            if(varAccess==null){
                return Undefined.INSTANCE.typeOf();
            }
            return JsTypes.typeof(varAccess.value());
        }

        @Override
        public Object exec(Scope scope) {
            return unaryFunction.apply(scope);
        }
    }

    // literal nodes
    public static class ThisNode extends ASTNode implements ExecuteWithReturn {
        // Represents 'this' keyword
        @Override
        public Object exec(Scope scope) {
            Scope.VarAccess varAccess = scope.getVar("this");
            return varAccess!=null ? varAccess.value() : scope;
        }
    }

    private static final List<String> futuredReservedWords = List.of(
            "class", "debugger",
            "delete", "export", "extends",
            "import", "in", "instanceof", "new",
            "super", "void", "with"
    );

    public static class IdentifierNode extends ASTNode implements ExecuteWithReturn, GetSetReturn {
        protected String name;

        public IdentifierNode(String name) {
            if(futuredReservedWords.contains(name)){
                throw new RuntimeException("Identifier name is a future reserved word: " + name);
            }
            this.name = name;
        }
        @Override
        public Object exec(Scope scope) {
            return GetSet.scopeGet(scope, name);
        }

        @Override
        public GetSet createGetSet(Scope scope) {
            return GetSet.createGetSet(scope, name, scope);
        }
    }

    public static class ConstantNode extends ASTNode implements ExecuteWithReturn {
        protected final Object value;

        public ConstantNode(Object value) {
            this.value = value;
        }

        @Override
        public Object exec(Scope scope) {
            return value;
        }
    }

    public static class ArrayLiteralNode extends ASTNode implements ExecuteWithReturn {
        protected List<ASTNode> elements;

        public ArrayLiteralNode(List<ASTNode> elements) {
            this.elements = elements;
        }
        @Override
        public Object exec(Scope scope) {
            List<Object> array = new ArrayList<>();
            for (ASTNode element : elements) {
                array.add(wrapOptionalNotFound(element,scope));
            }
            return array;
        }
    }

    public static class ObjectLiteralNode extends ASTNode implements ExecuteWithReturn {
        protected List<PropertyNode> properties;

        public ObjectLiteralNode(List<PropertyNode> properties) {
            this.properties = properties;
        }
        @Override
        public Object exec(Scope scope) {
            Map<String, Object> object = new java.util.HashMap<>();
            for (PropertyNode property : properties) {
                Object value = wrapOptionalNotFound(property.value, scope);
                if(value instanceof JsFunction functionValue){
                    value = functionValue.setParent(functionValue);
                }
                object.put(property.key, value);
            }
            return object;
        }
    }

    public static class PropertyNode extends ASTNode {
        protected String key;
        protected ASTNode value;

        public PropertyNode(String key, ASTNode value) {
            this.key = key;
            this.value = value;
        }
    }

    public static class OptionalNode extends ASTNode implements ExecuteWithReturn {
        protected ASTNode object;

        public OptionalNode(ASTNode object) {
            this.object = object;
        }

        @Override
        public Object exec(Scope scope) {
            try{
                Object val = object.exec(scope);
                if(val==null || val==Undefined.INSTANCE) throw new OptionalNotFoundException("undefined optional");
                return val;
            }catch (NotFoundException e){
                throw new OptionalNotFoundException(e.getMessage());
            }
        }
    }

    public static class MemberExpressionNode extends ASTNode implements ExecuteWithReturn, GetSetReturn {
        protected ASTNode object;
        protected String staticProperty;
        protected ASTNode dynamicProperty;

        public MemberExpressionNode(ASTNode object, ASTNode dynamicProperty) {
            this.object = object;
            this.dynamicProperty = dynamicProperty;
        }

        public MemberExpressionNode(ASTNode object, String staticProperty) {
            this.object = object;
            this.staticProperty = staticProperty;
        }

        @Override
        public Object exec(Scope scope) {
            return createGetSet(scope).value();
        }

        @Override
        public GetSet createGetSet(Scope scope) {
            Object parent = object.exec(scope);
            Object property = staticProperty!=null ? staticProperty : dynamicProperty.exec(scope);
            return GetSet.createGetSet(parent, property, scope);
        }
    }

    // function nodes

    public static class FunctionDeclarationNode extends ASTNode implements ExecuteWithReturn {
        protected JsFunction function;

        public FunctionDeclarationNode(String name, List<String> params, ASTNode body, boolean useLocalScope) {
            //check duplicated paremeter names
            for (int i = 0; i < params.size(); i++) {
                for (int j = i + 1; j < params.size(); j++) {
                    if (params.get(i).equals(params.get(j))) {
                        throw new RuntimeException("Duplicate parameter name: " + params.get(i));
                    }
                }
            }
            this.function = new JsFunction(name, params, body, useLocalScope);
        }

        @Override
        public Object exec(Scope scope) {
            return function.setParent(scope);
        }

    }

    public static class CallExpressionNode extends ASTNode implements ExecuteWithReturn {
        protected ASTNode callee;
        protected List<ASTNode> arguments;

        public CallExpressionNode(ASTNode callee, List<ASTNode> arguments) {
            this.callee = callee;
            this.arguments = arguments;
        }

        @Override
        public Object exec(Scope scope) {
            Object functionRaw = callee.exec(scope);
            if(!(functionRaw instanceof JsFunction function)){
                throw new RuntimeException(JsTypes.typeof(functionRaw)+" is not function");
            }
            // Prepare arguments
            List<Object> args = new ArrayList<>();
            for (int i = 0; i < Math.max(arguments.size(), function.params.size()); i++) {
                Object argValue = Undefined.INSTANCE;
                if (i < arguments.size()) {
                    argValue = wrapOptionalNotFound(arguments.get(i),scope);
                }
                args.add(argValue);
            }
            return function.exec(scope, args);
        }
    }

    public static class ExecuteScriptException extends RuntimeException {
        private final Object throwValue;
        public ExecuteScriptException(String message, Object throwValue) {
            super(message!=null ? message : "script throw " + throwValue);
            this.throwValue = throwValue;
        }
        public Object throwValue() {
            return throwValue;
        }
    }

    public static class BreakBlockException extends ExecuteScriptException {
        private final boolean nextLoop;
        public BreakBlockException(boolean nextLoop) {
            super(nextLoop ? "continue":"break", null);
            this.nextLoop = nextLoop;
        }
        public boolean nextLoop(){
            return nextLoop;
        }
    }

    public static class ReturnException extends ExecuteScriptException {
        public ReturnException(Object value) {
            super("return", value);
        }
    }

    public static class NotFoundException extends ExecuteScriptException {
        public NotFoundException(String message) {
            super(message, null);
        }
    }

    public static class OptionalNotFoundException extends ExecuteScriptException {
        public OptionalNotFoundException(String message) {
            super(message, null);
        }
    }

}

