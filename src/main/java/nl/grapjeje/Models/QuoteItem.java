package nl.grapjeje.Models;

public class QuoteItem {
    private String itemName;
    private int quantity;
    private double priceExclVAT;
    private double priceInclVAT;
    private double vatRate;
    private double totalExclVAT;
    private double totalInclVAT;

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPriceExclVAT() { return priceExclVAT; }
    public void setPriceExclVAT(double priceExclVAT) { this.priceExclVAT = priceExclVAT; }
    public double getPriceInclVAT() { return priceInclVAT; }
    public void setPriceInclVAT(double priceInclVAT) { this.priceInclVAT = priceInclVAT; }
    public double getVatRate() { return vatRate; }
    public void setVatRate(double vatRate) { this.vatRate = vatRate; }
    public double getTotalExclVAT() { return totalExclVAT; }
    public void setTotalExclVAT(double totalExclVAT) { this.totalExclVAT = totalExclVAT; }
    public double getTotalInclVAT() { return totalInclVAT; }
    public void setTotalInclVAT(double totalInclVAT) { this.totalInclVAT = totalInclVAT; }

    public double getUnitPriceInclVat() {
        return priceExclVAT * (1 + vatRate / 100);
    }
    public double getTotalPriceExclVat() {
        return priceExclVAT * quantity;
    }
    public double getTotalPriceInclVat() {
        return this.getUnitPriceInclVat() * quantity;
    }

    public static QuoteItem createItem(String itemName, int quantity, double vatRate, double priceExclVAT) {
        QuoteItem item = new QuoteItem();
        item.setItemName(itemName);
        item.setQuantity(quantity);
        item.setVatRate(vatRate);
        item.setPriceExclVAT(priceExclVAT);
        item.setPriceInclVAT(item.getUnitPriceInclVat());
        item.setTotalExclVAT(item.getTotalPriceExclVat());
        item.setTotalInclVAT(item.getTotalPriceInclVat());
        return item;
    }
}