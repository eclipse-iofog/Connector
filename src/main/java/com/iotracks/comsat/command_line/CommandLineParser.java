package com.iotracks.comsat.command_line;

import java.util.HashMap;
import java.util.Map;

import javax.json.JsonObject;

import com.iotracks.comsat.ComSat;
import com.iotracks.comsat.InstanceUtils;
import com.iotracks.comsat.utils.Constants;

public class CommandLineParser {

	public String parse(String command, String params) throws Exception {
		if (command.equals("stop")) {
			synchronized (ComSat.exitLock) {
				ComSat.exitLock.notifyAll();
			}
			return "ComSat stopped... :)";
		}
		
//		if (command.equals("reboot")) {
//			Runtime.getRuntime().exec("shutdown -r");
//			return "Rebooting in a minute";
//		}
//		
		return showHelp();
	}
	
	public boolean localParser(String... params) {
		if (params == null || params.length == 0 || params[0].equals("-h") || params[0].equals("help") || 
				params[0].equals("-?") || params[0].equals("--help")) {
			System.out.println(showHelp());
			return true;
		}
		
		if (params[0].equals("-v") || params[0].equals("--version")) {
			System.out.println(showVersion());
			return true;
		}
		
		String command = params[0];
		JsonObject response = isAnotherInstanceRunning(command, "");
		
		if (command.equals("start") && response != null) {
			System.out.println("ComSat is already running!");
			return true;
		} else if (command.equals("stop")) {
			if (response == null)
				System.out.println("ComSat is not running!");
			else
				System.out.println("Stopping ComSat...");
			return true;
		}
		
		return false;
	}
	
	private String showHelp() {
		return "Help!";
	}

	private String showVersion() {
		return "Version " + Constants.VERSION;
	}

	private JsonObject isAnotherInstanceRunning(String command, String param) {
        InstanceUtils instanceUtils = new InstanceUtils();
        Map<String, String> params = new HashMap<>();
        params.put("command", command);
        params.put("params", param);
        try {
        	JsonObject result = instanceUtils.sendHttpRequest("https://comsat4.iotracks.com" + Constants.API_COMMAND_LINE, params);
        	return result;
        } catch (Exception e) {
        	return null;
        }
	}
	
}
