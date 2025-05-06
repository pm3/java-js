package eu.aston.javajs.types;

import java.util.List;

import eu.aston.javajs.Scope;

@FunctionalInterface
public interface IJsFunctionExec {
    Object exec(Scope scope, List<Object> args);
}
