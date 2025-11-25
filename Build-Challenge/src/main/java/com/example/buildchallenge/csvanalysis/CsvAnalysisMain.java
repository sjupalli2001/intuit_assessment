package com.example.buildchallenge.csvanalysis;

import java.io.InputStream;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

/**
 * Main class for CSV sales analysis.
 */
public class CsvAnalysisMain {

    public static void main(String[] args) throws Exception {

        // Load CSV from resources using context classloader (works in Maven exec)
        InputStream is = Thread.currentThread()
                .getContextClassLoader()
                .getResourceAsStream("sample_sales.csv");

        if (is == null) {
            System.err.println("ERROR: sample_sales.csv not found in src/main/resources/");
            return;
        }

        // Load records
        List<SalesRecord> records = SalesAnalyzer.load(is);

        // Run analyses
        double totalSales = SalesAnalyzer.total(records);
        Map<String, Double> salesByRegion = SalesAnalyzer.byRegion(records);
        Map<YearMonth, Double> monthly = SalesAnalyzer.monthlyTotals(records);
        List<Map.Entry<String, Double>> topProducts =
                SalesAnalyzer.topNProductsByRevenue(records, 3);

        // Print results
        System.out.println("===== CSV Analysis Results =====");
        System.out.println("Total sales: " + totalSales);
        System.out.println("Sales by region: " + salesByRegion);
        System.out.println("Top 3 products: " + topProducts);
        System.out.println("Monthly totals:");
        monthly.forEach((ym, amount) ->
                System.out.printf("  %s -> %.2f%n", ym, amount)
        );
    }
}
