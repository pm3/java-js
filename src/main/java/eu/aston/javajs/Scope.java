package eu.aston.javajs;

import java.util.HashMap;
import java.util.Map;

import eu.aston.javajs.types.IJsFunctionExec;
import eu.aston.javajs.types.JsFunction;

public class Scope {

    private final Map<String, Object> variables;
    private final Object[] localStack;
    private final Ref[] extRefStack;
    private final Scope parentScope;
    private final Scope rootScope;

    //root scope
    public Scope() {
        this.variables = new HashMap<>();
        this.variables.put("this", new HashMap<>());
        this.localStack = null;
        this.extRefStack = null;
        this.parentScope = null;
        this.rootScope = this;
    }

    public Scope(Scope parentScope, int size, Ref[] extRefStack) {
        this.variables = null;
        this.localStack = new Object[size];
        this.extRefStack = extRefStack;
        this.parentScope = parentScope;
        this.rootScope = parentScope.rootScope != null ? parentScope.rootScope : parentScope;
    }

    public Map<String, Object> rootThis() {
        return rootScope.variables;
    }

    public Object getValue(String name) {
        return rootScope.variables.get(name);
    }

    public void setValue(String name, Object value) {
        rootScope.variables.put(name, value);
    }

    public JsFunction getFunction(String name) {
        return getValue(name) instanceof JsFunction fn ? fn : null;
    }

    public void nativeFunction(String name, IJsFunctionExec nativeFunction) {
        JsFunction function = JsFunction.nativeFunction(name, nativeFunction);
        Map<String, Object> vars = variables != null ? variables : rootScope.variables;
        vars.put(function.name(), function);
    }

    public void setStackValue(int index, String name, Object value) {
        Object val = localStack[index];
        if (val instanceof Ref ref) {
            ref.value = value;
        } else {
            localStack[index] = value;
        }
    }

    public Object getStackValue(int index, String name) {
        Object val = localStack[index];
        return val instanceof Ref ref ? ref.value : val;
    }

    public static class ScopeDef {
        VariablesAnalyzer.Fn fn;

        public ScopeDef(VariablesAnalyzer.Fn fn) {
            this.fn = fn;
        }

        public int size() {
            return fn.vars.size();
        }

        public Scope createInitScope(Scope scope) {
            //System.out.println("init scope " + fn.name);
            if (fn.extRefs.isEmpty()) {
                return new Scope(scope, fn.vars.size(), null);
            }
            Scope[] parents = new Scope[fn.deepLevel + 1];
            Scope s = scope;
            for (int i = 0; i < parents.length; i++) {
                parents[i] = s;
                s = s.parentScope;
            }
            Ref[] refs = new Ref[fn.extRefs.size()];
            for (VariablesAnalyzer.ExtRef extRef : fn.extRefs) {
                Object val = parents[extRef.deep].localStack[extRef.varIndex];
                if (val instanceof Ref ref) {
                    refs[extRef.index] = ref;
                } else {
                    Ref ref = new Ref(val);
                    parents[extRef.deep].localStack[extRef.varIndex] = ref;
                    refs[extRef.index] = ref;
                }
            }
            return new Scope(scope, fn.vars.size(), refs);
        }
    }

    public interface IGetSet {
        Object get(Scope scope);

        void set(Scope scope, Object value);

        default void init(Scope scope, Object value) {
            set(scope, value);
        }
    }

    public static class LocalGetSet implements IGetSet {
        private final int index;

        public LocalGetSet(int index) {
            this.index = index;
        }

        @Override
        public Object get(Scope scope) {
            Object val = scope.localStack[index];
            return val instanceof Ref ref ? ref.value : val;
        }

        @Override
        public void set(Scope scope, Object value) {
            Object val = scope.localStack[index];
            if (val instanceof Ref ref) {
                ref.value = value;
            } else {
                scope.localStack[index] = value;
            }
        }

        @Override
        public void init(Scope scope, Object value) {
            Object val = scope.localStack[index];
            if (val instanceof Ref ref && ref.value == null) {
                ref.value = value;
            } else {
                scope.localStack[index] = value;
            }
        }
    }

    public static class ExtGetSet implements IGetSet {
        private final int index;

        public ExtGetSet(int index) {
            this.index = index;
        }

        @Override
        public Object get(Scope scope) {
            return scope.extRefStack[index].value;
        }

        @Override
        public void set(Scope scope, Object value) {
            scope.extRefStack[index].value = value;
        }
    }

    public static class MapGetSet implements IGetSet {
        private final String name;

        public MapGetSet(String name) {
            this.name = name;
        }

        @Override
        public Object get(Scope scope) {
            if (scope.rootScope.variables.containsKey(name)) {
                return scope.rootScope.variables.get(name);
            }
            throw new RuntimeException("Variable '" + name + "' not found");
        }

        @Override
        public void set(Scope scope, Object value) {
            scope.rootScope.variables.put(name, value);
        }
    }

    public static class Ref {
        public Object value;

        public Ref(Object value) {
            this.value = value;
        }
    }
}
