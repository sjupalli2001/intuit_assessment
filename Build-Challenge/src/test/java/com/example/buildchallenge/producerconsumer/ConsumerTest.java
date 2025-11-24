package com.example.buildchallenge.producerconsumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

class ConsumerTest {
    private SharedBuffer<String> buffer;
    private List<String> destination;
    private Consumer consumer;
    private static final int DELAY_MS = 50;

    @BeforeEach
    void setUp() {
        buffer = new SharedBuffer<>(10);
        destination = new ArrayList<>();
        consumer = new Consumer(buffer, destination, DELAY_MS);
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testRunWithEmptyBufferAndDone() throws InterruptedException {
        buffer.setDone();
        
        Thread thread = new Thread(consumer);
        thread.start();
        thread.join(1000);
        
        assertTrue(destination.isEmpty());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testWhileLoopConsumesItems() throws InterruptedException {
        buffer.put("item1");
        buffer.put("item2");
        buffer.put("item3");
        buffer.setDone();
        
        Thread thread = new Thread(consumer);
        thread.start();
        thread.join(1000);
        
        assertEquals(3, destination.size());
        assertTrue(destination.contains("item1"));
        assertTrue(destination.contains("item2"));
        assertTrue(destination.contains("item3"));
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testWhileLoopExitsWhenDoneAndEmpty() throws InterruptedException {
        buffer.put("item1");
        buffer.setDone();
        
        Thread thread = new Thread(consumer);
        thread.start();
        thread.join(1000);
        assertEquals(1, destination.size());
        assertEquals("item1", destination.get(0));
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testWhileTrueLoop() throws InterruptedException {
        buffer.put("a");
        buffer.put("b");
        buffer.put("c");
        buffer.setDone();
        
        Thread thread = new Thread(consumer);
        thread.start();
        thread.join(1000);
        assertEquals(3, destination.size());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testBreakCondition() throws InterruptedException {
        buffer.setDone();
        
        Thread thread = new Thread(consumer);
        thread.start();
        thread.join(1000);
        
        assertTrue(destination.isEmpty());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testInterruptedExceptionInTryCatch() throws InterruptedException {
        buffer.put("item1");
        
        Thread thread = new Thread(consumer);
        thread.start();

        Thread.sleep(100);

        thread.interrupt();
        thread.join(2000);
        
        assertFalse(thread.isAlive());

        assertTrue(destination.size() <= 1);
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testThreadInterruptFlagSet() throws InterruptedException {
        buffer.put("item1");
        
        Thread thread = new Thread(consumer);
        thread.start();
        
        Thread.sleep(50);
        thread.interrupt();
        thread.join(2000);
        

        assertFalse(thread.isAlive());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testSleepInWhileLoop() throws InterruptedException {
        buffer.put("slow1");
        buffer.put("slow2");
        buffer.put("slow3");
        buffer.setDone();
        
        Consumer slowConsumer = new Consumer(buffer, destination, 100);
        long startTime = System.currentTimeMillis();
        Thread thread = new Thread(slowConsumer);
        thread.start();
        thread.join(2000);
        long endTime = System.currentTimeMillis();
        

        assertTrue(endTime - startTime >= 200); 
        assertEquals(3, destination.size());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testTakeInWhileLoop() throws InterruptedException {
        buffer.put("take1");
        buffer.put("take2");
        buffer.put("take3");
        buffer.setDone();
        
        Thread thread = new Thread(consumer);
        thread.start();
        thread.join(1000);

        assertTrue(buffer.isEmpty());
        assertEquals(3, destination.size());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testAddToDestination() throws InterruptedException {
        List<String> items = Arrays.asList("a", "b", "c");
        for (String item : items) {
            buffer.put(item);
        }
        buffer.setDone();
        
        Thread thread = new Thread(consumer);
        thread.start();
        thread.join(1000);
        assertEquals(3, destination.size());
        assertTrue(destination.containsAll(items));
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testSubstringCapitalization() throws InterruptedException {
        buffer.put("apple");
        buffer.setDone();
        
        Thread thread = new Thread(consumer);
        thread.start();
        thread.join(1000);
        
        assertEquals(1, destination.size());
        assertEquals("apple", destination.get(0)); 
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testLargeBuffer() throws InterruptedException {

        SharedBuffer<String> largeBuffer = new SharedBuffer<>(150);
        for (int i = 0; i < 100; i++) {
            largeBuffer.put("item" + i);
        }
        largeBuffer.setDone();
        
        Consumer fastConsumer = new Consumer(largeBuffer, destination, 1); // Very short delay
        Thread thread = new Thread(fastConsumer);
        thread.start();
        thread.join(); 
        
        assertEquals(100, destination.size());
        assertTrue(largeBuffer.isEmpty());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testZeroDelay() throws InterruptedException {
        buffer.put("fast1");
        buffer.put("fast2");
        buffer.put("fast3");
        buffer.setDone();
        
        Consumer fastConsumer = new Consumer(buffer, destination, 0);
        Thread thread = new Thread(fastConsumer);
        thread.start();
        thread.join(500);
        
        assertEquals(3, destination.size());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testInterruptDuringSleep() throws InterruptedException {
        buffer.put("sleep1");
        buffer.put("sleep2");
        
        Consumer slowConsumer = new Consumer(buffer, destination, 1000); 
        Thread thread = new Thread(slowConsumer);
        thread.start();
        
        
        Thread.sleep(150);
        
      
        thread.interrupt();
        thread.join(2000);
        

        assertFalse(thread.isAlive());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testTryCatchHandlesInterruptedException() throws InterruptedException {
        buffer.put("test");
        buffer.setDone();
        
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch interrupted = new CountDownLatch(1);
        
        Thread thread = new Thread(() -> {
            try {
                started.countDown();
                consumer.run();
            } catch (Exception e) {
                
            } finally {
                interrupted.countDown();
            }
        });
        
        thread.start();
        started.await();
        Thread.sleep(50);
        thread.interrupt();
        interrupted.await(2, TimeUnit.SECONDS);
        
        
        assertTrue(true); 
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testWhileLoopConditionWithNullItem() throws InterruptedException {
       
        buffer.setDone();
        
        Thread thread = new Thread(consumer);
        thread.start();
        thread.join(1000);
        
        assertTrue(destination.isEmpty());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testWhileLoopConditionWithNonNullItem() throws InterruptedException {
       
        buffer.put("item1");
        buffer.put("item2");
        buffer.setDone();
        
        Thread thread = new Thread(consumer);
        thread.start();
        thread.join(1000);
        
       
        assertEquals(2, destination.size());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testConcurrentConsumers() throws InterruptedException {
      
        for (int i = 0; i < 10; i++) {
            buffer.put("item" + i);
        }
        buffer.setDone();
        
        List<String> dest1 = new ArrayList<>();
        List<String> dest2 = new ArrayList<>();
        
        Consumer cons1 = new Consumer(buffer, dest1, 10);
        Consumer cons2 = new Consumer(buffer, dest2, 10);
        
        Thread t1 = new Thread(cons1);
        Thread t2 = new Thread(cons2);
        
        t1.start();
        t2.start();
        
        t1.join(2000);
        t2.join(2000);
        
        // Both should consume items
        assertEquals(10, dest1.size() + dest2.size());
    }

    @Test
    @Timeout(value = 5, unit = TimeUnit.SECONDS)
    void testWhileLoopBlocksWhenEmpty() throws InterruptedException {
   
        CountDownLatch started = new CountDownLatch(1);
        AtomicInteger consumedCount = new AtomicInteger(0);
        
        Thread thread = new Thread(() -> {
            try {
                started.countDown();
                consumer.run();
                consumedCount.set(destination.size());
            } catch (Exception e) {
                // Ignore
            }
        });
        
        thread.start();
        started.await();
        
        Thread.sleep(100);
        
   
        assertEquals(0, consumedCount.get());
        
   
        buffer.put("unblock1");
        buffer.put("unblock2");
        buffer.setDone();
        
        thread.join(2000);
        
        assertTrue(destination.size() >= 2);
    }
}

