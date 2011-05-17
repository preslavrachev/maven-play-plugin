package com.google.code.play.selenium.step;

import com.google.code.play.selenium.Step;

public class AndWaitStep
    implements Step
{

    protected SeleniumCommand innerCommand;

    public AndWaitStep( SeleniumCommand innerCommand )
    {
        this.innerCommand = innerCommand;
    }

    public String execute()
        throws Exception
    {
        innerCommand.execute();
        innerCommand.commandProcessor.doCommand( "waitForPageToLoad", new String[] { "30000" } );
        return null;
    }

    public String toString()
    {
        return innerCommand.command + "AndWait('" + innerCommand.param1
            + ( innerCommand.param2 != null ? "' ,'" + innerCommand.param2 : "" ) + "')";
    }

}
