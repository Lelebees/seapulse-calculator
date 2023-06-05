module com.lelebees.seapulsecalculator {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.common;
    requires com.google.gson;
    requires org.apache.logging.log4j;


    opens com.lelebees.seapulsecalculator to javafx.fxml;
    opens com.lelebees.seapulsecalculator.domain to com.google.gson;
    exports com.lelebees.seapulsecalculator;
    exports com.lelebees.seapulsecalculator.presentation;
    opens com.lelebees.seapulsecalculator.presentation to javafx.fxml;
}