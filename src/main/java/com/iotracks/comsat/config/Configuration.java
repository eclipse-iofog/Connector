package com.iotracks.comsat.config;

import javax.json.JsonObject;

/**
 * Created by Saeid on 6/25/2016.
 */
public class Configuration {
    private String id;
    private int port1;
    private int port2;
    private int maxConnections1;
    private int maxConnections2;
    private String passCode1;
    private String passCode2;
    private int heartBeatThreshold1;
    private int heartBeatThreshold2;

    public Configuration() {}

    public Configuration(String id, int port1, int maxConnections1, String passCode1, int heartBeatThreshold1) {
        this.id = id;
        this.port1 = port1;
        this.maxConnections1 = maxConnections1;
        this.passCode1 = passCode1;
        this.heartBeatThreshold1 = heartBeatThreshold1;
    }

    public Configuration(String id, int port1, int port2, int maxConnections1, int maxConnections2, String passCode1, String passCode2, int heartBeatThreshold1, int heartBeatThreshold2) {
        this.id = id;
        this.port1 = port1;
        this.port2 = port2;
        this.maxConnections1 = maxConnections1;
        this.maxConnections2 = maxConnections2;
        this.passCode1 = passCode1;
        this.passCode2 = passCode2;
        this.heartBeatThreshold1 = heartBeatThreshold1;
        this.heartBeatThreshold2 = heartBeatThreshold2;
    }
    
    public Configuration(JsonObject config) throws Exception {
  		this.id = config.getString("id");
        this.port1 = tryParse(config.getString("port1"), 0);
        this.port2 = tryParse(config.getString("port2"), 0);
        this.maxConnections1 = tryParse(config.getString("maxconnectionsport1"), 0);
        this.maxConnections2 = tryParse(config.getString("maxconnectionsport2"), 0);
        this.passCode1 = config.getString("passcodeport1");
        this.passCode2 = config.getString("passcodeport2");
        this.heartBeatThreshold1 = tryParse(config.getString("heartbeatabsencethresholdport1"), 0);
        this.heartBeatThreshold2 = tryParse(config.getString("heartbeatabsencethresholdport2"), 0);
    }
    
    public static int tryParse(String str, int defaultValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
			return defaultValue;
		}
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPort1() {
        return port1;
    }

    public void setPort1(int port1) {
        this.port1 = port1;
    }

    public int getPort2() {
        return port2;
    }

    public void setPort2(int port2) {
        this.port2 = port2;
    }

    public int getMaxConnections1() {
        return maxConnections1;
    }

    public void setMaxConnections1(int maxConnections1) {
        this.maxConnections1 = maxConnections1;
    }

    public int getMaxConnections2() {
        return maxConnections2;
    }

    public void setMaxConnections2(int maxConnections2) {
        this.maxConnections2 = maxConnections2;
    }

    public String getPassCode1() {
        return passCode1;
    }

    public void setPassCode1(String passCode1) {
        this.passCode1 = passCode1;
    }

    public String getPassCode2() {
        return passCode2;
    }

    public void setPassCode2(String passCode2) {
        this.passCode2 = passCode2;
    }

    public int getHeartBeatThreshold1() {
        return heartBeatThreshold1;
    }

    public void setHeartBeatThreshold1(int heartBeatThreshold1) {
        this.heartBeatThreshold1 = heartBeatThreshold1;
    }

    public int getHeartBeatThreshold2() {
        return heartBeatThreshold2;
    }

    public void setHeartBeatThreshold2(int heartBeatThreshold2) {
        this.heartBeatThreshold2 = heartBeatThreshold2;
    }
    
//    public String toString() {
//    	return String.format("Configuration: Port1: %d, Port2: %d, MaxConnectin1: %d, MaxConnection2: %d, Passcode1: %s, Passcode2: %s, Threshold1: %d, Threshold2: %d",
//    			this.id, this.port1, this.port2, this.maxConnections1, this.maxConnections2, this.passCode1, this.passCode2, this.heartBeatThreshold1, this.heartBeatThreshold2); 
//    }
}
