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

public class InstanceUtils {
	public static boolean isAnotherInstanceRunning() {
		boolean result;
		try {
			Map<String, String> params = new HashMap<>();
			params.put("command", "status");
			params.put("params", "");
			sendHttpRequest(format("https://%s%s", getAddress(), API_COMMAND_LINE), params);
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
			Response response = sendHttpRequest(format("https://%s%s", getAddress(), API_COMMAND_LINE), params);
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

		javax.ws.rs.client.Client client = ClientBuilder.newBuilder().sslContext(SslManager.getSSLContext()).build();
		Entity<Form> payload = Entity.form(formData);

		return client.target(url)
				.request(MediaType.APPLICATION_FORM_URLENCODED)
				.header("Accept", "application/json")
				.post(payload);
	}
}
