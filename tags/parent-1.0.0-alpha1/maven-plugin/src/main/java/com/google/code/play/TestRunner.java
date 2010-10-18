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
 *     http://www.apache.org/licenses/LICENSE-2.0
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
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.cli.CommandLineUtils;

/**
 * 
 */
public class TestRunner
{

    // public String methodName = "main";

    // public Class<?>[] parameterTypes = new Class<?>[] {String[].class};

    public boolean daemon = false;

    public MavenProject project;

    public Log log;

    public Log getLog()
    {
        return log;
    }

    public File playHome;

    /**
     * The main class to execute.
     */
    // public String mainClass = "play.server.Server";

    /**
     * The class arguments.
     */
    // public String[] arguments;
    // public Object[] arguments = new Object[] {new String[] {}};

    /**
     * A list of system properties to be passed. Note: as the execution is not forked, some system properties required
     * by the JVM cannot be passed here. Use MAVEN_OPTS or the exec:exec instead. See the user guide for more
     * information.
     */
    public Property[] systemProperties;

    public List<File> classpath;

    /**
     * Arguments for the executed program
     */
    private String commandlineArgs;

    private Properties originalSystemProperties;

    public Properties modifiedSystemProperties;

    public List<TestResults> result = null;

    private File applicationPath;

    private String playId;

    public TestRunner( File applicationPath, String playId )
    {
        this.applicationPath = applicationPath;
        this.playId = playId;
        result = new ArrayList<TestResults>();
    }

