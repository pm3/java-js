package eu.aston.javajs;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class Scope implements IJsType, AutoCloseable {

    private final Map<String, VarAccess> variables = new java.util.HashMap<>();
    private final Scope parentScope;
    private int level = 1;

    public JsFunction getFunction(String name) {
        VarAccess varAccess = getVar(name);
        if (varAccess != null && varAccess.value instanceof JsFunction) {
            return (JsFunction) varAccess.value;
        }
        return null;
    }

    public static class VarAccess {
        private final boolean constant;
        private Object value;
        private final int level;

        public VarAccess(boolean constant, Object value, int level) {
            this.constant = constant;
            this.value = value;
            this.level = level;
        }

        public boolean constant() {
            return constant;
        }

        public Object value() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }

    public Scope() {
        this.parentScope = null;
    }

    private Scope(Scope scope) {
        this.parentScope = scope;
    }

    public void defineLocalVar(boolean constant, String identifier, Object value) {
        if(variables.containsKey(identifier)){
            throw new RuntimeException("Variable "+identifier+" already defined");
        }
        variables.put(identifier, new VarAccess(constant, value, level));
    }

    public void defineVar(String identifier, Object value){
        if(variables.containsKey(identifier)){
            throw new RuntimeException("Variable "+identifier+" already defined");
        }
        variables.put(identifier, new VarAccess(false, value, 0));
    }

    public VarAccess getVar(String identifier) {
        VarAccess varAccess = variables.get(identifier);
        if (varAccess == null && parentScope != null) {
            varAccess = parentScope.getVar(identifier);
        }
        return varAccess;
    }

    public void replaceVar(String identifier, Object newValue) {
        VarAccess varAccess = getVar(identifier);
        if (varAccess!=null) {
            if (varAccess.constant) {
                throw new RuntimeException("Assignment to constant variable '" + identifier+"'");
            }
            varAccess.value = newValue;
        } else {
            throw new RuntimeException("Cannot set undefined variable '" + identifier+"'");
        }
    }

    public void putVariable(String name, Object value) {
        variables.put(name, new VarAccess(false, value, level));
    }

    public void nativeFunction(String name, BiFunction<Scope, List<Object>, Object> nativeFunction) {
        int pos1 = name.indexOf("(");
        int pos2 = name.indexOf(")");
        String functionName = pos1>0 ? name.substring(0, pos1) : name;
        List<String> params = pos1>0 && pos2>pos1 ? List.of(name.substring(pos1+1, pos2).split(",")) : List.of();
        JsFunction function = new JsFunction(functionName, params, nativeFunction);
        variables.put(functionName, new VarAccess(true, function, 0));
    }

    public Scope newBlock() {
        this.level++;
        return this;
    }

    public Scope newFunctionBlock(boolean usedLocalScope, Object parent) {
        if(usedLocalScope){
            this.level++;
            return this;
        }
        Scope newScope = new Scope(this);
        if(parent!=null){
            newScope.putVariable("this", parent);
        }
        return newScope;
    }

    @Override
    public void close() {
        if(level>0){
            variables.entrySet().removeIf(entry -> entry.getValue().level >= level);
            level--;
        }
    }

    @Override
    public boolean toBoolean() {
        return true;
    }
    @Override
    public String toString() {
        return "[object]";
    }

    @Override
    public String typeOf() {
        return "object";
    }
}
