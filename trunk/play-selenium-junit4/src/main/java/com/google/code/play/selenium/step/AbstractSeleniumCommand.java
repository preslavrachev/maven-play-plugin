package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.CommandProcessor;

import com.google.code.play.selenium.StoredVars;

public abstract class AbstractSeleniumCommand
{

    protected StoredVars storedVars;

    protected CommandProcessor commandProcessor;

    protected String command;

    protected String param1;

    protected String param2;

    public AbstractSeleniumCommand( StoredVars storedVars, CommandProcessor commandProcessor, String command,
                                    String param1 )
    {
        this.storedVars = storedVars;
        this.commandProcessor = commandProcessor;
        this.command = command;
        this.param1 = param1;
        this.param2 = "";
    }

    public AbstractSeleniumCommand( StoredVars storedVars, CommandProcessor commandProcessor, String command,
                                    String param1, String param2 )
    {
        this( storedVars, commandProcessor, command, param1 );
        this.param2 = param2;
    }

}
