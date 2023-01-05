module com.lelebees.seapulsecalculator {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.lelebees.seapulsecalculator to javafx.fxml;
    exports com.lelebees.seapulsecalculator;
}