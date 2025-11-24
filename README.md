# Intuit Assessment - Java Build Challenge

This project contains two Java programming challenges demonstrating concurrent programming and data analysis capabilities.

## Project Overview

This Maven-based Java project implements two distinct challenges:

1. **Producer-Consumer Pattern**: A multi-threaded application demonstrating the classic producer-consumer pattern with thread synchronization.
2. **CSV Sales Analysis**: A data analysis tool that processes sales data from CSV files and generates various analytical reports.

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- An IDE (IntelliJ IDEA, Eclipse, or VS Code) is recommended but not required

## Project Structure

```
Build-Challenge/
├── pom.xml                          # Maven configuration
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/buildchallenge/
│   │   │       ├── producerconsumer/    # Producer-Consumer challenge
│   │   │       │   ├── Producer.java
│   │   │       │   ├── Consumer.java
│   │   │       │   ├── SharedBuffer.java
│   │   │       │   └── ProducerConsumerMain.java
│   │   │       └── csvanalysis/         # CSV Analysis challenge
│   │   │           ├── SalesRecord.java
│   │   │           ├── SalesAnalyzer.java
│   │   │           └── CsvAnalysisMain.java
│   │   └── resources/
│   │       └── sample_sales.csv         # Sample sales data
│   └── test/
│       └── java/
│           └── com/example/buildchallenge/
│               ├── producerconsumer/    # Producer-Consumer tests
│               │   ├── ProducerTest.java
│               │   ├── ConsumerTest.java
│               │   ├── SharedBufferTest.java
│               │   └── ProducerConsumerMainTest.java
│               └── csvanalysis/         # CSV Analysis tests
│                   ├── SalesRecordTest.java
│                   ├── SalesAnalyzerTest.java
│                   └── CsvAnalysisMainTest.java
└── Output/                             # Program output screenshots
    ├── ProducerConsumerOutput.png
    ├── ProducerConsumerTestResults.png
    └── SalesCsvAnalysisOutput.png
```

## Challenge 1: Producer-Consumer Pattern

### Description
Implements a thread-safe producer-consumer pattern using a shared buffer. The producer thread generates items from a source list and places them in a bounded buffer, while the consumer thread retrieves items from the buffer and adds them to a destination list.

### Key Components

- **SharedBuffer<T>**: A thread-safe bounded buffer implementation using `synchronized` blocks and `wait()/notify()` for thread coordination
- **Producer**: A thread that produces items from a source list and puts them into the buffer with a configurable delay
- **Consumer**: A thread that consumes items from the buffer and adds them to a destination list with a configurable delay
- **ProducerConsumerMain**: The main class that orchestrates the producer and consumer threads

### Features

- Thread-safe buffer operations
- Bounded buffer with capacity control
- Producer delay: 100ms per item
- Consumer delay: 150ms per item
- Graceful thread interruption handling
- Source items: "apple", "banana", "Capsicum", "Dates"

### Running the Producer-Consumer Application

```bash
cd Build-Challenge
mvn compile exec:java -Dexec.mainClass="com.example.buildchallenge.producerconsumer.ProducerConsumerMain"
```

## Challenge 2: CSV Sales Analysis

### Description
A comprehensive sales data analysis tool that processes CSV files containing sales records and generates various analytical reports including total sales, regional breakdowns, top products, and monthly trends.

### Key Components

- **SalesRecord**: A data model class representing a single sales record with date, region, salesperson, product, quantity, and price
- **SalesAnalyzer**: A utility class providing static methods for various sales analyses:
  - `load()`: Loads sales records from a CSV InputStream
  - `total()`: Calculates total sales revenue
  - `byRegion()`: Groups sales by region
  - `topNProductsByRevenue()`: Finds top N products by revenue
  - `monthlyTotals()`: Aggregates sales by month (YearMonth)
- **CsvAnalysisMain**: The main class that loads the CSV file and displays analysis results

### Features

- CSV parsing with header skipping and blank line filtering
- Total sales calculation
- Regional sales breakdown
- Top N products by revenue (sorted descending)
- Monthly sales aggregation (sorted chronologically using TreeMap)
- Stream-based processing for efficient data manipulation

### Running the CSV Analysis Application

```bash
cd Build-Challenge
mvn compile exec:java -Dexec.mainClass="com.example.buildchallenge.csvanalysis.CsvAnalysisMain"
```

## Building the Project

To compile the project:

```bash
cd Build-Challenge
mvn clean compile
```

To compile both main and test classes:

```bash
mvn clean test-compile
```

To build the JAR file:

```bash
mvn clean package
```

The JAR file will be created at: `target/java-build-challenge-1.0.0.jar`

## Running Tests

### Run All Tests

```bash
cd Build-Challenge
mvn test
```

### Run Tests for a Specific Package

To run only Producer-Consumer tests:

```bash
mvn test -Dtest=com.example.buildchallenge.producerconsumer.*Test
```

To run only CSV Analysis tests:

```bash
mvn test -Dtest=com.example.buildchallenge.csvanalysis.*Test
```

### Run a Specific Test Class

```bash
mvn test -Dtest=SalesRecordTest
```

### View Test Reports

After running tests, detailed reports are available in:
- `target/surefire-reports/` - Text and XML test reports

## Test Coverage

### Producer-Consumer Tests

- **ProducerTest**: Tests producer thread behavior, item production, interruption handling, and loop iteration
- **ConsumerTest**: Tests consumer thread behavior, item consumption, interruption handling, and loop termination
- **SharedBufferTest**: Tests thread-safe buffer operations, blocking behavior, capacity limits, and concurrent access
- **ProducerConsumerMainTest**: Tests main method execution and thread coordination

### CSV Analysis Tests

- **SalesRecordTest**: Tests CSV parsing, constructor, getters, and total calculation
- **SalesAnalyzerTest**: Tests all analysis methods including load, total, byRegion, topNProductsByRevenue, and monthlyTotals
- **CsvAnalysisMainTest**: Tests main method execution, resource loading, and output generation

## Technologies Used

- **Java 17**: Programming language
- **Maven**: Build automation and dependency management
- **JUnit 5 (Jupiter)**: Testing framework
- **Java Streams API**: For data processing and analysis
- **Java Concurrency API**: For multi-threaded programming

## Sample Data

The project includes a sample sales CSV file (`src/main/resources/sample_sales.csv`) with the following structure:

```csv
date,region,salesperson,product,quantity,unitPrice
2024-01-05,North,Alice,Widget,10,9.99
2024-01-06,South,Bob,Gadget,5,19.99
...
```

## Output

The `Output/` directory contains screenshots demonstrating:
- Producer-Consumer program execution output
- Producer-Consumer test results
- CSV Analysis program execution output

## Key Design Patterns

1. **Producer-Consumer Pattern**: Demonstrates thread coordination using shared buffers
2. **Singleton-like Static Methods**: SalesAnalyzer uses static utility methods
3. **Builder Pattern**: SalesRecord.fromCsv() for object creation from CSV
4. **Strategy Pattern**: Different analysis strategies in SalesAnalyzer

## Thread Safety

The Producer-Consumer implementation ensures thread safety through:
- `synchronized` blocks for critical sections
- `wait()` and `notifyAll()` for thread coordination
- Proper handling of `InterruptedException`
- Bounded buffer to prevent memory issues

## Error Handling

- **Producer-Consumer**: Handles `InterruptedException` gracefully, restoring interrupt status
- **CSV Analysis**: Handles `IOException` when reading files, validates CSV format, and handles missing resources


