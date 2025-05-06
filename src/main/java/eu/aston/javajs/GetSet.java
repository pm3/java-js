package eu.aston.javajs;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import eu.aston.javajs.AstNodes.NotFoundException;
import eu.aston.javajs.types.JsFunction;
import eu.aston.javajs.types.JsTypes;
import eu.aston.javajs.types.Undefined;

@SuppressWarnings({"rawtypes", "unchecked"})
public record GetSet(Object value, Consumer<Object> setter) {

    public static final String LENGTH = "length";

    public static GetSet createGetSet(Object parent, Object property, Scope scope, TokenPos tokenPos) {
        Object value = switch (parent) {
            case Map map -> mapGet(map, property, scope);
            case List list -> listGet(list, property, scope, tokenPos);
            case String str -> stringGet(str, property, scope, tokenPos);
            case JsFunction fn -> functionGet(fn, property, scope, tokenPos);
            case null, default ->
                    throw new NotFoundException("Cannot read property '" + property + "' of " + JsTypes.typeof(parent),
                                                tokenPos);
        };
        return new GetSet(value, (newValue) -> execSet(parent, property, newValue, tokenPos));
    }

    public static void execSet(Object parent, Object property, Object value, TokenPos tokenPos) {
        switch (parent) {
            case Map map -> mapPut(map, property, value, tokenPos);
            case List list -> listSet(list, property, value, tokenPos);
            case null, default ->
                    throw new NotFoundException("Cannot set property '" + property + "' on " + JsTypes.typeof(parent),
                                                tokenPos);
        }
    }

    public static Object stringGet(String str, Object property, Scope scope, TokenPos tokenPos) {
        if (LENGTH.equals(property)) {
            return str.length();
        }
        if (property instanceof Number index) {
            return index.intValue() >= 0 && index.intValue() < str.length() ? str.charAt(index.intValue())
                                                                            : Undefined.INSTANCE;
        }
        if (property instanceof String) {
            JsFunction function = scope.getFunction("String." + property);
            if (function != null) {
                return function.setParent(str);
            }
        }
        throw new NotFoundException("String function '" + property + "' is not defined", tokenPos);
    }

    public static Object mapGet(Map map, Object property, Scope scope) {
        if (LENGTH.equals(property)) {
            return map.size();
        }
        String strProperty = JsTypes.toString(property);
        if (map.containsKey(strProperty)) {
            return map.get(strProperty);
        }
        JsFunction typeFunction = scope.getFunction("Object." + property);
        if (typeFunction != null) {
            return typeFunction.setParent(map);
        }
        return Undefined.INSTANCE;
    }

    public static void mapPut(Map map, Object property, Object value, TokenPos tokenPos) {
        try {
            map.put(JsTypes.toString(property), value);
        } catch (Exception e) {
            throw new AstNodes.ExecuteScriptException("Cannot set property - object is readonly", tokenPos);
        }
    }

    public static Object listGet(List list, Object property, Scope scope, TokenPos tokenPos) {
        if (LENGTH.equals(property)) {
            return list.size();
        }
        if (property instanceof String) {
            JsFunction function = scope.getFunction("Array." + property);
            if (function != null) {
                return function.setParent(list);
            }
            throw new NotFoundException("Array function '" + property + "' is not defined", tokenPos);
        }
        Integer index = parseIndex(property);
        if (index != null) {
            return index >= 0 && index < list.size() ? list.get(index) : Undefined.INSTANCE;
        }
        return Undefined.INSTANCE;
    }

    public static void listSet(List list, Object property, Object value, TokenPos tokenPos) {
        Integer index = parseIndex(property);
        if (index != null) {
            if (index < 0) {
                throw new NotFoundException("Array index out of bounds", tokenPos);
            }
            if (index > AstNodes.INFINITE_LOOP_LIMIT) {
                throw new NotFoundException("Array index out of bounds, max limit is " + AstNodes.INFINITE_LOOP_LIMIT,
                                            tokenPos);
            }
            while (index >= list.size()) {
                list.add(Undefined.INSTANCE);
            }
            list.set(index, value);
            return;
        }
        throw new NotFoundException("Cannot set property '" + property + "' of array", tokenPos);
    }

    public static Integer parseIndex(Object property) {
        Integer index = null;
        if (property instanceof Number num) {
            index = num.intValue();
        } else if (property instanceof String str2) {
            try {
                index = Integer.parseInt(str2);
            } catch (Exception ignore) {
                //ignore
            }
        }
        return index;
    }

    private static Object functionGet(JsFunction fn, Object property, Scope scope, TokenPos tokenPos) {
        if (property instanceof String) {
            JsFunction function = scope.getFunction("Function." + property);
            if (function != null) {
                return function.setParent(fn);
            }
        }
        throw new NotFoundException("Function type function '" + property + "' is not defined", tokenPos);
    }
}

