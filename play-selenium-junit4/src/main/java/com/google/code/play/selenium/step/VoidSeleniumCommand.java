package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.CommandProcessor;

import com.google.code.play.selenium.StoredVars;

public class VoidSeleniumCommand
    extends AbstractSeleniumCommand
{

    public VoidSeleniumCommand( StoredVars storedVars, CommandProcessor commandProcessor, String command, String param1 )
    {
        super( storedVars, commandProcessor, command, param1 );
    }

    public VoidSeleniumCommand( StoredVars storedVars, CommandProcessor commandProcessor, String command,
                                String param1, String param2 )
    {
        super( storedVars, commandProcessor, command, param1, param2 );
    }

    public void execute()
        throws Exception
    {
        String xparam1 = param1;
        if ( xparam1 != null )
        {
            xparam1 = storedVars.changeBraces( param1 );
            xparam1 = xparam1.replaceAll( "<\\s*[bB][rR]\\s*/\\s*>", "\n" );// TODO-improve
        }
        String xparam2 = param2;
        if ( xparam2 != null )
        {
            xparam2 = storedVars.changeBraces( param2 );
            xparam2 = xparam2.replaceAll( "<\\s*[bB][rR]\\s*/\\s*>", "\n" );// TODO-improve
        }

        if ( param2 != null )
        {
            commandProcessor.doCommand( command, new String[] { xparam1, xparam2 } );
        }
        else if ( param1 != null )
        {
            commandProcessor.doCommand( command, new String[] { xparam1 } );
        }
        else
        {
            commandProcessor.doCommand( command, new String[] {} ); // czy ro moze sie zdarzyc?
        }
    }

}
