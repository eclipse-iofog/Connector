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
import javax.json.JsonObject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.format;
import static org.eclipse.iofog.comsat.utils.Constants.API_COMMAND_LINE;
import static org.eclipse.iofog.comsat.utils.Settings.getAddress;
import static org.eclipse.iofog.comsat.utils.Settings.isDevMode;

public class InstanceUtils {
	public static boolean isAnotherInstanceRunning() {
		boolean result;
		try {
			Map<String, String> params = new HashMap<>();
			params.put("command", "status");
			params.put("params", "");
			sendHttpRequest(format("%s://%s%s", isDevMode()? Constants.HTTP : Constants.HTTPS, getAddress(), API_COMMAND_LINE), params);
			result = true;
		} catch (Exception e) {
			result = false;
		}
		return result;
	}

	public static String sendCommandlineParameters(String... args) {
		String result;
		try {
			Map<String, String> params = new HashMap<>();
			params.put("command", args[0]);
			params.put("params", "");
			Response response = sendHttpRequest(format("%s://%s%s", isDevMode()? Constants.HTTP : Constants.HTTPS, getAddress(), API_COMMAND_LINE), params);
			String entity = response.readEntity(String.class);
			JsonObject jsonObject = Json.createReader(new StringReader(entity)).readObject();
			result = jsonObject.getString("response");
		} catch (Exception ex) {
			LogUtil.warning(ex.getMessage());
			result = ex.getMessage();
		}
		return result;
	}

	private static Response sendHttpRequest(String url, Map<String, String> params) throws Exception {
		Form formData = new Form();
		params.forEach(formData::param);

		ClientBuilder clientBuilder = ClientBuilder.newBuilder();
		if (!isDevMode()){
			clientBuilder.sslContext(SslManager.getSSLContext());
		}
		javax.ws.rs.client.Client client = clientBuilder.build();
		Entity<Form> payload = Entity.form(formData);

		return client.target(url)
				.request(MediaType.APPLICATION_FORM_URLENCODED)
				.header("Accept", "application/json")
				.post(payload);
	}
}
