package eu.aston.javajs;

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
