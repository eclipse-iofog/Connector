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

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class CustomLogFormatter extends Formatter {

	@Override
	public String format(LogRecord log) {
		Date date = new Date(log.getMillis());
		String level = log.getLevel().getName();
		String logmessage = level + " - " + date.toString() + " : ";
		logmessage = logmessage + log.getMessage() + "\r\n" ;

		Throwable thrown = log.getThrown();
		if (thrown != null) {
			logmessage = logmessage+ thrown.toString();
		}
		return logmessage;
	}

}
