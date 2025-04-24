package eu.aston.javajs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import eu.aston.javajs.AstNodes.*;

@SuppressWarnings({"rawtypes", "unchecked"})
public record GetSet(Object value, Consumer<Object> setter) {

    public static final String LENGTH = "length";

    public static GetSet createGetSet(Object parent, Object property, Scope scope){
        Object value = switch (parent) {
            case Map map -> mapGet(map, property, scope);
            case List list -> listGet(list, property, scope);
            case String str -> stringGet(str, property, scope);
            case Scope scope2 -> scopeGet(scope2, property);
            case null, default -> throw new NotFoundException(
                    "Cannot read property '" + property + "' of " + JsTypes.typeof(parent));
        };
        return new GetSet(value, (newValue)->execSet(parent, property, newValue));
    }

    public static void execSet(Object parent, Object property, Object value){
        switch (parent) {
            case Map map -> mapPut(map, property, value);
            case List list -> listSet(list, property, value);
            case Scope scope2 -> scopeSet(scope2, property, value);
            case null, default -> throw new NotFoundException(
                    "Cannot set property '" + property + "' on " + JsTypes.typeof(parent));
        }
    }

    public static Object stringGet(String str, Object property, Scope scope) {
        if(LENGTH.equals(property)){
            return str.length();
        }
        if(property instanceof Number index){
            return index.intValue()>=0 && index.intValue()<str.length() ? str.charAt(index.intValue()) : Undefined.INSTANCE;
        }
        if(property instanceof String){
            JsFunction function = scope.getFunction("String."+property);
            if(function!=null){
                return function.setParent(str);
            }
        }
        throw new NotFoundException("String function '" + property + "' is not defined");
    }

    public static Object mapGet(Map map, Object property, Scope scope) {
        if(LENGTH.equals(property)){
            return map.size();
        }
        String strProperty = JsTypes.toString(property);
        if(map.containsKey(strProperty)){
            return map.get(strProperty);
        }
        JsFunction typeFunction = scope.getFunction("Object."+property);
        if(typeFunction!=null){
            return typeFunction.setParent(map);
        }
        return Undefined.INSTANCE;
    }

    public static void mapPut(Map map, Object property, Object value) {
        try{
            map.put(JsTypes.toString(property), value);
        }catch (Exception e){
            throw new RuntimeException("Cannot set property - object is readonly");
        }
    }

    public static Object listGet(List list, Object property, Scope scope) {
        if(LENGTH.equals(property)){
            return list.size();
        }
        if(property instanceof String){
            JsFunction function = scope.getFunction("Array."+property);
            if(function!=null){
                return function.setParent(list);
            }
        }
        Integer index = parseIndex(property);
        if(index!=null){
            return index>=0 && index<list.size() ? list.get(index) : Undefined.INSTANCE;
        }
        return Undefined.INSTANCE;
    }

    public static void listSet(List list, Object property, Object value) {
        Integer index = parseIndex(property);
        if(index!=null){
            if(index<0){
                throw new NotFoundException("Array index out of bounds");
            }
            if(index>AstNodes.INFINITE_LOOP_LIMIT){
                throw new NotFoundException("Array index out of bounds, max limit is "+AstNodes.INFINITE_LOOP_LIMIT);
            }
            while(index>=list.size()){
                list.add(Undefined.INSTANCE);
            }
            list.set(index, value);
            return;
        }
        throw new NotFoundException("Cannot set property '" + property + "' of array");
    }

    public static Integer parseIndex(Object property) {
        Integer index = null;
        if(property instanceof Number num){
            index = num.intValue();
        } else if(property instanceof String str2){
            try{
                index = Integer.parseInt(str2);
            }catch (Exception ignore){
                //ignore
            }
        }
        return index;
    }

    public static Object scopeGet(Scope scope2, Object property) {
        String name = JsTypes.toString(property);
        Scope.VarAccess varAccess = scope2.getVar(name);
        if(varAccess!=null){
            return varAccess.value();
        }
        throw new NotFoundException("ReferenceError: " + name + " is not defined");
    }

    public static void scopeSet(Scope scope, Object property, Object value) {
        String name = JsTypes.toString(property);
        Scope.VarAccess varAccess = scope.getVar(name);
        if(varAccess!=null){
            if(varAccess.constant()){
                throw new NotFoundException("TypeError: Assignment to constant variable '" + name +"'");
            }
            varAccess.setValue(value);
            return;
        }
        JsFunction function = scope.getFunction(name);
        if(function!=null){
            throw new NotFoundException("TypeError: Assignment to constant function '" + name +"'");
        }
        throw new NotFoundException("ReferenceError: " + name + " is not defined");
        //scope.putVariable(name, value);
    }

}

