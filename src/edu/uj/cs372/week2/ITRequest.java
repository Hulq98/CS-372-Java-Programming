package edu.uj.cs372.week2;

public class ITRequest {
    
    private int requestID;
    private String employeeName;
    private String status;


    public ITRequest(int requestID, String employeeName, String status){
        this.requestID = requestID;
        this.employeeName = employeeName;
        this.status = status;
    }

    public int getRequestID(){
        return requestID;
    }

    public String getStatus(){
        return status;
    }

    @Override 
    public String toString(){
        return "Request ID: " + requestID +
            ", Employee: " + employeeName +
            ", Status: " + status;
    }
}
