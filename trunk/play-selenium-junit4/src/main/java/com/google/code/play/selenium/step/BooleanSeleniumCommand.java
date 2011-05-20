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
        if ( !"".equals( xparam1 ) )
        {
            xparam1 = storedVars.changeBraces( param1 );
            // not needed here
            // xparam1 = MultiLineHelper.brToNewLine( xparam1 );
        }
        String xparam2 = param2;
        if ( !"".equals( xparam2 ) )
        {
            xparam2 = storedVars.changeBraces( param2 );
            // not needed here
            // xparam2 = MultiLineHelper.brToNewLine( xparam2 );
        }

        if ( !"".equals( param2 ) )
        {
            result = commandProcessor.getBoolean( command, new String[] { xparam1, xparam2 } );
        }
        else if ( !"".equals( param1 ) )
        {
            result = commandProcessor.getBoolean( command, new String[] { xparam1 } );
        }
        else
        {
            result = commandProcessor.getBoolean( command, new String[] {} ); // czy to moze sie zdarzyc?
        }
        return result;
    }

}
