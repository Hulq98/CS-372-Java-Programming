package edu.uj.cs372.week2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ITRequestManager {

    private HashMap<Integer, ITRequest> requests = new HashMap<>();

    // Add request
    public void addRequest(ITRequest request) {
        if (requests.containsKey(request.getRequestID())) {
            System.out.println("Request ID already exists.");
            return;
        }
        requests.put(request.getRequestID(), request);
    }

    // Find request by ID
    public ITRequest findRequest(int id) {
        return requests.get(id);
    }

    // Filter requests by status of open
    public List<ITRequest> getOpenRequests() {
        List<ITRequest> openRequests = new ArrayList<>();

        for (ITRequest request : requests.values()) {
            if (request.getStatus().equalsIgnoreCase("Open")) {
                openRequests.add(request);
            }
        }

        return openRequests;
    }

    // Count requests that are open 
    public int countOpenRequests() {
        return getOpenRequests().size();
    }
}