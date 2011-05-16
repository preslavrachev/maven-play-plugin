package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.SeleneseTestCase;

import com.google.code.play.selenium.Step;

public class AssertEqualsStep implements Step {

		// public SeleneseTestCase seleneseTestCase;
		protected SeleniumCommand innerCommand;
		public Object expected;

		public AssertEqualsStep(
				/* SeleneseTestCase seleneseTestCase, */SeleniumCommand innerCommand,
				Object expected) {
			// this.seleneseTestCase = seleneseTestCase;
			this.innerCommand = innerCommand;
			this.expected = expected;
		}

		public String execute() throws Exception {
			SeleneseTestCase.seleniumEquals(expected, innerCommand.execute());
			return null;
		}

		public String toString() {
			String innerCmd = "get"
					+ innerCommand.command.substring("get".length());
			return "assert" + innerCmd + "('" + innerCommand.param1 + "' ,'"
					+ innerCommand.param2 + "')";
		}

}

