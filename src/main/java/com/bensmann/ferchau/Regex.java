package com.bensmann.ferchau;

public class Regex {

    public static boolean regex(String str) {
        String r = "(|minor|heavy)\\s*damage[s]{0,1}";
        return str.matches(r);
    }

    public static void main(String[] args) {
    }

}
