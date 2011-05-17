package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.SeleneseTestCase;

import com.google.code.play.selenium.Step;

public class VerifyTrueStep implements Step {

		public SeleneseTestCase seleneseTestCase;

		protected SeleniumCommand innerCommand;

		public VerifyTrueStep(SeleneseTestCase seleneseTestCase,
				SeleniumCommand innerCommand) {
			this.seleneseTestCase = seleneseTestCase;
			this.innerCommand = innerCommand;
		}

		public String execute() throws Exception {
            boolean innerCommandResult = innerCommand.executeBoolean();
            seleneseTestCase.verifyTrue(innerCommandResult);
			return null;
		}

		public String toString() {
			String cmd = innerCommand.command.substring("is".length());
			return "verify" + cmd + "('" + innerCommand.param1 + "')";
		}

}