    public void run()
        throws IOException, MojoExecutionException/*
                                                   * nie podoba mi sie
                                                   */
    {
        IsolatedThreadGroup threadGroup = new IsolatedThreadGroup( "anything" /* name */);
        Thread bootstrapThread = new Thread( threadGroup, new Runnable()
        {
            public void run()
            {
                try
                {
                    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

                    Method playInitMethod =
                        getMethod( classLoader, "play.Play", "init", new Class<?>[] { File.class, String.class } );
                    Method playStartMethod = getMethod( classLoader, "play.Play", "start", new Class<?>[0] );
                    Method playStopMethod = getMethod( classLoader, "play.Play", "stop", new Class<?>[0] );
                    // tmp Field playUsePrecompiledField = getField( classLoader, "play.Play", "usePrecompiled" );
                    Field playPluginsField = getField( classLoader, "play.Play", "plugins" );

                    Method playPluginBeforeInvocationMethod =
                        getMethod( classLoader, "play.PlayPlugin", "beforeInvocation", new Class<?>[0] );
                    Method playPluginAfterInvocationMethod =
                        getMethod( classLoader, "play.PlayPlugin", "afterInvocation", new Class<?>[0] );
                    Method playPluginInvocationFinallyMethod =
                        getMethod( classLoader, "play.PlayPlugin", "invocationFinally", new Class<?>[0] );

                    Method testEngineAllUnitTestsMethod =
                        getMethod( classLoader, "play.test.TestEngine", "allUnitTests", new Class<?>[] {} );
                    Method testEngineAllFunctionalTestsMethod =
                        getMethod( classLoader, "play.test.TestEngine", "allFunctionalTests", new Class<?>[] {} );
                    Method testEngineRunMethod =
                        getMethod( classLoader, "play.test.TestEngine", "run", new Class<?>[] { String.class } );

                    Field testResultsPassedField = getField( classLoader, "play.test.TestEngine$TestResults", "passed" );
                    Field testResultsResultsField =
                        getField( classLoader, "play.test.TestEngine$TestResults", "results" );

                    Field testResultNameField = getField( classLoader, "play.test.TestEngine$TestResult", "name" );
                    Field testResultErrorField = getField( classLoader, "play.test.TestEngine$TestResult", "error" );
                    Field testResultPassedField = getField( classLoader, "play.test.TestEngine$TestResult", "passed" );
                    Field testResultTimeField = getField( classLoader, "play.test.TestEngine$TestResult", "time" );
                    Field testResultTraceField = getField( classLoader, "play.test.TestEngine$TestResult", "trace" );
                    // not used Field testResultSourceInfosField = getField(classLoader,
                    // "play.test.TestEngine$TestResult", "sourceInfos");
                    // not used Field testResultSourceCodeField = getField(classLoader,
                    // "play.test.TestEngine$TestResult", "sourceCode");
                    // not used Field testResultSourceFileField = getField(classLoader,
                    // "play.test.TestEngine$TestResult", "sourceFile");
                    // not used Field testResultSourceLineField = getField(classLoader,
                    // "play.test.TestEngine$TestResult", "sourceLine");

                    // initialize Play! engine
                    // tmp playUsePrecompiledField.setBoolean(playUsePrecompiledField, true);
                    playInitMethod.invoke( playInitMethod, new Object[] { applicationPath, playId } );

                    // start Play! engine
                    playStartMethod.invoke( playStartMethod, new Object[0] );

                    // initialize plugins (see: Invoker)
                    List<Object> playPlugins = (List<Object>) playPluginsField.get( playPluginsField );
                    for ( int i = 0; i < playPlugins.size(); i++ )
                    {
                        Object playPlugin = playPlugins.get( i );
                        playPluginBeforeInvocationMethod.invoke( playPlugin, new Object[0] );
                    }

                    try
                    {
                        printToConsole( "-------------------------------------------------------%n" );
                        printToConsole( " T E S T S%n" );
                        printToConsole( "-------------------------------------------------------%n" );

                        // get all unit test classes
                        List<Class> unitTestClasses =
                            (List<Class>) testEngineAllUnitTestsMethod.invoke( testEngineAllUnitTestsMethod,
                                                                               new Object[0] );
                        List<Class> functionalTestClasses =
                            (List<Class>) testEngineAllFunctionalTestsMethod.invoke( testEngineAllFunctionalTestsMethod,
                                                                                     new Object[0] );

                        List<Class> testsToRun = new ArrayList<Class>();
                        testsToRun.addAll( unitTestClasses );
                        // not ready yet testsToRun.addAll( functionalTestClasses );

                        // run tests
                        for ( Class testClass : testsToRun )
                        {
                            String className = testClass.getName();
                            printToConsole( "Running %s%n", className );

                            Object testResults =
                                testEngineRunMethod.invoke( testEngineRunMethod, new Object[] { className } );
                            TestResults myTestResults = new TestResults( className );

                            boolean classPassed = testResultsPassedField.getBoolean( testResults );
                            myTestResults.passed = classPassed;
                            // System.out.println("passed:"+classPassed);
                            List<Object> results = (List<Object>) testResultsResultsField.get( testResults );
                            // System.out.println("results:");
                            for ( int j = 0; j < results.size(); j++ )
                            {
                                Object result = results.get( j );
                                TestResult myTestResult = new TestResult();
                                // System.out.println(" result:");
                                String name = (String) testResultNameField.get( result );
                                myTestResult.name = name;
                                // System.out.println("  name:"+name);
                                String error = (String) testResultErrorField.get( result );
                                myTestResult.error = error;
                                // System.out.println("  error:"+error);
                                boolean methodPassed = testResultPassedField.getBoolean( result );
                                myTestResult.passed = methodPassed;
                                // System.out.println("  passed:"+methodPassed);
                                long time = testResultTimeField.getLong( result );
                                myTestResult.time = time;
                                // System.out.println("  time:"+time);
                                String trace = (String) testResultTraceField.get( result );
                                myTestResult.trace = trace;
                                // System.out.println("  trace:"+trace);
                                /*
                                 * not used String sourceInfos = (String)testResultSourceInfosField.get(result);
                                 * myTestResult.sourceInfos = sourceInfos;
                                 * System.out.println("  sourceInfos:"+sourceInfos); String sourceCode =
                                 * (String)testResultSourceCodeField.get(result); myTestResult.sourceCode = sourceCode;
                                 * System.out.println("  sourceCode:"+sourceCode); String sourceFile =
                                 * (String)testResultSourceFileField.get(result); myTestResult.sourceFile = sourceFile;
                                 * System.out.println("  sourceFile:"+sourceFile); int sourceLine =
                                 * testResultSourceLineField.getInt(result); myTestResult.sourceLine = sourceLine;
                                 * System.out.println("  sourceLine:"+sourceLine); not used
                                 */

                                myTestResults.results.add( myTestResult );
                            }

                            result.add( myTestResults );

                            String lineSuffix = "";
                            if ( myTestResults.getErrorsCount() > 0 || myTestResults.getFailuresCount() > 0 )
                            {
                                lineSuffix = " <<< FAILURE!";
                            }
                            printToConsole( "Tests run: %d, Failures: %d, Errors: %d, Skipped: %d, Time elapsed: %s sec%s%n",
                                            myTestResults.getCount(), myTestResults.getFailuresCount(),
                                            myTestResults.getErrorsCount(), myTestResults.getSkippedCount(),
                                            elapsedTimeAsString( myTestResults.getSummarizedTime() ), lineSuffix );
                        }

                        int allCount = 0;
                        int errorsCount = 0;
                        int failuresCount = 0;
                        // przeniesc ponizsze do mojo?
                        printToConsole( "%nResults :%n%n" );
                        boolean printed = false;
                        for ( TestResults testResults : result )
                        {
                            for ( TestResult testResult : testResults.results )
                            {
                                allCount++;
                                if ( testResult.isFailure() )
                                {
                                    failuresCount++;
                                    if ( !printed )
                                    {
                                        printToConsole( "Failed tests:%n" );
                                        printed = true;
                                    }
                                    printToConsole( "  %s(%s)%n", testResult.name, testResults.className );
                                }
                            }
                        }
                        if ( printed )
                        {
                            printToConsole( "%n" );
                        }

                        printed = false;
                        for ( TestResults testResults : result )
                        {
                            for ( TestResult testResult : testResults.results )
                            {
                                if ( testResult.isError() )
                                {
                                    errorsCount++;
                                    if ( !printed )
                                    {
                                        printToConsole( "Tests in error:%n" );
                                        printed = true;
                                    }
                                    printToConsole( "  %s(%s)%n", testResult.name, testResults.className );
                                }
                            }
                        }
                        if ( printed )
                        {
                            printToConsole( "%n" );
                        }

                        // TODO-implement skippedCount
                        printToConsole( "Tests run: %d, Failures: %d, Errors: %d, Skipped: %d%n%n", allCount,
                                        failuresCount, errorsCount, 0 );
                    }
                    finally
                    {
                        // finalize plugins
                        for ( int i = 0; i < playPlugins.size(); i++ )
                        {
                            Object playPlugin = playPlugins.get( i );
                            playPluginAfterInvocationMethod.invoke( playPlugin, new Object[0] );
                        }
                        for ( int i = 0; i < playPlugins.size(); i++ )
                        {
                            Object playPlugin = playPlugins.get( i );
                            playPluginInvocationFinallyMethod.invoke( playPlugin, new Object[0] );
                        }

                        // stop Play! engine
                        playStopMethod.invoke( playStopMethod, new Object[0] );
                    }
                }
                catch ( NoSuchMethodException e )
                { // just pass it on
                    Thread.currentThread().getThreadGroup().uncaughtException( Thread.currentThread(),
                                                                               new Exception(
                                                                                              "The specified mainClass doesn't contain a main method with appropriate signature.",
                                                                                              e ) );
                }
                catch ( Exception e )
                { // just pass it on
                    Thread.currentThread().getThreadGroup().uncaughtException( Thread.currentThread(), e );
                }
            }
        }, "a" + "." + "b" + "()" );
        bootstrapThread.setContextClassLoader( getClassLoader() );
        setSystemProperties();
        try
        {
            bootstrapThread.setDaemon( daemon );
            bootstrapThread.start();
            joinNonDaemonThreads( threadGroup );
        }
        finally
        {
            restoreSystemProperties();
        }

        synchronized ( threadGroup )
        {
            if ( threadGroup.uncaughtException != null )
            {
                throw new MojoExecutionException( "An exception occured while executing the Java class. "
                    + threadGroup.uncaughtException.getMessage(), threadGroup.uncaughtException );
            }
        }
    }

