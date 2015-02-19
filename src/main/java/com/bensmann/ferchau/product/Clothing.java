package com.bensmann.ferchau.product;

/**
 * The subclass must have additional attributes for the size (as int) and the material (as String).
 * The new attributes shall be initialized in the constructor like the other attributes name, description and price,
 * as well as implemented in the method toString() to override the output stream.
 */
class Clothing extends Product {

    private int size;

    private String material;

    Clothing(String name, String desc, double price, int size, String material) {
        super(name, desc, price);
        this.size = size;
        this.material = material;
    }

    @Override
    public String toString() {
        return String.format("%s _ %d _ %s", super.toString(), size, material);
    }

}
