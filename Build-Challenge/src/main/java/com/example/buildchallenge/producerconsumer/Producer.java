package com.example.buildchallenge.producerconsumer;

import java.util.List;
/**
 * Producer thread that places items from source list into shared buffer.
 */
public class Producer implements Runnable {
    private final SharedBuffer<String> buffer;
    private final List<String> source;
    private final int delayMs;
    
    public Producer(SharedBuffer<String> buffer, List<String> source, int delayMs) {
        this.buffer = buffer;
        this.source = source;
        this.delayMs = delayMs;
    }
    
    @Override
    public void run() {
        try {
            for (String item : source) {
                buffer.put(item);  // May block if buffer is full
                String capitalized = item.substring(0, 1).toUpperCase() + item.substring(1);
                System.out.println("Produced: " + capitalized);
                Thread.sleep(delayMs);
            }
            buffer.setDone();   // Signal production complete
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

