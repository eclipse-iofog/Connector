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

package org.eclipse.iofog.comsat;

import java.io.StringReader;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class InstanceUtils {
	
	public JsonObject sendHttpRequest(String url, Map<String, String> params) throws Exception {
		Form formData = new Form();
		params.forEach(formData::param);
		
//		Client client = ClientBuilder.newBuilder().sslContext(SslManager.getSSLContext()).build();
		Client client = ClientBuilder.newClient();
		Entity<Form> payload = Entity.form(formData);
		Response response = client.target(url)
		  .request(MediaType.APPLICATION_FORM_URLENCODED)
		  .header("Accept", "application/json")
		  .post(payload);

		String result = response.readEntity(String.class); 
		
		return Json.createReader(new StringReader(result)).readObject();
	}
	
}
