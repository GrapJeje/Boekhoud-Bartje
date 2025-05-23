package nl.grapjeje;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import nl.grapjeje.Models.Quote;
import nl.grapjeje.Models.QuoteItem;
import nl.grapjeje.Utils.PDFGenerator;

import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        TextField bedrijfsNaam = new TextField();
        bedrijfsNaam.setPromptText("Bedrijfsnaam");

        TextField straat = new TextField();
        straat.setPromptText("Straat");

        TextField huisnummer = new TextField();
        huisnummer.setPromptText("Huisnummer");

        TextField postcode = new TextField();
        postcode.setPromptText("Postcode");

        TextField plaats = new TextField();
        plaats.setPromptText("Plaats");

        TextField kvk = new TextField();
        kvk.setPromptText("KvK-nummer");

        TextField factuurnummer = new TextField();
        factuurnummer.setPromptText("Factuurnummer");

        DatePicker factuurDatum = new DatePicker();

        ComboBox<String> btwTarief = new ComboBox<>();
        btwTarief.getItems().addAll("0%", "9%", "21%");
        btwTarief.setPromptText("BTW-tarief");

        VBox itemsBox = new VBox(10);
        itemsBox.setPadding(new Insets(10));
        Label itemsLabel = new Label("Offerte items:");

        Button voegItemToe = new Button("Voeg item toe");
        voegItemToe.setOnAction(ev -> {
            TextField naamField = new TextField();
            naamField.setPromptText("Naam");

            TextField aantalField = new TextField();
            aantalField.setPromptText("Aantal");

            TextField prijsField = new TextField();
            prijsField.setPromptText("Prijs excl. BTW");

            HBox itemRow = new HBox(10, naamField, aantalField, prijsField);
            itemsBox.getChildren().add(itemRow);
        });

        Button exporteerPdf = new Button("Genereer PDF");

        exporteerPdf.setOnAction(e -> {
            try {
                String naam = bedrijfsNaam.getText().trim();
                String straatNaam = straat.getText().trim();
                String huisnummerTekst = huisnummer.getText().trim();
                String pc = postcode.getText().trim();
                String city = plaats.getText().trim();
                String factuurNr = factuurnummer.getText().trim();
                String datum = "";
                if (factuurDatum.getValue() != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                    datum = factuurDatum.getValue().format(formatter);
                }
                String kvkNummer = kvk.getText().trim();
                String btwTekst = (btwTarief.getValue() != null) ? btwTarief.getValue().replace("%", "").trim() : "21";

                if (naam.isEmpty() || straatNaam.isEmpty() || huisnummerTekst.isEmpty() || pc.isEmpty()
                        || city.isEmpty() || factuurNr.isEmpty() || datum.isEmpty() || kvkNummer.isEmpty()) {
                    throw new IllegalArgumentException("Vul alle verplichte velden in.");
                }

                int huisNr;
                int btw;
                try {
                    huisNr = Integer.parseInt(huisnummerTekst);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("Huisnummer moet een getal zijn.");
                }

                try {
                    btw = Integer.parseInt(btwTekst);
                } catch (NumberFormatException nfe) {
                    throw new IllegalArgumentException("BTW-tarief is ongeldig.");
                }

                List<QuoteItem> items = new ArrayList<>();

                for (javafx.scene.Node node : itemsBox.getChildren()) {
                    if (node instanceof HBox hBox) {
                        TextField naamField = (TextField) hBox.getChildren().get(0);
                        TextField aantalField = (TextField) hBox.getChildren().get(1);
                        TextField prijsField = (TextField) hBox.getChildren().get(2);

                        String naamItem = naamField.getText().trim();
                        String aantalTekst = aantalField.getText().trim();
                        String prijsTekst = prijsField.getText().trim();

                        if (naamItem.isEmpty() || aantalTekst.isEmpty() || prijsTekst.isEmpty()) {
                            throw new IllegalArgumentException("Alle itemvelden moeten ingevuld zijn.");
                        }

                        int aantal = Integer.parseInt(aantalTekst);
                        double prijs = Double.parseDouble(prijsTekst);

                        items.add(QuoteItem.createItem(naamItem, aantal, btw, prijs));
                    }
                }


                // Maak offerte
                Quote quote = Quote.createQuote(
                        naam,
                        straatNaam,
                        huisNr,
                        pc,
                        city,
                        factuurNr,
                        datum,
                        30,
                        kvkNummer,
                        items
                );

                PDFGenerator.createInvoice(quote);

                bedrijfsNaam.clear();
                straat.clear();
                huisnummer.clear();
                postcode.clear();
                plaats.clear();
                kvk.clear();
                factuurnummer.clear();
                factuurDatum.setValue(null);
                btwTarief.setValue(null);
                itemsBox.getChildren().clear();

                new Alert(Alert.AlertType.INFORMATION, "PDF succesvol gegenereerd!").showAndWait();

            } catch (IllegalArgumentException iae) {
                new Alert(Alert.AlertType.WARNING, "Invoerfout: " + iae.getMessage()).showAndWait();
            } catch (Exception ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Er is een onverwachte fout opgetreden: " + ex.getMessage()).showAndWait();
            }
        });



        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(10);
        grid.setPadding(new Insets(15, 0, 0, 0));

        grid.add(new Label("Bedrijfsnaam:"), 0, 0);
        grid.add(bedrijfsNaam, 1, 0);

        grid.add(new Label("Straat:"), 0, 1);
        grid.add(straat, 1, 1);

        grid.add(new Label("Huisnummer:"), 0, 2);
        grid.add(huisnummer, 1, 2);

        grid.add(new Label("Postcode:"), 0, 3);
        grid.add(postcode, 1, 3);

        grid.add(new Label("Plaats:"), 0, 4);
        grid.add(plaats, 1, 4);

        grid.add(new Label("KvK-nummer:"), 0, 5);
        grid.add(kvk, 1, 5);

        grid.add(new Label("Factuurnummer:"), 0, 6);
        grid.add(factuurnummer, 1, 6);

        grid.add(new Label("Factuurdatum:"), 0, 7);
        grid.add(factuurDatum, 1, 7);

        grid.add(new Label("BTW-tarief:"), 0, 8);
        grid.add(btwTarief, 1, 8);

        voegItemToe.setId("voegItemToe");
        exporteerPdf.setId("exporteerPdf");
        itemsBox.getStyleClass().add("items-box");
        itemsLabel.getStyleClass().add("items-label");

        VBox root = new VBox(15, grid, itemsLabel, itemsBox, voegItemToe, exporteerPdf);
        root.setPadding(new Insets(20));

        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.TOP_CENTER);
        mainContainer.getStyleClass().add("main-container");

        Label header = new Label("Offerte Generator");
        header.getStyleClass().add("header");

        VBox card = new VBox(20);
        card.getStyleClass().add("card");
        card.getChildren().addAll(grid, itemsLabel, itemsBox);

        HBox buttonContainer = new HBox(15, voegItemToe, exporteerPdf);
        buttonContainer.getStyleClass().add("button-container");
        buttonContainer.setAlignment(Pos.CENTER);

        itemsBox.getStyleClass().add("items-section");
        itemsLabel.getStyleClass().add("items-header");
        voegItemToe.setId("voegItemToe");
        exporteerPdf.setId("exporteerPdf");

        mainContainer.getChildren().addAll(header, card, buttonContainer);

        Scene scene = new Scene(mainContainer, 700, 700);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/style.css")).toExternalForm());
        primaryStage.setTitle("Offerte Generator");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
