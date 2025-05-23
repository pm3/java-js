package eu.aston.javajs.types;

import java.time.LocalDateTime;
import java.util.List;

import eu.aston.javajs.AstVisitor;
import eu.aston.javajs.Scope;

public class JsExtra {

    public static void defineDateFunctions(Scope scope) {
        scope.nativeFunction("dateExtract(date,field)", JsExtra::dateExtract);
        scope.nativeFunction("dateFormat(date,format)", JsExtra::dateFormat);
        scope.nativeFunction("dateDiff(date,date,unit)", JsExtra::dateDiff);
        scope.nativeFunction("dateAdd(date,val,field)", JsExtra::dateAdd);
        scope.nativeFunction("now()", JsExtra::now);
    }

    public static LocalDateTime toLocalDateTime(Object val) {
        return null;
    }

    public static Object dateExtract(AstVisitor visitor, List<Object> args) {
        return null;
    }

    public static Object dateFormat(AstVisitor visitor, List<Object> args) {
        return null;
    }

    public static Object dateDiff(AstVisitor visitor, List<Object> args) {
        return null;
    }

    public static Object dateAdd(AstVisitor visitor, List<Object> args) {
        return null;
    }

    public static Object now(AstVisitor visitor, List<Object> args) {
        return null;
    }
}
