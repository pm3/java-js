package eu.aston.javajs.types;

import java.util.List;

import eu.aston.javajs.AstNodes.ASTNode;
import eu.aston.javajs.AstNodes.ReturnException;
import eu.aston.javajs.AstVisitor;
import eu.aston.javajs.Scope;

public class JsFunction implements IJsType {

    final String name;
    final List<String> params;
    public final IJsFunctionExec exec;
    final Scope.ScopeDef scopeDef;
    final boolean inlineThis;
    final private Object parent;
    final private Scope instanceScope;

    public JsFunction(String name, List<String> params, IJsFunctionExec exec, boolean inlineThis,
                      Scope.ScopeDef scopeDef) {
        this.name = name;
        this.params = params;
        this.exec = exec;
        this.scopeDef = scopeDef;
        this.inlineThis = inlineThis;
        this.parent = null;
        this.instanceScope = null;
    }

    public JsFunction(JsFunction fn, Object parent) {
        this.name = fn.name;
        this.params = fn.params;
        this.exec = fn.exec;
        this.scopeDef = fn.scopeDef;
        this.inlineThis = fn.inlineThis;
        this.parent = parent;
        this.instanceScope = fn.instanceScope;
    }

    public JsFunction(JsFunction fn, Scope instanceScope) {
        this.name = fn.name;
        this.params = fn.params;
        this.exec = fn.exec;
        this.scopeDef = fn.scopeDef;
        this.inlineThis = fn.inlineThis;
        this.parent = fn.parent;
        this.instanceScope = instanceScope;
    }

    public String name() {
        return name;
    }

    public List<String> params() {
        return params;
    }

    public JsFunction setParent(Object parent) {
        if (inlineThis) {
            return this;
        }
        return new JsFunction(this, parent);
    }

    public JsFunction initScope(Scope scope) {
        if (scopeDef == null) {
            return this;
        }
        return new JsFunction(this, scopeDef.createInitScope(scope));
    }

    public Scope createNewScope(Scope scope, List<Object> args) {
        if (scopeDef != null) {
            Scope functionScope = instanceScope != null ? instanceScope : new Scope(scope, scopeDef.size(), null);
            functionScope.setStackValue(0, "this", parent != null ? parent : scope.rootThis());
            functionScope.setStackValue(1, "arguments", args);
            for (int i = 0; i < params.size(); i++) {
                functionScope.setStackValue(i + 2, params.get(i), args.get(i));
            }
            return functionScope;
        } else {
            Scope functionScope = new Scope(scope, 1, null);
            functionScope.setStackValue(0, "this", parent != null ? parent : scope.rootThis());
            return functionScope;
        }
    }

    @Override
    public boolean toBoolean() {
        return true;
    }

    @Override
    public String toString() {
        return name != null ? "[function " + name + "]" : "[function]";
    }

    @Override
    public String typeOf() {
        return "function";
    }

    public static class LocalFunctionExec implements IJsFunctionExec {
        private final ASTNode body;

        public LocalFunctionExec(ASTNode body) {
            this.body = body;
        }

        @Override
        public Object exec(AstVisitor visitor, List<Object> args) {
            try {
                body.accept(visitor);
            } catch (ReturnException e) {
                return e.throwValue();
            }
            return Undefined.INSTANCE;
        }
    }

    public static JsFunction nativeFunction(String name, IJsFunctionExec nativeFunction) {
        int pos1 = name.indexOf("(");
        int pos2 = name.indexOf(")");
        String functionName = pos1 > 0 ? name.substring(0, pos1) : name;
        List<String> params = pos1 > 0 && pos2 > pos1 ? List.of(name.substring(pos1 + 1, pos2).split(",")) : List.of();
        return new JsFunction(functionName, params, nativeFunction, false, null);
    }
}
