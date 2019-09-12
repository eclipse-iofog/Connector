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

package org.eclipse.iofog.connector.utils;

import javax.json.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

public class Settings {
	private static final Map<Integer, Boolean> validPorts = new HashMap<>();
	private static int brokerPort;
	private static String address;
	private static boolean devMode;

	public enum Setting {
		BROKER_PORT("broker") {
			@Override
			public void loadSetting(JsonObject settings) throws Exception {
				brokerPort = getValue(settings::getInt, getName());
			}
		},
		PORTS("ports") {
			@Override
			public void loadSetting(JsonObject settings) throws Exception {
				JsonArray ports = getValue(settings::getJsonArray, getName());
				validatePorts(ports);
			}
		},
		EXCLUDED_PORTS("exclude") {
			@Override
			public void loadSetting(JsonObject settings) throws Exception {
				JsonArray excludePorts = getValue(settings::getJsonArray, getName());
				validateExcludePorts(excludePorts);
			}
		},
		ADDRESS("address") {
			@Override
			public void loadSetting(JsonObject settings) throws Exception {
				address = getValue(settings::getString, getName());
			}
		},
		DEV_MODE("dev") {
			@Override
			public void loadSetting(JsonObject settings) throws Exception {
				devMode = getValue(settings::getBoolean, getName());
			}
		};

		private final String name;

		Setting(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public abstract void loadSetting(JsonObject settings) throws Exception;
	}
	
	public static void loadSettings() throws Exception {
			JsonObject settings = Json.createReader(new FileInputStream(Constants.SETTINGS_FILENAME)).readObject();
			Setting.BROKER_PORT.loadSetting(settings);
			Setting.ADDRESS.loadSetting(settings);
			Setting.DEV_MODE.loadSetting(settings);
			Setting.PORTS.loadSetting(settings);
			Setting.EXCLUDED_PORTS.loadSetting(settings);
	}

	public static void saveSettings(String setting, JsonValue value) throws Exception {
		JsonObject settings = Json.createReader(new FileInputStream(Constants.SETTINGS_FILENAME)).readObject();
		final JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder();
		for (Map.Entry<String, JsonValue> entry : settings.entrySet()) {
			if (entry.getKey().equals(setting)) {
				jsonObjectBuilder.add(setting, value);
			} else {
				jsonObjectBuilder.add(entry.getKey(), entry.getValue());
			}

		}
		JsonObject newSettings = jsonObjectBuilder.build();
		Json.createWriter(new FileOutputStream(Constants.SETTINGS_FILENAME)).writeObject(newSettings);
	}

	private static <T> T getValue(Function<String, T> extractor, String key) throws InvalidSettingException {
		try {
			return extractor.apply(key);
		} catch (NullPointerException ex) {
			throw new InvalidSettingException("Following setting is not presented in " + Constants.SETTINGS_FILENAME + ": " + key);
		}
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
		return address.concat(":").concat(devMode ? String.valueOf(Constants.HTTP_PORT) : String.valueOf(Constants.HTTPS_PORT));
	}

	public static boolean isDevMode(){
		return devMode;
	}

	public static long getNumberOfAvailablePorts() {
		return validPorts.values().stream().filter(v -> v.booleanValue()).count();
	}
}
