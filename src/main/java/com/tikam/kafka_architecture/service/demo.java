package com.tikam.kafka_architecture.service;

public class demo {
    public static void main(String[] args) {
        AddCalculator cal = (int a, int b) -> a + b;;
        cal.add(10,20);
    }
}


@FunctionalInterface
interface  AddCalculator {
    abstract  int add(int a, int b);
}
