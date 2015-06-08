package eu.artofcoding.example.javareferences;

import java.lang.ref.WeakReference;
import java.util.logging.Logger;

public class WeakReferenceExample {

    private static final Logger logger = Logger.getLogger(WeakReferenceExample.class.getName());

    public static void collect() throws InterruptedException {
        logger.info("calling System.gc()");
        System.gc();
        logger.info("waiting");
        Thread.sleep(5000);
    }

    public static void main(String[] args) throws InterruptedException {
        logger.info("strongReference = new Referee()");
        Referee strongReference = new Referee();
        WeakReference<Referee> weakReference = new WeakReference<>(strongReference);
        collect();
        logger.info("aReference = null");
        strongReference = null;
        collect();
    }

}
