package eu.aston.javajs.types;

import java.util.List;

import eu.aston.javajs.AstVisitor;

@FunctionalInterface
public interface IJsFunctionExec {
    Object exec(AstVisitor visitor, List<Object> args);
}
