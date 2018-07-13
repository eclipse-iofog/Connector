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
