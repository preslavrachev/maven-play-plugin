package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.SeleneseTestCase;

import com.google.code.play.selenium.Step;

public class VerifyFalseStep implements Step {

		public SeleneseTestCase seleneseTestCase;

		protected SeleniumCommand innerCommand;

		public VerifyFalseStep(SeleneseTestCase seleneseTestCase,
				SeleniumCommand innerCommand) {
			this.seleneseTestCase = seleneseTestCase;
			this.innerCommand = innerCommand;
		}

		public String execute() throws Exception {
			seleneseTestCase.verifyFalse(innerCommand.executeBoolean());
			return null;
		}

		public String toString() {
			String cmd = innerCommand.command.substring("is".length());
			return "verifyNot" + cmd + "('" + innerCommand.param1 + "')";
		}

}

