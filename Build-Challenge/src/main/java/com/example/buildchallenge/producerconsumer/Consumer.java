package com.example.buildchallenge.producerconsumer;

import java.util.List;

public class Consumer implements Runnable {
    private final SharedBuffer<String> buffer;
    private final List<String> destination;
    private final int delayMs;
    
    public Consumer(SharedBuffer<String> buffer, List<String> destination, int delayMs) {
        this.buffer = buffer;
        this.destination = destination;
        this.delayMs = delayMs;
    }
    
    @Override
    public void run() {
        try {
            while (true) {
                String item = buffer.take();
                if (item == null && buffer.isDone()) {
                    break;
                }
                destination.add(item);
                String capitalized = item.substring(0, 1).toUpperCase() + item.substring(1);
                System.out.println("Consumed: " + capitalized);
                Thread.sleep(delayMs);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

