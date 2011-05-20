package com.google.code.play.selenium.step;

public class MultiLineHelper
{

    private MultiLineHelper()
    {
    }

    /**
     * Converts <br />
     * tags to "\n" characters
     * 
     * @param parameter input text
     * @return converted input text
     */
    public static String brToNewLine( String parameter )
    {
        return parameter.replaceAll( "<\\s*[bB][rR]\\s*/\\s*>", "\n" );
    }

}
