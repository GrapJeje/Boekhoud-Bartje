package nl.grapjeje;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Velden
        TextField bedrijfsNaam = new TextField();
        bedrijfsNaam.setPromptText("Bedrijfsnaam");

        TextField kvk = new TextField();
        kvk.setPromptText("KvK-nummer");

        // Datum kiezen
        DatePicker factuurDatum = new DatePicker();

        // Btw kiezen
        ComboBox<String> btwTarief = new ComboBox<>();
        btwTarief.getItems().addAll("0%", "9%", "21%");
        btwTarief.setPromptText("BTW-tarief");

        // Knop
        Button exporteerPdf = new Button("Genereer PDF");

        VBox root = new VBox(10, bedrijfsNaam, kvk, factuurDatum, btwTarief, exporteerPdf);
        Scene scene = new Scene(root, 400, 300);

        primaryStage.setTitle("Offerte Generator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
