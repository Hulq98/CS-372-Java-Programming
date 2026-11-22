package edu.uj.cs372.Week5.model;

// Stores the additional information for each request.
public class RequestData {

    public ITRequest request;
    public String name;
    public String title;
    public String description;
    public boolean completed;

    public RequestData(ITRequest request, String name,
                       String title, String description,
                       boolean completed) {

        this.request = request;
        this.name = name;
        this.title = title;
        this.description = description;
        this.completed = completed;
    }
}
