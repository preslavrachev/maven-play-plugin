package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.SeleneseTestCase;

import com.google.code.play.selenium.Step;

public class VerifyNotEqualsStep implements Step {

		public SeleneseTestCase seleneseTestCase;
		protected SeleniumCommand innerCommand;
		public Object expected;

		public VerifyNotEqualsStep(SeleneseTestCase seleneseTestCase,
				SeleniumCommand innerCommand, Object expected) {
			this.seleneseTestCase = seleneseTestCase;
			this.innerCommand = innerCommand;
			this.expected = expected;
		}

		public String execute() throws Exception {
			seleneseTestCase.verifyNotEquals(expected, innerCommand.execute());// TODO-brak
																				// obslugi
																				// regexp
																				// itd.
			return null;
		}

		public String toString() {
			String innerCmd = innerCommand.command.substring("get".length());
			return "verifyNot"
					+ innerCmd
					+ "('"
					+ innerCommand.param1
					+ (innerCommand.param2 != null ? "' ,'"
							+ innerCommand.param2 : "") + "')";
		}

}

