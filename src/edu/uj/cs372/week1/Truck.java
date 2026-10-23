package edu.uj.cs372.week1;

public class Truck extends Vehicle {

    public Truck(String make, String model, int year) {
        super(make, model, year);
    }

    @Override
    public void startEngine() {
        System.out.println("Truck starts");
    }
}