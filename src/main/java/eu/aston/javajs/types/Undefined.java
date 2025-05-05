package eu.aston.javajs.types;

public class Undefined implements IJsType {
    public static final Undefined INSTANCE = new Undefined();

    @Override
    public boolean toBoolean() {
        return false;
    }

    @Override
    public String toString() {
        return "undefined";
    }

    @Override
    public String typeOf() {
        return "undefined";
    }
}
