package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.CommandProcessor;

import com.google.code.play.selenium.StoredVars;

public class StringSeleniumCommand
    extends AbstractSeleniumCommand
{

    public StringSeleniumCommand( StoredVars storedVars, CommandProcessor commandProcessor, String command,
                                  String param1 )
    {
        super( storedVars, commandProcessor, command, param1 );
    }

    public StringSeleniumCommand( StoredVars storedVars, CommandProcessor commandProcessor, String command,
                                  String param1, String param2 )
    {
        super( storedVars, commandProcessor, command, param1, param2 );
    }

    public String getString()
        throws Exception
    {
        String result = null;

        String xparam1 = param1;
        if ( xparam1 != null )
        {
            xparam1 = storedVars.changeBraces( param1 );
            // not needed here
            // xparam1 = MultiLineHelper.brToNewLine( xparam1 );
        }
        String xparam2 = param2;
        if ( xparam2 != null )
        {
            xparam2 = storedVars.changeBraces( param2 );
            // not needed here
            // xparam2 = MultiLineHelper.brToNewLine( xparam2 );
        }

        if ( !"".equals( param2 ) )
        {
            result = commandProcessor.getString( command, new String[] { xparam1, xparam2 } );
        }
        else if ( !"".equals( param1 ) )
        {
            result = commandProcessor.getString( command, new String[] { xparam1 } );
        }
        else
        {
            result = commandProcessor.getString( command, new String[] {} ); // czy to moze sie zdarzyc?
        }
        return result;
    }

}
