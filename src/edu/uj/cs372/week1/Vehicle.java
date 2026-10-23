package edu.uj.cs372.week1;

public abstract class Vehicle {
    String make;
    String model;
    int year;

    public Vehicle(String make, String model, int year){
        this.make = make;
        this.model = model;
        this.year = year;
    }
    public void displayInfo(){
        System.out.println(year + " " + make + " " + model);

    }
    public void startEngine(){
        System.out.println("Vehicle starts");
    }
}