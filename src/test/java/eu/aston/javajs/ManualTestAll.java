package eu.aston.javajs;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

import eu.aston.javajs.AstNodes.*;

public class ManualTestAll {

    public static void main(String[] args) {
        try {
            File dir = new File("tests");
            //scanDir(dir, "10-wtf.js");
            scanDir(dir, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void scanDir(File dir, String filter) {
        for(File f : dir.listFiles()){
            if(f.isFile() && f.getName().endsWith(".js")){
                if(filter != null && !f.getName().contains(filter)){
                    continue;
                }
                try{
                    System.out.println("Executing: " + f.getAbsolutePath());
                    String script = java.nio.file.Files.readString(f.toPath());
                    String script2  = """
                    function assert(ok, message){
                        if(ok){
                            print("ok " + message);
                        } else{
                            print("!!!!!!!!!!!!!! Assertion failed: " + message);
                        }
                    }
                    function assertError(fn, message){
                        try{
                            fn();
                            print("!!!!!!!!!!!!!! Expected an error but none was thrown: "+message);
                        }catch(e){
                            print("ok - thrown as expected: "+message+" - "+e);
                        }
                    }


                    """;
                    Scope rootScope = new Scope();
                    JsSdk.defineFunctions(rootScope);
                    rootScope.nativeFunction("print()", (scope, args)-> {System.out.println(args); return null; });
                    rootScope.nativeFunction("Error(val)", (scope, args) -> JsTypes.toString(args.getFirst()));
                    JsLexer lexer = new JsLexer(script+script2);
                    JsParser parser = new JsParser(lexer.tokenize());
                    ASTNode root = parser.parse();

                    //JsParser.printTree(root, " ");
                    root.exec(rootScope);

                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("!!!!!!!!!!!!!! error in script "+f.getAbsolutePath()+" "+e.getMessage());
                }
            }
            if(f.isDirectory()){
                scanDir(f, filter);
            }
        }
    }

    public static void printTree(ASTNode node, String indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(indent);
        sb.append(node.getClass().getSimpleName()).append(" > ");
        for(Field f : node.getClass().getDeclaredFields()){
            if(f.getType().equals(String.class)){
                try {
                    sb.append(f.getName()).append("=").append(f.get(node)).append(" ");
                } catch (IllegalAccessException ignore) {
                }
            }
        }
        System.out.println(sb);
        for(Field f : node.getClass().getDeclaredFields()){
            if(f.getType().isAssignableFrom(ASTNode.class)){
                try {
                    ASTNode child = (ASTNode) f.get(node);
                    if(child != null){
                        System.out.println(indent+"  "+f.getName()+":");
                        printTree(child, indent+"    ");
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            if(f.getType().isAssignableFrom(List.class)){
                try {
                    if(f.get(node) instanceof List<?> list){
                        System.out.println(indent+"  "+f.getName()+":");
                        for(Object child : list){
                            if(child instanceof ASTNode childNode){
                                printTree(childNode, indent+"    ");
                            }
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void runScript(String script){

        JsLexer lexer = new JsLexer(script);
        JsParser parser = new JsParser(lexer.tokenize());
        ASTNode programNode = parser.parse();

        for(int i=0; i<10; i++){
            Scope rootScope = new Scope();
            JsSdk.defineFunctions(rootScope);
            programNode.exec(rootScope);
        }
    }

}
