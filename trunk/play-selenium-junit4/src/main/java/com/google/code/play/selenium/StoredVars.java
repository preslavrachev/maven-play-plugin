package com.google.code.play.selenium;

import java.util.HashMap;
import java.util.Map;

public class StoredVars
{
    private Map<String, String> storedVars;

    public StoredVars()
    {
        storedVars = new HashMap<String, String>();
        storedVars.put( "space", " " );
        storedVars.put( "nbsp", "\u00A0" );
    }

    public String getVariable( String name )
    {
        String result = storedVars.get( name );
        if ( result == null )
            result = "";// ???
        return result;
    }

    public void setVariable( String name, String value )
    {
        storedVars.put( name, value );
    }

    public String fillValues( String text )
    {
        String result = text;
        for ( String name : storedVars.keySet() )
        {
            String value = storedVars.get( name );
            if ( value == null )
            {
                value = "";// ??
            }
            result = result.replace( "$[" + name + "]", value );
        }
        return result;
    }

    public String changeBraces( String text )
    {
        String result = text;
        for ( String name : storedVars.keySet() )
        {
            result = result.replace( "$[" + name + "]", "${" + name + "}" );
        }
        return result;
    }

}
