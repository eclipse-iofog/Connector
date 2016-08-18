package com.iotracks.comsat.utils;

import java.util.logging.FileHandler;
import java.util.logging.Logger;

public class LogUtil {
	private static Logger logger;
	
	static { 
		logger = Logger.getLogger("comsat");
		FileHandler fh;  
		try {
			fh = new FileHandler(Constants.LOG_FILENAME);   
			fh.setFormatter(new CustomLogFormatter());
			logger.addHandler(fh);
			logger.setUseParentHandlers(false);
		} catch (Exception e) {
			logger.warning(e.getMessage());
		}
	}
	
	public static void warning(String msg) {
		logger.warning(msg);
	}
	
	public static void info(String msg) {
		logger.info(msg);
	}

}
