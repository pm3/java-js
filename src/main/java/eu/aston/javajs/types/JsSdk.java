package eu.aston.javajs.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import eu.aston.javajs.Scope;

@SuppressWarnings({"unchecked", "rawtypes"})
public class JsSdk {

    public static Scope createRootScope() {
        Scope scope = new Scope();
        defineFunctions(scope);
        return scope;
    }


    public static void defineFunctions(Scope scope) {
        // String prototype methods
        scope.nativeFunction("String.charAt(index)", parentTypeFunction(String.class, JsSdk::string_charAt));
        scope.nativeFunction("String.at(index)", parentTypeFunction(String.class, JsSdk::string_at));
        scope.nativeFunction("String.charCodeAt(index)", parentTypeFunction(String.class, JsSdk::string_charCodeAt));
        scope.nativeFunction("String.concat()", parentTypeFunction(String.class, JsSdk::string_concat));
        scope.nativeFunction("String.endsWith(searchString,endPosition)",
                             parentTypeFunction(String.class, JsSdk::string_endsWith));
        scope.nativeFunction("String.includes(searchString,position)",
                             parentTypeFunction(String.class, JsSdk::string_includes));
        scope.nativeFunction("String.indexOf(searchValue,fromIndex)",
                             parentTypeFunction(String.class, JsSdk::string_indexOf));
        scope.nativeFunction("String.lastIndexOf(searchValue,fromIndex)",
                             parentTypeFunction(String.class, JsSdk::string_lastIndexOf));
        scope.nativeFunction("String.match(regexp)", parentTypeFunction(String.class, JsSdk::string_match));
        scope.nativeFunction("String.matchAll(regexp)", parentTypeFunction(String.class, JsSdk::string_matchAll));
        scope.nativeFunction("String.padEnd(targetLength,padString)",
                             parentTypeFunction(String.class, JsSdk::string_padEnd));
        scope.nativeFunction("String.padStart(targetLength,padString)",
                             parentTypeFunction(String.class, JsSdk::string_padStart));
        scope.nativeFunction("String.repeat(count)", parentTypeFunction(String.class, JsSdk::string_repeat));
        scope.nativeFunction("String.replace(searchFor,replaceWith)",
                             parentTypeFunction(String.class, JsSdk::string_replace));
        scope.nativeFunction("String.replaceAll(searchFor,replaceWith)",
                             parentTypeFunction(String.class, JsSdk::string_replaceAll));
        scope.nativeFunction("String.search(regexp)", parentTypeFunction(String.class, JsSdk::string_search));
        scope.nativeFunction("String.slice(start,end)", parentTypeFunction(String.class, JsSdk::string_slice));
        scope.nativeFunction("String.split(separator,limit)", parentTypeFunction(String.class, JsSdk::string_split));
        scope.nativeFunction("String.startsWith(searchString, position)",
                             parentTypeFunction(String.class, JsSdk::string_startsWith));
        scope.nativeFunction("String.substring(start,end)", parentTypeFunction(String.class, JsSdk::string_substring));
        scope.nativeFunction("String.toLocaleLowerCase()",
                             parentTypeFunction(String.class, JsSdk::string_toLocaleLowerCase));
        scope.nativeFunction("String.toLocaleUpperCase()",
                             parentTypeFunction(String.class, JsSdk::string_toLocaleUpperCase));
        scope.nativeFunction("String.toLowerCase()", parentTypeFunction(String.class, JsSdk::string_toLowerCase));
        scope.nativeFunction("String.toUpperCase()", parentTypeFunction(String.class, JsSdk::string_toUpperCase));
        scope.nativeFunction("String.trim()", parentTypeFunction(String.class, JsSdk::string_trim));
        scope.nativeFunction("String.trimEnd()", parentTypeFunction(String.class, JsSdk::string_trimEnd));
        scope.nativeFunction("String.trimStart()", parentTypeFunction(String.class, JsSdk::string_trimStart));

        // Array static methods
        scope.setValue("Array", Map.of("isArray", JsFunction.nativeFunction("isArray(val)", JsSdk::array_isArray)));
        // Array prototype methods
        scope.nativeFunction("Array.concat(arrays)", parentTypeFunction(List.class, JsSdk::array_concat));
        scope.nativeFunction("Array.copyWithin(target,start,end)",
                             parentTypeFunction(List.class, JsSdk::array_copyWithin));
        scope.nativeFunction("Array.every(callbackFn)", parentTypeFunction(List.class, JsSdk::array_every));
        scope.nativeFunction("Array.fill(value,start,end)", parentTypeFunction(List.class, JsSdk::array_fill));
        scope.nativeFunction("Array.filter(callbackFn)", parentTypeFunction(List.class, JsSdk::array_filter));
        scope.nativeFunction("Array.find(callbackFn)", parentTypeFunction(List.class, JsSdk::array_find));
        scope.nativeFunction("Array.findIndex(callbackFn)", parentTypeFunction(List.class, JsSdk::array_findIndex));
        scope.nativeFunction("Array.findLast(callbackFn)", parentTypeFunction(List.class, JsSdk::array_findLast));
        scope.nativeFunction("Array.findLastIndex(callbackFn)",
                             parentTypeFunction(List.class, JsSdk::array_findLastIndex));
        scope.nativeFunction("Array.flat(depth)", parentTypeFunction(List.class, JsSdk::array_flat));
        scope.nativeFunction("Array.flatMap(callbackFn)", parentTypeFunction(List.class, JsSdk::array_flatMap));
        scope.nativeFunction("Array.forEach(callbackFn)", parentTypeFunction(List.class, JsSdk::array_forEach));
        scope.nativeFunction("Array.includes(searchElement,fromIndex)",
                             parentTypeFunction(List.class, JsSdk::array_includes));
        scope.nativeFunction("Array.indexOf(searchElement,fromIndex)",
                             parentTypeFunction(List.class, JsSdk::array_indexOf));
        scope.nativeFunction("Array.join(separator)", parentTypeFunction(List.class, JsSdk::array_join));
        scope.nativeFunction("Array.lastIndexOf(searchElement,fromIndex)",
                             parentTypeFunction(List.class, JsSdk::array_lastIndexOf));
        scope.nativeFunction("Array.map(callbackFn)", parentTypeFunction(List.class, JsSdk::array_map));
        scope.nativeFunction("Array.pop()", parentTypeFunction(List.class, JsSdk::array_pop));
        scope.nativeFunction("Array.push()", parentTypeFunction(List.class, JsSdk::array_push));
        scope.nativeFunction("Array.reduce(callbackFn,initialValue)",
                             parentTypeFunction(List.class, JsSdk::array_reduce));
        scope.nativeFunction("Array.reduceRight(callbackFn,initialValue)",
                             parentTypeFunction(List.class, JsSdk::array_reduceRight));
        scope.nativeFunction("Array.reverse()", parentTypeFunction(List.class, JsSdk::array_reverse));
        scope.nativeFunction("Array.shift()", parentTypeFunction(List.class, JsSdk::array_shift));
        scope.nativeFunction("Array.slice(start,end)", parentTypeFunction(List.class, JsSdk::array_slice));
        scope.nativeFunction("Array.some(callbackFn)", parentTypeFunction(List.class, JsSdk::array_some));
        scope.nativeFunction("Array.sort(compareFn)", parentTypeFunction(List.class, JsSdk::array_sort));
        scope.nativeFunction("Array.splice(start,deleteCount)", parentTypeFunction(List.class, JsSdk::array_splice));
        scope.nativeFunction("Array.unshift()", parentTypeFunction(List.class, JsSdk::array_unshift));
        scope.nativeFunction("Array.with(index,value)", parentTypeFunction(List.class, JsSdk::array_with));

        scope.nativeFunction("Function.apply(thisArg, argsArray)",
                             parentTypeFunction(JsFunction.class, JsSdk::function_apply));
        scope.nativeFunction("Function.call(thisArg)", parentTypeFunction(JsFunction.class, JsSdk::function_call));

        scope.setValue("Object", Map.of("isExtensible",
                                        JsFunction.nativeFunction("isExtensible(val)", JsSdk::object_isExtensible),
                                        "assign",
                                        JsFunction.nativeFunction("assign(target,sources)", JsSdk::object_assign),
                                        "entries", JsFunction.nativeFunction("entries(obj)", JsSdk::object_entries),
                                        "getOwnPropertyNames", JsFunction.nativeFunction("getOwnPropertyNames(obj)",
                                                                                         JsSdk::object_getOwnPropertyNames),
                                        "groupBy",
                                        JsFunction.nativeFunction("groupBy(items,callbackFn)", JsSdk::object_groupBy),
                                        "hasOwn", JsFunction.nativeFunction("hasOwn(obj,prop)", JsSdk::object_hasOwn),
                                        "keys", JsFunction.nativeFunction("keys(obj)", JsSdk::object_keys), "values",
                                        JsFunction.nativeFunction("values(obj)", JsSdk::object_values)));

        // Global functions
        scope.nativeFunction("parseInt(val,radix)", JsSdk::parseInt);
        scope.nativeFunction("parseFloat(val)", JsSdk::parseFloat);
        scope.nativeFunction("isNaN(val)", JsSdk::isNaN);

        scope.nativeFunction("Boolean(val)", (scope2, args) -> JsTypes.toBoolean(args.getFirst()));
        scope.nativeFunction("Number(val)", (scope2, args) -> JsTypes.toNumber(args.getFirst()));
        scope.nativeFunction("String(val)", (scope2, args) -> JsTypes.toString(args.getFirst()));

        scope.setValue("JSON", Map.of("parse", JsFunction.nativeFunction("parse(val)", JsSdk::json_parse), "stringify",
                                      JsFunction.nativeFunction("stringify(val)", JsSdk::json_stringify)));
    }

