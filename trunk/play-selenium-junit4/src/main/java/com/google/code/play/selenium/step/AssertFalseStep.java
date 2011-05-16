package com.google.code.play.selenium.step;

import junit.framework.Assert;

import com.google.code.play.selenium.Step;

public class AssertFalseStep implements Step {

		protected SeleniumCommand innerCommand;

		public AssertFalseStep(SeleniumCommand innerCommand) {
			this.innerCommand = innerCommand;
		}

		public String execute() throws Exception {
			Assert.assertFalse(innerCommand.executeBoolean());
			return null;
		}

		public String toString() {
			String cmd = innerCommand.command.substring("is".length());
			return "assertNot" + cmd + "('" + innerCommand.param1 + "')";
		}

}

