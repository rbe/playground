package bindingdemo;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class LowLevelAPI {

    public static void main(String[] args) {

        final DoubleProperty a = new SimpleDoubleProperty(1);
        final DoubleProperty b = new SimpleDoubleProperty(2);
        final DoubleProperty c = new SimpleDoubleProperty(3);
        final DoubleProperty d = new SimpleDoubleProperty(4);

        DoubleBinding db = new MyDoubleBinding(a, b, c, d);
        System.out.println(db.get());
        b.set(3);
        System.out.println(db.get());
    }

    private static class MyDoubleBinding extends DoubleBinding {

        private final DoubleProperty a;
        private final DoubleProperty b;
        private final DoubleProperty c;
        private final DoubleProperty d;

        public MyDoubleBinding(DoubleProperty a, DoubleProperty b, DoubleProperty c, DoubleProperty d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            super.bind(a, b, c, d);
        }

        @Override
        protected double computeValue() {
            return (a.get() * b.get()) + (c.get() * d.get());
        }

    }

}
