package nl.grapjeje.Utils;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import nl.grapjeje.Models.Quote;
import nl.grapjeje.Models.QuoteItem;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PDFGenerator {

    private static final Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
    private static final Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
    private static final Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
    private static final Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 8);

    public static void createInvoice(Quote quote) throws DocumentException, IOException {
        try {
            Document document = new Document();

            String downloadsPath = System.getProperty("user.home") + File.separator + "Downloads";
            String filePath = downloadsPath + File.separator + "Factuur_" + quote.getInvoiceNumber() + ".pdf";

            System.out.println("PDF wordt opgeslagen in: " + filePath);

            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            addCompanyHeader(document, quote);
            addInvoiceInfo(document, quote);
            addItemsTable(document, quote);
            addTotals(document, quote);
            addFooter(document);

            document.close();
        } catch (FileNotFoundException e) {
            System.err.println("Bestand niet gevonden: " + e.getMessage());
            e.printStackTrace();
        } catch (DocumentException e) {
            System.err.println("PDF document fout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void addCompanyHeader(Document document, Quote quote)
            throws DocumentException {
        Paragraph companyHeader = new Paragraph();
        companyHeader.setAlignment(Element.ALIGN_LEFT);

        // Company name
        Paragraph companyName = new Paragraph(quote.getCompanyName(), titleFont);
        companyName.setSpacingAfter(10f);
        document.add(companyName);

        // Address
        Paragraph companyAddress = new Paragraph();
        companyAddress.add(new Chunk(quote.getStreetName() + " " + quote.getHouseNumber() + "\n", normalFont));
        companyAddress.add(new Chunk(quote.getPostalCode() + " " + quote.getCity() + "\n", normalFont));
        companyAddress.setSpacingAfter(20f);
        document.add(companyAddress);
    }

    private static void addInvoiceInfo(Document document, Quote quote) throws DocumentException {

        // Calculate due date
        LocalDate date = LocalDate.parse(quote.getInvoiceDate(), DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        LocalDate dueDate = date.plusDays(quote.getExpiryDate());

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(20f);

        // Invoice number
        addInvoiceInfoRow(table, "Factuurnummer:", quote.getInvoiceNumber());

        // Invoice date
        addInvoiceInfoRow(table, "Factuurdatum:", quote.getInvoiceDate());

        // Due date
        addInvoiceInfoRow(table, "Vervaldatum:", dueDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

        // Payment term
        addInvoiceInfoRow(table, "Betaaltermijn:", quote.getExpiryDate() + " dagen");

        // KVK number
        addInvoiceInfoRow(table, "KvK-nummer:", quote.getKvkNumber());

        document.add(table);
    }

    private static void addInvoiceInfoRow(PdfPTable table, String label, String value) {
        PdfPCell cell1 = new PdfPCell(new Phrase(label, normalFont));
        cell1.setBorder(Rectangle.NO_BORDER);
        cell1.setHorizontalAlignment(Element.ALIGN_LEFT);

        PdfPCell cell2 = new PdfPCell(new Phrase(value, normalFont));
        cell2.setBorder(Rectangle.NO_BORDER);
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);

        table.addCell(cell1);
        table.addCell(cell2);
    }

    private static void addItemsTable(Document document, Quote quote) throws DocumentException {
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(20f);

        String[] headers = {"Omschrijving", "Aantal", "BTW %", "Prijs per stuk (excl)", "Prijs totaal (excl)", "Prijs totaal (incl)"};
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new Color(230, 230, 230));
            cell.setPadding(5f);
            table.addCell(cell);
        }

        for (QuoteItem item : quote.getItems()) {
            table.addCell(createCell(item.getItemName(), Element.ALIGN_LEFT));
            table.addCell(createCell(String.valueOf(item.getQuantity()), Element.ALIGN_CENTER));
            table.addCell(createCell(item.getVatRate() + "%", Element.ALIGN_CENTER));
            table.addCell(createCell(String.format("€ %.2f", item.getPriceExclVAT()), Element.ALIGN_RIGHT));
            table.addCell(createCell(String.format("€ %.2f", item.getTotalPriceExclVat()), Element.ALIGN_RIGHT));
            table.addCell(createCell(String.format("€ %.2f", item.getTotalPriceInclVat()), Element.ALIGN_RIGHT));
        }

        document.add(table);
    }

    private static PdfPCell createCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, normalFont));
        cell.setPadding(5f);
        cell.setHorizontalAlignment(alignment);
        return cell;
    }

    private static void addTotals(Document document, Quote quote) throws DocumentException {
        double totalExclVat = quote.getItems().stream().mapToDouble(QuoteItem::getTotalPriceExclVat).sum();
        double totalVat = quote.getItems().stream().mapToDouble(item ->
                item.getTotalPriceInclVat() - item.getTotalPriceExclVat()).sum();
        double totalInclVat = quote.getItems().stream().mapToDouble(QuoteItem::getTotalPriceInclVat).sum();

        PdfPTable table = new PdfPTable(2);
        table.setWidthPercentage(50);
        table.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.setSpacingBefore(10f);

        addTotalRow(table, "Totaal exclusief BTW:", String.format("€ %.2f", totalExclVat));
        addTotalRow(table, "Totaal BTW:", String.format("€ %.2f", totalVat));

        PdfPCell labelCell = new PdfPCell(new Phrase("Totaal inclusief BTW:", headerFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5f);

        PdfPCell valueCell = new PdfPCell(new Phrase(String.format("€ %.2f", totalInclVat), headerFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5f);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(labelCell);
        table.addCell(valueCell);

        document.add(table);
    }

    private static void addTotalRow(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, normalFont));
        labelCell.setBorder(Rectangle.NO_BORDER);
        labelCell.setPadding(5f);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, normalFont));
        valueCell.setBorder(Rectangle.NO_BORDER);
        valueCell.setPadding(5f);
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private static void addFooter(Document document) throws DocumentException {
        Paragraph footer = new Paragraph();
        footer.setFont(smallFont);
        footer.add("Bedankt voor uw bestelling. Gelieve het totaalbedrag binnen de aangegeven termijn over te maken op IBAN NL00BANK0123456789 tnv " +
                "Bedrijfsnaam. Voor vragen kunt u contact opnemen via info@bedrijfsnaam.nl of telefonisch op 012-3456789.");
        footer.setSpacingBefore(30f);
        document.add(footer);
    }
}