    // parseInt function
    public static Number parseInt(Scope scope, List<Object> args) {
        if (args.getFirst() != null && args.get(1) != null) {
            try {
                int radix = Integer.parseInt(args.get(1).toString());
                if (radix == 2 || radix == 8 || radix == 10 || radix == 16) {
                    return Long.parseLong(args.getFirst().toString(), radix);
                }
            } catch (Exception ignore) {
            }
        }
        return JsTypes.toNumber(!args.isEmpty() ? args.getFirst() : null);
    }

    // parseFloat function
    public static Number parseFloat(Scope scope, List<Object> args) {
        return JsTypes.toNumber(args.getFirst());
    }

    // isNaN function
    public static Boolean isNaN(Scope scope, List<Object> args) {
        return args.getFirst() instanceof Double d && Double.isNaN(d);
    }

    @FunctionalInterface
    public interface ScopeFunction<T> {
        Object apply(Scope scope, List<Object> args, T parent);
    }

    public static <T> IJsFunctionExec parentTypeFunction(Class<T> type, ScopeFunction<T> fn) {
        return (scope, args) -> {
            Object parent = scope.getValue(0, "this");
            if (parent != null && type.isAssignableFrom(parent.getClass())) {
                return fn.apply(scope, args, (T) parent);
            }
            return null;
        };
    }

