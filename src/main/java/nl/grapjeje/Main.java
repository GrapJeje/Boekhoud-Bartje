package nl.grapjeje;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import nl.grapjeje.Models.Quote;
import nl.grapjeje.Models.QuoteItem;
import nl.grapjeje.Utils.PDFGenerator;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends Application {

    private TextField bedrijfsNaam, straat, huisnummer, postcode, plaats, kvk, factuurnummer;
    private DatePicker factuurDatum;
    private ComboBox<String> btwTarief;
    private VBox itemsBox;

    @Override
    public void start(Stage primaryStage) {
        initializeUIComponents();
        setupMainLayout(primaryStage);
    }

    private void initializeUIComponents() {
        bedrijfsNaam = createTextField("Bedrijfsnaam");
        straat = createTextField("Straat");
        huisnummer = createTextField("Huisnummer");
        postcode = createTextField("Postcode");
        plaats = createTextField("Plaats");
        kvk = createTextField("KvK-nummer");
        factuurnummer = createTextField("Factuurnummer");

        factuurDatum = new DatePicker();

        btwTarief = new ComboBox<>();
        btwTarief.getItems().addAll("0%", "9%", "21%");
        btwTarief.setPromptText("BTW-tarief");

        itemsBox = new VBox(10);
        itemsBox.setPadding(new Insets(10));
    }

    private TextField createTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        return textField;
    }

    private void setupMainLayout(Stage primaryStage) {
        GridPane formGrid = createFormGrid();
        Label itemsLabel = createItemsLabel();
        Button voegItemToe = createAddItemButton();
        Button exporteerPdf = createExportButton();

        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.getStyleClass().add("main-container");

        Label header = new Label("Offerte Generator");
        header.getStyleClass().add("header");

        VBox card = new VBox(20);
        card.getStyleClass().add("card");
        card.getChildren().addAll(formGrid, itemsLabel, itemsBox);

        HBox buttonContainer = new HBox(15, voegItemToe, exporteerPdf);
        buttonContainer.getStyleClass().add("button-container");
        buttonContainer.setAlignment(Pos.CENTER);

        mainContainer.getChildren().addAll(header, card, buttonContainer);

        ScrollPane scrollPane = new ScrollPane(mainContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);

        Scene scene = new Scene(scrollPane);
        primaryStage.setScene(scene);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/styles/style.css")).toExternalForm());
        primaryStage.setTitle("Offerte Generator");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setVgap(8);
        grid.setHgap(10);
        grid.setPadding(new Insets(15, 0, 0, 0));

        addFormRow(grid, "Bedrijfsnaam:", bedrijfsNaam, 0);
        addFormRow(grid, "Straat:", straat, 1);
        addFormRow(grid, "Huisnummer:", huisnummer, 2);
        addFormRow(grid, "Postcode:", postcode, 3);
        addFormRow(grid, "Plaats:", plaats, 4);
        addFormRow(grid, "KvK-nummer:", kvk, 5);
        addFormRow(grid, "Factuurnummer:", factuurnummer, 6);
        addFormRow(grid, "Factuurdatum:", factuurDatum, 7);
        addFormRow(grid, "BTW-tarief:", btwTarief, 8);

        return grid;
    }

    private void addFormRow(GridPane grid, String labelText, Control control, int row) {
        grid.add(new Label(labelText), 0, row);
        grid.add(control, 1, row);
    }

    private Label createItemsLabel() {
        Label label = new Label("Offerte items:");
        label.getStyleClass().add("items-header");
        return label;
    }

    private Button createAddItemButton() {
        Button button = new Button("Voeg item toe");
        button.setId("voegItemToe");
        button.setOnAction(ev -> addItemRow());
        return button;
    }

    private void addItemRow() {
        TextField nameField = createTextField("Naam");
        TextField amountField = createTextField("Aantal");
        TextField priceField = createTextField("Prijs excl. BTW");

        HBox itemRow = new HBox(10, nameField, amountField, priceField);
        itemsBox.getChildren().add(itemRow);
    }

    private Button createExportButton() {
        Button button = new Button("Genereer PDF");
        button.setId("exporteerPdf");
        button.setOnAction(e -> handleExportAction());
        return button;
    }

    private void handleExportAction() {
        try {
            Quote quote = createQuoteFromInput();
            PDFGenerator.createInvoice(quote);
            resetForm();
            showAlert(Alert.AlertType.INFORMATION, "PDF succesvol gegenereerd!");
        } catch (IllegalArgumentException iae) {
            showAlert(Alert.AlertType.WARNING, "Invoerfout: " + iae.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Er is een onverwachte fout opgetreden: " + ex.getMessage());
        }
    }

    private Quote createQuoteFromInput() {
        String naam = validateField(bedrijfsNaam.getText().trim(), "Bedrijfsnaam");
        String straatNaam = validateField(straat.getText().trim(), "Straat");
        String huisnummerTekst = validateField(huisnummer.getText().trim(), "Huisnummer");
        String pc = validateField(postcode.getText().trim(), "Postcode");
        String city = validateField(plaats.getText().trim(), "Plaats");
        String factuurNr = validateField(factuurnummer.getText().trim(), "Factuurnummer");
        String kvkNummer = validateField(kvk.getText().trim(), "KvK-nummer");

        String datum = "";
        if (factuurDatum.getValue() != null) {
            datum = factuurDatum.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        } else throw new IllegalArgumentException("Factuurdatum is verplicht");

        int huisNr = parseInteger(huisnummerTekst, "Huisnummer");
        List<QuoteItem> items = parseQuoteItems();

        return Quote.createQuote(
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
    }

    private String validateField(String value, String fieldName) {
        if (value.isEmpty()) throw new IllegalArgumentException(fieldName + " is verplicht");
        return value;
    }

    private int parseInteger(String value, String fieldName) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(fieldName + " moet een getal zijn");
        }
    }

    private List<QuoteItem> parseQuoteItems() {
        List<QuoteItem> items = new ArrayList<>();

        for (javafx.scene.Node node : itemsBox.getChildren()) {
            if (node instanceof HBox hBox) {
                TextField naamField = (TextField) hBox.getChildren().get(0);
                TextField aantalField = (TextField) hBox.getChildren().get(1);
                TextField prijsField = (TextField) hBox.getChildren().get(2);

                String naamItem = validateField(naamField.getText().trim(), "Itemnaam");
                String aantalTekst = validateField(aantalField.getText().trim(), "Aantal");
                String prijsTekst = validateField(prijsField.getText().trim(), "Prijs");

                int aantal = parseInteger(aantalTekst, "Aantal");
                double prijs = Double.parseDouble(prijsTekst);
                int btw = (btwTarief.getValue() != null) ?
                        Integer.parseInt(btwTarief.getValue().replace("%", "").trim()) : 21;

                items.add(QuoteItem.createItem(naamItem, aantal, btw, prijs));
            }
        }

        if (items.isEmpty()) throw new IllegalArgumentException("Voeg minimaal één item toe");
        return items;
    }

    private void resetForm() {
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
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        new Alert(alertType, message).showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}