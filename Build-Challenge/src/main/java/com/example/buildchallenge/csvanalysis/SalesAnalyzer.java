package com.example.buildchallenge.csvanalysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class SalesAnalyzer {

    // Load CSV from InputStream
    public static List<SalesRecord> load(InputStream is) throws IOException {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
            return br.lines()
                     .filter(l -> !l.isBlank())
                     .skip(1) // skip header
                     .map(SalesRecord::fromCsv)
                     .collect(Collectors.toList());
        }
    }

    // 1. Total sales
    public static double total(List<SalesRecord> records) {
        return records.stream()
                .mapToDouble(SalesRecord::total)
                .sum();
    }

    // 2. Sales by region
    public static Map<String, Double> byRegion(List<SalesRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        SalesRecord::getRegion,
                        Collectors.summingDouble(SalesRecord::total)
                ));
    }

    // 3. Top N products by revenue
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

    // 4. Monthly totals (YearMonth -> total revenue)
    public static Map<YearMonth, Double> monthlyTotals(List<SalesRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(
                        r -> YearMonth.from(r.getDate()),
                        TreeMap::new,
                        Collectors.summingDouble(SalesRecord::total)
                ));
    }
}
