package com.google.code.play.selenium.step;

import junit.framework.Assert;

import com.google.code.play.selenium.Step;

public class WaitForFalseStep
    implements Step
{

    private BooleanSeleniumCommand innerCommand;

    public WaitForFalseStep( BooleanSeleniumCommand innerCommand )
    {
        this.innerCommand = innerCommand;
    }

    public void execute()
        throws Exception
    {
        for ( int second = 0;; second++ )
        {
            if ( second >= 60 )
                Assert.fail( "timeout" );
            try
            {
                boolean innerCommandResult = !innerCommand.getBoolean();
                if ( innerCommandResult )
                    break;
            }
            catch ( Exception e )
            {
            }
            Thread.sleep( 1000 );
        }
    }

    public String toString()
    {
        String cmd = innerCommand.command.substring( "is".length() );

        StringBuffer buf = new StringBuffer();
        buf.append( "waitFor" );
        if ( cmd.endsWith( "Present" ) )
        {
            buf.append( cmd.replace( "Present", "NotPresent" ) );
        }
        else
        {
            buf.append( "Not" ).append( cmd );
        }
        buf.append( "('" ).append( innerCommand.param1 ).append( "')" );
        return buf.toString();
    }

}
