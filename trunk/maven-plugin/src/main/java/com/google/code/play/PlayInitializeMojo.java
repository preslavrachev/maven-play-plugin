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

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Adds application sources ('app' directory) to Play! application.
 * Adds application resources ('app' directory) to Play! application.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 * @goal initialize
 * @phase initialize
 */
public class PlayInitializeMojo
    extends AbstractPlayMojo
{

    /**
     * ...
     * 
     * @parameter expression="${play.compileApp}"
     */
    private boolean compileApp;

    /**
     * ...
     * 
     * @parameter expression="${play.compileTest}"
     */
    private boolean compileTest;

    protected void internalExecute()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        File baseDir = project.getBasedir();

        ConfigurationParser configParser = new ConfigurationParser( new File( baseDir, "conf" ), playHome, playId );
        configParser.parse();
        Map<String, File> modules = configParser.getModules();

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
