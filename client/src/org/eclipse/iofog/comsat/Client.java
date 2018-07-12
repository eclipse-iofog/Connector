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

import org.eclipse.iofog.comsat.utils.CommandLineAction;
import org.eclipse.iofog.comsat.utils.LogUtil;
import org.eclipse.iofog.comsat.utils.Settings;

import java.text.ParseException;

public class Client {

	public static void main(String[] args) throws ParseException {
		try {
			Settings.loadSettings();

			String message = args == null || args.length == 0
					? CommandLineAction.HELP_ACTION.perform(args)
					: CommandLineAction.getActionByKey(args[0]).perform(args);

			System.out.println(message);

		} catch (Exception ex) {
			LogUtil.warning(ex.getMessage());
		}
	}
}
