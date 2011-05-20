package com.google.code.play.selenium.step;

import junit.framework.Assert;

import com.google.code.play.selenium.Step;

public class WaitForFalseStep
    implements Step
{

    protected SeleniumCommand innerCommand;

    public WaitForFalseStep( SeleniumCommand innerCommand )
    {
        this.innerCommand = innerCommand;
    }

    public String execute()
        throws Exception
    {
        for ( int second = 0;; second++ )
        {
            if ( second >= 60 )
                Assert.fail( "timeout" );
            try
            {
                boolean innerCommandResult = !innerCommand.executeBoolean();
                if ( innerCommandResult )
                    break;
            }
            catch ( Exception e )
            {
            }
            Thread.sleep( 1000 );
        }
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
        return "waitFor" + cmd + "('" + innerCommand.param1
            + ( innerCommand.param2 != null ? "', '" + innerCommand.param2 : "" ) + "')";
    }

}
