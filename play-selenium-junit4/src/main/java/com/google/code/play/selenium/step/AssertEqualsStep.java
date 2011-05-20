package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.SeleneseTestCase;

import junit.framework.Assert;

import com.google.code.play.selenium.Step;

public class AssertEqualsStep
    implements Step
{

    protected SeleniumCommand innerCommand;

    public String expected;

    public AssertEqualsStep( SeleniumCommand innerCommand, String expected )
    {
        this.innerCommand = innerCommand;
        this.expected = expected;
    }

    public String execute()
        throws Exception
    {
        String innerCommandResult = innerCommand.execute();
        String xexpected = expected.replaceAll("<\\s*[bB][rR]\\s*/\\s*>", "\n");//TODO-improve
        boolean seleniumEqualsResult = SeleneseTestCase.seleniumEquals( xexpected, innerCommandResult );
        Assert.assertTrue( seleniumEqualsResult );
        return null;
    }

    public String toString()
    {
        String cmd = innerCommand.command.substring( "get".length() );
        return "assert" + cmd + "('" + innerCommand.param1 + "', '" + expected + "')";
    }

}
