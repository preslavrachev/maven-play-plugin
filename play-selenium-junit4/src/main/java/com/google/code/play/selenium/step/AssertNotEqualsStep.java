package com.google.code.play.selenium.step;

import junit.framework.Assert;

import com.thoughtworks.selenium.SeleneseTestCase;

import com.google.code.play.selenium.Step;

public class AssertNotEqualsStep implements Step {

		protected SeleniumCommand innerCommand;
		public Object expected;

		public AssertNotEqualsStep(
				SeleniumCommand innerCommand,
				Object expected) {
			this.innerCommand = innerCommand;
			this.expected = expected;
		}

		public String execute() throws Exception {
            String innerCommandResult = innerCommand.execute();
            boolean seleniumEqualsResult = SeleneseTestCase.seleniumEquals(expected, innerCommandResult);
            Assert.assertFalse( seleniumEqualsResult);
            return null;
		}

		public String toString() {
			String cmd = innerCommand.command.substring("get".length());
			return "assertNot" + cmd + "('" + innerCommand.param1 + "' ,'"
					+ innerCommand.param2 + "')";
		}

}

