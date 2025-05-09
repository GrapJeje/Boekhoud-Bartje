package nl.grapjeje.Models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Setter
@Getter
public class Quote {
    // Company
    private String companyName;
    private String streetName;
    private Integer houseNumber;
    private String postalCode;
    private String city;

    // Billing information
    private String invoiceNumber;
    private Date invoiceDate;
    private Integer expiryDate; // In days
    private String kvkNumber;

    // Quote/Estimate Items
    private List<QuoteItem> items;

    // Total Summary
    private Double totalExclVAT;
    private Double totalVAT;
    private Double totalInclVAT;
}
