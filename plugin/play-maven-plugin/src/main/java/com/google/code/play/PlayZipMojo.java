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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.shared.dependency.tree.DependencyTreeBuilderException;

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
    extends AbstractDependencyProcessingPlayMojo
{
    /**
     * Application resources include filter
     * 
     * @parameter expression="${play.zipIncludes}" default-value="app/**,conf/**,public/**,tags/**"
     * @since 1.0.0
     */
    private String zipIncludes;

    /**
     * Application resources exclude filter.
     * 
     * @parameter expression="${play.zipExcludes}" default-value=""
     * @since 1.0.0
     */
    private String zipExcludes;

    /**
     * Should project dependencies ("lib" and "modules" directories) be packaged. No include/exclude filters.
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
            File zipOutputDirectory = new File( project.getBuild().getDirectory() );
            String zipName = project.getBuild().getFinalName();
            File destFile = new File( zipOutputDirectory, zipName + ".zip" );

            Archiver zipArchiver = archiverManager.getArchiver( "zip" );
            zipArchiver.setDuplicateBehavior( Archiver.DUPLICATES_FAIL ); // Just in case
            zipArchiver.setDestFile( destFile );

            getLog().debug( "Zip includes: " + zipIncludes );
            getLog().debug( "Zip excludes: " + zipExcludes );
            String[] includes = ( zipIncludes != null ? zipIncludes.split( "," ) : null );
            String[] excludes = ( zipExcludes != null ? zipExcludes.split( "," ) : null );
            zipArchiver.addDirectory( baseDir, includes, excludes );

            if ( zipDependencies )
            {
                processDependencies( zipArchiver );
            }
            zipArchiver.createArchive();

            project.getArtifact().setFile( destFile );
        }
        catch ( ArchiverException e )
        {
            throw new MojoExecutionException( "?", e );
        }
        catch ( DependencyTreeBuilderException e )
        {
            throw new MojoExecutionException( "?", e );
        }
        catch ( NoSuchArchiverException e )
        {
            throw new MojoExecutionException( "?", e );
        }
    }

    private void processDependencies( Archiver zipArchiver )
        throws DependencyTreeBuilderException, IOException
    {
        // preparation
        Set<?> projectArtifacts = project.getArtifacts();

        Set<Artifact> excludedArtifacts = new HashSet<Artifact>();
        /*Artifact playSeleniumJunit4Artifact =
            getDependencyArtifact( projectArtifacts, "com.google.code.maven-play-plugin", "play-selenium-junit4",
                                   "jar" );
        if ( playSeleniumJunit4Artifact != null )
        {
            excludedArtifacts.addAll( getDependencyArtifacts( projectArtifacts, playSeleniumJunit4Artifact ) );
        }*/

        Set<Artifact> filteredArtifacts = new HashSet<Artifact>(); // TODO-rename to filteredClassPathArtifacts
        for ( Iterator<?> iter = projectArtifacts.iterator(); iter.hasNext(); )
        {
            Artifact artifact = (Artifact) iter.next();
            if ( artifact.getArtifactHandler().isAddedToClasspath() && !excludedArtifacts.contains( artifact ) )
            {
                // TODO-add checkPotentialReactorProblem( artifact );
                filteredArtifacts.add( artifact );
            }
        }

        // modules/*/lib
        Map<String, Artifact> moduleArtifacts = findAllModuleArtifacts( false );
        for ( Map.Entry<String, Artifact> moduleArtifactEntry : moduleArtifacts.entrySet() )
        {
            String moduleName = moduleArtifactEntry.getKey();
            Artifact moduleZipArtifact = moduleArtifactEntry.getValue();

            File moduleZipFile = moduleZipArtifact.getFile();
            String moduleSubDir = String.format( "modules/%s-%s/", moduleName, moduleZipArtifact.getVersion() );
            zipArchiver.addArchivedFileSet( moduleZipFile, moduleSubDir );
            Set<Artifact> dependencySubtree = getModuleDependencyArtifacts( filteredArtifacts, moduleZipArtifact );
            for ( Artifact classPathArtifact : dependencySubtree )
            {
                File jarFile = classPathArtifact.getFile();
                String destinationFileName = jarFile.getName();
                // Scala module hack
                if ( "scala".equals( moduleName ) )
                {
                    destinationFileName = scalaHack( classPathArtifact );
                }
                String destinationPath =
                                String.format( "modules/%s-%s/lib/%s", moduleName,
                                               moduleZipArtifact.getVersion(), destinationFileName );
                zipArchiver.addFile( jarFile, destinationPath );
                filteredArtifacts.remove( classPathArtifact );
            }

        }

        // lib
        for ( Iterator<?> iter = filteredArtifacts.iterator(); iter.hasNext(); )
        {
            Artifact artifact = (Artifact) iter.next();
            File jarFile = artifact.getFile();
            String destinationFileName = "lib/" + jarFile.getName();
            zipArchiver.addFile( jarFile, destinationFileName );
        }
    }

    private String scalaHack( Artifact dependencyArtifact ) throws IOException
    {
        String destinationFileName = dependencyArtifact.getFile().getName();
        if ( "org.scala-lang".equals( dependencyArtifact.getGroupId() )
            && ( "scala-compiler".equals( dependencyArtifact.getArtifactId() ) || "scala-library".equals( dependencyArtifact.getArtifactId() ) )
            && "jar".equals( dependencyArtifact.getType() ) )
        {
            destinationFileName = dependencyArtifact.getArtifactId() + ".jar";
        }
        return destinationFileName;
    }

}

// TODO - add name conflicts detection for modules and jars
