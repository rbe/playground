package eu.artofcoding.example.javareferences;

import java.util.logging.Logger;

public class StrongReference {

    private static final Logger logger = Logger.getLogger(StrongReference.class.getName());

    public static void collect() throws InterruptedException {
        logger.info("calling System.gc()");
        System.gc();
        logger.info("waiting");
        Thread.sleep(5000);
    }

    public static void main(String[] args) throws InterruptedException {
        logger.info("aReference = new AReference()");
        Referee referee = new Referee();
        collect();
        logger.info("aReference = null");
        referee = null;
        collect();
    }

}
