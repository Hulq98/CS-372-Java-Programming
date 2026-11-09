
package edu.uj.cs372.Week3;

public class ITRequest {
    private int id;
    private String employeeName;
    private String status;

    public ITRequest(int id, String employeeName) {
        this.id = id;
        this.employeeName = employeeName;
        this.status = "Open";
    }

    public void markClosed() {
        status = "Closed";
    }

    @Override
    public String toString() {
        return id + " - " + employeeName + " - " + status;
    }
}
