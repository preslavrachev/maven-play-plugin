package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.CommandProcessor;

import com.google.code.play.selenium.Step;

public class SeleniumCommand implements Step {

		public CommandProcessor commandProcessor;

		public String command;

		public String param1;

		public String param2;

		public SeleniumCommand(CommandProcessor commandProcessor,
				String command, String param1) {
			this.commandProcessor = commandProcessor;
			this.command = command;
			this.param1 = param1;
			this.param2 = null;
		}

		public SeleniumCommand(CommandProcessor commandProcessor,
				String command, String param1, String param2) {
			this(commandProcessor, command, param1);
			this.param2 = param2;
		}

		public String execute() throws Exception {
			String result = null;

			if (command.startsWith("is") || command.startsWith("get")) {// TODO-zrobic
																		// osobna
																		// klase?
				if (param2 != null) {
					result = commandProcessor.getString(command, new String[] {
							param1, param2 });
				} else if (param1 != null) {
					result = commandProcessor.getString(command,
							new String[] { param1 });
				} else {
					result = commandProcessor.getString(command,
							new String[] {}); // czy ro moze sie zdarzyc?
				}
			} else {
				if (param2 != null) {
					result = commandProcessor.doCommand(command, new String[] {
							param1, param2 });
				} else if (param1 != null) {
					result = commandProcessor.doCommand(command,
							new String[] { param1 });
				} else {
					result = commandProcessor.doCommand(command,
							new String[] {}); // czy ro moze sie zdarzyc?
				}
			}
			return result;
		}

		public boolean executeBoolean() throws Exception {
			String result = execute();
			boolean b;
			if ("true".equals(result)) {
				b = true;
				return b;
			}
			if ("false".equals(result)) {
				b = false;
				return b;
			}
			throw new RuntimeException(
					"result was neither 'true' nor 'false': " + result);
		}

		public String toString() {
			String result = null;
			if (param1 != null) {
				if (param2 != null) {
					result = command + "('" + param1 + "','" + param2 + "')";
				} else {
					result = command + "('" + param1 + "')";
				}
			} else {
				result = command + "()";
			}
			return result;
		}
}

