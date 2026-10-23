package edu.uj.cs372.week1;

/*
Short reflection: 

I chose vehicle for my base class because cars and trucks have a lot of common information like make model and year. 
I created car and truck has my child classes from vehicle because it gave me the ability to have inherited information, even though they are different types of vehicles and also have their own behavior. 
I overrode this in each class. I made vehicle an abstract class because it let me share information to the child classes and methods without creating a vehicle object. 
I demonstrated polymorphism through displaying different messages with println for each type of vehicle. 
One design trade-off that I made was keeping subclasses simple and not adding a lot of different methods and attributes. 
In the future, I might be able expand on this like the number of doors for cars or the ability to tow through boolean values. 


Github Repo link: https://github.com/Hulq98/CS-372-Java-Programming
*/

public class Main{
    public static void main(String[] args){


        Vehicle[] vehicles = {
            new Car("Ford", "Mustang", 2007),
            new Truck("Ford", "F150", 2013),
            new Car("Honda", "Civic", 2004)

        };

        for (Vehicle vehicle : vehicles){
            vehicle.displayInfo();
            vehicle.startEngine();
            System.out.println();
        }

    }

}