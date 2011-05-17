package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.SeleneseTestCase;

import junit.framework.Assert;

import com.google.code.play.selenium.Step;

public class AssertEqualsStep
    implements Step
{

    protected SeleniumCommand innerCommand;

    public Object expected;

    public AssertEqualsStep( SeleniumCommand innerCommand, Object expected )
    {
        this.innerCommand = innerCommand;
        this.expected = expected;
    }

    public String execute()
        throws Exception
    {
        String innerCommandResult = innerCommand.execute();
        boolean seleniumEqualsResult = SeleneseTestCase.seleniumEquals( expected, innerCommandResult );
        Assert.assertTrue( seleniumEqualsResult );
        return null;
    }

    public String toString()
    {
        String cmd = innerCommand.command.substring( "get".length() );
        return "assert" + cmd + "('" + innerCommand.param1 + "' ,'" + innerCommand.param2 + "')";
    }

}
