package com.bensmann.ferchau;

/**
 * Write a method with only one boolean parameter.
 * Depending on the parameter the method has to return "a", "b", or "c".
 */
public class OneBoolean {

    public static String bool1(boolean x) {
        return x ? "a, b" : "c";
    }

    public static String[] bool2(boolean x) {
        if (x) {
            return new String[]{"a", "b"};
        } else {
            return new String[]{"c"};
        }
    }

}
