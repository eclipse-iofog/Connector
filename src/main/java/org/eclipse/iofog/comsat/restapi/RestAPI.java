package org.eclipse.iofog.comsat.restapi;

public class RestAPI {
	
	private static RestAPI instance = null;
	private RestAPIServer apiServer;
	
	public static RestAPI getInstance() {
		if (instance == null) {
			synchronized (RestAPI.class) {
				if (instance == null) 
					instance = new RestAPI();
			}
		}
		
		return instance;
	}
	
	public void start() {
        apiServer = RestAPIServer.getInstance();
        new Thread(apiServer).start();
	}
	
	public void stop() {
		if (apiServer != null) 
			apiServer.stop();
	}

}
