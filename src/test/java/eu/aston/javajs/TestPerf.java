package eu.aston.javajs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import eu.aston.javajs.types.JsSdk;
import org.junit.jupiter.api.Test;

public class TestPerf {

    @Test
    public void performance() throws IOException {
        AstNodes.INFINITE_LOOP_LIMIT = 128 * 1024;
        String script = Files.readString(Path.of("performance/perf.js"));
        long l1 = System.currentTimeMillis();
        JsLexer lexer = new JsLexer(script);
        JsParser parser = new JsParser(lexer.tokenize());
        AstNodes.ASTNode programNode = parser.parse();
        long l2 = System.currentTimeMillis();
        System.out.println("parse time " + (l2 - l1));
        long avg = 0;
        int loops = 190;
        for (int i = 0; i < loops; i++) {
            long t1 = System.nanoTime();
            Scope rootScope = JsSdk.createRootScope();
            rootScope.nativeFunction("print()", (scope, args) -> {
                System.out.println(args);
                return null;
            });
            programNode.exec(rootScope);
            long t2 = System.nanoTime();
            //System.out.println("run time "+(t2-t1));
            avg += t2 - t1;
        }
        System.out.println("avg run " + (1.0 * avg / loops / 1_000_000));
    }
}
