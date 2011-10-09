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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Adds application sources ('app' directory) to Play! application.
 * Adds application resources ('app' directory) to Play! application.
 * ...
 * finish!
 * ...
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 * @goal initialize
 * @phase initialize
 * @requiresDependencyResolution compile
 */
public class PlayInitializeMojo
    extends AbstractPlayMojo
{

    /**
     * ...
     * 
     * @parameter expression="${play.compileApp}" default-value="true"
     */
    private boolean compileApp = true;

    /**
     * ...
     * 
     * @parameter expression="${play.compileTest}" default-value="true"
     */
    private boolean compileTest = true;

    protected void internalExecute()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        File baseDir = project.getBasedir();
        File confDir = new File( baseDir, "conf" );
        File configurationFile = new File( confDir, "application.conf" );

        ConfigurationParser configParser = new ConfigurationParser( configurationFile, playHome, playId );
        configParser.parse();
        
        // Get modules
        Map<String, File> modules = configParser.getModules(); // Play 1.1.x
        // Play 1.2.x
        String playVersion = null;
        Set<?> artifacts = project.getArtifacts();
        for ( Iterator<?> iter = artifacts.iterator(); iter.hasNext(); )
        {
            Artifact artifact = (Artifact) iter.next();
            //System.out.println("artifact: " + artifact.getGroupId() + ":" + artifact.getArtifactId());
            if ("play".equals( artifact.getArtifactId() ) ) {
                //temporary solution, maybe use zip to unzip resource from a jar file
                playVersion = artifact.getVersion();
                //System.out.println("Play version: " + playVersion);
            //java.net.URL artifactUrl = artifact.getFile().toURI().toURL();
            }
        }

        getLog().debug( "Play! version: " + playVersion );
        if ( (playVersion != null/*nie podoba mi sie to*/) && "1.2".compareTo( /*Play.version*/playVersion ) <= 0 )
        {
            File modulesDir = new File( baseDir, "modules" );
            if ( modulesDir.isDirectory() )
            {
                File[] files = modulesDir.listFiles();
                if ( files != null )
                {
                    for ( File file : files )
                    {
                        String moduleName = file.getName();
                        if ( file.isDirectory() )
                        {
                            // module itself
                            getLog().debug( "Added module '" + moduleName + "': " + file.getAbsolutePath() );
                            modules.put( moduleName, file );
                        }
                        else if ( file.isFile() )
                        {
                            // shortcut to module located in "modules" subdirectory of Play! framework location
                            String realModulePath = new LineNumberReader(new InputStreamReader(new FileInputStream(file), "utf-8")).readLine();
                            //String realModulePath = play.libs.IO.readContentAsString( file );
                            file = new File( realModulePath );
                            getLog().debug( "Added module '" + moduleName + "': " + file.getAbsolutePath() );
                            modules.put( moduleName, file );
                        }
                    }
                }
            }
        }
        
        if ( compileApp )
        {
            File appPath = new File( baseDir, "app" );
            project.addCompileSourceRoot( appPath.getAbsolutePath() );
            getLog().debug( "Added source directory: " + appPath.getAbsolutePath() );

            Resource resource = new Resource();
            resource.setDirectory( appPath.getAbsolutePath() );
            resource.addExclude( "**/*.java" );
            project.addResource( resource );
            getLog().debug( "Added resource: " + resource.getDirectory() );

            File confPath = new File( baseDir, "conf" );
            resource = new Resource();
            resource.setDirectory( confPath.getAbsolutePath() );
            resource.addExclude( "application.conf" );
            resource.addExclude( "messages" );
            resource.addExclude( "messages.*" );
            resource.addExclude( "routes" );
            project.addResource( resource );
            getLog().debug( "Added resource: " + resource.getDirectory() );

            for ( String moduleName : modules.keySet() )
            {
                File modulePath = modules.get( moduleName );
                File moduleAppPath = new File( modulePath, "app" );
                if ( moduleAppPath.isDirectory() )
                {
                    project.addCompileSourceRoot( moduleAppPath.getAbsolutePath() );
                    getLog().debug( "Added source directory: " + moduleAppPath.getAbsolutePath() );

                    resource = new Resource();
                    resource.setDirectory( moduleAppPath.getAbsolutePath() );
                    resource.addExclude( "**/*.java" );
                    project.addResource( resource );
                    getLog().debug( "Added resource: " + resource.getDirectory() );
                }
            }
        }

        if ( compileTest )
        {
            File testPath = new File( baseDir, "test" );
            project.addTestCompileSourceRoot( testPath.getAbsolutePath() );
            getLog().debug( "Added test source directory: " + testPath.getAbsolutePath() );

            Resource resource = new Resource();
            resource.setDirectory( testPath.getAbsolutePath() );
            resource.addExclude( "**/*.java" );
            project.addTestResource( resource );
            getLog().debug( "Added test resource: " + resource.getDirectory() );
            
            // add test sources from dependent modules?
        }
    }
}
