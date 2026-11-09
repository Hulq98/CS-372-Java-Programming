package edu.uj.cs372.Week3;

import java.util.ArrayList;
import java.util.HashMap;

public class ITManager {

    private HashMap<Integer, ITRequest> requests = new HashMap<>();
    private int nextId = 100;

    public void addRequest(String employeeName) {
        ITRequest request = new ITRequest(nextId, employeeName);
        requests.put(nextId, request);
        nextId++;
    }

    public void closeRequest(ITRequest request) {
        request.markClosed();
    }

    public ArrayList<ITRequest> getRequests() {
        return new ArrayList<>(requests.values());
    }
}