package org.eclipse.iofog.connector.restapi.response;

public class NewMappingResponse {
    String status;
    String id;
    int port1;
    int port2;
    String passcode1;
    String passcode2;
    long timestamp;

    public NewMappingResponse(String status, String id, int port1, int port2, String passcode1, String passcode2, long timestamp) {
        this.status = status;
        this.id = id;
        this.port1 = port1;
        this.port2 = port2;
        this.passcode1 = passcode1;
        this.passcode2 = passcode2;
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public int getPort1() {
        return port1;
    }

    public int getPort2() {
        return port2;
    }

    public String getPasscode1() {
        return passcode1;
    }

    public String getPasscode2() {
        return passcode2;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
