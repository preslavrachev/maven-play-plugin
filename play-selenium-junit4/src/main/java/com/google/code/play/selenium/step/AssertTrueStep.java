package com.google.code.play.selenium.step;

import junit.framework.Assert;

import com.google.code.play.selenium.Step;

public class AssertTrueStep implements Step {

		protected SeleniumCommand innerCommand;

		public AssertTrueStep(SeleniumCommand innerCommand) {
			this.innerCommand = innerCommand;
		}

		public String execute() throws Exception {
			Assert.assertTrue(innerCommand.executeBoolean());
			return null;
		}

		public String toString() {
			String cmd = innerCommand.command.substring("is".length());
			return "assert" + cmd + "('" + innerCommand.param1 + "')";
		}

}

