package eu.aston.javajs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.aston.javajs.types.IJsFunctionExec;
import eu.aston.javajs.types.JsFunction;

public class Scope {

    private final Map<String, Object> variables;
    private final Ref[] stack;
    private final Scope parentScope;
    private final Scope rootScope;

    //root scope
    public Scope() {
        this.variables = new HashMap<>();
        this.variables.put("this", new HashMap<>());
        this.stack = null;
        this.parentScope = null;
        this.rootScope = this;
    }

    public Scope(Scope parentScope, int size) {
        this.variables = null;
        this.stack = new Ref[size];
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

    public Object getValue(int index, String name) {
        if (index >= 0) {
            Ref ref = stack[index];
            if (ref == null) {
                throw new RuntimeException("ReferenceError: variable not found '" + name + "'");
            }
            return ref.value;
        }
        if (!rootScope.variables.containsKey(name)) {
            throw new RuntimeException("ReferenceError: variable not found '" + name + "'");
        }
        return rootScope.variables.get(name);
    }

    public void setStackValue(int index, String name, Object value) {
        if (index >= 0) {
            Ref ref = stack[index];
            if (ref != null) {
                ref.value = value;
            } else {
                stack[index] = new Ref(value);
            }
        } else {
            setValue(name, value);
        }
    }

    public void nativeFunction(String name, IJsFunctionExec nativeFunction) {
        JsFunction function = JsFunction.nativeFunction(name, nativeFunction);
        Map<String, Object> vars = variables != null ? variables : rootScope.variables;
        vars.put(function.name(), function);
    }

    public static class ScopeDef {
        VariablesAnalyzer.FnVar akt;
        int[][] extDef = null;
        int deepSize;

        public ScopeDef(VariablesAnalyzer.FnVar akt) {
            this.akt = akt;
        }

        public int size() {
            return akt.children.size();
        }

        private void createExtDef() {
            List<int[]> l = new ArrayList<>();
            for (VariablesAnalyzer.FnVar v : akt.children) {
                if (v.ext != null) {
                    l.add(v.ext);
                    if (deepSize < v.ext[1]) {
                        deepSize = v.ext[1];
                    }
                }
            }
            this.extDef = l.toArray(new int[0][0]);
        }

        public Ref[] createInitScope(Scope scope) {
            if (extDef == null) {
                createExtDef();
            }
            Scope[] parents = new Scope[deepSize + 1];
            Scope s = scope;
            for (int i = 0; i < parents.length; i++) {
                parents[i] = s;
                s = s.parentScope;
            }
            Ref[] extRefs = new Ref[extDef.length];
            for (int i = 0; i < extRefs.length; i++) {
                int[] deep = extDef[i];
                Ref ref = parents[deep[1]].stack[deep[2]];
                if (ref == null) {
                    ref = new Ref(null);
                    parents[deep[1]].stack[deep[2]] = ref;
                }
                extRefs[i] = ref;
            }
            return extRefs;
        }

        public void useInitScope(Scope functionScope, Ref[] initScope) {
            if (initScope == null || initScope.length == 0 || functionScope == null || functionScope.stack == null) {
                return;
            }
            if (extDef == null) {
                createExtDef();
            }
            for (int i = 0; i < extDef.length; i++) {
                int[] deep = extDef[i];
                if (deep[0] >= functionScope.stack.length) {
                    System.out.println("chyba s init scope ");
                }
                functionScope.stack[deep[0]] = initScope[i];
            }
        }
    }

    public static class Ref {
        public Object value;

        public Ref(Object value) {
            this.value = value;
        }
    }
}
