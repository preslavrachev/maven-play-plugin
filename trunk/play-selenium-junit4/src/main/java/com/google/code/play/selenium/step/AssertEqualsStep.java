package com.google.code.play.selenium.step;

import junit.framework.Assert;

import com.google.code.play.selenium.Step;

public class AssertEqualsStep
    implements Step
{

    private StringSeleniumCommand innerCommand;

    private String expected;

    public AssertEqualsStep( StringSeleniumCommand innerCommand, String expected )
    {
        this.innerCommand = innerCommand;
        this.expected = expected;
    }

    public void execute()
        throws Exception
    {
        String innerCommandResult = innerCommand.getString();
        String xexpected = MultiLineHelper.brToNewLine( expected );
        boolean seleniumEqualsResult = EqualsHelper.seleniumEquals( xexpected, innerCommandResult );
        Assert.assertTrue( seleniumEqualsResult );
    }

    public String toString()
    {
        String cmd = innerCommand.command.substring( "get".length() );

        StringBuffer buf = new StringBuffer();
        buf.append( "assert" ).append( cmd ).append( "('" );
        buf.append( innerCommand.param1 ).append( "', '" ).append( expected ).append( "')" );
        return buf.toString();
    }

}
