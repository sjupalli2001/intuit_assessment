package com.example.buildchallenge.producerconsumer;

import java.util.*;

public class ProducerConsumerMain {
    public static void main(String[] args) throws Exception {
        SharedBuffer<String> buffer = new SharedBuffer<>(5);
        List<String> src = List.of("apple","banana","Capsicum","Dates");
        List<String> dst = new ArrayList<>();
        Thread p = new Thread(new Producer(buffer, src, 100));
        Thread c = new Thread(new Consumer(buffer, dst, 150));
        p.start(); 
        c.start();
        p.join();
        c.join();
    }
}

