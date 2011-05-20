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

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

/**
 * Package Play! framework and Play! application as one ZIP achive (standalone distribution).
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 * @goal uberzip
 * @phase package
 */
public class PlayUberZipMojo
    extends AbstractPlayMojo
{

    // private final static String[] libIncludes = new String[]{"*.jar"};

    // private final static String[] libExcludes = new String[]{"provided-*.jar"};

    // private final static String[] confIncludes = new String[]{"application.conf", "messages", "messages.*",
    // "routes"};

    // private final static String[] moduleExcludes = new String[]{"dist/**", "documentation/**", "lib/**",
    // "nbproject/**", "samples-and-tests/**", "src/**", "build.xml", "commands.py"};

    /**
     * The directory for the generated ZIP.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private String outputDirectory;

    /**
     * The name of the generated ZIP.
     * 
     * @parameter expression="${project.build.finalName}"
     * @required
     */
    private String zipName;

    /**
     * Classifier to add to the generated ZIP. If given, the artifact will be an attachment instead.??? The classifier
     * will not be applied to the jar file of the project - only to the zip file.
     * 
     * @parameter default-value="with-framework"
     */
    private String classifier;

    protected void internalExecute()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        try
        {
            File baseDir = project.getBasedir();
            File destFile = new File( outputDirectory, getDestinationFileName() );

            ConfigurationParser configParser = new ConfigurationParser( new File( baseDir, "conf" ), playHome, playId );
            configParser.parse();
            Map<String, File> modules = configParser.getModules();

            ZipArchiver zipArchiver = new ZipArchiver();
            zipArchiver.setDuplicateBehavior( Archiver.DUPLICATES_FAIL );// Just in case
            zipArchiver.setDestFile( destFile );

            // APPLICATION
            // app
            zipArchiver.addDirectory( new File( baseDir, "app" ), "app/", null, null );
            // conf
            zipArchiver.addDirectory( new File( baseDir, "conf" ), "conf/", null, null );
            // lib
            zipArchiver.addDirectory( new File( baseDir, "lib" ), "lib/", null, null/* albo: libIncludes, libExcludes */);// TODO-bez
                                                                                                                          // podkatalogow
            // public
            zipArchiver.addDirectory( new File( baseDir, "public" ), "public/", null, null );
            // tags
            if ( new File( baseDir, "tags" ).isDirectory() )
            {
                zipArchiver.addDirectory( new File( baseDir, "tags" ), "tags/", null, null );
            }

            // PLAY! FRAMEWORK
            // framework
            zipArchiver.addFile( new File( playHome, "framework/play.jar" ), "framework/play.jar" );
            zipArchiver.addDirectory( new File( playHome, "framework/lib" ), "framework/lib/", null, null/*
                                                                                                          * albo:
                                                                                                          * libIncludes,
                                                                                                          * libExcludes
                                                                                                          */);// TODO-bez
                                                                                                              // podkatalogow
            zipArchiver.addDirectory( new File( playHome, "framework/pym" ), "framework/pym/", null, null );
            zipArchiver.addDirectory( new File( playHome, "framework/templates" ), "framework/templates/", null, null );
            // modules
            for ( String moduleName : modules.keySet() )
            {
                File modulePath = modules.get( moduleName );
                zipArchiver.addDirectory( modulePath, "modules/" + modulePath.getName() + "/", null, new String[] {
                    "documentation/**", "nbproject/**", "src/**", "build.xml", "commands.py" } );
            }
            // python
            zipArchiver.addDirectory( new File( playHome, "python" ), "python/", null, null );
            // resources
            zipArchiver.addFile( new File( playHome, "resources/messages" ), "resources/messages" );
            // other
            zipArchiver.addDirectory( playHome, "", new String[] { "COPYING", "play", "play.bat", "repositories" },
                                      null );

            zipArchiver.createArchive();
        }
        catch ( ArchiverException e )
        {
            throw new MojoExecutionException( "?", e );
        }
    }

    private String getDestinationFileName()
    {
        StringBuffer buf = new StringBuffer();
        buf.append( zipName );
        if ( classifier != null && !"".equals( classifier ) )
        {
            if ( !classifier.startsWith( "-" ) )
            {
                buf.append( '-' );
            }
            buf.append( classifier );
        }
        buf.append( ".zip" );
        return buf.toString();
    }

}
