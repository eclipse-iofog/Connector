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
import java.io.FileInputStream;

import static org.eclipse.iofog.comsat.utils.Constants.SETTINGS_FILENAME;

public class Settings {
	private static String address;

	public static void loadSettings() throws Exception {
			JsonObject settings = Json.createReader(new FileInputStream(SETTINGS_FILENAME)).readObject();
			address = settings.getString("address");
	}

	public static String getAddress() {
		return address;
	}
}
