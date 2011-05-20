package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.CommandProcessor;

import com.google.code.play.selenium.StoredVars;

public class BooleanSeleniumCommand
    extends AbstractSeleniumCommand
{

    public BooleanSeleniumCommand( StoredVars storedVars, CommandProcessor commandProcessor, String command,
                                   String param1 )
    {
        super( storedVars, commandProcessor, command, param1 );
    }

    public BooleanSeleniumCommand( StoredVars storedVars, CommandProcessor commandProcessor, String command,
                                   String param1, String param2 )
    {
        super( storedVars, commandProcessor, command, param1, param2 );
    }

    public boolean getBoolean()
        throws Exception
    {
        boolean result = false;

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

        if ( param2 != null )// FIXME- null or ""?
        {
            result = commandProcessor.getBoolean( command, new String[] { xparam1, xparam2 } );
        }
        else if ( param1 != null )// FIXME- null or ""?
        {
            result = commandProcessor.getBoolean( command, new String[] { xparam1 } );
        }
        else
        {
            result = commandProcessor.getBoolean( command, new String[] {} ); // czy ro moze sie zdarzyc?
        }
        return result;
    }

}
