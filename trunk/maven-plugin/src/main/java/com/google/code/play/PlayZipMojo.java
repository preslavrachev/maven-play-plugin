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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;

/**
 * Package Play! application as a ZIP achive.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 * @goal zip
 * @phase package
 * @requiresDependencyResolution runtime
 */
public class PlayZipMojo
    extends AbstractPlayMojo
{

    /**
     * The directory for the generated ZIP.
     * 
     * @parameter default-value="${project.build.directory}"
     * @required
     * @since 1.0.0
     */
    private String outputDirectory;

    /**
     * Generated ZIP file name.
     * 
     * @parameter default-value="${project.build.finalName}"
     * @required
     * @since 1.0.0
     */
    private String zipName;

    /**
     * Include filter
     * 
     * @parameter expression="${play.zipIncludes}" default-value="app/**,conf/**,public/**,tags/**"
     * @required
     * @since 1.0.0
     */
    private String zipIncludes;

    /**
     * Exclude filter
     * 
     * @parameter expression="${play.zipExcludes}"
     * @since 1.0.0
     */
    private String zipExcludes;

    /**
     * Package lib and modules directories or not
     * 
     * @parameter expression="${play.zipDependencies}" default-value="false"
     * @since 1.0.0
     */
    private boolean zipDependencies;

    /**
     * To look up Archiver/UnArchiver implementations.
     *
     * @component role="org.codehaus.plexus.archiver.manager.ArchiverManager"
     * @required
     */
    private ArchiverManager archiverManager;
    
    protected void internalExecute()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        try
        {
            File baseDir = project.getBasedir();
            File destFile = new File( outputDirectory, zipName + ".zip" );
            // File destFile = new File( outputDirectory, getDestinationFileName() );

            /*Zip*/Archiver zipArchiver = archiverManager.getArchiver( "zip" );//new ZipArchiver();
            zipArchiver.setDuplicateBehavior( Archiver.DUPLICATES_FAIL );// Just in case
            zipArchiver.setDestFile( destFile );

            getLog().debug( "Zip includes: " + zipIncludes );
            getLog().debug( "Zip excludes: " + zipExcludes );
            String[] includes = (zipIncludes != null ? zipIncludes.split(",") : null);
            String[] excludes = (zipExcludes != null ? zipExcludes.split(",") : null);
            zipArchiver.addDirectory( baseDir, includes, excludes);
            
            if (zipDependencies)
            {
                Map<Artifact, String> moduleTypeArtifacts = processModuleDependencies(zipArchiver);
                processJarDependencies(zipArchiver, moduleTypeArtifacts); 
            }
            zipArchiver.createArchive();

            project.getArtifact().setFile( destFile );
        }
        catch ( ArchiverException e )
        {
            throw new MojoExecutionException( "?", e );
        }
        catch ( NoSuchArchiverException e )
        {
            throw new MojoExecutionException( "?", e );
        }
    }

    private Map<Artifact, String> processModuleDependencies(Archiver archiver) throws ArchiverException, NoSuchArchiverException, IOException 
    {
        Map<Artifact, String> moduleTypeArtifacts = new HashMap<Artifact, String>();
        
        Set<?> artifacts = project.getArtifacts();

        for ( Iterator<?> iter = artifacts.iterator(); iter.hasNext(); )
        {
            Artifact artifact = (Artifact) iter.next();
            if ( "zip".equals( artifact.getType() ) )
            {
                if ( "module".equals( artifact.getClassifier() )
                    || "module-resources".equals( artifact.getClassifier() ) )
                {
                    //System.out.println("module: " + artifact.getGroupId() + ":" + artifact.getArtifactId());
                    
                    // TODO-dorobic detekcje konfliktow nazw
                    // System.out.println( "artifact: groupId=" + artifact.getGroupId() + ":artifactId="
                    // + artifact.getArtifactId() + ":type=" + artifact.getType() + ":classifier="
                    // + artifact.getClassifier() + ":scope=" + artifact.getScope() );
                    File zipFile = artifact.getFile();
                    String moduleName = artifact.getArtifactId();
                    if ( moduleName.startsWith( "play-" ) )
                    {
                        moduleName = moduleName.substring( "play-".length() );
                    }
                    String moduleSubDir = String.format("%s-%s", moduleName, artifact.getVersion());
                    archiver.addArchivedFileSet( zipFile, "modules/" + moduleSubDir + "/" );

                    moduleTypeArtifacts.put( artifact, moduleSubDir );
                    //System.out.println("module: " + artifact.getGroupId() + ":" + artifact.getArtifactId() + " added");
                }
            }
        }
        return moduleTypeArtifacts;
    }

    private void processJarDependencies(Archiver archiver, Map<Artifact, String> moduleTypeArtifacts) throws ArchiverException, NoSuchArchiverException, IOException 
    {
        Set<?> artifacts = project.getArtifacts();

        for ( Iterator<?> iter = artifacts.iterator(); iter.hasNext(); )
        {
            Artifact artifact = (Artifact) iter.next();
            if ( "jar".equals( artifact.getType() ) )
            {
                //System.out.println("jar: " + artifact.getGroupId() + ":" + artifact.getArtifactId());
                File jarFile = artifact.getFile();
                String libDir = "lib";
                for (Artifact moduleArtifact: moduleTypeArtifacts.keySet())
                {
                    //System.out.println("checking module: " + moduleArtifact.getGroupId() + ":" + moduleArtifact.getArtifactId());
                    if ( artifact.getGroupId().equals( moduleArtifact.getGroupId() )
                        && artifact.getArtifactId().equals( moduleArtifact.getArtifactId() ) )
                    {
                        libDir = String.format( "modules/%s/lib", moduleTypeArtifacts.get( moduleArtifact ) );
                        //System.out.println("checked ok - lib is " + libDir.getCanonicalPath());
                        break;
                    }
                }
                //System.out.println("jar: " + artifact.getGroupId() + ":" + artifact.getArtifactId() + " added to " + libDir);
                archiver.addFile( jarFile, libDir + "/" + jarFile.getName() );
            }
        }
    }
    
}
