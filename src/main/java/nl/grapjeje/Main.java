package nl.grapjeje;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.grapjeje.Models.Quote;
import nl.grapjeje.Models.QuoteItem;
import nl.grapjeje.Utils.PDFGenerator;

import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Velden
//        TextField bedrijfsNaam = new TextField();
//        bedrijfsNaam.setPromptText("Bedrijfsnaam");
//
//        TextField kvk = new TextField();
//        kvk.setPromptText("KvK-nummer");
//
//        // Datum kiezen
//        DatePicker factuurDatum = new DatePicker();
//
//        // Btw kiezen
//        ComboBox<String> btwTarief = new ComboBox<>();
//        btwTarief.getItems().addAll("0%", "9%", "21%");
//        btwTarief.setPromptText("BTW-tarief");
//
//        // Knop
//        Button exporteerPdf = new Button("Genereer PDF");
//
//        VBox root = new VBox(10, bedrijfsNaam, kvk, factuurDatum, btwTarief, exporteerPdf);
//        Scene scene = new Scene(root, 400, 300);
//
//        primaryStage.setTitle("Offerte Generator");
//        primaryStage.setScene(scene);
//        primaryStage.show();

        try {
            List<QuoteItem> items = new ArrayList<>();
            items.add(QuoteItem.createItem("Webdesign", 1, 21, 100.00));
            items.add(QuoteItem.createItem("Hosting", 1, 21, 50.00));
            items.add(QuoteItem.createItem("Domeinnaam", 1, 21, 10.00));

            Quote quote = Quote.createQuote(
                    "Grapjeje B.V.",
                    "Voorbeeldstraat",
                    123,
                    "1234 AB",
                    "Amsterdam",
                    "F20230001",
                    "01-06-2023",
                    30,
                    "12345678",
                    items
            );

            PDFGenerator.createInvoice(quote);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
