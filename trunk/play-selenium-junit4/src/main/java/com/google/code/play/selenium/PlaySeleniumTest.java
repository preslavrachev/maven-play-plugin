package com.google.code.play.selenium;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.HttpCommandProcessor;
import com.thoughtworks.selenium.SeleneseTestCase;
//import com.thoughtworks.selenium.Selenium;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
//import java.util.Map;

import org.dom4j.Document;//TODO-remove dom4j dependency
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.After;
import org.junit.Before;

import com.google.code.play.selenium.step.*;

public class PlaySeleniumTest
    extends SeleneseTestCase
{

    private String seleniumUrl = null;

    private CommandProcessor commandProcessor = null;

    @Before
    public void setUp()
        throws Exception
    {
        // don't do it! super.setUp();

        // URL testUrl = new
        // URL("http://localhost:9000/@tests/selenium/ActionPermissions.test.html");
        // String content = (String)testUrl.getContent();

        String seleniumBrowser = System.getProperty( "selenium.browser" );
        if ( seleniumBrowser == null )
        {
            seleniumBrowser = "*chrome";
        }
        seleniumUrl = System.getProperty( "selenium.url" );
        if ( seleniumUrl == null )
        {
            seleniumUrl = "http://localhost:9000";
        }
        commandProcessor = new HttpCommandProcessor( "localhost", 4444, seleniumBrowser, seleniumUrl );
        selenium = new DefaultSelenium( commandProcessor );
        selenium.start();
        // There are no cookies by default
        // selenium.deleteCookie("PLAY_SESSION",
        // "path=/,domain=localhost,recurse=true");
        // selenium.deleteCookie("PLAY_ERRORS",
        // "path=/,domain=localhost,recurse=true");
        // selenium.deleteCookie("PLAY_FLASH",
        // "path=/,domain=localhost,recurse=true");
    }

    @After
    public void tearDown()
        throws Exception
    {
        selenium.stop();

        super.tearDown();
    }

    protected void seleniumTest( String testPath )
        throws Exception
    {
        URL testUrl = new URL( seleniumUrl + "/@tests/" + testPath );
        URLConnection conn = testUrl.openConnection();
        // System.out.println("contentType:" + conn.getContentType());
        // System.out.println("contentLength:" + conn.getContentLength());
        // System.out.println("Header fields:");
        // Map<String, List<String>> headerFields = conn.getHeaderFields();
        // for (String headerField : headerFields.keySet()) {
        // System.out.println("  " + headerField + " : "
        // + headerFields.get(headerField));
        // }
        if ( !"HTTP/1.1 200 OK".equals( conn.getHeaderField( null ) ) )
        {
            // if (conn.getHeaderField(null).indexOf("200 OK") == -1) {
            return;// TODO handle errors
        }
        InputStream is = (InputStream) conn.getContent();
        try
        {
            String content = readContent( is );
            // System.out.println("Content:");
            // System.out.println(content);
            List<Step> steps = processContent( content );
            for ( Step step : steps )
            {
                // System.out.println("Executing: " + step.toString());
                // try {
                step.execute();
                // } catch (Exception e) {
                // throw e;
                // }
            }
        }
        finally
        {
            is.close();
        }
    }

    private String readContent( InputStream is )
        throws IOException
    {
        StringBuffer buf = new StringBuffer();
        InputStreamReader r = new InputStreamReader( is, "UTF-8" );
        BufferedReader br = new BufferedReader( r );
        try
        {
            String line = br.readLine();
            // int lineNo = 1;
            while ( line != null )
            {
                buf.append( line ).append( '\n' );
                // System.out.println( lineNo + ":" + line );
                line = br.readLine();
                // lineNo++;
            }
        }
        finally
        {
            br.close();
        }
        return buf.toString();
    }

    private List<Step> processContent( String content )
        throws DocumentException
    {
        List<Step> result = new ArrayList<Step>();

        Document xmlDoc = DocumentHelper.parseText( content );
        Element table = xmlDoc.getRootElement();
        Element tbody = table.element( "tbody" );
        List<Element> rows = tbody.elements( "tr" );
        for ( Element row : rows )
        {
            List<Element> data = row.elements( "td" );
            Step cmd = null;
            if ( data.size() == 1 )
            { // comment
                String cmt = data.get( 0 ).getTextTrim();
                cmd = new CommentStep( cmt.substring( "//".length() ) );
            }
            else
            {
                String command = data.get( 0 ).getText();
                String param1 = data.get( 1 ).getText();
                if ( !"".equals( param1 ) )
                {
                    param1 = xmlUnescape( param1 );
                }
                String param2 = data.get( 2 ).getText();
                if ( !"".equals( param2 ) )
                {
                    param2 = xmlUnescape( param2 );
                }

                if ( command.endsWith( "AndWait" ) )
                {
                    String innerCmd = command.substring( 0, command.indexOf( "AndWait" ) );
                    cmd = new AndWaitStep( new SeleniumCommand( commandProcessor, innerCmd, param1, param2 ) );
                }
                else if ( command.startsWith( "verify" ) )
                {
                    String verifyWhat = command.substring( "verify".length() );
                    if ( verifyWhat.endsWith( "NotPresent" ) )
                    {
                        String innerCmd = verifyWhat.replace( "NotPresent", "Present" );
                        cmd =
                            new VerifyFalseStep( this, new SeleniumCommand( commandProcessor, "is" + innerCmd, param1 ) );
                    }
                    else if ( verifyWhat.startsWith( "Not" ) )
                    {
                        String innerCmd = verifyWhat.substring( "Not".length() );
                        if ( isParameterLessCommand( innerCmd ) )
                        {
                            param2 = param1; // value to compare with
                            param1 = ""; // parameterless command
                        }
                        if ( isBooleanCommand( innerCmd ) )
                        {
                            cmd =
                                new VerifyFalseStep( this, new SeleniumCommand( commandProcessor, "is" + innerCmd,
                                                                                param1 ) );
                        }
                        else
                        {
                            cmd =
                                new VerifyNotEqualsStep( this, new SeleniumCommand( commandProcessor, "get" + innerCmd,
                                                                                    param1 ), param2 );

                        }
                    }
                    else
                    {
                        String innerCmd = verifyWhat;
                        if ( isParameterLessCommand( innerCmd ) )
                        {
                            param2 = param1; // value to compare with
                            param1 = ""; // parameterless command
                        }
                        if ( isBooleanCommand( innerCmd ) )
                        {
                            cmd =
                                new VerifyTrueStep( this, new SeleniumCommand( commandProcessor, "is" + innerCmd,
                                                                               param1 ) );
                        }
                        else
                        {
                            cmd =
                                new VerifyEqualsStep( this, new SeleniumCommand( commandProcessor, "get" + innerCmd,
                                                                                 param1 ), param2 );
                        }
                    }
                }
                else if ( command.startsWith( "assert" ) )
                {
                    String assertWhat = command.substring( "assert".length() );
                    if ( assertWhat.endsWith( "NotPresent" ) )
                    {
                        String innerCmd = "is" + assertWhat.replace( "NotPresent", "Present" );
                        cmd = new AssertFalseStep( new SeleniumCommand( commandProcessor, innerCmd, param1 ) );
                    }
                    else if ( assertWhat.startsWith( "Not" ) )
                    {
                        String innerCmd = assertWhat.substring( "Not".length() );
                        if ( isParameterLessCommand( innerCmd ) )
                        {
                            param2 = param1; // value to compare with
                            param1 = ""; // parameterless command
                        }
                        if ( isBooleanCommand( innerCmd ) )
                        {
                            cmd =
                                new AssertFalseStep( new SeleniumCommand( commandProcessor, "is" + innerCmd, param1 ) );
                        }
                        else
                        {
                            cmd =
                                new AssertNotEqualsStep( new SeleniumCommand( commandProcessor, "get" + innerCmd,
                                                                              param1 ), param2 );
                        }
                    }
                    else
                    {
                        String innerCmd = assertWhat;
                        if ( isParameterLessCommand( innerCmd ) )
                        {
                            param2 = param1; // value to compare with
                            param1 = ""; // parameterless command
                        }
                        if ( isBooleanCommand( innerCmd ) )
                        {
                            cmd = new AssertTrueStep( new SeleniumCommand( commandProcessor, "is" + innerCmd, param1 ) );
                        }
                        else
                        {
                            cmd =
                                new AssertEqualsStep(
                                                      new SeleniumCommand( commandProcessor, "get" + innerCmd, param1 ),
                                                      param2 );
                        }
                    }
                }
                else if ( command.startsWith( "waitFor" ) )
                {
                    String waitForWhat = command.substring( "waitFor".length() );
                    if ( "Condition".equals( waitForWhat ) || "FrameToLoad".equals( waitForWhat )
                        || "PageToLoad".equals( waitForWhat ) || "PopUp".equals( waitForWhat ) )
                    {
                        cmd = new SeleniumCommand( commandProcessor, command, param1, param2 );
                    }
                    else
                    {
                        if ( waitForWhat.startsWith( "Not" ) )
                        {
                            String innerCmd = "is" + waitForWhat.substring( "Not".length() );
                            cmd = new WaitForNotStep( new SeleniumCommand( commandProcessor, innerCmd, param1 ) );
                        }
                        else if ( waitForWhat.endsWith( "NotPresent" ) )
                        {
                            String innerCmd = "is" + waitForWhat.replace( "NotPresent", "Present" );
                            cmd = new WaitForNotStep( new SeleniumCommand( commandProcessor, innerCmd, param1 ) );
                        }
                        else
                        {
                            String innerCmd = "is" + waitForWhat;
                            cmd = new WaitForStep( new SeleniumCommand( commandProcessor, innerCmd, param1 ) );
                        }
                    }
                }
                else
                {
                    cmd = new SeleniumCommand( commandProcessor, command, param1, param2 );
                }
            }
            result.add( cmd );
            // System.out.println( cmd.toString() );
        }

        return result;
    }

    private String xmlUnescape( String value )
    {
        String result = value;
        result = result.replace( "&quot;", "\"" );
        result = result.replace( "&apos;", "'" );
        result = result.replace( "&lt;", "<" );
        result = result.replace( "&gt;", ">" );
        result = result.replace( "&amp;", "&" );
        return result;
    }

    private boolean isBooleanCommand( String command )
    {
        boolean result =
            ( "AlertPresent".equals( command ) || "Checked".equals( command ) || "ConfirmationPresent".equals( command )
                || "CookiePresent".equals( command ) || "Editable".equals( command )
                || "ElementPresent".equals( command ) || "Ordered".equals( command )
                || "PromptPresent".equals( command ) || "SomethingSelected".equals( command )
                || "TextPresent".equals( command ) || "Visible".equals( command ) );
        return result;
    }

    private boolean isParameterLessCommand( String command )
    {
        boolean result =
            ( "Alert".equals( command ) || "BodyText".equals( command ) || "Confirmation".equals( command )
                || "Cookie".equals( command ) || "HtmlSource".equals( command ) || "Location".equals( command )
                || "MouseSpeed".equals( command ) || "Prompt".equals( command ) || "Speed".equals( command ) || "Title".equals( command ) );
        return result;
    }

}
