package org.eclipse.iofog.connector.restapi.response;

public class RemoveMappingResponse {
    String status;
    String id;
    long timestamp;

    public RemoveMappingResponse(String status, String id, long timestamp) {
        this.status = status;
        this.id = id;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
