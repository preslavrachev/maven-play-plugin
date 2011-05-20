package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.SeleneseTestCase;

import junit.framework.Assert;

import com.google.code.play.selenium.Step;

public class WaitForEqualsStep
    implements Step
{

    protected SeleniumCommand innerCommand;

    public String expected;

    public WaitForEqualsStep( SeleniumCommand innerCommand, String expected )
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
                boolean seleniumEqualsResult = SeleneseTestCase.seleniumEquals( xexpected, innerCommandResult );
                if ( seleniumEqualsResult )
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
        return "waitFor" + cmd + "('" + innerCommand.param1 + "', '" + expected + "')";
    }

}
