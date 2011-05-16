package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.SeleneseTestCase;

import com.google.code.play.selenium.Step;

public class AssertNotEqualsStep implements Step {

		// public SeleneseTestCase seleneseTestCase;
		protected SeleniumCommand innerCommand;
		public Object expected;

		public AssertNotEqualsStep(
				/* SeleneseTestCase seleneseTestCase, */SeleniumCommand innerCommand,
				Object expected) {
			// this.seleneseTestCase = seleneseTestCase;
			this.innerCommand = innerCommand;
			this.expected = expected;
		}

		public String execute() throws Exception {
			SeleneseTestCase.assertNotEquals(expected, innerCommand.execute());// TODO-brak
																				// obslugi
																				// regexp
																				// itd.
			return null;
		}

		public String toString() {
			String cmd = innerCommand.command.substring("get".length());
			return "assertNot" + cmd + "('" + innerCommand.param1 + "' ,'"
					+ innerCommand.param2 + "')";
		}

}

