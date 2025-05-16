package nl.grapjeje.Models;

import java.util.List;

public class Quote {
    // Company
    private String companyName;
    private String streetName;
    private Integer houseNumber;
    private String postalCode;
    private String city;

    // Billing information
    private String invoiceNumber;
    private String invoiceDate;
    private Integer expiryDate; // In days

    private String kvkNumber;

    // Quote/Estimate Items
    private List<QuoteItem> items;

    // Total Summary
    private Double totalExclVAT;
    private Double totalVAT;
    private Double totalInclVAT;

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public String getStreetName() { return streetName; }
    public void setStreetName(String streetName) { this.streetName = streetName; }
    public Integer getHouseNumber() { return houseNumber; }
    public void setHouseNumber(Integer houseNumber) { this.houseNumber = houseNumber; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }
    public String getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(String invoiceDate) { this.invoiceDate = invoiceDate; }
    public Integer getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Integer expiryDate) { this.expiryDate = expiryDate; }
    public String getKvkNumber() { return kvkNumber; }
    public void setKvkNumber(String kvkNumber) { this.kvkNumber = kvkNumber; }
    public List<QuoteItem> getItems() { return items; }
    public void setItems(List<QuoteItem> items) { this.items = items; }
    public Double getTotalExclVAT() { return totalExclVAT; }
    public void setTotalExclVAT(Double totalExclVAT) { this.totalExclVAT = totalExclVAT; }
    public Double getTotalVAT() { return totalVAT; }
    public void setTotalVAT(Double totalVAT) { this.totalVAT = totalVAT; }
    public Double getTotalInclVAT() { return totalInclVAT; }
    public void setTotalInclVAT(Double totalInclVAT) { this.totalInclVAT = totalInclVAT; }

    public static Quote createQuote(
            String companyName, String streetName,
            Integer houseNumber, String postalCode,
            String city, String invoiceNumber,
            String invoiceDate, Integer expiryDate,
            String kvkNumber, List<QuoteItem> items
    ) {
        Quote quote = new Quote();
        quote.setCompanyName(companyName);
        quote.setStreetName(streetName);
        quote.setHouseNumber(houseNumber);
        quote.setPostalCode(postalCode);
        quote.setCity(city);
        quote.setInvoiceNumber(invoiceNumber);
        quote.setInvoiceDate(invoiceDate);
        quote.setExpiryDate(expiryDate);
        quote.setKvkNumber(kvkNumber);
        quote.setItems(items);

        double totalExclVAT = 0;
        double totalVAT = 0;
        double totalInclVAT = 0;

        for (QuoteItem item : items) {
            totalExclVAT += item.getTotalPriceExclVat();
            totalVAT += item.getTotalPriceInclVat() - item.getTotalPriceExclVat();
            totalInclVAT += item.getTotalPriceInclVat();
        }

        quote.setTotalExclVAT(totalExclVAT);
        quote.setTotalVAT(totalVAT);
        quote.setTotalInclVAT(totalInclVAT);

        return quote;
    }
}
