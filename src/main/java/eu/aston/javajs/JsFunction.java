package eu.aston.javajs;

import java.util.List;
import java.util.function.BiFunction;
import eu.aston.javajs.AstNodes.*;

public class JsFunction implements IJsType {

    final String name;
    final List<String> params;
    final ASTNode body;
    final BiFunction<Scope, List<Object>, Object> nativeFunction;
    private boolean useLocalScope = false;
    final private Object parent;

    public JsFunction(String name, List<String> params, ASTNode body, boolean useLocalScope) {
        this.name = name;
        this.params = params;
        this.body = body;
        this.nativeFunction = null;
        this.parent = null;
        this.useLocalScope = useLocalScope;
    }

    public JsFunction(String name, List<String> params, BiFunction<Scope, List<Object>, Object> nativeFunction) {
        this.name = name;
        this.params = params;
        this.body = null;
        this.nativeFunction = nativeFunction;
        this.parent = null;
    }

    private JsFunction(String name, List<String> params, ASTNode body,
                       BiFunction<Scope, List<Object>, Object> nativeFunction, boolean useLocalScope, Object parent) {
        this.name = name;
        this.params = params;
        this.body = body;
        this.nativeFunction = nativeFunction;
        this.useLocalScope = useLocalScope;
        this.parent = parent;
    }

    public JsFunction setParent(Object parent) {
        return new JsFunction(name, params, body, nativeFunction, useLocalScope, parent);
    }

    public Object exec(Scope scope, List<Object> args) {
        Scope functionScope = parent instanceof Scope ? ((Scope) parent).newFunctionBlock(useLocalScope, null) : scope.newFunctionBlock(useLocalScope, parent);
        functionScope.putVariable("arguments", args);
        for (int i = 0; i < params.size(); i++) {
            String param = params.get(i);
            if (i < args.size()) {
                functionScope.putVariable(param, args.get(i));
            } else {
                functionScope.putVariable(param, Undefined.INSTANCE);
            }
        }
        if (body != null) {
            try {
                body.exec(functionScope);
            }catch (ReturnException e){
                return e.throwValue();
            }
            return Undefined.INSTANCE;
        }
        if (nativeFunction != null) {
            return nativeFunction.apply(functionScope, args);
        }
        return Undefined.INSTANCE;
    }

    @Override
    public boolean toBoolean() {
        return true;
    }

    @Override
    public String toString() {
        return name!=null ?"[function "+name+"]" : "[function]";
    }

    @Override
    public String typeOf() {
        return "function";
    }
}
