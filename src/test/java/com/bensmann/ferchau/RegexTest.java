package com.bensmann.ferchau;

import org.junit.Assert;
import org.junit.Test;

public class RegexTest {

    @Test
    public void testRegex() throws Exception {
        String[] t = {"damage", "minor damages", "heavy damage", "no damages"};
        for (String s : t) {
            System.out.println(s + " -> " + Regex.regex(s));
        }
        Assert.assertTrue("Must match", Regex.regex(t[0]));
        Assert.assertTrue("Must match", Regex.regex(t[1]));
        Assert.assertTrue("Must match", Regex.regex(t[2]));
        Assert.assertTrue("Must not match", !Regex.regex(t[3]));
    }

}
