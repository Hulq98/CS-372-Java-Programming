package edu.uj.cs372.Week5.service;

import edu.uj.cs372.Week5.model.ITRequest;
import java.util.ArrayList;
import java.util.HashMap;

public class ITManager {

    private HashMap<Integer, ITRequest> requests = new HashMap<>();
    private int nextId = 100;

    // Returns the new request instead of relying
    // on the order of the HashMap.
    public ITRequest addRequest(String employeeName) {

        ITRequest request = new ITRequest(nextId, employeeName);

        requests.put(nextId, request);
        nextId++;

        return request;
    }

    public void closeRequest(ITRequest request) {
        request.markClosed();
    }

    public ArrayList<ITRequest> getRequests() {
        return new ArrayList<>(requests.values());
    }
}

