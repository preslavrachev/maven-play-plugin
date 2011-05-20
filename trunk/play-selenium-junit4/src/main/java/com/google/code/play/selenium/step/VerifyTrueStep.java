package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.SeleneseTestCase;

import com.google.code.play.selenium.Step;

public class VerifyTrueStep
    implements Step
{

    private SeleneseTestCase seleneseTestCase;

    private BooleanSeleniumCommand innerCommand;

    public VerifyTrueStep( SeleneseTestCase seleneseTestCase, BooleanSeleniumCommand innerCommand )
    {
        this.seleneseTestCase = seleneseTestCase;
        this.innerCommand = innerCommand;
    }

    public void execute()
        throws Exception
    {
        boolean innerCommandResult = innerCommand.getBoolean();
        seleneseTestCase.verifyTrue( innerCommandResult );
    }

    public String toString()
    {
        String cmd = innerCommand.command.substring( "is".length() );

        StringBuffer buf = new StringBuffer();
        buf.append( "verify" ).append( cmd ).append( "('" );
        buf.append( innerCommand.param1 ).append( "')" );
        return buf.toString();
    }

}
