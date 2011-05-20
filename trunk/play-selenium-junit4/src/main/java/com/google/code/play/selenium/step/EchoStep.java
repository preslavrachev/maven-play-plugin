package com.google.code.play.selenium.step;

import com.google.code.play.selenium.Step;
import com.google.code.play.selenium.StoredVars;

public class EchoStep
    implements Step
{

    private StoredVars storedVars;

    private String param;

    public EchoStep( StoredVars storedVars, String param )
    {
        this.storedVars = storedVars;
        this.param = param;
    }

    public void execute()
        throws Exception
    {
        String result = storedVars.fillValues( param );
        System.out.println( "echo:" + result );
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "echo('" ).append( param ).append( "')" );
        return buf.toString();
    }

}
