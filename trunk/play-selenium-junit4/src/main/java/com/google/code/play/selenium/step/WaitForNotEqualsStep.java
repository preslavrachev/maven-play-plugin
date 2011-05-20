package com.google.code.play.selenium.step;

import junit.framework.Assert;

import com.google.code.play.selenium.Step;

public class WaitForNotEqualsStep
    implements Step
{

    protected SeleniumCommand innerCommand;

    public String expected;

    public WaitForNotEqualsStep( SeleniumCommand innerCommand, String expected )
    {
        this.innerCommand = innerCommand;
        this.expected = expected;
    }

    public String execute()
        throws Exception
    {
        String xexpected = expected.replaceAll("<\\s*[bB][rR]\\s*/\\s*>", "\n");//TODO-improve
        for ( int second = 0;; second++ )
        {
            if ( second >= 60 )
                Assert.fail( "timeout" );
            try
            {
                String innerCommandResult = innerCommand.execute();
                boolean seleniumNotEqualsResult = NotEqualsHelper.seleniumNotEquals( xexpected, innerCommandResult );
                if ( seleniumNotEqualsResult )
                    break;
            }
            catch ( Exception e )
            {
            }
            Thread.sleep( 1000 );
        }
        return null;
    }

    public String toString()
    {
        String cmd = innerCommand.command.substring( "get".length() );
        return "waitForNot" + cmd + "('" + innerCommand.param1 + "', '" + expected + "')";
    }

}