    private void printToConsole( String text, Object... args )
    {
        System.out.printf( text, args );
    }

    private Method getMethod( ClassLoader classLoader, String className, String methodName, Class<?>... parameterTypes )
        throws ClassNotFoundException, NoSuchMethodException
    {
        Method result = classLoader.loadClass( className ).getMethod( methodName, parameterTypes );
        // if (!result.isAccessible()) {
        // result.setAccessible(true);
        // }
        return result;
    }

    private Field getField( ClassLoader classLoader, String className, String fieldName )
        throws ClassNotFoundException, NoSuchFieldException
    {
        Field result = classLoader.loadClass( className ).getField( fieldName );
        // if (!result.isAccessible()) {
        // result.setAccessible(true);
        // }
        return result;
    }

    /**
     * Set up a classloader for the execution of the main class.
     * 
     * @return the classloader
     * @throws MojoExecutionException if a problem happens
     */
    private ClassLoader getClassLoader()
        throws MalformedURLException/* MojoExecutionException */
    {
        List<URL> classpathURLs = new ArrayList<URL>();
        if ( classpath != null )
        {
            for ( int i = 0; i < classpath.size(); i++ )
            {
                classpathURLs.add( classpath.get( i ).toURI().toURL() );
            }
        }
        return new URLClassLoader( (URL[]) classpathURLs.toArray( new URL[classpathURLs.size()] ) );
    }

