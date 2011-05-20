package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.SeleneseTestCase;

import com.google.code.play.selenium.Step;

public class VerifyFalseStep
    implements Step
{

    public SeleneseTestCase seleneseTestCase;

    protected BooleanSeleniumCommand innerCommand;

    public VerifyFalseStep( SeleneseTestCase seleneseTestCase, BooleanSeleniumCommand innerCommand )
    {
        this.seleneseTestCase = seleneseTestCase;
        this.innerCommand = innerCommand;
    }

    public void execute()
        throws Exception
    {
        boolean innerCommandResult = innerCommand.getBoolean();
        seleneseTestCase.verifyFalse( innerCommandResult );
    }

    public String toString()
    {
        String cmd = innerCommand.command.substring( "is".length() );

        StringBuffer buf = new StringBuffer();
        buf.append( "verify" );
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
