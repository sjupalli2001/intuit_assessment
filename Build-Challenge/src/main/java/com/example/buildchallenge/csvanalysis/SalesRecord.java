package com.example.buildchallenge.csvanalysis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SalesRecord {
    private final LocalDate date;
    private final String region, salesperson, product;
    private final int quantity;
    private final double price;
    private static final DateTimeFormatter F = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public SalesRecord(LocalDate d, String r, String s, String p, int q, double pr) {
        date=d; region=r; salesperson=s; product=p; quantity=q; price=pr;
    }
    public static SalesRecord fromCsv(String line) {
        String[] a=line.split(",");
        return new SalesRecord(LocalDate.parse(a[0].trim(),F),a[1].trim(),a[2].trim(),
                a[3].trim(),Integer.parseInt(a[4].trim()),Double.parseDouble(a[5].trim()));
    }
    public double total(){ return quantity*price; }
    public LocalDate getDate(){return date;}
    public String getRegion(){return region;}
    public String getSalesperson(){return salesperson;}
    public String getProduct(){return product;}
}
