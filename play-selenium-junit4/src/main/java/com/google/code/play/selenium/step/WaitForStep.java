package com.google.code.play.selenium.step;

import junit.framework.Assert;

import com.google.code.play.selenium.Step;

public class WaitForStep
    implements Step
{

    protected SeleniumCommand innerCommand;

    public WaitForStep( SeleniumCommand innerCommand )
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
                if ( innerCommand.executeBoolean() )
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
        return "waitFor" + cmd + "('" + innerCommand.param1
            + ( innerCommand.param2 != null ? "', '" + innerCommand.param2 : "" ) + "')";
    }

}
