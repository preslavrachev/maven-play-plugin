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

package com.google.code.play;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * ... //FIXME
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

            int classesGenerated = 0;
            for ( File playTestFile : playSeleniumTestFiles )
            {
                if ( playTestFile.isFile() )
                {
                    String playTestFileName = playTestFile.getName();
                    if ( playTestFileName.endsWith( ".test.html" ) )
                    {
                        String oryginalTestClassName =
                            playTestFileName.substring( 0, playTestFileName.indexOf( ".test.html" ) );
                        String javaTestClassName = oryginalTestClassName;
                        javaTestClassName = javaTestClassName.replace( ".", "_" );
                        javaTestClassName = javaTestClassName.replace( "-", "_" );
                        if ( Character.isDigit( javaTestClassName.charAt( 0 ) ) )
                        {
                            javaTestClassName = "_" + javaTestClassName;
                        }
                        File javaTestFile = new File( destDir, javaTestClassName + "Test.java" );
                        if ( !javaTestFile.exists() )
                        {
                            javaTestFile.getParentFile().mkdirs();// TODO-check the result and throw exception when
                                                                  // "false"
                            PrintWriter w =
                                new PrintWriter(
                                                 new BufferedWriter(
                                                                     new OutputStreamWriter(
                                                                                             new FileOutputStream(
                                                                                                                   javaTestFile ),
                                                                                             "UTF-8" ) ) );
                            try
                            {
                                generateTestSource( playTestFile.getName()/* maybe full path? */,
                                                    oryginalTestClassName, javaTestClassName, w );
                                classesGenerated++;
                            }
                            finally
                            {
                                w.flush();// ??
                                w.close();
                            }
                        }
                    }
                }
            }

            if ( classesGenerated == 0 )
            {
                getLog().info( "Nothing to generate - all Selenium JUnit4 test sources are up to date" );
            }
            project.addTestCompileSourceRoot( new File( project.getBuild().getDirectory(), "selenium/generated" ).getAbsolutePath() );
        }
    }

    private void generateTestSource( String playTestFileName, String oryginalTestClassName, String javaTestClassName,
                                     PrintWriter w )
        throws IOException, MojoExecutionException
    {
        w.println( "package selenium;" );// TODO parametrize
        w.println();
        w.println( "import org.junit.Test;" );
        w.println();
        w.println( "import com.google.code.play.selenium.PlaySeleniumTest;" );
        w.println();
        w.println( "public class " + javaTestClassName + "Test extends PlaySeleniumTest {" );
        w.println();
        w.println( "\t@Test" );
        w.println( "\tpublic void test" + javaTestClassName + "() throws Exception {" );// TODO-zrobic sensowna nazwe
        w.println( "\t\tseleniumTest(\"selenium/" + oryginalTestClassName + ".test.html\");" );
        w.println( "\t}" );
        w.println();
        w.println( "}" );
    }
}

// TODO
// - option to force test sources generation (not generating incrementally)
// - use includes/excludes?
// - proper xml unescaping
