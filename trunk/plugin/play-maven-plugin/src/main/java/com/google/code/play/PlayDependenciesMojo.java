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

import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;

import org.codehaus.plexus.util.FileUtils;

/**
 * Extracts project dependencies to "lib" and "modules" directories.
 * It's like Play! framework's "dependencies" command, but uses Maven dependencies,
 * instead of "conf/dependencies.yml" file.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 * @goal dependencies
 * @requiresDependencyResolution test
 */
public class PlayDependenciesMojo
    extends AbstractDependencyProcessingPlayMojo
{

    /**
     * Skip dependencies extraction.
     * 
     * @parameter expression="${play.dependenciesSkip}" default-value="false"
     * @required
     * @since 1.0.0
     */
    private boolean dependenciesSkip = false;

    /**
     * Should project's "lib" and "modules" subdirectories be cleaned before dependency resolution.
     * If true, dependenciesOverwrite is meaningless.
     * 
     * @parameter expression="${play.dependenciesClean}" default-value="false"
     * @since 1.0.0
     */
    private boolean dependenciesClean = false;

    /**
     * Should existing dependencies be overwritten.
     * 
     * @parameter expression="${play.dependenciesOverwrite}" default-value="false"
     * @since 1.0.0
     */
    private boolean dependenciesOverwrite = false;

    /**
     * Should jar dependencies be processed. They are necessary for Play! Framework,
     * but not needed for Maven build (Maven uses dependency mechanism).
     * 
     * @parameter expression="${play.dependenciesSkipJars}" default-value="false"
     * @since 1.0.0
     */
    private boolean dependenciesSkipJars = false; // TODO-change to true

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
        if ( dependenciesSkip )
        {
            getLog().info( "Dependencies extraction skipped" );
            return;
        }

        File baseDir = project.getBasedir();

        try
        {
            if ( dependenciesClean )
            {
                if ( !dependenciesSkipJars )
                {
                    FileUtils.deleteDirectory( new File( baseDir, "lib" ) );
                }
                FileUtils.deleteDirectory( new File( baseDir, "modules" ) );
            }

            Set<?> projectArtifacts = project.getArtifacts();

            Set<Artifact> excludedArtifacts = new HashSet<Artifact>();
            Artifact playSeleniumJunit4Artifact =
                            getDependencyArtifact( projectArtifacts, "com.google.code.maven-play-plugin",
                                                    "play-selenium-junit4", "jar" );
            if ( playSeleniumJunit4Artifact != null )
            {
                excludedArtifacts.addAll( getDependencyArtifacts( projectArtifacts, playSeleniumJunit4Artifact ) );
            }

            Set<Artifact> filteredArtifacts = new HashSet<Artifact>(); // TODO-rename to filteredClassPathArtifacts
            for ( Iterator<?> iter = projectArtifacts.iterator(); iter.hasNext(); )
            {
                Artifact artifact = (Artifact) iter.next();
                if ( artifact.getArtifactHandler().isAddedToClasspath()
                    && !Artifact.SCOPE_PROVIDED.equals( artifact.getScope() ) && !excludedArtifacts.contains( artifact ) )
                {
                    // TODO-add checkPotentialReactorProblem( artifact );
                    filteredArtifacts.add( artifact );
                }
            }

            // modules/*/lib
            Map<String, Artifact> moduleArtifacts = findAllModuleArtifacts( true );
            for ( Map.Entry<String, Artifact> moduleArtifactEntry : moduleArtifacts.entrySet() )
            {
                String moduleName = moduleArtifactEntry.getKey();
                Artifact moduleZipArtifact = moduleArtifactEntry.getValue();

                if ( !Artifact.SCOPE_PROVIDED.equals( moduleZipArtifact.getScope() ) )
                {
                    checkPotentialReactorProblem( moduleZipArtifact );

                    File moduleZipFile = moduleZipArtifact.getFile();
                    String moduleSubDir =
                                    String.format( "modules/%s-%s/", moduleName, moduleZipArtifact.getVersion() );
                    File moduleDirectory = new File( baseDir, moduleSubDir );
                    createModuleDirectory( moduleDirectory, dependenciesOverwrite
                                           || moduleDirectory.lastModified() < moduleZipFile.lastModified() );
                    if ( moduleDirectory.list().length == 0 )
                    {
                        UnArchiver zipUnArchiver = archiverManager.getUnArchiver( "zip" );
                        zipUnArchiver.setSourceFile( moduleZipFile );
                        zipUnArchiver.setDestDirectory( moduleDirectory );
                        zipUnArchiver.setOverwrite( false/* ??true */ );
                        zipUnArchiver.extract();
                        moduleDirectory.setLastModified( System.currentTimeMillis() );
                        if ( !dependenciesSkipJars )
                        {
                            Set<Artifact> dependencySubtree = getModuleDependencyArtifacts( filteredArtifacts, moduleZipArtifact );

                            if ( !dependencySubtree.isEmpty() )
                            {
                                File moduleLibDir = new File( moduleDirectory, "lib" );
                                createLibDirectory( moduleLibDir );

                                for ( Artifact classPathArtifact : dependencySubtree )
                                {
                                    File jarFile = classPathArtifact.getFile();
                                    if ( dependenciesOverwrite )
                                    {
                                        FileUtils.copyFileToDirectory( jarFile, moduleLibDir );
                                    }
                                    else
                                    {
                                        if ( jarFile == null )
                                        {
                                            getLog().info( "null file" ); // TODO-???
                                        }
                                        FileUtils.copyFileToDirectoryIfModified( jarFile, moduleLibDir );
                                    }
                                    filteredArtifacts.remove( classPathArtifact );
                                }
                            }
                        }
                    }
                }
            }

            // lib
            if ( !dependenciesSkipJars && !filteredArtifacts.isEmpty() )
            {
                File libDir = new File( baseDir, "lib" );
                createLibDirectory( libDir );
                for ( Iterator<?> iter = filteredArtifacts.iterator(); iter.hasNext(); )
                {
                    Artifact classPathArtifact = (Artifact) iter.next();
                    File jarFile = classPathArtifact.getFile();
                    if ( dependenciesOverwrite )
                    {
                        FileUtils.copyFileToDirectory( jarFile, libDir );
                    }
                    else
                    {
                        FileUtils.copyFileToDirectoryIfModified( jarFile, libDir );
                    }
                }
            }
        }
        catch ( ArchiverException e )
        {
            // throw new MojoExecutionException( "Error unpacking file [" + file.getAbsolutePath() + "]" + "to ["
            // + unpackDirectory.getAbsolutePath() + "]", e );
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

    private void createLibDirectory( File libDirectory )
        throws IOException
    {
        if ( libDirectory.exists() )
        {
            if ( !libDirectory.isDirectory() )
            {
                throw new IOException( String.format( "\"%s\" is not a directory", libDirectory.getCanonicalPath() ) );
            }
        }
        else
        {
            if ( !libDirectory.mkdirs() )
            {
                throw new IOException(
                                       String.format( "Cannot create \"%s\" directory", libDirectory.getCanonicalPath() ) );
            }
        }
    }

    private void checkPotentialReactorProblem( Artifact artifact )
    {
        File artifactFile = artifact.getFile();
        if ( artifactFile.isDirectory() )
        {
            throw new ArchiverException(
                                         String.format( "\"%s:%s:%s:%s\" dependent artifact's file is a directory, not a file. This is probably Maven reactor build problem.",
                                                        artifact.getGroupId(), artifact.getArtifactId(),
                                                        artifact.getType(), artifact.getVersion() ) );
        }
    }

}

// TODO
// 1. Add name conflict detection for modules and jars
