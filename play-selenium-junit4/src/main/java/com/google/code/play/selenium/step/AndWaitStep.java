package com.google.code.play.selenium.step;

import com.google.code.play.selenium.Step;

public class AndWaitStep
    implements Step
{

    protected VoidSeleniumCommand innerCommand;

    public AndWaitStep( VoidSeleniumCommand innerCommand )
    {
        this.innerCommand = innerCommand;
    }

    public void execute()
        throws Exception
    {
        innerCommand.execute();
        innerCommand.commandProcessor.doCommand( "waitForPageToLoad", new String[] { "30000" } );
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append( innerCommand.command ).append( "AndWait('" );
        if ( !"".equals( innerCommand.param1 ) )
        {
            buf.append( innerCommand.param1 );
            if ( !"".equals( innerCommand.param2 ) )
            {
                buf.append( "', '" ).append( innerCommand.param2 );
            }
        }
        buf.append( "')" );
        return buf.toString();
    }

}
