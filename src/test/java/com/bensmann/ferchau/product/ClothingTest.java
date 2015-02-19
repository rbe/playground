package com.bensmann.ferchau.product;

import org.junit.Assert;
import org.junit.Test;

public class ClothingTest {

    @Test
    public void testToString() {
        Clothing c = new Clothing("Jacke", "Hält schön warm", 170.35d, 5, "Polyethylennylongibbetnich");
        Assert.assertEquals("Clothing#toString does not have excpected value", "Jacke _ Hält schön warm _ 170.35 EUR _ 5 _ Polyethylennylongibbetnich", c.toString());
    }

}
