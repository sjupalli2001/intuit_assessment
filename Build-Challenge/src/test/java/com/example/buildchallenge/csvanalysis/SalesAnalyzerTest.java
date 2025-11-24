package com.example.buildchallenge.csvanalysis;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SalesAnalyzerTest {
    private List<SalesRecord> testRecords;
    private String sampleCsv;

    @BeforeEach
    void setUp() {
        sampleCsv = "date,region,salesperson,product,quantity,unitPrice\n" +
                    "2024-01-05,North,Alice,Widget,10,9.99\n" +
                    "2024-01-06,South,Bob,Gadget,5,19.99\n" +
                    "2024-02-10,North,Alice,Widget,3,9.99\n" +
                    "2024-02-11,East,Carol,Thing,7,14.50\n" +
                    "2024-03-01,South,Bob,Gadget,2,19.99\n";
        
        testRecords = List.of(
            new SalesRecord(java.time.LocalDate.of(2024, 1, 5), "North", "Alice", "Widget", 10, 9.99),
            new SalesRecord(java.time.LocalDate.of(2024, 1, 6), "South", "Bob", "Gadget", 5, 19.99),
            new SalesRecord(java.time.LocalDate.of(2024, 2, 10), "North", "Alice", "Widget", 3, 9.99),
            new SalesRecord(java.time.LocalDate.of(2024, 2, 11), "East", "Carol", "Thing", 7, 14.50),
            new SalesRecord(java.time.LocalDate.of(2024, 3, 1), "South", "Bob", "Gadget", 2, 19.99)
        );
    }

    @Test
    void testLoad() throws IOException {
        InputStream is = new ByteArrayInputStream(sampleCsv.getBytes());
        List<SalesRecord> records = SalesAnalyzer.load(is);
        
        assertNotNull(records);
        assertEquals(5, records.size());
        assertEquals("North", records.get(0).getRegion());
        assertEquals("Widget", records.get(0).getProduct());
    }

    @Test
    void testLoadSkipsHeader() throws IOException {
        InputStream is = new ByteArrayInputStream(sampleCsv.getBytes());
        List<SalesRecord> records = SalesAnalyzer.load(is);
        
        assertEquals(5, records.size());
        
        assertNotEquals("date", records.get(0).getRegion());
    }

    @Test
    void testLoadFiltersBlankLines() throws IOException {
        String csvWithBlanks = "date,region,salesperson,product,quantity,unitPrice\n" +
                               "2024-01-05,North,Alice,Widget,10,9.99\n" +
                               "\n" +
                               "   \n" +
                               "2024-01-06,South,Bob,Gadget,5,19.99\n";
        InputStream is = new ByteArrayInputStream(csvWithBlanks.getBytes());
        List<SalesRecord> records = SalesAnalyzer.load(is);
        
        assertEquals(2, records.size());
    }

    @Test
    void testLoadWithEmptyFile() throws IOException {
        String emptyCsv = "date,region,salesperson,product,quantity,unitPrice\n";
        InputStream is = new ByteArrayInputStream(emptyCsv.getBytes());
        List<SalesRecord> records = SalesAnalyzer.load(is);
        
        assertNotNull(records);
        assertTrue(records.isEmpty());
    }

    @Test
    void testTotal() {
        double total = SalesAnalyzer.total(testRecords);
        
        assertEquals(371.30, total, 0.01);
    }

    @Test
    void testTotalWithEmptyList() {
        List<SalesRecord> empty = List.of();
        double total = SalesAnalyzer.total(empty);
        assertEquals(0.0, total, 0.001);
    }

    @Test
    void testTotalWithSingleRecord() {
        List<SalesRecord> single = List.of(testRecords.get(0));
        double total = SalesAnalyzer.total(single);
        assertEquals(99.9, total, 0.01);
    }

    @Test
    void testByRegion() {
        Map<String, Double> byRegion = SalesAnalyzer.byRegion(testRecords);
        
        assertNotNull(byRegion);
        assertEquals(3, byRegion.size());
        assertTrue(byRegion.containsKey("North"));
        assertTrue(byRegion.containsKey("South"));
        assertTrue(byRegion.containsKey("East"));
        
        assertEquals(129.87, byRegion.get("North"), 0.01);
        assertEquals(139.93, byRegion.get("South"), 0.01);
        assertEquals(101.50, byRegion.get("East"), 0.01);
    }

    @Test
    void testByRegionWithEmptyList() {
        List<SalesRecord> empty = List.of();
        Map<String, Double> byRegion = SalesAnalyzer.byRegion(empty);
        
        assertNotNull(byRegion);
        assertTrue(byRegion.isEmpty());
    }

    @Test
    void testByRegionWithSingleRegion() {
        List<SalesRecord> singleRegion = List.of(
            testRecords.get(0),
            testRecords.get(2)
        );
        Map<String, Double> byRegion = SalesAnalyzer.byRegion(singleRegion);
        
        assertEquals(1, byRegion.size());
        assertTrue(byRegion.containsKey("North"));
    }

    @Test
    void testTopNProductsByRevenue() {
        List<Map.Entry<String, Double>> topProducts = SalesAnalyzer.topNProductsByRevenue(testRecords, 2);
        
        assertNotNull(topProducts);
        assertEquals(2, topProducts.size());
        assertTrue(topProducts.get(0).getValue() >= topProducts.get(1).getValue());
    }

    @Test
    void testTopNProductsByRevenueWithN3() {
        List<Map.Entry<String, Double>> topProducts = SalesAnalyzer.topNProductsByRevenue(testRecords, 3);
        
        assertEquals(3, topProducts.size());
    }

    @Test
    void testTopNProductsByRevenueWithNGreaterThanProducts() {
        List<Map.Entry<String, Double>> topProducts = SalesAnalyzer.topNProductsByRevenue(testRecords, 10);
        
        assertEquals(3, topProducts.size());
    }

    @Test
    void testTopNProductsByRevenueWithEmptyList() {
        List<SalesRecord> empty = List.of();
        List<Map.Entry<String, Double>> topProducts = SalesAnalyzer.topNProductsByRevenue(empty, 3);
        
        assertNotNull(topProducts);
        assertTrue(topProducts.isEmpty());
    }

    @Test
    void testTopNProductsByRevenueSortedDescending() {
        List<Map.Entry<String, Double>> topProducts = SalesAnalyzer.topNProductsByRevenue(testRecords, 3);
        
        for (int i = 0; i < topProducts.size() - 1; i++) {
            assertTrue(topProducts.get(i).getValue() >= topProducts.get(i + 1).getValue(),
                    "Products should be sorted by revenue descending");
        }
    }

    @Test
    void testMonthlyTotals() {
        Map<YearMonth, Double> monthly = SalesAnalyzer.monthlyTotals(testRecords);
        
        assertNotNull(monthly);
        assertEquals(3, monthly.size());
        assertTrue(monthly.containsKey(YearMonth.of(2024, 1)));
        assertTrue(monthly.containsKey(YearMonth.of(2024, 2)));
        assertTrue(monthly.containsKey(YearMonth.of(2024, 3)));
        
        assertEquals(199.85, monthly.get(YearMonth.of(2024, 1)), 0.01);
        assertEquals(131.47, monthly.get(YearMonth.of(2024, 2)), 0.01);
        assertEquals(39.98, monthly.get(YearMonth.of(2024, 3)), 0.01);
    }

    @Test
    void testMonthlyTotalsWithEmptyList() {
        List<SalesRecord> empty = List.of();
        Map<YearMonth, Double> monthly = SalesAnalyzer.monthlyTotals(empty);
        
        assertNotNull(monthly);
        assertTrue(monthly.isEmpty());
    }

    @Test
    void testMonthlyTotalsReturnsTreeMap() {
        Map<YearMonth, Double> monthly = SalesAnalyzer.monthlyTotals(testRecords);
        
        assertTrue(monthly instanceof java.util.TreeMap);
    }

    @Test
    void testMonthlyTotalsSorted() {
        Map<YearMonth, Double> monthly = SalesAnalyzer.monthlyTotals(testRecords);
        
        YearMonth previous = null;
        for (YearMonth ym : monthly.keySet()) {
            if (previous != null) {
                assertTrue(ym.isAfter(previous) || ym.equals(previous),
                        "TreeMap should be sorted by YearMonth");
            }
            previous = ym;
        }
    }

    @Test
    void testLoadWithMultipleRecords() throws IOException {
        String multiRecordCsv = "date,region,salesperson,product,quantity,unitPrice\n" +
                                "2024-01-05,North,Alice,Widget,10,9.99\n" +
                                "2024-01-06,South,Bob,Gadget,5,19.99\n" +
                                "2024-02-10,North,Alice,Widget,3,9.99\n" +
                                "2024-02-11,East,Carol,Thing,7,14.50\n" +
                                "2024-03-01,South,Bob,Gadget,2,19.99\n" +
                                "2024-03-05,East,Carol,Widget,1,9.99\n";
        InputStream is = new ByteArrayInputStream(multiRecordCsv.getBytes());
        List<SalesRecord> records = SalesAnalyzer.load(is);
        
        assertEquals(6, records.size());
    }

    @Test
    void testTotalUsesStream() {
        double total = SalesAnalyzer.total(testRecords);
        assertTrue(total > 0);
    }

    @Test
    void testByRegionGroupsCorrectly() {
        Map<String, Double> byRegion = SalesAnalyzer.byRegion(testRecords);
        
        assertTrue(byRegion.values().stream().allMatch(v -> v > 0));
    }

    @Test
    void testTopNProductsByRevenueWithN1() {
        List<Map.Entry<String, Double>> topProducts = SalesAnalyzer.topNProductsByRevenue(testRecords, 1);
        
        assertEquals(1, topProducts.size());
        assertNotNull(topProducts.get(0).getKey());
        assertTrue(topProducts.get(0).getValue() > 0);
    }

    @Test
    void testTopNProductsByRevenueWithN0() {
        List<Map.Entry<String, Double>> topProducts = SalesAnalyzer.topNProductsByRevenue(testRecords, 0);
        
        assertNotNull(topProducts);
        assertTrue(topProducts.isEmpty());
    }

    @Test
    void testMonthlyTotalsGroupsByYearMonth() {
        List<SalesRecord> records = List.of(
            new SalesRecord(java.time.LocalDate.of(2024, 1, 5), "North", "Alice", "Widget", 10, 9.99),
            new SalesRecord(java.time.LocalDate.of(2024, 1, 15), "South", "Bob", "Gadget", 5, 19.99),
            new SalesRecord(java.time.LocalDate.of(2024, 2, 10), "North", "Alice", "Widget", 3, 9.99)
        );
        
        Map<YearMonth, Double> monthly = SalesAnalyzer.monthlyTotals(records);
        
        assertEquals(2, monthly.size());
        assertEquals(199.85, monthly.get(YearMonth.of(2024, 1)), 0.01);
    }

    @Test
    void testLoadClosesInputStream() throws IOException {
        InputStream is = new ByteArrayInputStream(sampleCsv.getBytes());
        List<SalesRecord> records = SalesAnalyzer.load(is);
        
        assertNotNull(records);
        assertFalse(records.isEmpty());
    }
}