    //string functions
    //String.prototype.charAt()
    public static String string_charAt(Scope scope, List<Object> args, String parent) {
        int index = JsTypes.toNumber(args.getFirst()).intValue();
        if (index >= 0 && index < parent.length()) {
            return String.valueOf(parent.charAt(index));
        }
        return "";
    }

    //String.prototype.charCodeAt()
    public static Integer string_charCodeAt(Scope scope, List<Object> args, String parent) {
        int index = JsTypes.toNumber(args.getFirst()).intValue();
        if (index >= 0 && index < parent.length()) {
            return (int) parent.charAt(index);
        }
        return null;
    }

    //String.prototype.concat()
    public static String string_concat(Scope scope, List<Object> args, String parent) {
        StringBuilder sb = new StringBuilder(parent);
        for (Object arg : args) {
            sb.append(JsTypes.toString(arg));
        }
        return sb.toString();
    }

    //String.prototype.endsWith()
    public static Boolean string_endsWith(Scope scope, List<Object> args, String parent) {
        if (args.isEmpty()) {
            return false;
        }
        String searchString = JsTypes.toString(args.getFirst());
        int endPosition = args.size() > 1 ? JsTypes.toNumber(args.get(1)).intValue() : parent.length();

        if (endPosition > parent.length()) {
            endPosition = parent.length();
        }
        String subStr = parent.substring(0, endPosition);
        return subStr.endsWith(searchString);
    }

    //String.prototype.includes()
    public static Boolean string_includes(Scope scope, List<Object> args, String parent) {
        if (args.isEmpty()) {
            return false;
        }
        String searchString = JsTypes.toString(args.getFirst());
        int position = args.size() > 1 ? JsTypes.toNumber(args.get(1)).intValue() : 0;

        if (position < 0) {
            position = 0;
        }
        if (position > parent.length()) {
            return false;
        }

        return parent.indexOf(searchString, position) >= 0;
    }

    //String.prototype.indexOf()
    public static Integer string_indexOf(Scope scope, List<Object> args, String parent) {
        if (args.isEmpty()) {
            return -1;
        }
        String searchValue = JsTypes.toString(args.getFirst());
        int fromIndex = args.size() > 1 ? JsTypes.toNumber(args.get(1)).intValue() : 0;

        if (fromIndex < 0) {
            fromIndex = 0;
        }
        if (fromIndex > parent.length()) {
            return -1;
        }

        return parent.indexOf(searchValue, fromIndex);
    }

    //String.prototype.lastIndexOf()
    public static Integer string_lastIndexOf(Scope scope, List<Object> args, String parent) {
        if (args.isEmpty()) {
            return -1;
        }
        String searchValue = JsTypes.toString(args.getFirst());
        int fromIndex = args.size() > 1 ? JsTypes.toNumber(args.get(1)).intValue() : parent.length();

        if (fromIndex < 0) {
            return -1;
        }
        if (fromIndex > parent.length()) {
            fromIndex = parent.length();
        }

        return parent.lastIndexOf(searchValue, fromIndex);
    }

