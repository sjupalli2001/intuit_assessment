package com.example.buildchallenge.csvanalysis;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SalesRecordTest {
    private SalesRecord record;
    private LocalDate testDate;
    private static final String TEST_REGION = "North";
    private static final String TEST_SALESPERSON = "Alice";
    private static final String TEST_PRODUCT = "Widget";
    private static final int TEST_QUANTITY = 10;
    private static final double TEST_PRICE = 9.99;

    @BeforeEach
    void setUp() {
        testDate = LocalDate.of(2024, 1, 5);
        record = new SalesRecord(testDate, TEST_REGION, TEST_SALESPERSON, TEST_PRODUCT, TEST_QUANTITY, TEST_PRICE);
    }

    @Test
    void testConstructor() {
        assertNotNull(record);
        assertEquals(testDate, record.getDate());
        assertEquals(TEST_REGION, record.getRegion());
        assertEquals(TEST_SALESPERSON, record.getSalesperson());
        assertEquals(TEST_PRODUCT, record.getProduct());
    }

    @Test
    void testFromCsv() {
        String csvLine = "2024-01-05,North,Alice,Widget,10,9.99";
        SalesRecord parsed = SalesRecord.fromCsv(csvLine);
        
        assertNotNull(parsed);
        assertEquals(LocalDate.of(2024, 1, 5), parsed.getDate());
        assertEquals("North", parsed.getRegion());
        assertEquals("Alice", parsed.getSalesperson());
        assertEquals("Widget", parsed.getProduct());
        assertEquals(99.9, parsed.total(), 0.001);
    }

    @Test
    void testFromCsvWithSpaces() {
        String csvLine = "2024-01-05, North , Alice , Widget , 10 , 9.99";
        SalesRecord parsed = SalesRecord.fromCsv(csvLine);
        
        assertNotNull(parsed);
        assertEquals("North", parsed.getRegion());
        assertEquals("Alice", parsed.getSalesperson());
        assertEquals("Widget", parsed.getProduct());
    }

    @Test
    void testTotal() {
        double expectedTotal = TEST_QUANTITY * TEST_PRICE;
        assertEquals(expectedTotal, record.total(), 0.001);
    }

    @Test
    void testTotalWithZeroQuantity() {
        SalesRecord zeroQty = new SalesRecord(testDate, TEST_REGION, TEST_SALESPERSON, TEST_PRODUCT, 0, TEST_PRICE);
        assertEquals(0.0, zeroQty.total(), 0.001);
    }

    @Test
    void testTotalWithZeroPrice() {
        SalesRecord zeroPrice = new SalesRecord(testDate, TEST_REGION, TEST_SALESPERSON, TEST_PRODUCT, TEST_QUANTITY, 0.0);
        assertEquals(0.0, zeroPrice.total(), 0.001);
    }

    @Test
    void testTotalWithLargeValues() {
        SalesRecord large = new SalesRecord(testDate, TEST_REGION, TEST_SALESPERSON, TEST_PRODUCT, 1000, 99.99);
        assertEquals(99990.0, large.total(), 0.001);
    }

    @Test
    void testGetDate() {
        assertEquals(testDate, record.getDate());
    }

    @Test
    void testGetRegion() {
        assertEquals(TEST_REGION, record.getRegion());
    }

    @Test
    void testGetSalesperson() {
        assertEquals(TEST_SALESPERSON, record.getSalesperson());
    }

    @Test
    void testGetProduct() {
        assertEquals(TEST_PRODUCT, record.getProduct());
    }

    @Test
    void testFromCsvWithDifferentDate() {
        String csvLine = "2024-03-15,South,Bob,Gadget,5,19.99";
        SalesRecord parsed = SalesRecord.fromCsv(csvLine);
        
        assertEquals(LocalDate.of(2024, 3, 15), parsed.getDate());
        assertEquals("South", parsed.getRegion());
        assertEquals("Bob", parsed.getSalesperson());
        assertEquals("Gadget", parsed.getProduct());
    }

    @Test
    void testFromCsvWithDecimalPrice() {
        String csvLine = "2024-02-11,East,Carol,Thing,7,14.50";
        SalesRecord parsed = SalesRecord.fromCsv(csvLine);
        
        assertEquals(101.50, parsed.total(), 0.001);
    }

    @Test
    void testFromCsvParsesIntegerQuantity() {
        String csvLine = "2024-01-05,North,Alice,Widget,10,9.99";
        SalesRecord parsed = SalesRecord.fromCsv(csvLine);
        
        assertEquals(99.9, parsed.total(), 0.001);
    }

    @Test
    void testFromCsvParsesDoublePrice() {
        String csvLine = "2024-01-05,North,Alice,Widget,10,9.99";
        SalesRecord parsed = SalesRecord.fromCsv(csvLine);
        
        assertEquals(99.9, parsed.total(), 0.001);
    }

    @Test
    void testConstructorWithAllFields() {
        LocalDate date = LocalDate.of(2024, 4, 20);
        String region = "West";
        String salesperson = "David";
        String product = "Thing";
        int quantity = 9;
        double price = 14.50;
        
        SalesRecord rec = new SalesRecord(date, region, salesperson, product, quantity, price);
        
        assertEquals(date, rec.getDate());
        assertEquals(region, rec.getRegion());
        assertEquals(salesperson, rec.getSalesperson());
        assertEquals(product, rec.getProduct());
        assertEquals(130.50, rec.total(), 0.001);
    }

    @Test
    void testFromCsvWithMultipleRecords() {
        String[] csvLines = {
            "2024-01-05,North,Alice,Widget,10,9.99",
            "2024-01-06,South,Bob,Gadget,5,19.99",
            "2024-02-10,North,Alice,Widget,3,9.99"
        };
        
        for (String line : csvLines) {
            SalesRecord rec = SalesRecord.fromCsv(line);
            assertNotNull(rec);
            assertNotNull(rec.getDate());
            assertNotNull(rec.getRegion());
            assertNotNull(rec.getSalesperson());
            assertNotNull(rec.getProduct());
        }
    }

    @Test
    void testTotalCalculationAccuracy() {
        SalesRecord rec1 = new SalesRecord(testDate, TEST_REGION, TEST_SALESPERSON, TEST_PRODUCT, 3, 9.99);
        assertEquals(29.97, rec1.total(), 0.001);
        
        SalesRecord rec2 = new SalesRecord(testDate, TEST_REGION, TEST_SALESPERSON, TEST_PRODUCT, 7, 14.50);
        assertEquals(101.50, rec2.total(), 0.001);
    }

    @Test
    void testFromCsvDateFormat() {
        String csvLine = "2024-12-31,North,Alice,Widget,1,1.00";
        SalesRecord parsed = SalesRecord.fromCsv(csvLine);
        
        assertEquals(LocalDate.of(2024, 12, 31), parsed.getDate());
    }

    @Test
    void testFromCsvHandlesTrim() {
        String csvLine = " 2024-01-05 , North , Alice , Widget , 10 , 9.99 ";
        SalesRecord parsed = SalesRecord.fromCsv(csvLine);
        
        assertEquals("North", parsed.getRegion());
        assertEquals("Alice", parsed.getSalesperson());
        assertEquals("Widget", parsed.getProduct());
    }
}

