package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.SeleneseTestCase;

import com.google.code.play.selenium.Step;

public class VerifyEqualsStep
    implements Step
{

    public SeleneseTestCase seleneseTestCase;

    protected SeleniumCommand innerCommand;

    public String expected;

    public VerifyEqualsStep( SeleneseTestCase seleneseTestCase, SeleniumCommand innerCommand, String expected )
    {
        this.seleneseTestCase = seleneseTestCase;
        this.innerCommand = innerCommand;
        this.expected = expected;
    }

    public String execute()
        throws Exception
    {
        String innerCommandResult = innerCommand.execute();
        String xexpected = expected.replaceAll("<\\s*[bB][rR]\\s*/\\s*>", "\n");//TODO-improve
        boolean seleniumEqualsResult = SeleneseTestCase.seleniumEquals( xexpected, innerCommandResult );
        seleneseTestCase.verifyTrue( seleniumEqualsResult );
        return null;
    }

    public String toString()
    {
        String cmd = innerCommand.command.substring( "get".length() );
        return "verify" + cmd + "('" + innerCommand.param1
            + "', '" + expected + "')";
    }

}
