package com.google.code.play.selenium.step;

import com.google.code.play.selenium.Step;

public class CommandStep
    implements Step
{

    protected VoidSeleniumCommand command;

    public CommandStep( VoidSeleniumCommand command )
    {
        this.command = command;
    }

    public void execute()
        throws Exception
    {
        command.execute();
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append( command.command ).append( "(" );
        if ( command.param1 != null )
        {
            buf.append( "'" ).append( command.param1 ).append( "'" );
            if ( command.param2 != null )
            {
                buf.append( ", '" ).append( command.param2 ).append( "'" );
            }
        }
        buf.append( ")" );
        return buf.toString();
    }
}
