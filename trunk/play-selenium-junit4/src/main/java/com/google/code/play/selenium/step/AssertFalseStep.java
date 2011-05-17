package com.google.code.play.selenium.step;

import junit.framework.Assert;

import com.google.code.play.selenium.Step;

public class AssertFalseStep
    implements Step
{

    protected SeleniumCommand innerCommand;

    public AssertFalseStep( SeleniumCommand innerCommand )
    {
        this.innerCommand = innerCommand;
    }

    public String execute()
        throws Exception
    {
        boolean innerCommandResult = innerCommand.executeBoolean();
        Assert.assertFalse( innerCommandResult );
        return null;
    }

    public String toString()
    {
        String cmd = innerCommand.command.substring( "is".length() );
        if ( cmd.endsWith( "Present" ) )
        {
            cmd = cmd.replace( "Present", "NotPresent" );
        }
        else
        {
            cmd = "Not" + cmd;
        }
        return "assert" + cmd + "('" + innerCommand.param1 + "')";
    }

}
