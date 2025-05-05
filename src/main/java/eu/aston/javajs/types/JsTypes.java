package eu.aston.javajs.types;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsTypes {

    public static boolean toBoolean(Object value) {
        return switch (value) {
            case null -> false;
            case Boolean b -> b;
            case String s -> !s.isEmpty();
            case Integer number -> number != 0;
            case Long number -> number != 0L;
            case Double number -> !number.isNaN() && number != 0.0;
            case List<?> l -> !l.isEmpty();
            case Map<?, ?> m -> !m.isEmpty();
            case IJsType t -> t.toBoolean();
            default -> false;
        };
    }

    public static Number toNumber(Object value) {
        return switch (value) {
            case null -> 0;
            case Number n -> n;
            case Boolean b -> b ? 1 : 0;
            case String s -> toNumberString(s);
            default -> Double.NaN;
        };
    }

    public static Number toNumberString(String s) {
        if (s.trim().isEmpty()) {
            return 0;
        }
        if (s.equals("NaN")) {
            return Double.NaN;
        }
        if (s.equals("Infinity")) {
            return Double.POSITIVE_INFINITY;
        }
        if (s.matches("0[xX][0-9a-fA-F]+")) {
            return Integer.parseInt(s.substring(2), 16);
        }
        if (s.matches("\\d+")) {
            //check if is integer or long
            long num = Long.parseLong(s);
            return num < Integer.MAX_VALUE ? (int) num : num;
        }
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    public static String toString(Object value) {
        return switch (value) {
            case null -> "null";
            case List<?> l -> toStringList(l);
            case Map<?, ?> ignore -> "[object Object]";
            default -> value.toString();
        };
    }

    static String toStringList(List<?> value) {
        return value.stream().map(JsTypes::toString).collect(Collectors.joining(","));
    }

    public static Object unaryMinus(Object value) {
        if (value instanceof String) {
            value = toNumberString((String) value);
        }
        return switch (value) {
            case null -> 0;
            case Integer i -> -i;
            case Long l -> -l;
            case Double d -> -d;
            case Boolean b -> b ? -1 : 0;
            default -> Double.NaN;
        };
    }

    public static Object typeof(Object value) {
        return switch (value) {
            case null -> "null";
            case Boolean ignore -> "boolean";
            case Number ignore -> "number";
            case String ignore -> "string";
            case List<?> ignore -> "array";
            case Map<?, ?> ignore -> "object";
            case IJsType t -> t.typeOf();
            default -> value.getClass().getSimpleName();
        };
    }

    public static String unescapeString(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }

        // Remove surrounding quotes if present
        if ((str.startsWith("\"") && str.endsWith("\"")) || (str.startsWith("'") && str.endsWith("'"))) {
            str = str.substring(1, str.length() - 1);
        }

        StringBuilder result = new StringBuilder();
        boolean escapeActive = false;

        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);

            if (escapeActive) {
                switch (c) {
                    case 'n' -> result.append('\n');
                    case 'r' -> result.append('\r');
                    case 't' -> result.append('\t');
                    case 'b' -> result.append('\b');
                    case 'f' -> result.append('\f');
                    case '\\' -> result.append('\\');
                    case '\'' -> result.append('\'');
                    case '\"' -> result.append('\"');
                    case '0' -> result.append('\0');
                    case 'u' -> {
                        // Handle Unicode escape sequence \\uXXXX
                        if (i + 4 < str.length()) {
                            String hex = str.substring(i + 1, i + 5);
                            try {
                                result.append((char) Integer.parseInt(hex, 16));
                                i += 4; // Skip the 4 hex digits
                            } catch (NumberFormatException e) {
                                // Invalid hex sequence, just append \\u and continue
                                result.append('\\').append('u');
                            }
                        } else {
                            // Incomplete Unicode sequence
                            result.append('\\').append('u');
                        }
                    }
                    default -> result.append(c); // Unknown escape sequence, just append the character
                }
                escapeActive = false;
            } else if (c == '\\') {
                escapeActive = true;
            } else {
                result.append(c);
            }
        }

        // If the string ends with a single backslash
        if (escapeActive) {
            result.append('\\');
        }

        return result.toString();
    }
}

