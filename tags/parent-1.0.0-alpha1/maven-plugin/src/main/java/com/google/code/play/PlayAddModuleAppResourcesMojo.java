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
import java.util.Map;

import org.apache.maven.model.Resource;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * Adds dependent modules resources ('app' directories) to Play! application.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 * @goal add-module-app-resources
 * @phase generate-resources
 */
public class PlayAddModuleAppResourcesMojo
    extends AbstractPlayMojo
{

    /**
     * ...
     * 
     * @parameter expression="${play.compileApp}"
     */
    private boolean compileApp;

    protected void internalExecute()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        if ( compileApp )
        {
            File baseDir = project.getBasedir();

            ConfigurationParser configParser = new ConfigurationParser( new File( baseDir, "conf" ), playHome, playId );
            configParser.parse();
            Map<String, File> modules = configParser.getModules();

            for ( String moduleName : modules.keySet() )
            {
                File modulePath = modules.get( moduleName );
                File moduleAppPath = new File( modulePath, "app" );
                if ( moduleAppPath.isDirectory() )
                {
                    Resource resource = new Resource();
                    resource.setDirectory( moduleAppPath.getAbsolutePath() );
                    resource.addExclude( "**/*.java" );
                    project.addResource( resource );
                    this.getLog().debug( "Added resource: " + resource.getDirectory() );
                }
            }
        }
    }

}

/*
 * TODO: - Add resources from "conf" directory (see what to exclude in "war" mojo)
 */
