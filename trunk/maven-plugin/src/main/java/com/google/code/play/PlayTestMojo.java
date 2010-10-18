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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Run Play! application unit tests
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 * @goal test
 */
public class PlayTestMojo
    extends AbstractPlayMojo
{

    /**
     * Set this to 'true' to bypass unit tests entirely. Its use is NOT RECOMMENDED, especially if you enable it using
     * the "maven.test.skip" property, because maven.test.skip disables both running the tests and compiling the tests.
     * Consider using the skipTests parameter instead.
     * 
     * @parameter expression="${maven.test.skip}"
     */
    private boolean skip;

    /**
     * Set this to true to ignore a failure during testing. Its use is NOT RECOMMENDED, but quite convenient on
     * occasion.
     * 
     * @parameter expression="${maven.test.failure.ignore}"
     */
    private boolean testFailureIgnore;

    /**
     * Base directory where all reports are written to.
     * 
     * @parameter expression="${project.build.directory}/surefire-reports"
     */
    private File reportsDirectory;

    protected void internalExecute()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        if ( skip /* || skipTests || skipExec */)
        {
            getLog().info( "Tests are skipped." );
            return;
        }

        File baseDir = project.getBasedir();

        checkApplication();
        List<File> modules = loadModules();
        List<File> classpath = getClasspath( modules );
        // doJava
        // print("~ Running in test mode");
        // print("~ Ctrl+C to stop");
        // print("~ ");

        // TODO-wydzielic do podprocedury
        // po co przejmowac sie tymi katalogami
        // File tmpDir = new File(baseDir, "tmp");
        // // print("~ Deleting %s", tmpDir.getPath());
        // if (tmpDir.isDirectory()) {
        // FileUtils.deleteDirectory(tmpDir);
        // }
        // print("~");
        // File testResultDir = new File(baseDir, "test-result");
        // if (testResultDir.isDirectory()) {
        // FileUtils.deleteDirectory(testResultDir);
        // }

        // run tests
        TestRunner runner = new TestRunner( baseDir, playId/* "test" */);
        runner.project = project;
        runner.log = getLog();
        runner.playHome = playHome;
        runner.classpath = classpath;
        Property[] systemProperties = new Property[2];
        systemProperties[0] = new Property( "application.path", project.getBasedir().getPath() );
        systemProperties[1] = new Property( "play.id", playId );
        runner.systemProperties = systemProperties;
        runner.run();

        boolean somethingFailed = false;
        // write results
        for ( TestRunner.TestResults testResults : runner.result )
        {
            if ( !reportsDirectory.exists() )
            {
                reportsDirectory.mkdirs();// TODO-check result
            }
            File txtFile = new File( reportsDirectory, testResults.className + ".txt" );
            PrintWriter writer = createPrintFileWriter( txtFile, "UTF-8" );
            try
            {
                writer.printf( "-------------------------------------------------------------------------------%n" );
                writer.printf( "Test set: %s%n", testResults.className );
                writer.printf( "-------------------------------------------------------------------------------%n" );
                String lineSuffix = "";
                if ( !testResults.passed )
                {
                    lineSuffix = " <<< FAILURE!";
                }
                writer.printf( "Tests run: %d, Failures: %d, Errors: %d, Skipped: %d, Time elapsed: %s sec%s%n",
                               testResults.getCount(), testResults.getFailuresCount(), testResults.getErrorsCount(),
                               testResults.getSkippedCount(), elapsedTimeAsString( testResults.getSummarizedTime() ),
                               lineSuffix );
                if ( !testResults.passed )
                {
                    for ( TestRunner.TestResult testResult : testResults.results )
                    {
                        if ( !testResult.passed )
                        {
                            lineSuffix = "";
                            if ( testResult.isError() )
                            { // error
                                lineSuffix = "  <<< ERROR!";
                            }
                            else
                            { // failure
                                lineSuffix = "  <<< FAILURE!";
                            }
                            writer.printf( "%s(%s)  Time elapsed: %s sec%s%n", testResult.name, testResults.className,
                                           elapsedTimeAsString( testResult.time ), lineSuffix );
                            if ( testResult.trace != null )
                            { // error
                                writer.printf( "%s%n", testResult.trace );
                            }
                            else
                            { // failure
                                writer.printf( "%s%n", testResult.error );
                            }
                        }
                    }
                }
            }
            finally
            {
                writer.close();
            }

            File xmlFile = new File( reportsDirectory, "TEST-" + testResults.className + ".xml" );
            writer = createPrintFileWriter( xmlFile, "UTF-8" );
            try
            {
                writer.printf( "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>%n" );
                if ( testResults.passed )
                {
                    writer.printf( "<testsuite errors=\"%d\" skipped=\"%d\" tests=\"%d\" time=\"%s\" failures=\"%d\" name=\"%s\">%n",
                                   testResults.getErrorsCount(), testResults.getSkippedCount(), testResults.getCount(),
                                   elapsedTimeAsString( testResults.getSummarizedTime() ),
                                   testResults.getFailuresCount(), testResults.className );
                }
                else
                {
                    somethingFailed = true;
                    writer.printf( "<testsuite failures=\"%d\" time=\"%s\" errors=\"%d\" skipped=\"%d\" tests=\"%d\" name=\"%s\">%n",
                                   testResults.getFailuresCount(),
                                   elapsedTimeAsString( testResults.getSummarizedTime() ),
                                   testResults.getErrorsCount(), testResults.getSkippedCount(), testResults.getCount(),
                                   testResults.className );
                }
                writer.printf( "  <properties>%n" );
                for ( Object key : runner.modifiedSystemProperties.keySet() )
                {
                    Object value = runner.modifiedSystemProperties.get( key );
                    writer.printf( "    <property value=\"%s\" name=\"%s\"/>%n", value, key );
                }
                writer.printf( "  </properties>%n" );
                for ( TestRunner.TestResult testResult : testResults.results )
                {
                    if ( testResult.passed )
                    {
                        writer.printf( "  <testcase time=\"%s\" classname=\"%s\" name=\"%s\"/>%n",
                                       elapsedTimeAsString( testResult.time ), testResults.className, testResult.name );
                    }
                    else
                    {
                        writer.printf( "  <testcase time=\"%s\" classname=\"%s\" name=\"%s\">%n",
                                       elapsedTimeAsString( testResult.time ), testResults.className, testResult.name );
                        if ( testResult.isError() )
                        {
                            int p = testResult.error.indexOf( "has been caught, " );
                            String errorClass = testResult.error.substring( 2, p - 1 );
                            String message = testResult.error.substring( p + 17 );
                            writer.printf( "    <error message=\"%s\" type=\"%s\">%s</error>%n",
                                           xmlEncodeEntity( message ), errorClass, xmlEncodeEntity( testResult.trace ) );
                        }
                        else
                        {
                            String message = testResult.error.substring( 9 );
                            writer.printf( "    <failure message=\"%s\" type=\"java.lang.AssertionError\">java.lang.AssertionError: %s%n",
                                           xmlEncodeEntity( message ), xmlEncodeEntity( message ) );// TODO-optimize
                            writer.printf( "</failure>%n" );
                        }
                        writer.printf( "  </testcase>%n" );
                    }
                }
                writer.printf( "</testsuite>%n" );
            }
            finally
            {
                writer.close();
            }
        }

        if ( somethingFailed && !testFailureIgnore )
        {
            // TODO: i18n
            String msg =
                "There are test failures.\n\nPlease refer to " + reportsDirectory + " for the individual test results.";
            throw new MojoFailureException( msg );
        }
    }

    protected void/* String */resolvePlayId()
    {
        playId = "test";
    }

    /* copied from surefire AbstractReporter class */
    private NumberFormat numberFormat = NumberFormat.getInstance( Locale.ENGLISH );

    private static final int MS_PER_SEC = 1000;

    protected String elapsedTimeAsString( long runTime )
    {
        return numberFormat.format( (double) runTime / MS_PER_SEC );
    }

    /* copied from surefire AbstractReporter class - end */

    // private void runTests() throws IOException {
    // //List<Class> unitTests = TestEngine.allUnitTests();
    //
    // //wait for the server to start
    // // try {
    // // Thread.sleep(30000);
    // // }
    // // catch (InterruptedException e) {
    // // //ignore
    // // }
    // /*try {
    // Socket s = new Socket("localhost", 9000);
    // try {
    // BufferedInputStream in = new BufferedInputStream(s.getInputStream());
    // //BufferedOutputStream out = new BufferedOutputStream(s.getOutputStream());
    // //write(out, "INFO");
    // /*int something = *//*//in.read();
    // //caption = SERVER_NAME + "\n" + result;
    // }
    // finally {
    // s.close();
    // }
    // }
    // catch (IOException e) {
    // e.printStackTrace();
    // }*/
    // //TODO-port z konfiguracji
    // URL url = new URL("http://localhost:9000/@tests?select=all&auto=yes");
    // WebClient webClient = new WebClient();
    // print("get page");
    // /*Page page = */webClient.getPage(url);
    // print("end get page");
    // }

    /**
     * Enkoduje encje XML apos, quot, lt, gt i amp.
     * 
     * @param text tekst wejĹ>ciowy
     * @return tekst zakodowany
     */
    private String xmlEncodeEntity( String text )
    {
        StringBuffer buf = new StringBuffer();
        Pattern p = Pattern.compile( "[" + concat( ENTITIES.keySet() ) + "]" );
        Matcher m = p.matcher( text );
        while ( m.find() )
        {
            String ch = m.group();
            m.appendReplacement( buf, (String) ENTITIES.get( ch ) );
        }
        m.appendTail( buf );
        return buf.toString();
    }

    /**
     * Takie brakujące join, które skleja stringi z kolekcji i zwraca wynik.
     * 
     * @param strings kolekcja stringów
     * @return sklejone stringi
     */
    private String concat( Collection<String> strings )
    {
        StringBuffer buf = new StringBuffer();
        for ( Iterator<String> it = strings.iterator(); it.hasNext(); )
        {
            buf.append( it.next() );
        }
        return buf.toString();
    }

    protected final static Map<String, String> ENTITIES = new HashMap<String, String>();
    static
    {
        ENTITIES.put( "&", "&amp;" );
        ENTITIES.put( "<", "&lt;" );
        ENTITIES.put( ">", "&gt;" );
        ENTITIES.put( "\'", "&apos;" );
        ENTITIES.put( "\"", "&quot;" );
    }

}

/*
 * TODO: - count skipped tests - requires completely new implementation in TestRunner - sprawdzic obsluge klas testow z
 * adnotacja @Skip na poziomie klasy - przeniesc "test-result" i "tmp" do "target/play/test" na koncu mojo
 */