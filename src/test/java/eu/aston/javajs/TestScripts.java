package eu.aston.javajs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;


public class TestScripts {

    @TestFactory
    public Stream<DynamicTest> testAllScripts() throws IOException {
        List<DynamicTest> tests = new ArrayList<>();
        Path dir = Path.of("tests");
        for(Path path : Files.list(dir).toList()){
            if(Files.isDirectory(path)) continue;
            String script = Files.readString(path);
            Scope rootScope = new Scope();
            JsSdk.defineFunctions(rootScope);
            rootScope.nativeFunction("assert(eq,msg)", (scope, args)->assertNative(tests,args));
            rootScope.nativeFunction("print()", (scope, args)-> {System.out.println(args); return null; });
            runScript(rootScope, script+script2);
        }
        return tests.stream();

    }

    private void runScript(Scope rootScope, String script) {
        JsLexer lexer = new JsLexer(script+script2);
        JsParser parser = new JsParser(lexer.tokenize());
        AstNodes.ASTNode root = parser.parse();
        root.exec(rootScope);
    }

    public static Object assertNative(List<DynamicTest> tests, List<Object> args){
        boolean eq = JsTypes.toBoolean(args.getFirst());
        String message = JsTypes.toString(args.get(1));
        tests.add(DynamicTest.dynamicTest(message, ()->{ Assertions.assertTrue(eq, message); }));
        if(eq) {
                System.out.println("ok - "+message);
        } else {
            System.out.println("!!!!error - "+message);
        }
        return null;
    }
    public static final String script2  = """
                    function assertError(fn, message){
                        try{
                            fn();
                            assert(false, message);
                        }catch(e){
                            assert(true, message)
                        }
                    }
                    """;
}
