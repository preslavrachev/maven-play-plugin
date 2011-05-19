package com.google.code.play.selenium.step;

import com.google.code.play.selenium.Step;
import com.google.code.play.selenium.StoredVars;

//import com.thoughtworks.selenium.CommandProcessor;

public class StoreStep
    implements Step
{
    private StoredVars storedVars;

    private SeleniumCommand innerStoreCommand;

    private SeleniumCommand innerGetCommand;

    public StoreStep( StoredVars storedVars, SeleniumCommand innerStoreCommand/*
                                                                               * CommandProcessor commandProcessor,
                                                                               * String command, String param1, String
                                                                               * param2
                                                                               */)
    {
        this.innerStoreCommand = innerStoreCommand;
        // innerStoreCommand = new SeleniumCommand( commandProcessor, command, param1, param2 );
        String storeWhat = innerStoreCommand.command.substring( "store".length() );
        innerGetCommand =
            new SeleniumCommand( storedVars, innerStoreCommand.commandProcessor, "get" + storeWhat,
                                 innerStoreCommand.param1 );
        this.storedVars = storedVars;
    }

    public String execute()
        throws Exception
    {
        innerStoreCommand.execute();
        String result = innerGetCommand.execute();
        storedVars.setVariable( innerStoreCommand.param2, result );
        return result;
    }

    public String toString()
    {
        return innerStoreCommand.toString();
    }

}
