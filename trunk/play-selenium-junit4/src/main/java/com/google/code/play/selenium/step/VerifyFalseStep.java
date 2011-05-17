package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.SeleneseTestCase;

import com.google.code.play.selenium.Step;

public class VerifyFalseStep
    implements Step
{

    public SeleneseTestCase seleneseTestCase;

    protected SeleniumCommand innerCommand;

    public VerifyFalseStep( SeleneseTestCase seleneseTestCase, SeleniumCommand innerCommand )
    {
        this.seleneseTestCase = seleneseTestCase;
        this.innerCommand = innerCommand;
    }

    public String execute()
        throws Exception
    {
        boolean innerCommandResult = innerCommand.executeBoolean();
        seleneseTestCase.verifyFalse( innerCommandResult );
        return null;
    }

    public String toString()
    {
        String cmd = innerCommand.command.substring( "is".length() );
        if ( cmd.endsWith( "Present" ) )
        {
            cmd = cmd.replace( "Present", "NotPresent" );
        }
        else
        {
            cmd = "Not" + cmd;
        }
        return "verify" + cmd + "('" + innerCommand.param1 + "')";
    }

}
