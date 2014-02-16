package bindingdemo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * User: rbe
 * Date: 21.09.13
 * Time: 14:24
 */
class Bill {

    // Define the property
    private DoubleProperty amountDue = new SimpleDoubleProperty();

    // Define a getter for the property's value
    public final double getAmountDue() {
        return amountDue.get();
    }

    // Define a setter for the property's value
    public final void setAmountDue(double value) {
        amountDue.set(value);
    }

    // Define a getter for the property itself
    public DoubleProperty amountDueProperty() {
        return amountDue;
    }

}
