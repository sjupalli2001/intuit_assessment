package com.example.buildchallenge.csvanalysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * Utility class for sales data analysis.
 */

public class SalesAnalyzer {

        
    /**
     * Loads sales records from CSV, skipping header and blank lines.
     */

    public static List<SalesRecord> load(InputStream is) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            return br.lines()
                     .filter(l -> !l.isBlank())
                     .skip(1) // skip header
                     .map(SalesRecord::fromCsv)
                     .collect(Collectors.toList());
        }
    }

    
    public static double total(List<SalesRecord> records) {
        return records.stream()
                .mapToDouble(SalesRecord::total)
                .sum();
    }


   /**
     * Groups sales by region and sums revenue per region.
     */

    public static Map<String, Double> byRegion(List<SalesRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getRegion,
                        Collectors.summingDouble(SalesRecord::total)
                ));
    }

    /**
     * Returns top N products sorted by revenue (descending).
     */

    public static List<Map.Entry<String, Double>> topNProductsByRevenue(List<SalesRecord> records, int n) {
        Map<String, Double> revenueByProduct =
                records.stream()
                        .collect(Collectors.groupingBy(
                                SalesRecord::getProduct,
                                Collectors.summingDouble(SalesRecord::total)
                        ));

        return revenueByProduct.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .limit(n)
                .collect(Collectors.toList());
    }

    /**
     * Aggregates sales by month using TreeMap for chronological order.
     */
    public static Map<YearMonth, Double> monthlyTotals(List<SalesRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getDate()),
                        TreeMap::new,
                        Collectors.summingDouble(SalesRecord::total)
                ));
    }
}
