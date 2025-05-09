package nl.grapjeje.Models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class QuoteItem {
    private String itemName;
    private Integer quantity;
    private Double priceExclVAT;
    private Double priceInclVAT;
    private Double vatRate;
    private Double totalExclVAT;
    private Double totalInclVAT;
}
