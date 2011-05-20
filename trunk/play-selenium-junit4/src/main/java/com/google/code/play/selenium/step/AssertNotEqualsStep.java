package com.google.code.play.selenium.step;

import junit.framework.Assert;

import com.google.code.play.selenium.Step;

public class AssertNotEqualsStep
    implements Step
{

    protected SeleniumCommand innerCommand;

    public String expected;

    public AssertNotEqualsStep( SeleniumCommand innerCommand, String expected )
    {
        this.innerCommand = innerCommand;
        this.expected = expected;
    }

    public String execute()
        throws Exception
    {
        String innerCommandResult = innerCommand.execute();
        String xexpected = expected.replaceAll("<\\s*[bB][rR]\\s*/\\s*>", "\n");//TODO-improve
        boolean seleniumNotEqualsResult = NotEqualsHelper.seleniumNotEquals( xexpected, innerCommandResult );
        Assert.assertTrue( seleniumNotEqualsResult );
        return null;
    }

    public String toString()
    {
        String cmd = innerCommand.command.substring( "get".length() );
        return "assertNot" + cmd + "('" + innerCommand.param1 + "', '" + expected + "')";
    }

}
