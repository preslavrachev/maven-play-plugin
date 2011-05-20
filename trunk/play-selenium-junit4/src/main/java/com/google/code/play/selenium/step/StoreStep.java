package com.google.code.play.selenium.step;

import com.google.code.play.selenium.StoredVars;

public class StoreStep
    extends CommandStep
{
    private StoredVars storedVars;

    private StringSeleniumCommand innerGetCommand;

    public StoreStep( StoredVars storedVars, VoidSeleniumCommand innerStoreCommand,
                      StringSeleniumCommand innerGetCommand )
    {
        super( innerStoreCommand );
        this.storedVars = storedVars;
        this.innerGetCommand = innerGetCommand;
    }

    public void execute()
        throws Exception
    {
        super.execute();
        String result = innerGetCommand.getString();
        storedVars.setVariable( command.param2, result );
    }

}
