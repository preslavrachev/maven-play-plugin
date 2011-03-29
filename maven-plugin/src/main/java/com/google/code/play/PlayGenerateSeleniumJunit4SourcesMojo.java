package com.google.code.play;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Adds application resources ('app' directory) to Play! application. FIXME
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 * @goal generate-selenium-junit4-sources
 * @phase generate-test-sources
 */
public class PlayGenerateSeleniumJunit4SourcesMojo
    extends AbstractPlayMojo
{

    /**
     * ...
     * 
     * @parameter expression="${play.skipSeleniumTests}"
     */
    private boolean skip;

    protected void internalExecute()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        if ( !skip )
        {
            File baseDir = project.getBasedir();
            File playTests = new File( baseDir, "test" );
            File playSeleniumTests = new File( playTests, "selenium" );

            File destDir = new File( project.getBuild().getDirectory(), "selenium/generated/selenium" );// TODO-sparametryzowac
                                                                                                        // nazwe pakietu
            File[] playSeleniumTestFiles = playSeleniumTests.listFiles();

            if ( playSeleniumTestFiles == null || playSeleniumTestFiles.length == 0 )
            {
                return;// nothing to do, add some info???
            }

            boolean somethingGenerated = false;
            for ( File playTestFile : playSeleniumTestFiles )
            {
                if ( playTestFile.isFile() )
                {
                    String playTestFileName = playTestFile.getName();
                    if ( "Application.test.html".equals( playTestFileName ) )//FIXME remove this hack
                    {
                        continue;// tych testów na razie nie konwertuje dobrze
                    }
                    if ( playTestFileName.endsWith( ".test.html" ) )
                    {
                        // System.out.println("file: " + playTestFileName);
                        String oryginalTestClassName =
                            playTestFileName.substring( 0, playTestFileName.indexOf( ".test.html" ) );
                        String javaTestClassName = oryginalTestClassName.replace( ".", "_" );
                        File javaTestFile = new File( destDir, javaTestClassName + "Test.java" );
                        if ( !javaTestFile.exists()
                            || ( javaTestFile.lastModified() + 0/* lastUpdatedWithinMsecs */< playTestFile.lastModified() ) )
                        {
                            javaTestFile.getParentFile().mkdirs();// TODO-check the result and throw exception when "false"

                            BufferedReader r =
                                new BufferedReader(
                                                    new InputStreamReader( new FileInputStream( playTestFile ), "UTF-8" ) );
                            try
                            {
                                PrintWriter w =
                                    new PrintWriter(
                                                     new BufferedWriter(
                                                                         new OutputStreamWriter(
                                                                                                 new FileOutputStream(
                                                                                                                       javaTestFile ),
                                                                                                 "UTF-8" ) ) );
                                try
                                {
                                    generateTestSource( playTestFile.getName()/* maybe full path? */, oryginalTestClassName,
                                         javaTestClassName, r, w );
                                    somethingGenerated = true;
                                }
                                finally
                                {
                                    w.flush();// ??
                                    w.close();
                                }
                            }
                            finally
                            {
                                r.close();
                            }
                        }
                    }
                }
            }
            if (!somethingGenerated)
            {
                getLog().info( "Nothing to generate - all Selenium JUnit4 test sources are up to date" );
            }
            project.addTestCompileSourceRoot( new File( project.getBuild().getDirectory(), "selenium/generated" ).getAbsolutePath() );
        }
    }

    private void generateTestSource( String playTestFileName, String oryginalTestClassName, String javaTestClassName,
                      BufferedReader r, PrintWriter w )
        throws IOException, MojoExecutionException
    {
        w.println( "package selenium;" );// TODO parametrize
        w.println();
        w.println( "import com.thoughtworks.selenium.DefaultSelenium;" );
        w.println( "import com.thoughtworks.selenium.SeleneseTestCase;" );
        w.println( "import java.net.URL;" );
        w.println( "import org.junit.After;" );
        w.println( "import org.junit.Before;" );
        w.println( "import org.junit.Test;" );
        w.println( "//import java.util.regex.Pattern;" );// TODO-opcjonalnie
        w.println();
        w.println( "public class " + javaTestClassName + "Test extends SeleneseTestCase {" );
        w.println();
        w.println( "\t@Before" );
        w.println( "\tpublic void setUp() throws Exception {" );
        w.println( "\t\tURL testUrl = new URL(\"http://localhost:9000/@tests/selenium/" + oryginalTestClassName
            + ".test.html\");" );
        w.println( "\t\ttestUrl.getContent();//ignore result, invoked only to reload fixtures" );
        w.println( "\t\t" );
        w.println( "\t\tString seleniumBrowser = System.getProperty(\"selenium.browser\");" );
        w.println( "\t\tif (seleniumBrowser == null) {" );
        w.println( "\t\t    seleniumBrowser = \"*chrome\";" );
        w.println( "\t\t}" );
        w.println( "\t\tString seleniumUrl = System.getProperty(\"selenium.url\");" );
        w.println( "\t\tif (seleniumUrl == null) {" );
        w.println( "\t\t    seleniumUrl = \"http://localhost:9000\";" );
        w.println( "\t\t}" );
        w.println( "\t\tselenium = new DefaultSelenium(\"localhost\", 4444, seleniumBrowser, seleniumUrl);" );
        w.println( "\t\tselenium.start();" );
        w.println( "\t\t//There are no cookies by default" );
        w.println( "\t\t//selenium.deleteCookie(\"PLAY_SESSION\", \"path=/,domain=localhost,recurse=true\");" );
        w.println( "\t\t//selenium.deleteCookie(\"PLAY_ERRORS\", \"path=/,domain=localhost,recurse=true\");" );
        w.println( "\t\t//selenium.deleteCookie(\"PLAY_FLASH\", \"path=/,domain=localhost,recurse=true\");" );
        w.println( "\t}" );
        w.println();
        w.println( "\t@Test" );
        w.println( "\tpublic void test" + javaTestClassName + "() throws Exception {" );// TODO-zrobic sensowna nazwe
                                                                                        // metody
        processCommands( playTestFileName, r, w );

        w.println( "\t}" );
        w.println();
        w.println( "\t@After" );
        w.println( "\tpublic void tearDown() throws Exception {" );
        w.println( "\t\tselenium.stop();" );
        w.println( "\t}" );
        w.println();
        w.println( "}" );
    }

    private static class Command
    {
        public String command;

        public String param1;

        public String param2;
    }

    private String javaEscapeValue( String value )
    {
        String result = value.replace( "\\", "\\\\" ).replace( "\"", "\\\"" );
        return result;
    }

    private String xmlUnescape( String value )
    {
        String result = value;
        // TODO-this is temporal
        result = result.replace( "&lt;", "<" );
        result = result.replace( "&gt;", ">" );
        return result;
    }

    private void processCommands( String playTestFileName, BufferedReader r, PrintWriter w )
        throws IOException, MojoExecutionException
    {
        String line = r.readLine();
        while ( line != null )
        {
            line = line.trim();
            if ( "".equals( line ) )
            {
                // pusta linia
            }
            else if ( line.startsWith( "#" ) )
            {
                // play macro
            }
            else if ( line.startsWith( "//" ) )
            {
                // comment
                w.println( "\t\t" + line );
            }
            else
            {
                // command
                Command cmd = new Command();

                int p = line.indexOf( '(' );
                if ( p < 0 )
                {
                    throw new MojoExecutionException( "Error parsing file \"" + playTestFileName + "\", line : \""
                        + line + "\"" );
                }// ????
                String command = line.substring( 0, p ).trim();
                cmd.command = command;
                // System.out.println(command);
                int p2 = line.indexOf( '\'', p + 1 );// GS-obsluzyc sytuacje, gdy nie ma parametrow
                int p3 = line.indexOf( '\'', p2 + 1 );
                String param = line.substring( p2 + 1, p3 );
                cmd.param1 = "\"" + javaEscapeValue( param ) + "\"";
                // System.out.println(param);
                p = line.indexOf( ',', p3 + 1 );
                if ( p >= 0 )
                {
                    // jest drugi parametr
                    p2 = line.indexOf( '\'', p + 1 );
                    p3 = line.indexOf( '\'', p2 + 1 );
                    param = line.substring( p2 + 1, p3 );
                    cmd.param2 = "\"" + javaEscapeValue( param ) + "\"";
                    // System.out.println(param);
                }
                else
                {
                    if ( "type".equals( command ) || "verifyTable".equals( command ) || "verifyValue".equals( command ) )
                    {// jakie jeszcze polecenia?
                        cmd.param2 = "\"\"";
                    }
                }

                String[] test = play.test.Helpers.seleniumCommand( line );
                if ( test == null )
                {
                    throw new MojoExecutionException( "Error parsing file \"" + playTestFileName + "\", line : \""
                        + line + "\"" );
                }
                cmd.command = test[0];
                cmd.param1 = "\"" + javaEscapeValue( xmlUnescape( test[1] ) ) + "\"";
                cmd.param2 = "\"" + javaEscapeValue( xmlUnescape( test[2] ) ) + "\"";
                if ( "".equals( test[2] ) )
                {
                    if ( "type".equals( command )
                        ||
                        // command.startsWith("verify") ||
                        "verifyTable".equals( command ) || "verifyNotTable".equals( command )
                        || "verifySelectedValue".equals( command ) || "verifyNotSelectedValue".equals( command )
                        || "verifyText".equals( command ) || "verifyNotText".equals( command )
                        || "verifyValue".equals( command ) || "verifyNotValue".equals( command ) )
                    {// jakie jeszcze polecenia?
                     // bez zmian
                    }
                    else
                    {
                        cmd.param2 = null;
                    }
                }

                boolean isAndWait = false; // command fooAndWait
                boolean isWaitFor = false; // command waitForFoo

                String realCmd = "selenium." + cmd.command;
                if ( realCmd.endsWith( "AndWait" ) )
                {
                    realCmd = realCmd.substring( 0, realCmd.indexOf( "AndWait" ) );
                    isAndWait = true;
                }

                /* ?else */if ( realCmd.startsWith( "selenium.verify" ) )
                {
                    if ( cmd.param2 == null )
                    { // tylko jeden parametr
                        String innerCmd = "selenium.is" + realCmd.substring( "selenium.verify".length() );
                        realCmd = "verifyTrue";
                        if ( innerCmd.indexOf( "Not" ) >= 0 )
                        {
                            realCmd = "verifyFalse";
                            innerCmd = innerCmd.replace( "Not", "" );
                        }
                        cmd.param1 = innerCmd + "(" + cmd.param1 + ")";
                        cmd.param2 = null;
                    }
                    else
                    { // dwa parametry
                        String innerCmd = "selenium.get" + realCmd.substring( "selenium.verify".length() );
                        realCmd = "verifyEquals";
                        if ( innerCmd.indexOf( "Not" ) >= 0 )
                        {
                            realCmd = "verifyNotEquals";
                            innerCmd = innerCmd.replace( "Not", "" );
                        }
                        String par2 = cmd.param2;
                        cmd.param2 = innerCmd + "(" + cmd.param1 + ")";
                        cmd.param1 = par2;
                        // specjalna obsluga regexp
                        if ( cmd.param1.startsWith( "\"regexp:" ) )
                        {
                            realCmd = "verifyTrue";
                            cmd.param1 =
                                "java.util.regex.Pattern.compile(\"" + cmd.param1.substring( "\"regexp:".length() ) + ").matcher("
                                    + cmd.param2 + ").find()";
                            cmd.param2 = null;
                        }
                        else if ( cmd.param1.startsWith( "\"exact:" ) )
                        {
                            cmd.param1 = "\"" + cmd.param1.substring( "\"exact:".length() );
                        }
                    }
                }
                // dokładnie to samo, co dla "verify", tylko dla "assert" - zrobic podmetodę
                else if ( realCmd.startsWith( "selenium.assert" ) )
                {
                    if ( cmd.param2 == null )
                    { // tylko jeden parametr
                        String innerCmd = "selenium.is" + realCmd.substring( "selenium.assert".length() );
                        realCmd = "assertTrue";
                        if ( innerCmd.indexOf( "Not" ) >= 0 )
                        {
                            realCmd = "assertFalse";
                            innerCmd = innerCmd.replace( "Not", "" );
                        }
                        cmd.param1 = innerCmd + "(" + cmd.param1 + ")";
                        cmd.param2 = null;
                    }
                    else
                    { // dwa parametry
                        String innerCmd = "selenium.get" + realCmd.substring( "selenium.assert".length() );
                        realCmd = "assertEquals";
                        if ( innerCmd.indexOf( "Not" ) >= 0 )
                        {
                            realCmd = "assertNotEquals";
                            innerCmd = innerCmd.replace( "Not", "" );
                        }
                        String par2 = cmd.param2;
                        cmd.param2 = innerCmd + "(" + cmd.param1 + ")";
                        cmd.param1 = par2;
                        // specjalna obsluga regexp
                        if ( cmd.param1.startsWith( "\"regexp:" ) )
                        {
                            realCmd = "assertTrue";// TODO-a moze czasem false?
                            cmd.param1 =
                                "java.util.regex.Pattern.compile(\"" + cmd.param1.substring( "\"regexp:".length() ) + ").matcher("
                                    + cmd.param2 + ").find()";
                            cmd.param2 = null;
                        }
                        else if ( cmd.param1.startsWith( "\"exact:" ) )
                        {
                            cmd.param1 = "\"" + cmd.param1.substring( "\"exact:".length() );
                        }
                    }
                }
                else if ( realCmd.startsWith( "selenium.waitFor" ) )
                {
                    if ( !"selenium.waitForCondition".equals( realCmd )
                        && !"selenium.waitForFrameToLoad".equals( realCmd )
                        && !"selenium.waitForPageToLoad".equals( realCmd ) && !"selenium.waitForPopUp".equals( realCmd ) )
                    {
                        realCmd = "selenium.is" + realCmd.substring( "selenium.waitFor".length() );
                        isWaitFor = true;
                    }
                }

                String javaCmd = realCmd + "(" + cmd.param1;
                if ( cmd.param2 != null )
                {
                    javaCmd += ", " + cmd.param2;
                }
                javaCmd += ")";

                if ( isWaitFor )
                {
                    w.println( "\t\tfor (int second = 0;; second++) {" );
                    w.println( "\t\t\tif (second >= 60) fail(\"timeout\");" );
                    w.println( "\t\t\ttry { if (" + javaCmd + ") break; } catch (Exception e) {}" );
                    w.println( "\t\t\tThread.sleep(1000);" );
                    w.println( "\t\t}" );
                }
                else
                {
                    w.println( "\t\t" + javaCmd + ";" );
                    if ( isAndWait )
                    {
                        w.println( "\t\tselenium.waitForPageToLoad(\"30000\");" );// TODO-dorobic parametryzacje czasu
                                                                                  // timeout
                    }
                }
            }
            line = r.readLine();
        }
    }

}

// TODO
// - configuration to skip fixtures reloading (speeds up tests execution)
// - generate test sources incrementally (only for newer files)
// - use includes/excludes?
// - proper xml unescaping
