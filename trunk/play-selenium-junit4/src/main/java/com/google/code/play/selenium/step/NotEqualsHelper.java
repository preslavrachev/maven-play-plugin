package com.google.code.play.selenium.step;

import java.util.regex.Pattern;

public class NotEqualsHelper
{

    private NotEqualsHelper()
    {
    }

    /**
     * Compares two strings, but handles "regexp:" strings like HTML Selenese
     * 
     * @param expectedPattern
     * @param actual
     * @return true if actual matches the expectedPattern, or false otherwise
     */
    public static boolean seleniumNotEquals( String expectedPattern, String actual )
    {
        if ( actual.startsWith( "regexp:" ) || actual.startsWith( "regex:" ) || actual.startsWith( "regexpi:" )
            || actual.startsWith( "regexi:" ) )
        {
            // swap 'em
            String tmp = actual;
            actual = expectedPattern;
            expectedPattern = tmp;
        }
        Boolean b;
        b = handleRegex( "regexp:", expectedPattern, actual, 0 );
        if ( b != null )
        {
            return b.booleanValue();
        }
        b = handleRegex( "regex:", expectedPattern, actual, 0 );
        if ( b != null )
        {
            return b.booleanValue();
        }
        b = handleRegex( "regexpi:", expectedPattern, actual, Pattern.CASE_INSENSITIVE );
        if ( b != null )
        {
            return b.booleanValue();
        }
        b = handleRegex( "regexi:", expectedPattern, actual, Pattern.CASE_INSENSITIVE );
        if ( b != null )
        {
            return b.booleanValue();
        }

        if ( expectedPattern.startsWith( "exact:" ) )
        {
            String expectedExact = expectedPattern.replaceFirst( "exact:", "" );
            if ( /* ! */expectedExact.equals( actual ) )
            {
                System.out.println( "expected " + actual + " to not match " + expectedPattern );
                return false;
            }
            return true;
        }

        String expectedGlob = expectedPattern.replaceFirst( "glob:", "" );
        expectedGlob = expectedGlob.replaceAll( "([\\]\\[\\\\{\\}$\\(\\)\\|\\^\\+.])", "\\\\$1" );

        expectedGlob = expectedGlob.replaceAll( "\\*", ".*" );
        expectedGlob = expectedGlob.replaceAll( "\\?", "." );
        if ( /* ! */Pattern.compile( expectedGlob, Pattern.DOTALL ).matcher( actual ).matches() )
        {
            System.out.println( "expected \"" + actual + "\" to not match glob \"" + expectedPattern
                + "\" (had transformed the glob into regexp \"" + expectedGlob + "\"" );
            return false;
        }
        return true;
    }

    private static Boolean handleRegex( String prefix, String expectedPattern, String actual, int flags )
    {
        if ( expectedPattern.startsWith( prefix ) )
        {
            String expectedRegEx = expectedPattern.replaceFirst( prefix, ".*" ) + ".*";
            Pattern p = Pattern.compile( expectedRegEx, flags );
            if ( /* ! */p.matcher( actual ).matches() )
            {
                System.out.println( "expected " + actual + " to not match regexp " + expectedPattern );
                return Boolean.FALSE;
            }
            return Boolean.TRUE;
        }
        return null;
    }

}
