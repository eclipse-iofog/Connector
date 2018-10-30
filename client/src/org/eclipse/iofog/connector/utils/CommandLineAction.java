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

package org.eclipse.iofog.connector.utils;

import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.eclipse.iofog.connector.utils.InstanceUtils.isAnotherInstanceRunning;

public enum CommandLineAction {

	STOP_ACTION {
		@Override
		public List<String> getKeys() {
			return singletonList("stop");
		}

		@Override
		public String perform(String[] args) {
			return isAnotherInstanceRunning() ? "Enter \"service iofog-connector stop\"" : "Connector is already stopped.";
		}
	},
	START_ACTION {
		@Override
		public List<String> getKeys() {
			return singletonList("start");
		}

		@Override
		public String perform(String[] args) {
			return isAnotherInstanceRunning() ? "Connector is already running." : "Enter \"service connector start\"";
		}
	},
	STATUS_ACTION {
		@Override
		public List<String> getKeys() {
			return singletonList("status");
		}

		@Override
		public String perform(String[] args) {
			return isAnotherInstanceRunning() ? "Connector is up and running." : "Connector is stopped.";
		}
	},
	HELP_ACTION {
		@Override
		public List<String> getKeys() {
			return asList("help", "--help", "-h", "-?");
		}

		@Override
		public String perform(String[] args) {
			return CommandLineAction.showHelp();
		}
	},
	VERSION_ACTION {
		@Override
		public List<String> getKeys() {
			return asList("version", "--version", "-v");
		}

		@Override
		public String perform(String[] args) {
			return CommandLineAction.showVersion();
		}
	};

	public abstract List<String> getKeys();

	public abstract String perform(String[] args);

	public static CommandLineAction getActionByKey(String cmdKey) {
		return Arrays.stream(values())
				.filter(action -> action.getKeys().contains(cmdKey))
				.findAny()
				.orElse(HELP_ACTION);
	}

	private static String showHelp() {

		return ("Usage: iofog-connector [OPTION]\n\n"
				+ "Option           GNU long option         Meaning\n"
				+ "======           ===============         =======\n"
				+ "-h, -?           --help                  Show this message\n"
				+ "-v               --version               Display the software version and\n"
				+ "                                         license information\n" + "\n" + "\n"
				+ "Command          Arguments               Meaning\n"
				+ "=======          =========               =======\n"
				+ "help                                     Show this message\n"
				+ "version                                  Display the software version and\n"
				+ "                                         license information\n"
				+ "status                                   Display current status information\n"
				+ "                                         about the software\n"
				+ "Report bugs to: edgemaster@iofog.org\n" + "ioFog home page: http://iofog.org\n"
				+ "For users with Eclipse accounts, report bugs to: https://bugs.eclipse.org/bugs/enter_bug.cgi?product=iofog");
	}

	private static String showVersion() {
		return "Connector " + Constants.VERSION +
				"\nCopyright (C) 2018 Edgeworx, Inc." +
				"\nEclipse ioFog is provided under the Eclipse Public License (EPL2)" +
				"\nhttps://www.eclipse.org/legal/epl-v20.html";
	}
}
