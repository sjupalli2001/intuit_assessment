package com.example.buildchallenge.producerconsumer;

import java.util.LinkedList;
import java.util.Queue;


/**
 * Thread-safe bounded buffer using synchronized blocks and wait/notify.
 */
public class SharedBuffer<T> {
    private final Queue<T> buffer;
    private final int capacity;
    private volatile boolean done = false;
    
    public SharedBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new LinkedList<>();
    }
    
    public synchronized void put(T item) throws InterruptedException {
        while (buffer.size() >= capacity) {
            wait();   // Wait if buffer full
        }
        buffer.offer(item);
        notifyAll();   // Notify waiting consumers
    }
    
    public synchronized T take() throws InterruptedException {
        while (buffer.isEmpty() && !done) {
            wait();  // Wait if buffer empty and production ongoing
        }
        if (buffer.isEmpty() && done) {
            return null;  // Signal end of consumption
        }
        T item = buffer.poll();
        notifyAll();   // Notify waiting producers
        return item;
    }
    
    public synchronized void setDone() {
        this.done = true;
        notifyAll();  // Wake up all waiting threads
    }
    
    public boolean isDone() {
        return done;
    }
    
    public synchronized boolean isEmpty() {
        return buffer.isEmpty();
    }
    
    public synchronized int size() {
        return buffer.size();
    }
}
