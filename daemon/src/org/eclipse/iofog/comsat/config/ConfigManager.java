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

package org.eclipse.iofog.comsat.config;

import org.eclipse.iofog.comsat.exceptions.DuplicateIdException;
import org.eclipse.iofog.comsat.utils.Constants;
import org.eclipse.iofog.comsat.utils.LogUtil;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.NotFoundException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Saeid on 6/25/2016.
 */
public class ConfigManager {

    private static Map<String, Configuration> configRepository = new HashMap<>();
    private static Map<String, DirectConnection> directRequestsRepository = new HashMap<>();
    private static int directsCount = 0;
    
    public static synchronized void loadConfiguration() {

        try {
            JsonReader in = Json.createReader(new FileInputStream(Constants.CONFIG_FILENAME));
            JsonObject configsObject = in.readObject();
            JsonArray mappingsArray = configsObject.getJsonArray("mappings");

            for (int i = 0; i < mappingsArray.size(); i++) {
                JsonObject mapping = mappingsArray.getJsonObject(i);

                Configuration config = new Configuration();
                config.setId(mapping.getString("id"));
                config.setPort1(mapping.getInt("port1"));
                config.setPort2(mapping.getInt("port2"));
                config.setMaxConnections1(mapping.getInt("maxconnectionsport1"));
                config.setMaxConnections2(mapping.getInt("maxconnectionsport2"));
                config.setPassCode1(mapping.getString("passcodeport1"));
                config.setPassCode2(mapping.getString("passcodeport2"));
                config.setHeartBeatThreshold1(mapping.getInt("heartbeatabsencethresholdport1"));
                config.setHeartBeatThreshold2(mapping.getInt("heartbeatabsencethresholdport2"));

                if (config.getPort1() == 0) {
					directsCount++;
				}
                configRepository.put(config.getId(), config);
            }
        } catch (Exception e) {
            LogUtil.warning(e.getMessage());
        }
    }
    
    public static synchronized void saveConfiguration() throws Exception {
    	JsonArrayBuilder mappingsArrayBuilder = Json.createArrayBuilder();
    	configRepository.forEach((id, config) -> {
    		JsonObject mapping = Json.createObjectBuilder()
    				.add("id", config.getId())
    				.add("port1", config.getPort1())
    				.add("port2", config.getPort2())
    				.add("maxconnectionsport1", config.getMaxConnections1())
    				.add("maxconnectionsport2", config.getMaxConnections1())
    				.add("passcodeport1", config.getPassCode1())
    				.add("passcodeport2", config.getPassCode2())
    				.add("heartbeatabsencethresholdport1", config.getHeartBeatThreshold1())
    				.add("heartbeatabsencethresholdport2", config.getHeartBeatThreshold2())
    				.build();
    		
    		mappingsArrayBuilder.add(mapping);
    	});
    	
    	JsonArray mappingsArray = mappingsArrayBuilder.build();
        JsonObject mappingsObject = Json.createObjectBuilder()
        		.add("mappings", mappingsArray)
        		.build();
        Json.createWriter(new FileOutputStream(Constants.CONFIG_FILENAME)).writeObject(mappingsObject);
    }    

    public static synchronized void addMapping(Configuration config) throws Exception {
    	if (configRepository.containsKey(config.getId()))
    		throw new DuplicateIdException();
        if (config.getPort1() == 0)
        	directsCount++;
        configRepository.put(config.getId(), config);
        
        saveConfiguration();
        if (config.getPort1() == 0)
        	directsCount++;
    }
    
    public static synchronized boolean isDirectValid(String id, String passKey) {
    	Configuration config = configRepository.get(id);
    	return config != null && config.getPort1() == 0 && config.getPassCode1().equals(passKey);
    }
    
    public static synchronized void putDirectRequest(String directId, DirectConnection directConnection) {
    	directRequestsRepository.put(directId, directConnection);
    }
    
    public static synchronized DirectConnection getDirectConnection(String directId) {
    	return directRequestsRepository.remove(directId);
    }

    public static synchronized void removeMapping(String mappingId) throws Exception {
    	if (!configRepository.containsKey(mappingId)) {
			throw new NotFoundException("invalid id");
		}
        Configuration config = configRepository.remove(mappingId);
        
        saveConfiguration();
        if (config.getPort1() == 0)
        	directsCount--;
    }
    
    public static synchronized int getDirectsCount() {
		return directsCount;
	}

	public static synchronized Map<String, Configuration> getMappings() {
    	return configRepository;
    }
    
    public static synchronized void compareMappings(JsonArray mappings) throws Exception {
    	boolean modified = false;
    	List<String> mappingIds = new ArrayList<>();
    	for (int i = 0; i < mappings.size(); i++) {
    		JsonObject mapping = mappings.getJsonObject(i);
    		String id = mapping.getString("id");
    		if (id == null || id.trim().isEmpty())
    			continue;
    		if (!configRepository.containsKey(id)) { 
    			configRepository.put(id, new Configuration(mapping));
    			modified = true;
    		}
    		mappingIds.add(id);
    	}
    	
    	Map<String, Configuration> temp = new HashMap<>();
    	for (String i: configRepository.keySet()) {
    		if (mappingIds.contains(i))
    			temp.put(i, configRepository.get(i));
    		else
    			modified = true;
    	}
    	configRepository = temp;
    	
    	if (modified)
            saveConfiguration();
    }


}
