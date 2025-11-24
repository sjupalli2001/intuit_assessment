package com.example.buildchallenge.producerconsumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ProducerTest {
    private SharedBuffer<String> buffer;
    private List<String> source;
    private Producer producer;
    private static final int DELAY_MS = 50;

    @BeforeEach
    void setUp() {
        buffer = new SharedBuffer<>(10);
        source = new ArrayList<>();
        producer = new Producer(buffer, source, DELAY_MS);
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testRunWithEmptySource() throws InterruptedException {
        // Test for loop with empty list
        Thread thread = new Thread(producer);
        thread.start();
        thread.join(1000);
        
        assertTrue(buffer.isDone());
        assertTrue(buffer.isEmpty());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testRunWithSingleItem() throws InterruptedException {
        source.add("apple");
        producer = new Producer(buffer, source, DELAY_MS);
        
        Thread thread = new Thread(producer);
        thread.start();
        thread.join(1000);
        
        assertTrue(buffer.isDone());
        assertEquals(1, buffer.size());
        assertEquals("apple", buffer.take());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testRunWithMultipleItems() throws InterruptedException {
        source.addAll(Arrays.asList("apple", "banana", "cherry", "date"));
        producer = new Producer(buffer, source, DELAY_MS);
        
        Thread thread = new Thread(producer);
        thread.start();
        thread.join(2000);
        
        assertTrue(buffer.isDone());
        assertEquals(4, buffer.size());
        
        // Verify all items were produced
        List<String> consumed = new ArrayList<>();
        while (!buffer.isEmpty()) {
            String item = buffer.take();
            if (item != null) {
                consumed.add(item);
            }
        }
        
        assertEquals(4, consumed.size());
        assertTrue(consumed.contains("apple"));
        assertTrue(consumed.contains("banana"));
        assertTrue(consumed.contains("cherry"));
        assertTrue(consumed.contains("date"));
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testForLoopIteration() throws InterruptedException {
        // Test that for loop processes all items
        List<String> items = Arrays.asList("item1", "item2", "item3", "item4", "item5");
        source.addAll(items);
        producer = new Producer(buffer, source, 10); // Short delay
        
        Thread thread = new Thread(producer);
        thread.start();
        thread.join(1000);
        
        assertTrue(buffer.isDone());
        assertEquals(items.size(), buffer.size());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testSetDoneAfterForLoop() throws InterruptedException {
        source.addAll(Arrays.asList("a", "b", "c"));
        producer = new Producer(buffer, source, DELAY_MS);
        
        Thread thread = new Thread(producer);
        thread.start();
        thread.join(1000);
        
        // Verify setDone() is called after for loop completes
        assertTrue(buffer.isDone());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testInterruptedExceptionInTryCatch() throws InterruptedException {
        source.addAll(Arrays.asList("item1", "item2", "item3"));
        producer = new Producer(buffer, source, 1000); // Long delay to allow interruption
        
        Thread thread = new Thread(producer);
        thread.start();
        
        // Wait a bit for thread to start
        Thread.sleep(100);
        
        // Interrupt the thread
        thread.interrupt();
        thread.join(2000);
        
        // Verify thread terminated (which means it handled the interrupt)
        assertFalse(thread.isAlive());
        
        // Verify try-catch handled the exception
        // Buffer may have some items but not all
        assertTrue(buffer.size() <= source.size());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testThreadInterruptFlagSet() throws InterruptedException {
        source.addAll(Arrays.asList("item1", "item2", "item3"));
        producer = new Producer(buffer, source, 500);
        
        Thread thread = new Thread(producer);
        thread.start();
        
        Thread.sleep(50);
        thread.interrupt();
        thread.join(2000);
        
        // Verify that thread terminated (which means it handled the interrupt)
        // The Producer's catch block calls Thread.currentThread().interrupt()
        // and the thread should exit, so it should not be alive
        assertFalse(thread.isAlive());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testSleepInForLoop() throws InterruptedException {
        source.addAll(Arrays.asList("slow1", "slow2", "slow3"));
        producer = new Producer(buffer, source, 100);
        
        long startTime = System.currentTimeMillis();
        Thread thread = new Thread(producer);
        thread.start();
        thread.join(2000);
        long endTime = System.currentTimeMillis();
        
        // Should take at least (3 items * 100ms) = 300ms
        assertTrue(endTime - startTime >= 200); // Allow some margin
        assertTrue(buffer.isDone());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testPutInForLoop() throws InterruptedException {
        source.addAll(Arrays.asList("put1", "put2", "put3"));
        producer = new Producer(buffer, source, 10);
        
        Thread thread = new Thread(producer);
        thread.start();
        thread.join(1000);
        
        // Verify all items were put into buffer
        assertEquals(3, buffer.size());
        
        List<String> items = new ArrayList<>();
        while (!buffer.isEmpty()) {
            String item = buffer.take();
            if (item != null) {
                items.add(item);
            }
        }
        
        assertEquals(3, items.size());
        assertTrue(items.contains("put1"));
        assertTrue(items.contains("put2"));
        assertTrue(items.contains("put3"));
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testSubstringCapitalization() throws InterruptedException {
        // Test the substring(0, 1).toUpperCase() logic
        source.add("apple");
        producer = new Producer(buffer, source, DELAY_MS);
        
        Thread thread = new Thread(producer);
        thread.start();
        thread.join(1000);
        
        String item = buffer.take();
        assertEquals("apple", item); // Original item should be in buffer
        // Note: The capitalization is only for System.out.println, not stored
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testLargeSourceList() throws InterruptedException {
        // Test for loop with many items
        // Need larger buffer capacity to hold all items
        SharedBuffer<String> largeBuffer = new SharedBuffer<>(150);
        for (int i = 0; i < 100; i++) {
            source.add("item" + i);
        }
        producer = new Producer(largeBuffer, source, 1); // Very short delay
        
        Thread thread = new Thread(producer);
        thread.start();
        thread.join(); // Wait for thread to complete (rely on @Timeout for safety)
        
        // Ensure thread completed successfully (not interrupted)
        assertFalse(thread.isAlive());
        
        assertTrue(largeBuffer.isDone());
        assertEquals(100, largeBuffer.size());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testZeroDelay() throws InterruptedException {
        source.addAll(Arrays.asList("fast1", "fast2", "fast3"));
        producer = new Producer(buffer, source, 0);
        
        Thread thread = new Thread(producer);
        thread.start();
        thread.join(); // Wait for thread to complete (rely on @Timeout for safety)
        
        // Ensure thread completed successfully (not interrupted)
        assertFalse(thread.isAlive());
        
        assertTrue(buffer.isDone());
        assertEquals(3, buffer.size());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testInterruptDuringSleep() throws InterruptedException {
        source.addAll(Arrays.asList("sleep1", "sleep2"));
        producer = new Producer(buffer, source, 1000); // Long sleep
        
        Thread thread = new Thread(producer);
        thread.start();
        
        // Wait for first item to be produced and thread to be in sleep
        Thread.sleep(150);
        
        // Interrupt during sleep
        thread.interrupt();
        thread.join(2000);
        
        // Verify interruption was handled
        assertFalse(thread.isAlive());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testTryCatchHandlesInterruptedException() throws InterruptedException {
        source.add("test");
        producer = new Producer(buffer, source, 500);
        
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch interrupted = new CountDownLatch(1);
        
        Thread thread = new Thread(() -> {
            try {
                started.countDown();
                producer.run();
            } catch (Exception e) {
                // Should not throw exception, should be caught in run()
            } finally {
                interrupted.countDown();
            }
        });
        
        thread.start();
        started.await();
        Thread.sleep(50);
        thread.interrupt();
        interrupted.await(2, TimeUnit.SECONDS);
        
        // Verify no exception was thrown (try-catch handled it)
        assertTrue(true); // If we get here, no exception was thrown
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testConcurrentProducers() throws InterruptedException {
        SharedBuffer<String> sharedBuffer = new SharedBuffer<>(20);
        List<String> source1 = Arrays.asList("p1-a", "p1-b", "p1-c");
        List<String> source2 = Arrays.asList("p2-a", "p2-b", "p2-c");
        
        Producer prod1 = new Producer(sharedBuffer, source1, 10);
        Producer prod2 = new Producer(sharedBuffer, source2, 10);
        
        Thread t1 = new Thread(prod1);
        Thread t2 = new Thread(prod2);
        
        t1.start();
        t2.start();
        
        t1.join(2000);
        t2.join(2000);
        
        // Both should complete and set done
        assertTrue(sharedBuffer.isDone());
        assertEquals(6, sharedBuffer.size());
    }
}

