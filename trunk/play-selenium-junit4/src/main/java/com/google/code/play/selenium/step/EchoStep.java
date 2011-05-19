package com.google.code.play.selenium.step;

import com.google.code.play.selenium.Step;
import com.google.code.play.selenium.StoredVars;

public class EchoStep
    implements Step
{

    private StoredVars storedVars;

    public String param;

    public EchoStep( StoredVars storedVars, String param )
    {
        this.storedVars = storedVars;
        this.param = param;
    }

    public String execute()
        throws Exception
    {
        String result = storedVars.fillValues( param );
        System.out.println( "echo:" + result );
        return result;
    }

    public String toString()
    {
        return "echo('" + param + "')";
    }

}
