package com.lelebees.seapulsecalculator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AppLauncher {
    //I only have this so the JAR will work.
    public static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        logger.info("Starting Logging!");
        try {
            Main.main(args);
        } catch (Exception e) {
            logger.error(e.toString());
            logger.debug(e.getStackTrace());
        }

        logger.info("Ending logging...");
    }
}
