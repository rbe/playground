package com.bensmann.ferchau;

import org.junit.Assert;
import org.junit.Test;

public class OneBooleanTest {

    @Test
    public void testBool1() throws Exception {
        Assert.assertEquals("a, b", OneBoolean.bool1(true));
        Assert.assertEquals("c", OneBoolean.bool1(false));
    }

    @Test
    public void testBool2() throws Exception {
        Assert.assertArrayEquals(new String[]{"a", "b"}, OneBoolean.bool2(true));
        Assert.assertArrayEquals(new String[]{"c"}, OneBoolean.bool2(false));
    }

}
