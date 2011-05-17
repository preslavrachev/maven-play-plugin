package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.SeleneseTestCase;

import com.google.code.play.selenium.Step;

public class VerifyNotEqualsStep
    implements Step
{

    public SeleneseTestCase seleneseTestCase;

    protected SeleniumCommand innerCommand;

    public String expected;

    public VerifyNotEqualsStep( SeleneseTestCase seleneseTestCase, SeleniumCommand innerCommand, String expected )
    {
        this.seleneseTestCase = seleneseTestCase;
        this.innerCommand = innerCommand;
        this.expected = expected;
    }

    public String execute()
        throws Exception
    {
        String innerCommandResult = innerCommand.execute();
        boolean seleniumNotEqualsResult = NotEqualsHelper.seleniumNotEquals( expected, innerCommandResult );
        seleneseTestCase.verifyTrue( seleniumNotEqualsResult );
        return null;
    }

    public String toString()
    {
        String cmd = innerCommand.command.substring( "get".length() );
        return "verifyNot" + cmd + "('" + innerCommand.param1
            + ( innerCommand.param2 != null ? "' ,'" + innerCommand.param2 : "" ) + "')";
    }

}