    //String.prototype.match()
    public static List<String> string_match(Scope scope, List<Object> args, String parent) {
        if (args.isEmpty()) {
            return List.of();
        }
        try {
            String regexpStr = JsTypes.toString(args.getFirst());
            Pattern pattern = Pattern.compile(regexpStr);
            Matcher matcher = pattern.matcher(parent);

            if (matcher.find()) {
                List<String> result = new java.util.ArrayList<>();
                result.add(matcher.group());
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    result.add(matcher.group(i));
                }
                return result;
            }
        } catch (Exception e) {
            // Ignore invalid regex
        }
        return List.of();
    }

    //String.prototype.matchAll()
    public static List<List<String>> string_matchAll(Scope scope, List<Object> args, String parent) {
        if (args.isEmpty()) {
            return List.of();
        }
        try {
            String regexpStr = JsTypes.toString(args.getFirst());
            Pattern pattern = Pattern.compile(regexpStr);
            Matcher matcher = pattern.matcher(parent);

            List<List<String>> results = new java.util.ArrayList<>();
            while (matcher.find()) {
                List<String> match = new java.util.ArrayList<>();
                match.add(matcher.group());
                for (int i = 1; i <= matcher.groupCount(); i++) {
                    match.add(matcher.group(i));
                }
                results.add(match);
            }
            return results;
        } catch (Exception e) {
            // Ignore invalid regex
        }
        return List.of();
    }

    //String.prototype.padEnd()
    public static String string_padEnd(Scope scope, List<Object> args, String parent) {
        if (args.isEmpty()) {
            return parent;
        }
        int targetLength = JsTypes.toNumber(args.getFirst()).intValue();
        String padString = args.size() > 1 ? JsTypes.toString(args.get(1)) : " ";

        if (padString.isEmpty()) {
            padString = " ";
        }
        if (parent.length() >= targetLength) {
            return parent;
        }

        StringBuilder result = new StringBuilder(parent);
        while (result.length() < targetLength) {
            result.append(padString);
            if (result.length() > targetLength) {
                return result.substring(0, targetLength);
            }
        }
        return result.toString();
    }

    //String.prototype.padStart()
    public static String string_padStart(Scope scope, List<Object> args, String parent) {
        if (args.isEmpty()) {
            return parent;
        }
        int targetLength = JsTypes.toNumber(args.getFirst()).intValue();
        String padString = args.size() > 1 ? JsTypes.toString(args.get(1)) : " ";

        if (padString.isEmpty()) {
            padString = " ";
        }
        if (parent.length() >= targetLength) {
            return parent;
        }

        StringBuilder result = new StringBuilder();
        while (result.length() < targetLength - parent.length()) {
            result.append(padString);
            if (result.length() + parent.length() > targetLength) {
                return result.substring(0, targetLength - parent.length()) + parent;
            }
        }
        return result + parent;
    }

    //String.prototype.repeat()
    public static String string_repeat(Scope scope, List<Object> args, String parent) {
        if (args.isEmpty()) {
            return "";
        }
        int count = JsTypes.toNumber(args.getFirst()).intValue();

        if (count <= 0) {
            return "";
        }
        if (count == 1) {
            return parent;
        }

        return parent.repeat(count);
    }

    //String.prototype.replace()
    public static String string_replace(Scope scope, List<Object> args, String parent) {
        if (args.size() < 2) {
            return parent;
        }
        String searchFor = JsTypes.toString(args.getFirst());
        String replaceWith = JsTypes.toString(args.get(1));

        try {
            // Check if searchFor is a regex
            Pattern pattern = Pattern.compile(searchFor);
            Matcher matcher = pattern.matcher(parent);
            if (matcher.find()) {
                return matcher.replaceFirst(replaceWith);
            }
            return parent;
        } catch (Exception e) {
            // Not a valid regex, treat as string
            int index = parent.indexOf(searchFor);
            if (index != -1) {
                return parent.substring(0, index) + replaceWith + parent.substring(index + searchFor.length());
            }
            return parent;
        }
    }

    //String.prototype.replaceAll()
    public static String string_replaceAll(Scope scope, List<Object> args, String parent) {
        if (args.size() < 2) {
            return parent;
        }
        String searchFor = JsTypes.toString(args.getFirst());
        String replaceWith = JsTypes.toString(args.get(1));

        try {
            // Check if searchFor is a regex
            Pattern pattern = Pattern.compile(searchFor);
            return pattern.matcher(parent).replaceAll(replaceWith);
        } catch (Exception e) {
            // Not a valid regex, treat as string
            return parent.replace(searchFor, replaceWith);
        }
    }

    //String.prototype.search()
    public static Integer string_search(Scope scope, List<Object> args, String parent) {
        if (args.isEmpty()) {
            return -1;
        }
        try {
            String regexpStr = JsTypes.toString(args.getFirst());
            Pattern pattern = Pattern.compile(regexpStr);
            Matcher matcher = pattern.matcher(parent);

            if (matcher.find()) {
                return matcher.start();
            }
        } catch (Exception e) {
            // Ignore invalid regex
        }
        return -1;
    }

    //String.prototype.slice()
    public static String string_slice(Scope scope, List<Object> args, String parent) {
        if (args.isEmpty()) {
            return parent;
        }
        int start = JsTypes.toNumber(args.getFirst()).intValue();
        int end = args.size() > 1 ? JsTypes.toNumber(args.get(1)).intValue() : parent.length();

        if (start < 0) {
            start = parent.length() + start;
        }
        if (end < 0) {
            end = parent.length() + end;
        }

        if (start < 0) {
            start = 0;
        }
        if (end > parent.length()) {
            end = parent.length();
        }
        if (start >= end) {
            return "";
        }

        return parent.substring(start, end);
    }

    //String.prototype.split()
    public static List<String> string_split(Scope scope, List<Object> args, String parent) {
        if (args.isEmpty()) {
            return List.of(parent);
        }
        String separator = JsTypes.toString(args.getFirst());
        int limit = args.size() > 1 && args.get(1) instanceof Number num ? num.intValue() : Integer.MAX_VALUE;

        if (limit <= 0) {
            return List.of();
        }
        if (separator.isEmpty()) {
            return parent.chars().mapToObj(c -> String.valueOf((char) c)).limit(limit).collect(Collectors.toList());
        }

        try {
            // Check if separator is a regex
            Pattern pattern = Pattern.compile(separator);
            String[] parts = pattern.split(parent, limit);
            return Arrays.asList(parts);
        } catch (Exception e) {
            // Not a valid regex, treat as string
            String[] parts = parent.split(Pattern.quote(separator), limit);
            return Arrays.asList(parts);
        }
    }

    //String.prototype.startsWith()
    public static Boolean string_startsWith(Scope scope, List<Object> args, String parent) {
        if (args.isEmpty()) {
            return false;
        }
        String searchString = JsTypes.toString(args.getFirst());
        int position = args.size() > 1 ? JsTypes.toNumber(args.get(1)).intValue() : 0;

        if (position < 0) {
            position = 0;
        }
        if (position > parent.length()) {
            return false;
        }

        return parent.startsWith(searchString, position);
    }

    //String.prototype.substring()
    public static String string_substring(Scope scope, List<Object> args, String parent) {
        if (args.isEmpty()) {
            return parent;
        }
        int start = JsTypes.toNumber(args.getFirst()).intValue();
        int end = args.size() > 1 ? JsTypes.toNumber(args.get(1)).intValue() : parent.length();

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        if (start > end) {
            int temp = start;
            start = end;
            end = temp;
        }

        if (start > parent.length()) {
            start = parent.length();
        }
        if (end > parent.length()) {
            end = parent.length();
        }

        return parent.substring(start, end);
    }

    //String.prototype.toLocaleLowerCase()
    public static String string_toLocaleLowerCase(Scope scope, List<Object> args, String parent) {
        return parent.toLowerCase();
    }

    //String.prototype.toLocaleUpperCase()
    public static String string_toLocaleUpperCase(Scope scope, List<Object> args, String parent) {
        return parent.toUpperCase();
    }

    //String.prototype.toLowerCase()
    public static String string_toLowerCase(Scope scope, List<Object> args, String parent) {
        return parent.toLowerCase();
    }

    //String.prototype.toUpperCase()
    public static String string_toUpperCase(Scope scope, List<Object> args, String parent) {
        return parent.toUpperCase();
    }

    //String.prototype.trim()
    public static String string_trim(Scope scope, List<Object> args, String parent) {
        return parent.trim();
    }

    //String.prototype.trimEnd()
    public static String string_trimEnd(Scope scope, List<Object> args, String parent) {
        return parent.stripTrailing();
    }

    //String.prototype.trimStart()
    public static String string_trimStart(Scope scope, List<Object> args, String parent) {
        return parent.stripLeading();
    }

    //String.prototype.at()
    public static String string_at(Scope scope, List<Object> args, String parent) {
        int index = JsTypes.toNumber(args.getFirst()).intValue();
        if (index < 0) {
            index = parent.length() + index;
        }
        if (index >= 0 && index < parent.length()) {
            return String.valueOf(parent.charAt(index));
        }
        return "";
    }

    //Array methods

    public static boolean array_isArray(Scope scope, List<Object> args) {
        return args.getFirst() instanceof List;
    }


    //Array.prototype.concat()
    public static List<Object> array_concat(Scope scope, List<Object> args, List<Object> parent) {
        List<Object> result = new ArrayList<>(parent);

        for (Object arg : args) {
            if (arg instanceof List) {
                result.addAll((List<Object>) arg);
            } else {
                result.add(arg);
            }
        }

        return result;
    }

    //Array.prototype.copyWithin()
    public static List<Object> array_copyWithin(Scope scope, List<Object> args, List<Object> parent) {
        if (args.isEmpty()) {
            return parent;
        }

        int target = JsTypes.toNumber(args.getFirst()).intValue();
        int start = args.size() > 1 ? JsTypes.toNumber(args.get(1)).intValue() : 0;
        int end = args.size() > 2 ? JsTypes.toNumber(args.get(2)).intValue() : parent.size();

        // Handle negative indices
        if (target < 0) {
            target = parent.size() + target;
        }
        if (start < 0) {
            start = parent.size() + start;
        }
        if (end < 0) {
            end = parent.size() + end;
        }

        // Clamp indices
        target = Math.max(0, Math.min(parent.size(), target));
        start = Math.max(0, Math.min(parent.size(), start));
        end = Math.max(0, Math.min(parent.size(), end));

        // Create a copy of the elements to be copied
        List<Object> elementsToCopy = new ArrayList<>();
        for (int i = start; i < end; i++) {
            elementsToCopy.add(parent.get(i));
        }

        // Perform the copy
        for (int i = 0; i < elementsToCopy.size() && target < parent.size(); i++, target++) {
            parent.set(target, elementsToCopy.get(i));
        }

        return parent;
    }

    //Array.prototype.every()
    public static Boolean array_every(Scope scope, List<Object> args, List<Object> parent) {
        if (args.isEmpty() || parent.isEmpty()) {
            return true;
        }

        if (!(args.getFirst() instanceof JsFunction callbackFn)) {
            return false;
        }

        for (int i = 0; i < parent.size(); i++) {
            Object value = parent.get(i);
            Object result = callbackFn.exec(scope, Arrays.asList(value, i, parent));
            if (!JsTypes.toBoolean(result)) {
                return false;
            }
        }

        return true;
    }

    //Array.prototype.fill()
    public static List<Object> array_fill(Scope scope, List<Object> args, List<Object> parent) {
        if (args.isEmpty()) {
            return parent;
        }

        Object value = args.getFirst();
        int start = args.size() > 1 ? JsTypes.toNumber(args.get(1)).intValue() : 0;
        int end = args.size() > 2 ? JsTypes.toNumber(args.get(2)).intValue() : parent.size();

        // Handle negative indices
        if (start < 0) {
            start = parent.size() + start;
        }
        if (end < 0) {
            end = parent.size() + end;
        }

        // Clamp indices
        start = Math.max(0, Math.min(parent.size(), start));
        end = Math.max(0, Math.min(parent.size(), end));

        // Fill the array
        for (int i = start; i < end; i++) {
            parent.set(i, value);
        }

        return parent;
    }

    //Array.prototype.filter()
    public static List<Object> array_filter(Scope scope, List<Object> args, List<Object> parent) {
        if (args.isEmpty()) {
            return new ArrayList<>();
        }

        if (!(args.getFirst() instanceof JsFunction callbackFn)) {
            return new ArrayList<>();
        }

        List<Object> result = new ArrayList<>();

        for (int i = 0; i < parent.size(); i++) {
            Object value = parent.get(i);
            Object filterResult = callbackFn.exec(scope, Arrays.asList(value, i, parent));

            if (JsTypes.toBoolean(filterResult)) {
                result.add(value);
            }
        }

        return result;
    }

    //Array.prototype.find()
    public static Object array_find(Scope scope, List<Object> args, List<Object> parent) {
        if (args.isEmpty()) {
            return null;
        }

        if (!(args.getFirst() instanceof JsFunction callbackFn)) {
            return null;
        }

        for (int i = 0; i < parent.size(); i++) {
            Object value = parent.get(i);
            Object result = callbackFn.exec(scope, Arrays.asList(value, i, parent));

            if (JsTypes.toBoolean(result)) {
                return value;
            }
        }

        return null;
    }

    //Array.prototype.findIndex()
    public static Integer array_findIndex(Scope scope, List<Object> args, List<Object> parent) {
        if (args.isEmpty()) {
            return -1;
        }

        if (!(args.getFirst() instanceof JsFunction callbackFn)) {
            return -1;
        }

        for (int i = 0; i < parent.size(); i++) {
            Object value = parent.get(i);
            Object result = callbackFn.exec(scope, Arrays.asList(value, i, parent));

            if (JsTypes.toBoolean(result)) {
                return i;
            }
        }

        return -1;
    }

    //Array.prototype.findLast()
    public static Object array_findLast(Scope scope, List<Object> args, List<Object> parent) {
        if (args.isEmpty()) {
            return null;
        }

        if (!(args.getFirst() instanceof JsFunction callbackFn)) {
            return null;
        }

        for (int i = parent.size() - 1; i >= 0; i--) {
            Object value = parent.get(i);
            Object result = callbackFn.exec(scope, Arrays.asList(value, i, parent));

            if (JsTypes.toBoolean(result)) {
                return value;
            }
        }

        return null;
    }

    //Array.prototype.findLastIndex()
    public static Integer array_findLastIndex(Scope scope, List<Object> args, List<Object> parent) {
        if (args.isEmpty()) {
            return -1;
        }

        if (!(args.getFirst() instanceof JsFunction callbackFn)) {
            return -1;
        }

        for (int i = parent.size() - 1; i >= 0; i--) {
            Object value = parent.get(i);
            Object result = callbackFn.exec(scope, Arrays.asList(value, i, parent));

            if (JsTypes.toBoolean(result)) {
                return i;
            }
        }

        return -1;
    }

    //Array.prototype.flat()
    public static List<Object> array_flat(Scope scope, List<Object> args, List<Object> parent) {
        List<Object> newArray = new ArrayList<>();
        int depth = args.isEmpty() ? 1 : JsTypes.toNumber(args.getFirst()).intValue();
        flattenArray(newArray, parent, depth);
        return newArray;
    }

    private static void flattenArray(List<Object> newArray, List<Object> array, int depth) {
        for (Object item : array) {
            if (item instanceof List list2 && depth > 0) {
                flattenArray(newArray, list2, depth - 1);
            } else {
                newArray.add(item);
            }
        }
    }

    //Array.prototype.flatMap()
    public static List<Object> array_flatMap(Scope scope, List<Object> args, List<Object> parent) {
        if (args.isEmpty()) {
            return new ArrayList<>(parent);
        }

        if (!(args.getFirst() instanceof JsFunction callbackFn)) {
            return new ArrayList<>(parent);
        }

        List<Object> result = new ArrayList<>();

        for (int i = 0; i < parent.size(); i++) {
            Object value = parent.get(i);
            Object mappedValue = callbackFn.exec(scope, Arrays.asList(value, i, parent));

            if (mappedValue instanceof List) {
                result.addAll((List<Object>) mappedValue);
            } else {
                result.add(mappedValue);
            }
        }

        return result;
    }

    //Array.prototype.forEach()
    public static Object array_forEach(Scope scope, List<Object> args, List<Object> parent) {
        if (!args.isEmpty() && args.getFirst() instanceof JsFunction callbackFn) {
            for (int i = 0; i < parent.size(); i++) {
                Object value = parent.get(i);
                callbackFn.exec(scope, Arrays.asList(value, i, parent));
            }
        }
        return null;
    }

    //Array.prototype.includes()
    public static Boolean array_includes(Scope scope, List<Object> args, List<Object> parent) {
        if (args.isEmpty()) {
            return false;
        }

        Object searchElement = args.getFirst();
        int fromIndex = args.size() > 1 ? JsTypes.toNumber(args.get(1)).intValue() : 0;

        if (fromIndex < 0) {
            fromIndex = parent.size() + fromIndex;
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }

        for (int i = fromIndex; i < parent.size(); i++) {
            if (Objects.equals(parent.get(i), searchElement)) {
                return true;
            }
        }

        return false;
    }

    //Array.prototype.indexOf()
    public static Integer array_indexOf(Scope scope, List<Object> args, List<Object> parent) {
        if (args.isEmpty()) {
            return -1;
        }

        Object searchElement = args.getFirst();
        int fromIndex = args.size() > 1 ? JsTypes.toNumber(args.get(1)).intValue() : 0;

        if (fromIndex < 0) {
            fromIndex = parent.size() + fromIndex;
        }
        if (fromIndex < 0) {
            fromIndex = 0;
        }

        for (int i = fromIndex; i < parent.size(); i++) {
            if (Objects.equals(parent.get(i), searchElement)) {
                return i;
            }
        }

        return -1;
    }

    //Array.prototype.join()
    public static String array_join(Scope scope, List<Object> args, List<Object> parent) {
        String separator = args.isEmpty() ? "," : JsTypes.toString(args.getFirst());

        return parent.stream().map(JsTypes::toString).collect(Collectors.joining(separator));
    }

    //Array.prototype.lastIndexOf()
    public static Integer array_lastIndexOf(Scope scope, List<Object> args, List<Object> parent) {
        if (args.isEmpty() || parent.isEmpty()) {
            return -1;
        }

        Object searchElement = args.getFirst();
        int fromIndex = args.size() > 1 ? JsTypes.toNumber(args.get(1)).intValue() : parent.size() - 1;

        if (fromIndex < 0) {
            fromIndex = parent.size() + fromIndex;
        }
        if (fromIndex >= parent.size()) {
            fromIndex = parent.size() - 1;
        }
        if (fromIndex < 0) {
            return -1;
        }

        for (int i = fromIndex; i >= 0; i--) {
            if (Objects.equals(parent.get(i), searchElement)) {
                return i;
            }
        }

        return -1;
    }

    //Array.prototype.map()
    public static List<Object> array_map(Scope scope, List<Object> args, List<Object> parent) {
        if (args.isEmpty()) {
            return new ArrayList<>();
        }

        if (!(args.getFirst() instanceof JsFunction callbackFn)) {
            return new ArrayList<>();
        }

        List<Object> result = new ArrayList<>(parent.size());

        for (int i = 0; i < parent.size(); i++) {
            Object value = parent.get(i);
            Object mappedValue = callbackFn.exec(scope, Arrays.asList(value, i, parent));
            result.add(mappedValue);
        }

        return result;
    }

    //Array.prototype.pop()
    public static Object array_pop(Scope scope, List<Object> args, List<Object> parent) {
        return parent.isEmpty() ? null : parent.removeLast();
    }

    //Array.prototype.push()
    public static Integer array_push(Scope scope, List<Object> args, List<Object> parent) {
        parent.addAll(args);
        return parent.size();
    }

    //Array.prototype.reduce()
    public static Object array_reduce(Scope scope, List<Object> args, List<Object> parent) {
        if (args.isEmpty()) {
            return null;
        }
        if (parent.isEmpty() && args.size() < 2) {
            return null;
        }

        if (!(args.getFirst() instanceof JsFunction callbackFn)) {
            return null;
        }

        Object accumulator = args.size() > 1 ? args.get(1) : parent.getFirst();

        int startIndex = args.size() > 1 ? 0 : 1;

        for (int i = startIndex; i < parent.size(); i++) {
            Object currentValue = parent.get(i);
            accumulator = callbackFn.exec(scope, Arrays.asList(accumulator, currentValue, i, parent));
        }

        return accumulator;
    }

    //Array.prototype.reduceRight()
    public static Object array_reduceRight(Scope scope, List<Object> args, List<Object> parent) {
        if (args.isEmpty()) {
            return null;
        }
        if (parent.isEmpty() && args.size() < 2) {
            return null;
        }

        if (!(args.getFirst() instanceof JsFunction callbackFn)) {
            return null;
        }

        Object accumulator = args.size() > 1 ? args.get(1) : parent.getLast();

        int startIndex = args.size() > 1 ? parent.size() - 1 : parent.size() - 2;

        for (int i = startIndex; i >= 0; i--) {
            Object currentValue = parent.get(i);
            accumulator = callbackFn.exec(scope, Arrays.asList(accumulator, currentValue, i, parent));
        }

        return accumulator;
    }

    //Array.prototype.reverse()
    public static List<Object> array_reverse(Scope scope, List<Object> args, List<Object> parent) {
        Collections.reverse(parent);
        return parent;
    }

    //Array.prototype.shift()
    public static Object array_shift(Scope scope, List<Object> args, List<Object> parent) {
        return parent.isEmpty() ? null : parent.removeFirst();
    }

    //Array.prototype.slice()
    public static List<Object> array_slice(Scope scope, List<Object> args, List<Object> parent) {
        int start = args.isEmpty() ? 0 : JsTypes.toNumber(args.getFirst()).intValue();
        int end = args.size() > 1 ? JsTypes.toNumber(args.get(1)).intValue() : parent.size();

        if (start < 0) {
            start = parent.size() + start;
        }
        if (end < 0) {
            end = parent.size() + end;
        }

        start = Math.max(0, start);
        end = Math.min(parent.size(), end);

        if (start >= end) {
            return new ArrayList<>();
        }

        return new ArrayList<>(parent.subList(start, end));
    }

    //Array.prototype.some()
    public static Boolean array_some(Scope scope, List<Object> args, List<Object> parent) {
        if (args.isEmpty()) {
            return false;
        }
        if (parent.isEmpty()) {
            return false;
        }

        if (!(args.getFirst() instanceof JsFunction callbackFn)) {
            return false;
        }

        for (int i = 0; i < parent.size(); i++) {
            Object value = parent.get(i);
            Object result = callbackFn.exec(scope, Arrays.asList(value, i, parent));

            if (JsTypes.toBoolean(result)) {
                return true;
            }
        }

        return false;
    }

    //Array.prototype.sort()
    public static List<Object> array_sort(Scope scope, List<Object> args, List<Object> parent) {
        if (parent.size() <= 1) {
            return parent;
        }

        if (args.isEmpty()) {
            // Default sorting (convert to strings)
            parent.sort(Comparator.comparing(JsTypes::toString));
        } else if (args.getFirst() instanceof JsFunction callbackFn) {
            // Custom comparator function
            parent.sort((a, b) -> {
                Object result = callbackFn.exec(scope, Arrays.asList(a, b));
                return JsTypes.toNumber(result).intValue();
            });
        }

        return parent;
    }

    //Array.prototype.splice()
    public static List<Object> array_splice(Scope scope, List<Object> args, List<Object> parent) {
        if (args.isEmpty()) {
            return new ArrayList<>();
        }

        int start = JsTypes.toNumber(args.getFirst()).intValue();
        int deleteCount = args.size() > 1 ? JsTypes.toNumber(args.get(1)).intValue() : parent.size() - start;

        if (start < 0) {
            start = parent.size() + start;
        }
        start = Math.max(0, Math.min(parent.size(), start));
        deleteCount = Math.max(0, Math.min(parent.size() - start, deleteCount));

        // Get the elements to be removed
        List<Object> removed = new ArrayList<>();
        for (int i = 0; i < deleteCount; i++) {
            removed.add(parent.get(start));
            parent.remove(start);
        }

        // Insert new elements if provided
        if (args.size() > 2) {
            for (int i = 2; i < args.size(); i++) {
                parent.add(start + i - 2, args.get(i));
            }
        }

        return removed;
    }

    //Array.prototype.unshift()
    public static Integer array_unshift(Scope scope, List<Object> args, List<Object> parent) {
        for (int i = args.size() - 1; i >= 0; i--) {
            parent.addFirst(args.get(i));
        }
        return parent.size();
    }

    //Array.prototype.with()
    public static List<Object> array_with(Scope scope, List<Object> args, List<Object> parent) {
        if (args.size() < 2) {
            return new ArrayList<>(parent);
        }

        int index = JsTypes.toNumber(args.getFirst()).intValue();
        Object value = args.get(1);

        if (index < 0) {
            index = parent.size() + index;
        }

        // If index is out of bounds, return a copy of the original array
        if (index < 0 || index >= parent.size()) {
            return new ArrayList<>(parent);
        }

        // Create a new array with the updated value
        List<Object> result = new ArrayList<>(parent);
        result.set(index, value);
        return result;
    }

    //Object.isExtensible()
    public static boolean object_isExtensible(Scope scope, List<Object> args) {
        return false;
    }

    //Object.assign()
    public static Object object_assign(Scope scope, List<Object> args) {
        if (args.isEmpty()) {
            return null;
        }

        Object target = args.getFirst();
        if (!(target instanceof Map)) {
            return target;
        }

        Map<String, Object> targetMap = (Map<String, Object>) target;

        // Process sources starting from index 1
        for (int i = 1; i < args.size(); i++) {
            Object source = args.get(i);
            if (source instanceof Map sourceMap) {
                targetMap.putAll(sourceMap);
            }
        }

        return targetMap;
    }

    //Object.entries()
    public static List<List<Object>> object_entries(Scope scope, List<Object> args) {
        if (args.isEmpty() || !(args.getFirst() instanceof Map)) {
            return List.of();
        }

        Map<String, Object> obj = (Map<String, Object>) args.getFirst();
        List<List<Object>> entries = new ArrayList<>();

        for (Map.Entry<String, Object> entry : obj.entrySet()) {
            entries.add(Arrays.asList(entry.getKey(), entry.getValue()));
        }

        return entries;
    }

    //Object.getOwnPropertyNames()
    public static List<String> object_getOwnPropertyNames(Scope scope, List<Object> args) {
        if (args.isEmpty() || !(args.getFirst() instanceof Map)) {
            return List.of();
        }

        Map<String, Object> obj = (Map<String, Object>) args.getFirst();
        return new ArrayList<>(obj.keySet());
    }

    //Object.groupBy()
    public static Map<String, List<Object>> object_groupBy(Scope scope, List<Object> args) {
        if (args.size() < 2 || !(args.getFirst() instanceof List) || !(args.get(1) instanceof JsFunction callbackFn)) {
            return Map.of();
        }

        List<Object> items = (List<Object>) args.getFirst();
        Map<String, List<Object>> result = new HashMap<>();

        for (int i = 0; i < items.size(); i++) {
            Object item = items.get(i);
            Object key = callbackFn.exec(scope, Arrays.asList(item, i, items));
            String keyString = JsTypes.toString(key);

            if (!result.containsKey(keyString)) {
                result.put(keyString, new ArrayList<>());
            }

            result.get(keyString).add(item);
        }

        return result;
    }

    //Object.hasOwn()
    public static boolean object_hasOwn(Scope scope, List<Object> args) {
        if (args.size() < 2 || !(args.getFirst() instanceof Map map)) {
            return false;
        }
        return map.containsKey(JsTypes.toString(args.get(1)));
    }

    //Object.keys()
    public static List<String> object_keys(Scope scope, List<Object> args) {
        if (args.isEmpty() || !(args.getFirst() instanceof Map)) {
            return List.of();
        }

        Map<String, Object> obj = (Map<String, Object>) args.getFirst();
        return new ArrayList<>(obj.keySet());
    }

    //Object.values()
    public static List<Object> object_values(Scope scope, List<Object> args) {
        if (args.isEmpty() || !(args.getFirst() instanceof Map)) {
            return List.of();
        }

        Map<String, Object> obj = (Map<String, Object>) args.getFirst();
        return new ArrayList<>(obj.values());
    }

    // Function.prototype.apply()
    public static Object function_apply(Scope scope, List<Object> args, JsFunction fn) {
        Object thisArg = args.getFirst();
        List<Object> fnArgs =
                args.size() > 1 && args.get(1) instanceof List ? (List<Object>) args.get(1) : new ArrayList<>();
        while (fnArgs.size() < fn.params.size()) {
            fnArgs.add(Undefined.INSTANCE);
        }
        return fn.setParent(thisArg).exec(scope, fnArgs);
    }

    // Function.prototype.call()
    public static Object function_call(Scope scope, List<Object> args, JsFunction fn) {
        Object thisArg = args.isEmpty() ? null : args.getFirst();
        List<Object> fnArgs = args.size() > 1 ? args.subList(1, args.size()) : new ArrayList<>();
        while (fnArgs.size() < fn.params.size()) {
            fnArgs.add(Undefined.INSTANCE);
        }
        return fn.setParent(thisArg).exec(scope, fnArgs);
    }

    // Json.parse()
    public static Object json_parse(Scope scope, List<Object> args) {
        if (args.getFirst() instanceof String str) {
            return JsonTokenizer.parse(str);
        }
        return null;
    }

    // Json.stringify()
    public static String json_stringify(Scope scope, List<Object> args) {
        return JsonTokenizer.stringify(scope, args.getFirst());
    }

}
