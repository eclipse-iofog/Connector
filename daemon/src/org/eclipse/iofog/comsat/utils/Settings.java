/*
 * *******************************************************************************
 *  * Copyright (c) 2018 Edgeworx, Inc.
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Eclipse Public License v. 2.0 which is available at
 *  * http://www.eclipse.org/legal/epl-2.0
 *  *
 *  * SPDX-License-Identifier: EPL-2.0
 *  *******************************************************************************
 *
 */

package org.eclipse.iofog.comsat.utils;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class Settings {
	private static final Map<Integer, Boolean> validPorts = new HashMap<>();
	private static int brokerPort;
	private static String address;
	
	public static void loadSettings() throws Exception {
			JsonObject settings = Json.createReader(new FileInputStream(Constants.SETTINGS_FILENAME)).readObject();
			brokerPort = settings.getInt("broker");
			address = settings.getString("address");
			JsonArray ports = settings.getJsonArray("ports");
			validatePorts(ports);
			JsonArray excludePorts = settings.getJsonArray("exclude");
			validateExcludePorts(excludePorts);
	}

	private static void validatePorts(JsonArray ports) throws InvalidSettingException {
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
				throw new InvalidSettingException("Invalid port setting!");
			}
		}
	}

	private static void validateExcludePorts(JsonArray excludePorts) throws InvalidSettingException {
		for (int i = 0; i < excludePorts.size(); i++) {
			String[] exclude = excludePorts.getString(i).split("-");
			if (exclude.length == 2) {
				int from = tryParse(exclude[0]);
				int to = tryParse(exclude[1]);
				for (int j = from; j <= to; j++)
					validPorts.remove(j);
			} else if (exclude.length == 1) {
				int value = tryParse(exclude[0]);
				validPorts.remove(value);
			} else {
				throw new InvalidSettingException("Invalid exclude setting!");
			}
		}
	}
	
	private static int tryParse(String str) {
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

	public static String getAddress() {
		return address;
	}
}
