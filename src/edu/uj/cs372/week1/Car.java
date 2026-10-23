package edu.uj.cs372.week1;

public class Car extends Vehicle {

    public Car(String make, String model, int year) {
        super(make, model, year);
    }

    @Override
    public void startEngine() {
        System.out.println("Car starts");
    }
}