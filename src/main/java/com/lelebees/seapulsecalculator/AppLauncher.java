package com.lelebees.seapulsecalculator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class AppLauncher {
    //I only have this so the JAR will work.
    public static FileWriter logger;
    public static void main(String[] args) throws IOException {
        //Generate a log file to write logs to.
        LocalDateTime currentTime = LocalDateTime.now();
        String timeString = currentTime.format(DateTimeFormatter.ofPattern("yyy-MM-dd_HH-mm-ss"));
        File logFile = new File("logs/"+timeString+".txt");
        logger = new FileWriter(logFile);
        log("Starting Logging!");
        try {
            Main.main(args);
        } catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.out.println(Arrays.toString(e.getStackTrace()));
            log(e.toString());
            log(Arrays.toString(e.getStackTrace()));
        }

        log("Ending logging...");
        logger.close();
    }

    public static void log(String text) throws IOException {
        logger.append("["+LocalDateTime.now()+"]: ").append(text).append("\n");
    }
}
