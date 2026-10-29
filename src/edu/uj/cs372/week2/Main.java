package edu.uj.cs372.week2;

//Github Repo link: https://github.com/Hulq98/CS-372-Java-Programming

public class Main {

    public static void main(String[] args) {
       
       
        ITRequestManager manager = new ITRequestManager();

        // Add requests
        manager.addRequest(new ITRequest(100, "John", "Open"));
        manager.addRequest(new ITRequest(101, "Sam", "Closed"));
        manager.addRequest(new ITRequest(102, "Michael", "Open"));

        //Finding requests
        System.out.println("Find Request 101");
        System.out.println(manager.findRequest(101));

        //Print Open Requests
        System.out.println("Open Requests: ");
         for (ITRequest request : manager.getOpenRequests()) {
            System.out.println(request);
        }

        //Count requests that are open
        System.out.println("All Open Requests:");
        System.out.println("Total Open Requests: " + manager.countOpenRequests());

}
}