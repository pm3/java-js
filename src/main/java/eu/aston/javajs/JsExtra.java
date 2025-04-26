package eu.aston.javajs;

import java.time.LocalDateTime;
import java.util.List;

public class JsExtra {

    public static void defineDateFunctions(Scope scope){
        scope.nativeFunction("dateExtract(date,field)", JsExtra::dateExtract);
        scope.nativeFunction("dateFormat(date,format)", JsExtra::dateFormat);
        scope.nativeFunction("dateDiff(date,date,unit)", JsExtra::dateDiff);
        scope.nativeFunction("dateAdd(date,val,field)", JsExtra::dateAdd);
        scope.nativeFunction("now()", JsExtra::now);
    }

    public static LocalDateTime toLocalDateTime(Object val){
        return null;
    }

    public static Object dateExtract(Scope scope, List<Object> args){
        return null;
    }

    public static Object dateFormat(Scope scope, List<Object> args){
        return null;
    }

    public static Object dateDiff(Scope scope, List<Object> args){
        return null;
    }

    public static Object dateAdd(Scope scope, List<Object> args){
        return null;
    }

    public static Object now(Scope scope, List<Object> args){
        return null;
    }
}
