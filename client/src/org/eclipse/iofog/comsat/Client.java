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
