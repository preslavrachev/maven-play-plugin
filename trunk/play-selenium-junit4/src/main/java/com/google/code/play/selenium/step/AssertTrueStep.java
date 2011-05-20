package com.google.code.play.selenium.step;

import junit.framework.Assert;

import com.google.code.play.selenium.Step;

public class AssertTrueStep
    implements Step
{

    private BooleanSeleniumCommand innerCommand;

    public AssertTrueStep( BooleanSeleniumCommand innerCommand )
    {
        this.innerCommand = innerCommand;
    }

    public void execute()
        throws Exception
    {
        boolean innerCommandResult = innerCommand.getBoolean();
        Assert.assertTrue( innerCommandResult );
    }

    public String toString()
    {
        String cmd = innerCommand.command.substring( "is".length() );

        StringBuffer buf = new StringBuffer();
        buf.append( "assert" ).append( cmd ).append( "('" );
        buf.append( innerCommand.param1 ).append( "')" );
        return buf.toString();
    }

}
