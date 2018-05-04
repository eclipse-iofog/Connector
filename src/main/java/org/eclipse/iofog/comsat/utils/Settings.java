package main.java.org.eclipse.iofog.comsat.utils;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;

public class Settings {
	private static Map<Integer, Boolean> validPorts = new HashMap<>();
	private static int brokerPort;
	
	public static void loadSettings() throws Exception {
		JsonObject settings = Json.createReader(new FileInputStream(Constants.SETTINGS_FILENAME)).readObject();
		brokerPort = settings.getInt("broker");
		JsonArray ports = settings.getJsonArray("ports");
		for (int i = 0; i < ports.size(); i++) {
			String[] port = ports.getString(i).split("-");
			if (port.length == 2) {
				int from = tryParse(port[0]);
				int to = tryParse(port[1]);
				for (int j = from; j <= to; j++)
					validPorts.put(j, true);
			} else if (port.length == 1) {
				int value = tryParse(port[0]);
				validPorts.put(value, true);
			} else {
				throw new Exception("Invalid port setting!");
			}
		}

		JsonArray excludes = settings.getJsonArray("exclude");
		for (int i = 0; i < excludes.size(); i++) {
			String[] exclude = excludes.getString(i).split("-");
			if (exclude.length == 2) {
				int from = tryParse(exclude[0]);
				int to = tryParse(exclude[1]);
				for (int j = from; j <= to; j++)
					validPorts.remove(j);
			} else if (exclude.length == 1) {
				int value = tryParse(exclude[0]);
				validPorts.remove(value);
			} else {
				throw new Exception("Invalid exclude setting!");
			}
		}
	}
	
	private static int tryParse(String str) throws Exception {
		return Integer.parseInt(str);
	}
	 
	public static int getNextFreePort() {
		synchronized (validPorts) {
			for (Entry<Integer, Boolean> e: validPorts.entrySet()) {
				if (e.getValue()) {
					validPorts.put(e.getKey(), false);
					return e.getKey();
				}
			}
		}
		
		return -1;
	}

	public static void setPortAvailable(int port) {
		synchronized (validPorts) {
			validPorts.put(port, true);
		}
	}
	
	public static void setPortInUse(int port) {
		synchronized (validPorts) {
			validPorts.put(port, false);
		}
	}

	public static int getBrokerPort() {
		return brokerPort;
	}
}
