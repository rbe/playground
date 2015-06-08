package eu.artofcoding.example.javareferences;

import java.util.logging.Logger;

public class Referee {

    private static final Logger logger = Logger.getLogger(Referee.class.getName());

    @Override
    protected void finalize() throws Throwable {
        logger.info("Goodbye!");
    }

}
