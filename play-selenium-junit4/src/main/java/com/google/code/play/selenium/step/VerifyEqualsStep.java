package com.google.code.play.selenium.step;

import com.thoughtworks.selenium.SeleneseTestCase;

import com.google.code.play.selenium.Step;

public class VerifyEqualsStep
    implements Step
{

    private SeleneseTestCase seleneseTestCase;

    private StringSeleniumCommand innerCommand;

    private String expected;

    public VerifyEqualsStep( SeleneseTestCase seleneseTestCase, StringSeleniumCommand innerCommand, String expected )
    {
        this.seleneseTestCase = seleneseTestCase;
        this.innerCommand = innerCommand;
        this.expected = expected;
    }

    public void execute()
        throws Exception
    {
        String innerCommandResult = innerCommand.getString();
        String xexpected = MultiLineHelper.brToNewLine( expected );
        boolean seleniumEqualsResult = SeleneseTestCase.seleniumEquals( xexpected, innerCommandResult );
        seleneseTestCase.verifyTrue( seleniumEqualsResult );
    }

    public String toString()
    {
        String cmd = innerCommand.command.substring( "get".length() );

        StringBuffer buf = new StringBuffer();
        buf.append( "verify" ).append( cmd ).append( "('" );
        buf.append( innerCommand.param1 ).append( "', '" ).append( expected ).append( "')" );
        return buf.toString();
    }

}
