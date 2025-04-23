package eu.aston.javajs;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiFunction;

public class JsOps {

    public static BiFunction<Object,Object,Object> operation(String op) {
        return switch (op) {
            case "+" -> stringNumberOp(operateString("+"), operateInt("+"), operateLong("+"), operateDouble("+"));
            case "-" -> numberOp(operateInt("-"), operateLong("-"), operateDouble("-"));
            case "*" -> numberOp(operateInt("*"), operateLong("*"), operateDouble("*"));
            case "/" -> numberOp(operateInt("/"), operateLong("/"), operateDouble("/"));
            case "%" -> numberOp(operateInt("%"), operateLong("%"), operateDouble("%"));
            case "**" -> numberOp(operateInt("**"), operateLong("**"), operateDouble("**"));
            case "==" -> JsOps::equal;
            case "===" -> JsOps::strictEqual;
            case "!=" -> (a, b) -> !equal(a, b);
            case "!==" -> (a, b) -> !strictEqual(a, b);
            case "<" -> stringNumberOp(operateString("<"), operateInt("<"), operateLong("<"), operateDouble("<"));
            case ">" -> stringNumberOp(operateString(">"), operateInt(">"), operateLong(">"), operateDouble(">"));
            case "<=" -> stringNumberOp(operateString("<="), operateInt("<="), operateLong("<="), operateDouble("<="));
            case ">=" -> stringNumberOp(operateString(">="), operateInt(">="), operateLong(">="), operateDouble(">="));
            default -> null;
        };
    }

    public static BiFunction<Object,Object,Object> numberPlus(){
        return numberOp(operateInt("+"), operateLong("+"), operateDouble("+"));
    }

    private static boolean isNumber(Object obj) {
        return obj==null || obj instanceof Number || obj instanceof Boolean || obj==Undefined.INSTANCE;
    }

    private static BiFunction<Object,Object,Object> stringNumberOp(BiFunction<String, String, Object> operateString, BiFunction<Integer, Integer,Object> operateInt, BiFunction<Long, Long,Object> operateLong, BiFunction<Double, Double,Object> operateDouble) {
        return (left,right)->{
            if(isNumber(left) && isNumber(right)){
                return numberOp(operateInt, operateLong, operateDouble).apply(left, right);
            }
            String  leftStr = JsTypes.toString(left);
            String  rightStr = JsTypes.toString(right);
            return operateString.apply(leftStr, rightStr);
        };
    }

    private static BiFunction<Object,Object,Object> numberOp(BiFunction<Integer, Integer,Object> operateInt, BiFunction<Long, Long,Object> operateLong, BiFunction<Double, Double,Object> operateDouble) {
        return (left,right)->{
            Number leftNum = JsTypes.toNumber(left);
            Number rightNum = JsTypes.toNumber(right);
            if(leftNum instanceof Integer leftInt && rightNum instanceof Integer rightInt){
                return operateInt.apply(leftInt, rightInt);
            } else if(!(leftNum instanceof Double) && !(rightNum instanceof Double)){
                return operateLong.apply(leftNum.longValue(), rightNum.longValue());
            }
            return operateDouble.apply(leftNum.doubleValue(), rightNum.doubleValue());
        };
    }

    public static boolean equal(Object left, Object right){
        if(left==Undefined.INSTANCE) left = null;
        if(right==Undefined.INSTANCE) right = null;
        if(left == right){
            return true;
        }
        if(isNumber(left) || isNumber(right)) {
            return equalNumbers(JsTypes.toNumber(left), JsTypes.toNumber(right));
        }
        if(left instanceof Boolean || right instanceof Boolean) {
            return JsTypes.toBoolean(left)== JsTypes.toBoolean(right);
        }
        if(left instanceof Map || right instanceof Map || left instanceof List || right instanceof List){
            return false;
        }
        return Objects.equals(JsTypes.toString(left), JsTypes.toString(right));
    }
    public static boolean strictEqual(Object left, Object right){
        if(left==right) return true;
        if(left instanceof Map || right instanceof Map|| left instanceof List || right instanceof List
                || left==null || right==null || left==Undefined.INSTANCE || right==Undefined.INSTANCE){
            return false;
        }
        if(left instanceof Number && right instanceof Number) {
            if(left.equals(right)) return true;
            if (left instanceof Double || right instanceof Double) {
                return ((Number) left).doubleValue() == ((Number) right).doubleValue();
            } else if (left instanceof Long || right instanceof Long) {
                return ((Number) left).longValue() == ((Number) right).longValue();
            }
            return ((Number) left).intValue() == ((Number) right).intValue();
        }
        return left.equals(right);
    }

    public static boolean equalNumbers(Number left, Number right){
        if(Objects.equals(left, right)) return true;
        if(left instanceof Double || right instanceof Double){
            return left.doubleValue() == right.doubleValue();
        } else if (left instanceof Long || right instanceof Long) {
            return left.longValue() == right.longValue();
        }
        return left.intValue() == right.intValue();
    }

    public static BiFunction<String,String,Object> operateString(String operator) {
        return switch (operator) {
            case "+" -> (left, right) -> left+right;
            case "<" -> (left, right) -> left.compareTo(right)<0;
            case ">" -> (left, right) -> left.compareTo(right)>0;
            case "<=" -> (left, right) -> left.compareTo(right)<=0;
            case ">=" -> (left, right) -> left.compareTo(right)>=0;
            default -> null;
        };
    }

    public static BiFunction<Integer,Integer,Object> operateInt(String operator) {
        return switch (operator) {
            case "+" -> Integer::sum;
            case "-" -> (left, right) -> left - right;
            case "*" -> (left, right) -> left * right;
            case "/" -> (left, right) -> right != 0 ? left / right : Double.NaN; // Ochrana pred delením nulou
            case "%" -> (left, right) -> right != 0 ? left % right : Double.NaN; // Ochrana pred delením nulou
            case "**" -> Math::pow;
            case "<" -> (left, right) -> left < right;
            case ">" -> (left, right) -> left > right;
            case "<=" -> (left, right) -> left <= right;
            case ">=" -> (left, right) -> left >= right;
            default -> null;
        };
    }

    public static BiFunction<Long,Long,Object> operateLong(String operator) {
        return switch (operator) {
            case "+" -> Long::sum;
            case "-" -> (left, right) -> left - right;
            case "*" -> (left, right) -> left * right;
            case "/" -> (left, right) -> right != 0L ? left / right : Double.NaN; // Ochrana pred delením nulou
            case "%" -> (left, right) -> right != 0L ? left % right : Double.NaN; // Ochrana pred delením nulou
            case "**" -> Math::pow;
            case "<" -> (left, right) -> left < right;
            case ">" -> (left, right) -> left > right;
            case "<=" -> (left, right) -> left <= right;
            case ">=" -> (left, right) -> left >= right;
            default -> null;
        };
    }

    public static BiFunction<Double,Double,Object> operateDouble(String operator) {
        return switch (operator) {
            case "+" -> Double::sum;
            case "-" -> (left, right) -> left - right;
            case "*" -> (left, right) -> left * right;
            case "/" -> (left, right) -> right != 0.0 ? left / right : Double.NaN; // Ochrana pred delením nulou
            case "%" -> (left, right) -> right != 0.0 ? left % right : Double.NaN; // Ochrana pred delením nulou
            case "**" -> Math::pow;
            case "<" -> (left, right) -> left < right;
            case ">" -> (left, right) -> left > right;
            case "<=" -> (left, right) -> left <= right;
            case ">=" -> (left, right) -> left >= right;
            default -> null;
        };
    }
}
