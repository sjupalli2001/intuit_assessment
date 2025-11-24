package com.example.buildchallenge.producerconsumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SharedBufferTest {
    private SharedBuffer<String> buffer;
    private static final int CAPACITY = 5;

    @BeforeEach
    void setUp() {
        buffer = new SharedBuffer<>(CAPACITY);
    }

    @Test
    void testConstructor() {
        assertNotNull(buffer);
        assertTrue(buffer.isEmpty());
        assertEquals(0, buffer.size());
        assertFalse(buffer.isDone());
    }

    @Test
    void testPutAndTakeSingleItem() throws InterruptedException {
        String item = "test";
        buffer.put(item);
        assertEquals(1, buffer.size());
        assertFalse(buffer.isEmpty());
        
        String taken = buffer.take();
        assertEquals(item, taken);
        assertTrue(buffer.isEmpty());
        assertEquals(0, buffer.size());
    }

    @Test
    void testPutMultipleItems() throws InterruptedException {
        for (int i = 0; i < CAPACITY; i++) {
            buffer.put("item" + i);
        }
        assertEquals(CAPACITY, buffer.size());
        assertFalse(buffer.isEmpty());
    }

    @Test
    void testTakeMultipleItems() throws InterruptedException {
        for (int i = 0; i < CAPACITY; i++) {
            buffer.put("item" + i);
        }
        
        for (int i = 0; i < CAPACITY; i++) {
            String item = buffer.take();
            assertNotNull(item);
            assertEquals("item" + i, item);
        }
        assertTrue(buffer.isEmpty());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testPutBlocksWhenFull() throws InterruptedException {
        // Fill buffer to capacity
        for (int i = 0; i < CAPACITY; i++) {
            buffer.put("item" + i);
        }
        
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger putCount = new AtomicInteger(0);
        
        // Thread that tries to put when buffer is full (should block)
        Thread putter = new Thread(() -> {
            try {
                latch.countDown();
                buffer.put("blocked-item");
                putCount.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        putter.start();
        latch.await(); // Wait for thread to start
        
        // Give thread time to attempt put and block
        Thread.sleep(100);
        
        // Verify thread is blocked (putCount should still be 0)
        assertEquals(0, putCount.get());
        
        // Take one item to unblock
        buffer.take();
        
        // Wait for put to complete
        putter.join(2000);
        
        // Verify put eventually succeeded
        assertEquals(1, putCount.get());
        assertEquals(CAPACITY, buffer.size());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testTakeBlocksWhenEmpty() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger takeCount = new AtomicInteger(0);
        List<String> takenItems = new ArrayList<>();
        
        // Thread that tries to take when buffer is empty (should block)
        Thread taker = new Thread(() -> {
            try {
                latch.countDown();
                String item = buffer.take();
                if (item != null) {
                    takenItems.add(item);
                    takeCount.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        taker.start();
        latch.await(); // Wait for thread to start
        
        // Give thread time to attempt take and block
        Thread.sleep(100);
        
        // Verify thread is blocked (takeCount should still be 0)
        assertEquals(0, takeCount.get());
        
        // Put one item to unblock
        buffer.put("unblock-item");
        
        // Wait for take to complete
        taker.join(2000);
        
        // Verify take eventually succeeded
        assertEquals(1, takeCount.get());
        assertEquals(1, takenItems.size());
        assertEquals("unblock-item", takenItems.get(0));
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testTakeReturnsNullWhenDoneAndEmpty() throws InterruptedException {
        buffer.setDone();
        assertTrue(buffer.isDone());
        
        String item = buffer.take();
        assertNull(item);
        assertTrue(buffer.isEmpty());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testTakeDoesNotBlockWhenDoneAndEmpty() throws InterruptedException {
        buffer.setDone();
        
        // This should return null immediately without blocking
        long startTime = System.currentTimeMillis();
        String item = buffer.take();
        long endTime = System.currentTimeMillis();
        
        assertNull(item);
        // Should return quickly (less than 100ms)
        assertTrue(endTime - startTime < 100);
    }

    @Test
    void testSetDone() {
        assertFalse(buffer.isDone());
        buffer.setDone();
        assertTrue(buffer.isDone());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testMultipleProducersAndConsumers() throws InterruptedException {
        int numProducers = 3;
        int numConsumers = 2;
        int itemsPerProducer = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numProducers + numConsumers);
        CountDownLatch producerLatch = new CountDownLatch(numProducers);
        CountDownLatch consumerLatch = new CountDownLatch(numConsumers);
        List<String> consumedItems = new ArrayList<>();
        
        // Start producers
        for (int i = 0; i < numProducers; i++) {
            final int producerId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < itemsPerProducer; j++) {
                        buffer.put("producer" + producerId + "-item" + j);
                    }
                    producerLatch.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Start consumers
        for (int i = 0; i < numConsumers; i++) {
            executor.submit(() -> {
                try {
                    while (true) {
                        String item = buffer.take();
                        if (item == null && buffer.isDone()) {
                            break;
                        }
                        if (item != null) {
                            synchronized (consumedItems) {
                                consumedItems.add(item);
                            }
                        }
                    }
                    consumerLatch.countDown();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        // Wait for all producers to finish
        producerLatch.await(5, TimeUnit.SECONDS);
        buffer.setDone();
        
        // Wait for all consumers to finish
        consumerLatch.await(5, TimeUnit.SECONDS);
        
        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.SECONDS);
        
        // Verify all items were consumed
        assertEquals(numProducers * itemsPerProducer, consumedItems.size());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testInterruptedExceptionOnPut() throws InterruptedException {
        // Fill buffer to capacity
        for (int i = 0; i < CAPACITY; i++) {
            buffer.put("item" + i);
        }
        
        AtomicInteger exceptionCaught = new AtomicInteger(0);
        Thread putter = new Thread(() -> {
            try {
                buffer.put("blocked");
            } catch (InterruptedException e) {
                // Verify thread was interrupted - catching InterruptedException proves interruption occurred
                // Note: interrupt status is cleared when InterruptedException is thrown
                exceptionCaught.incrementAndGet();
                Thread.currentThread().interrupt(); // Restore interrupt status
            }
        });
        
        putter.start();
        Thread.sleep(100); // Let it block
        putter.interrupt();
        putter.join(1000);
        
        // Verify InterruptedException was caught (proves interruption occurred)
        assertEquals(1, exceptionCaught.get());
        // Verify buffer is still at capacity
        assertEquals(CAPACITY, buffer.size());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testInterruptedExceptionOnTake() throws InterruptedException {
        AtomicInteger exceptionCaught = new AtomicInteger(0);
        Thread taker = new Thread(() -> {
            try {
                buffer.take();
            } catch (InterruptedException e) {
                // Verify thread was interrupted - catching InterruptedException proves interruption occurred
                // Note: interrupt status is cleared when InterruptedException is thrown
                exceptionCaught.incrementAndGet();
                Thread.currentThread().interrupt(); // Restore interrupt status
            }
        });
        
        taker.start();
        Thread.sleep(100); // Let it block
        taker.interrupt();
        taker.join(1000);
        
        // Verify InterruptedException was caught (proves interruption occurred)
        assertEquals(1, exceptionCaught.get());
        // Verify buffer is still empty
        assertTrue(buffer.isEmpty());
    }

    @Test
    void testSizeAndIsEmpty() throws InterruptedException {
        assertTrue(buffer.isEmpty());
        assertEquals(0, buffer.size());
        
        buffer.put("item1");
        assertFalse(buffer.isEmpty());
        assertEquals(1, buffer.size());
        
        buffer.put("item2");
        assertEquals(2, buffer.size());
        
        buffer.take();
        assertEquals(1, buffer.size());
        
        buffer.take();
        assertTrue(buffer.isEmpty());
        assertEquals(0, buffer.size());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testWhileLoopInPut() throws InterruptedException {
        // Test that while loop correctly waits when buffer is full
        // Fill buffer completely
        for (int i = 0; i < CAPACITY; i++) {
            buffer.put("fill" + i);
        }
        
        CountDownLatch putLatch = new CountDownLatch(1);
        AtomicInteger successfulPuts = new AtomicInteger(0);
        
        // Thread that will be blocked by while loop
        Thread blockedPutter = new Thread(() -> {
            try {
                putLatch.countDown();
                // This should block in the while loop
                buffer.put("blocked-put");
                successfulPuts.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        blockedPutter.start();
        putLatch.await();
        Thread.sleep(100);
        
        // Verify blocked (while loop is waiting)
        assertEquals(0, successfulPuts.get());
        
        // Remove one item to exit while loop
        buffer.take();
        
        // Wait for put to complete
        blockedPutter.join(2000);
        assertEquals(1, successfulPuts.get());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testWhileLoopInTake() throws InterruptedException {
        CountDownLatch takeLatch = new CountDownLatch(1);
        AtomicInteger successfulTakes = new AtomicInteger(0);
        
        // Thread that will be blocked by while loop
        Thread blockedTaker = new Thread(() -> {
            try {
                takeLatch.countDown();
                // This should block in the while loop
                String item = buffer.take();
                if (item != null) {
                    successfulTakes.incrementAndGet();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        blockedTaker.start();
        takeLatch.await();
        Thread.sleep(100);
        
        // Verify blocked (while loop is waiting)
        assertEquals(0, successfulTakes.get());
        
        // Add one item to exit while loop
        buffer.put("unblock-item");
        
        // Wait for take to complete
        blockedTaker.join(2000);
        assertEquals(1, successfulTakes.get());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testWhileLoopInTakeWithDoneFlag() throws InterruptedException {
        // Test while loop condition: while (buffer.isEmpty() && !done)
        CountDownLatch takeLatch = new CountDownLatch(1);
        AtomicInteger takeResult = new AtomicInteger(-1); // -1 = not completed, 0 = null, 1 = item
        
        Thread taker = new Thread(() -> {
            try {
                takeLatch.countDown();
                String item = buffer.take();
                if (item == null) {
                    takeResult.set(0);
                } else {
                    takeResult.set(1);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        taker.start();
        takeLatch.await();
        Thread.sleep(100);
        
        // Set done flag - should exit while loop
        buffer.setDone();
        
        taker.join(2000);
        // Should return null when done and empty
        assertEquals(0, takeResult.get());
    }
}