    private Properties copyOf( Properties properties )
    {
        Properties result = new Properties();
        for ( Object key : properties.keySet() )
        {
            result.put( key, properties.get( key ) );
        }
        return result;
    }

    /**
     * Pass any given system properties to the java system properties.
     */
    private void setSystemProperties()
    {
        if ( systemProperties != null )
        {
            originalSystemProperties = copyOf( System.getProperties() );
            for ( int i = 0; i < systemProperties.length; i++ )
            {
                Property systemProperty = systemProperties[i];
                String value = systemProperty.getValue();
                System.setProperty( systemProperty.getKey(), value == null ? "" : value );
                modifiedSystemProperties = copyOf( System.getProperties() );
            }
        }
    }

    private void restoreSystemProperties()
    {
        if ( originalSystemProperties != null )
        {
            System.setProperties( originalSystemProperties );
        }
    }

    /**
     * @return true of the mojo has command line arguments
     */
    protected boolean hasCommandlineArgs()
    {
        return ( commandlineArgs != null );
    }

    /**
     * Parses the argument string given by the user. Strings are recognized as everything between STRING_WRAPPER.
     * PARAMETER_DELIMITER is ignored inside a string. STRING_WRAPPER and PARAMETER_DELIMITER can be escaped using
     * ESCAPE_CHAR.
     * 
     * @return Array of String representing the arguments
     * @throws MojoExecutionException for wrong formatted arguments
     */
    protected String[] parseCommandlineArgs()
        throws MojoExecutionException
    {
        if ( commandlineArgs == null )
        {
            return null;
        }
        else
        {
            try
            {
                return CommandLineUtils.translateCommandline( commandlineArgs );
            }
            catch ( Exception e )
            {
                throw new MojoExecutionException( e.getMessage() );
            }
        }
    }

    /**
     * a ThreadGroup to isolate execution and collect exceptions.
     */
    class IsolatedThreadGroup
        extends ThreadGroup
    {
        protected/* private */Throwable uncaughtException; // synchronize access
                                                           // to this

        public IsolatedThreadGroup( String name )
        {
            super( name );
        }

        public void uncaughtException( Thread thread, Throwable throwable )
        {
            if ( throwable instanceof ThreadDeath )
            {
                return; // harmless
            }
            boolean doLog = false;
            synchronized ( this )
            {
                if ( uncaughtException == null ) // only remember the first one
                {
                    uncaughtException = throwable; // will be reported
                                                   // eventually
                }
                else
                {
                    doLog = true;
                }
            }
            if ( doLog )
            {
                getLog().warn( "an additional exception was thrown", throwable );
            }
        }
    }

