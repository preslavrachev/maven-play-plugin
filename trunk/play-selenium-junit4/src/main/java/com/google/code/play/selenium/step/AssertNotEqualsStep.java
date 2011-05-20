package com.google.code.play.selenium.step;

import junit.framework.Assert;

import com.google.code.play.selenium.Step;

public class AssertNotEqualsStep
    implements Step
{

    private StringSeleniumCommand innerCommand;

    private String expected;

    public AssertNotEqualsStep( StringSeleniumCommand innerCommand, String expected )
    {
        this.innerCommand = innerCommand;
        this.expected = expected;
    }

    public void execute()
        throws Exception
    {
        String innerCommandResult = innerCommand.getString();
        String xexpected = MultiLineHelper.brToNewLine( expected );
        boolean seleniumNotEqualsResult = NotEqualsHelper.seleniumNotEquals( xexpected, innerCommandResult );
        Assert.assertTrue( seleniumNotEqualsResult );
    }

    public String toString()
    {
        String cmd = innerCommand.command.substring( "get".length() );

        StringBuffer buf = new StringBuffer();
        buf.append( "assertNot" ).append( cmd ).append( "('" );
        buf.append( innerCommand.param1 ).append( "', '" ).append( expected ).append( "')" );
        return buf.toString();
    }

}
