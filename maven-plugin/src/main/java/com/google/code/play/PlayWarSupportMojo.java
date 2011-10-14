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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
//import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Package Play! framework and Play! application as a WAR achive.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 * @goal war-support
 * @phase prepare-package
 */
public class PlayWarSupportMojo
    extends AbstractPlayMojo
{
    /**
     * The directory with Play! distribution.
     * 
     * @parameter expression="${play.home}"
     * @since 1.0.0
     */
    protected File playHome;

    /**
     * ...
     * 
     * @parameter expression="${play.id}" default-value="war"
     * @since 1.0.0
     */
    protected String playId;

    /**
     * The directory for the generated (filtered) files.
     * 
     * @parameter expression="${project.build.directory}/play/tmp"
     * @required
     * @since 1.0.0
     */
    private File outputDirectory;//??

    @Override
    protected void internalExecute()
        throws MojoExecutionException, MojoFailureException, IOException
    {
            checkPlayHome(playHome);
            
            File baseDir = project.getBasedir();
            File confDir = new File( baseDir, "conf" );
            File configurationFile = new File( confDir, "application.conf" );

            ConfigurationParser configParser = new ConfigurationParser( configurationFile, playId );
            configParser.parse();
            //Map<String, File> modules = configParser.getModules();

            /*File filteredApplicationConf =
                *///filterApplicationConf( new File( baseDir, "conf/application.conf" ), modules );

            /*File filteredWebXml = */filterWebXml( new File( playHome, "resources/war/web.xml" ), configParser.getApplicationName() );
    }

    private File filterWebXml( File webXml, String applicationName/*ConfigurationParser configParser*/ )
        throws IOException
    {
        if ( !outputDirectory.exists() )
        {
            if (!outputDirectory.mkdirs())
            {
                throw new IOException( String.format( "Cannot create \"%s\" directory", outputDirectory.getCanonicalPath() ) );
            }
        }
        File result = new File( outputDirectory, "filtered-web.xml" );
        BufferedReader reader = createBufferedFileReader( webXml, "UTF-8" );
        try
        {
            BufferedWriter writer = createBufferedFileWriter( result, "UTF-8" );
            try
            {
                getLog().debug( "web.xml file:" );
                String line = reader.readLine();
                while ( line != null )
                {
                    getLog().debug( "  " + line );
                    if ( line.indexOf( "%APPLICATION_NAME%" ) >= 0 )
                    {
                        line = line.replace( "%APPLICATION_NAME%", applicationName/*configParser.getApplicationName()*/ );
                    }
                    if ( line.indexOf( "%PLAY_ID%" ) >= 0 )
                    {
                        line = line.replace( "%PLAY_ID%", playId );
                    }
                    writer.write( line );
                    writer.newLine();
                    line = reader.readLine();
                }
            }
            finally
            {
                writer.close();
            }
        }
        finally
        {
            reader.close();
        }
        return result;
    }

/* not needed
    private File filterApplicationConf( File applicationConf, Map<String, File> modules )
        throws IOException
    {
        if ( !outputDirectory.exists() )
        {
            if (!outputDirectory.mkdirs())
            {
                throw new IOException( String.format( "Cannot create \"%s\" directory", outputDirectory.getCanonicalPath() ) );
            }
        }
        File result = new File( outputDirectory, "filtered-application.conf" );
        BufferedReader reader = createBufferedFileReader( applicationConf, "UTF-8" );
        try
        {
            BufferedWriter writer = createBufferedFileWriter( result, "UTF-8" );
            try
            {
                String line = reader.readLine();
                while ( line != null )
                {
                    if ( !line.trim().startsWith( "#" ) && line.contains( "${play.path}" ) )
                    {
                        line = line.replace( "${play.path}", ".." );
                    }
                    writer.write( line );
                    writer.newLine();
                    line = reader.readLine();
                }
            }
            finally
            {
                writer.close();
            }
        }
        finally
        {
            reader.close();
        }
        return result;
    }
*/

}

// TODO
/*
 * 1. filtrowanie 'web.xml' - zrobione, ale mozna usprawnic: - wczytywac 'web.xml' odpowiednim readerem xml wylapujacym
 * kodowanie w czasie rzeczywistym - co ze znakami nowej linii? - a moze wczytac inputstream'em? 3. ignorowanie plikow
 * wymienionych w 'war.exclude' deleteFrom(war_path, app.readConf('war.exclude').split("|")) Skąd ja to wziąłem? Nie
 * mogę znaleźć!
 */
