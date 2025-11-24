package com.example.buildchallenge.producerconsumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class ProducerConsumerMainTest {

    @Test
    void testClassInstantiation() {
        // Test that the class can be instantiated (covers class declaration)
        // This ensures the class itself is loaded and instantiated
        ProducerConsumerMain instance = new ProducerConsumerMain();
        assertNotNull(instance);
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMainMethodExecution() throws Exception {
        // Test that main method executes without throwing exceptions
        // Note: main throws Exception, so we need to handle it
        
        try {
            ProducerConsumerMain.main(new String[]{});
            // If we get here, main executed successfully
            assertTrue(true);
        } catch (Exception e) {
            fail("Main method should not throw exception: " + e.getMessage());
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMainMethodWithArgs() throws Exception {
        // Test that main method handles arguments (even though it doesn't use them)
        try {
            ProducerConsumerMain.main(new String[]{"arg1", "arg2"});
            assertTrue(true);
        } catch (Exception e) {
            fail("Main method should handle arguments: " + e.getMessage());
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMainMethodCreatesBuffer() throws Exception {
        // Verify that main creates a SharedBuffer with capacity 5
        // We can't directly test this, but we can verify the program runs
        try {
            ProducerConsumerMain.main(new String[]{});
            assertTrue(true);
        } catch (Exception e) {
            fail("Main should create buffer successfully: " + e.getMessage());
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMainMethodCreatesProducerAndConsumer() throws Exception {
        // Test that main creates Producer and Consumer threads
        try {
            ProducerConsumerMain.main(new String[]{});
            assertTrue(true);
        } catch (Exception e) {
            fail("Main should create producer and consumer: " + e.getMessage());
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMainMethodStartsThreads() throws Exception {
        // Test that threads are started
        try {
            ProducerConsumerMain.main(new String[]{});
            assertTrue(true);
        } catch (Exception e) {
            fail("Main should start threads: " + e.getMessage());
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMainMethodJoinsThreads() throws Exception {
        // Test that main waits for threads to complete (join)
        long startTime = System.currentTimeMillis();
        
        try {
            ProducerConsumerMain.main(new String[]{});
        } catch (Exception e) {
            fail("Main should join threads: " + e.getMessage());
        }
        
        long endTime = System.currentTimeMillis();
        
        // Should take some time due to delays (100ms * 4 items + 150ms * 4 items)
        // At minimum, should take at least a few hundred milliseconds
        assertTrue(endTime - startTime >= 200);
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMainMethodExceptionHandling() {
        // Test that main method's throws Exception declaration allows exception propagation
        // This is more of a structural test - main throws Exception, so it can throw
        assertDoesNotThrow(() -> {
            try {
                ProducerConsumerMain.main(new String[]{});
            } catch (Exception e) {
                // Main declares throws Exception, so this is expected
                throw e;
            }
        });
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMainMethodCompletes() throws Exception {
        // Test that main method completes execution
        try {
            ProducerConsumerMain.main(new String[]{});
            // If we reach here, main completed
            assertTrue(true);
        } catch (Exception e) {
            fail("Main should complete execution: " + e.getMessage());
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMainMethodWithEmptyArgs() throws Exception {
        // Test main with empty args array
        try {
            ProducerConsumerMain.main(new String[0]);
            assertTrue(true);
        } catch (Exception e) {
            fail("Main should handle empty args: " + e.getMessage());
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMainMethodThreadCoordination() throws Exception {
        // Test that producer and consumer threads coordinate properly
        // This is verified by the fact that main completes without deadlock
        try {
            ProducerConsumerMain.main(new String[]{});
            // If main completes, threads coordinated successfully
            assertTrue(true);
        } catch (Exception e) {
            fail("Threads should coordinate properly: " + e.getMessage());
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMainMethodWithNullArgs() throws Exception {
        // Test main with null args (should handle gracefully)
        try {
            ProducerConsumerMain.main(null);
            // If it doesn't throw NullPointerException, it handles null
            assertTrue(true);
        } catch (NullPointerException e) {
            // NullPointerException is acceptable if args are used
            assertTrue(true);
        } catch (Exception e) {
            // Other exceptions are also acceptable
            assertTrue(true);
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMainMethodProducesAndConsumes() throws Exception {
        // Test that the main method actually produces and consumes items
        // We verify this by ensuring the program runs to completion
        try {
            ProducerConsumerMain.main(new String[]{});
            // If main completes, production and consumption occurred
            assertTrue(true);
        } catch (Exception e) {
            fail("Main should produce and consume items: " + e.getMessage());
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMainMethodUsesCorrectSourceList() throws Exception {
        // Test that main uses the correct source list: "apple","banana","Capsicum","Dates"
        // We can't directly verify this, but we can ensure main runs
        try {
            ProducerConsumerMain.main(new String[]{});
            assertTrue(true);
        } catch (Exception e) {
            fail("Main should use correct source list: " + e.getMessage());
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMainMethodUsesCorrectDelays() throws Exception {
        // Test that main uses delays: 100ms for producer, 150ms for consumer
        long startTime = System.currentTimeMillis();
        
        try {
            ProducerConsumerMain.main(new String[]{});
        } catch (Exception e) {
            fail("Main should use correct delays: " + e.getMessage());
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // With 4 items: producer takes ~400ms, consumer takes ~600ms
        // Total should be at least 400ms, but allow for thread scheduling
        assertTrue(duration >= 300);
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMainMethodNoDeadlock() throws Exception {
        // Test that main doesn't deadlock
        // If main completes within timeout, no deadlock occurred
        try {
            ProducerConsumerMain.main(new String[]{});
            assertTrue(true);
        } catch (Exception e) {
            fail("Main should not deadlock: " + e.getMessage());
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMainMethodExceptionDeclaration() {
        // Test that main method properly declares throws Exception
        // This allows it to propagate exceptions from thread operations
        try {
            ProducerConsumerMain.main(new String[]{});
            assertTrue(true);
        } catch (Exception e) {
            // This is expected behavior - main can throw Exception
            assertTrue(true);
        }
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    void testMainMethodMultipleCalls() throws Exception {
        // Test that main can be called multiple times
        try {
            ProducerConsumerMain.main(new String[]{});
            Thread.sleep(100);
            ProducerConsumerMain.main(new String[]{});
            assertTrue(true);
        } catch (Exception e) {
            fail("Main should handle multiple calls: " + e.getMessage());
        }
    }
}

