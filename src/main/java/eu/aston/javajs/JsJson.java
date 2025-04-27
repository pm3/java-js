package eu.aston.javajs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsJson {

    public static class JsonTokenizer {
        private final String str;
        private int index;
        private char currentChar;

        public JsonTokenizer(String str) {
            this.str = str;
            this.index = 0;
            this.currentChar = str.charAt(index);
        }
        
        private void advance() {
            index++;
            if (index < str.length()) {
                currentChar = str.charAt(index);
            } else {
                currentChar = '\0';
            }
        }
        
        private void skipWhitespace() {
            while (index < str.length() && Character.isWhitespace(currentChar)) {
                advance();
            }
        }
        
        private String parseString() {
            StringBuilder sb = new StringBuilder();
            advance(); // Skip the opening quote
            
            while (index < str.length() && currentChar != '"') {
                if (currentChar == '\\') {
                    advance();
                    switch (currentChar) {
                        case '"': sb.append('"'); break;
                        case '\\': sb.append('\\'); break;
                        case '/': sb.append('/'); break;
                        case 'b': sb.append('\b'); break;
                        case 'f': sb.append('\f'); break;
                        case 'n': sb.append('\n'); break;
                        case 'r': sb.append('\r'); break;
                        case 't': sb.append('\t'); break;
                        case 'u':
                            // Parse 4 hex digits
                            StringBuilder hex = new StringBuilder();
                            for (int i = 0; i < 4; i++) {
                                advance();
                                hex.append(currentChar);
                            }
                            sb.append((char) Integer.parseInt(hex.toString(), 16));
                            break;
                        default:
                            sb.append(currentChar);
                    }
                } else {
                    sb.append(currentChar);
                }
                advance();
            }
            if(index==str.length()){
                throw new RuntimeException("unclosed string");
            }
            advance(); // Skip the closing quote
            return sb.toString();
        }
        
        private Object parseNumber() {
            StringBuilder sb = new StringBuilder();
            boolean isDouble = false;
            
            if (currentChar == '-') {
                sb.append(currentChar);
                advance();
            }
            
            while (index < str.length() && (Character.isDigit(currentChar) || currentChar == '.' || 
                    currentChar == 'e' || currentChar == 'E' || currentChar == '+' || currentChar == '-')) {
                if (currentChar == '.' || currentChar == 'e' || currentChar == 'E') {
                    isDouble = true;
                }
                sb.append(currentChar);
                advance();
            }
            
            String num = sb.toString();
            if (isDouble) {
                return Double.parseDouble(num);
            } else {
                long l = Long.parseLong(num);
                return l<Integer.MAX_VALUE ? (int)l : l;
            }
        }
        
        public Object parseValue() {
            skipWhitespace();
            
            if (currentChar == '{') {
                return parseObject();
            } else if (currentChar == '[') {
                return parseArray();
            } else if (currentChar == '"') {
                return parseString();
            } else if (currentChar == 't') {
                parseConstant("true");
                return true;
            } else if (currentChar == 'f') {
                parseConstant("false");
                return false;
            } else if (currentChar == 'n') {
                parseConstant("null");
                return null;
            } else if (Character.isDigit(currentChar) || currentChar == '-') {
                return parseNumber();
            } else {
                throw new RuntimeException("Unexpected token: " + currentChar);
            }
        }
        
        private HashMap<String, Object> parseObject() {
            HashMap<String, Object> map = new HashMap<>();
            
            advance(); // Skip the opening brace
            skipWhitespace();
            
            if (currentChar == '}') {
                advance(); // Skip the closing brace
                return map;
            }
            
            while (true) {
                skipWhitespace();
                
                if (currentChar != '"') {
                    throw new RuntimeException("Expected property name");
                }
                
                String key = parseString();
                
                skipWhitespace();
                
                if (currentChar != ':') {
                    throw new RuntimeException("Expected ':'");
                }
                
                advance(); // Skip the colon
                Object value = parseValue();
                map.put(key, value);
                
                skipWhitespace();
                
                if (currentChar == '}') {
                    advance(); // Skip the closing brace
                    break;
                }
                
                if (currentChar != ',') {
                    throw new RuntimeException("Expected ',' or '}' in string "+str.substring(index));
                }
                
                advance(); // Skip the comma
            }
            
            return map;
        }
        
        private List<Object> parseArray() {
            List<Object> list = new ArrayList<>();
            
            advance(); // Skip the opening bracket
            skipWhitespace();
            
            if (currentChar == ']') {
                advance(); // Skip the closing bracket
                return list;
            }
            
            while (true) {
                Object value = parseValue();
                list.add(value);
                
                skipWhitespace();
                
                if (currentChar == ']') {
                    advance(); // Skip the closing bracket
                    break;
                }
                
                if (currentChar != ',') {
                    throw new RuntimeException("Expected ',' or ']'");
                }
                
                advance(); // Skip the comma
            }
            
            return list;
        }

        public void parseConstant(String constName) {
            if(index + constName.length() > str.length()) {
                throw new RuntimeException("Unexpected token: " + str.substring(index));
            }
            if(!constName.equals(str.substring(index, index + constName.length()))) {
                throw new RuntimeException("Expected '" + constName + "'");
            }
            index += constName.length()-1;
            advance();
        }
    }

    public static Object parse(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        
        JsonTokenizer tokenizer = new JsonTokenizer(str);
        return tokenizer.parseValue();
    }

    public static String stringify(Scope scope, Object value) {
        if (value == null || value==Undefined.INSTANCE) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();
        stringifyValue(value, sb);
        return sb.toString();
    }

    private static boolean stringifyValue(Object value, StringBuilder sb) {
        if(value==null){
            sb.append("null");
        }
        else if (value instanceof Boolean || value instanceof Number) {
            sb.append(value);
        } else if (value instanceof String) {
            stringifyString((String) value, sb);
        } else if (value instanceof List) {
            stringifyArray((List<?>) value, sb);
        } else if (value instanceof Map) {
            stringifyObject((Map<?, ?>) value, sb);
        } else {
            sb.append("null");
            return false;
        }
        return true;
    }

    private static void stringifyString(String str, StringBuilder sb) {
        sb.append('"');
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '/': sb.append("\\/"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < ' ') {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
    }

    private static void stringifyArray(List<?> list, StringBuilder sb) {
        if (list.isEmpty()) {
            sb.append("[]");
            return;
        }

        sb.append("[");
        boolean first = true;
        for (Object o : list) {
            if (!first) {
                sb.append(",");
            }
            stringifyValue(o, sb);
            first = false;
        }
        sb.append("]");
    }

    private static void stringifyObject(Map<?, ?> map, StringBuilder sb) {
        if (map.isEmpty()) {
            sb.append("{}");
            return;
        }

        sb.append("{");
        boolean first = true;
        for (Object key : map.keySet()) {
            int pos = sb.length();
            if (!first) {
                sb.append(",");
            }
            stringifyString(key.toString(), sb);
            sb.append(": ");
            if(!stringifyValue(map.get(key), sb)){
                sb.setLength(pos);
                continue;
            }
            first = false;
        }
        sb.append("}");
    }
}