    private void joinNonDaemonThreads( ThreadGroup threadGroup )
    {
        boolean foundNonDaemon;
        do
        {
            foundNonDaemon = false;
            Collection<Thread> threads = getActiveThreads( threadGroup );
            for ( Iterator<Thread> iter = threads.iterator(); iter.hasNext(); )
            {
                Thread thread = (Thread) iter.next();
                if ( thread.isDaemon() )
                {
                    continue;
                }
                foundNonDaemon = true; // try again; maybe more threads were
                                       // created while we were busy
                joinThread( thread, 0 );
            }
        }
        while ( foundNonDaemon );
    }

    private void joinThread( Thread thread, long timeoutMsecs )
    {
        try
        {
            getLog().debug( "joining on thread " + thread );
            thread.join( timeoutMsecs );
        }
        catch ( InterruptedException e )
        {
            Thread.currentThread().interrupt(); // good practice if don't throw
            getLog().warn( "interrupted while joining against thread " + thread, e ); // not expected!
        }
        if ( thread.isAlive() ) // generally abnormal
        {
            getLog().warn( "thread " + thread + " was interrupted but is still alive after waiting at least "
                               + timeoutMsecs + "msecs" );
        }
    }

    private Collection<Thread> getActiveThreads( ThreadGroup threadGroup )
    {
        Thread[] threads = new Thread[threadGroup.activeCount()];
        int numThreads = threadGroup.enumerate( threads );
        Collection<Thread> result = new ArrayList<Thread>( numThreads );
        for ( int i = 0; i < threads.length && threads[i] != null; i++ )
        {
            result.add( threads[i] );
        }
        return result; // note: result should be modifiable
    }

    public static class TestResults
    {

        public String className;

        public List<TestResult> results = new ArrayList<TestResult>();

        public boolean passed = true;

        public TestResults( String className )
        {
            this.className = className;
        }

        public long getSummarizedTime()
        {
            long result = 0;
            for ( TestResult testResult : results )
            {
                result += testResult.time;
            }
            return result;
        }

        public int getCount()
        {
            return results.size();
        }

        public int getSkippedCount()
        {
            return 0;// TODO-implement
        }

        public int getFailuresCount()
        {
            int result = 0;
            for ( TestResult testResult : results )
            {
                if ( testResult.isFailure() )
                {
                    result++;
                }
            }
            return result;
        }

        public int getErrorsCount()
        {
            int result = 0;
            for ( TestResult testResult : results )
            {
                if ( testResult.isError() )
                {
                    result++;
                }
            }
            return result;
        }

        public int getPassedCount()
        {
            int result = 0;
            for ( TestResult testResult : results )
            {
                if ( testResult.isSuccess() )
                {
                    result++;
                }
            }
            return result;
        }

    }

    public static class TestResult
    {
        public String name;

        public String error;

        public boolean passed = true;

        public long time;

        public String trace;

        // not used public String sourceInfos;
        // not used public String sourceCode;
        // not used public String sourceFile;
        // not used public int sourceLine;

        public boolean isSuccess()
        {
            return passed;
        }

        public boolean isFailure()
        {
            return !passed && !error.startsWith( "A " );
        }

        public boolean isError()
        {
            return !passed && error.startsWith( "A " );
        }
    }

    /* copied from surefire AbstractReporter class */
    private NumberFormat numberFormat = NumberFormat.getInstance( Locale.ENGLISH );

    private static final int MS_PER_SEC = 1000;

    protected String elapsedTimeAsString( long runTime )
    {
        return numberFormat.format( (double) runTime / MS_PER_SEC );
    }
    /* copied from surefire AbstractReporter class - end */
}
