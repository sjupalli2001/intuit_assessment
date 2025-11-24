package com.example.buildchallenge.producerconsumer;

import java.util.*;

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
            wait();
        }
        buffer.offer(item);
        notifyAll();
    }
    
    public synchronized T take() throws InterruptedException {
        while (buffer.isEmpty() && !done) {
            wait();
        }
        if (buffer.isEmpty() && done) {
            return null;
        }
        T item = buffer.poll();
        notifyAll();
        return item;
    }
    
    public synchronized void setDone() {
        this.done = true;
        notifyAll();
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

