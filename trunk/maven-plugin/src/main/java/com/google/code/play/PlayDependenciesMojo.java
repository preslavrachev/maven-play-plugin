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
//import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
//import org.apache.maven.project.MavenProject;

import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.UnArchiver;
import org.codehaus.plexus.archiver.manager.ArchiverManager;
import org.codehaus.plexus.archiver.manager.NoSuchArchiverException;

import org.codehaus.plexus.util.FileUtils;

/**
 * ... finish! ...
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 * @goal dependencies
 * @xphase initialize
 * @requiresDependencyResolution runtime
 */
//a co z zaleznosciami "runtime"? czy na pewno nie sa potrzebne? Play! by je wciagnal
// jak jest "@requiresDependencyResolution compile", to nie wiem, jak odfiltrowac drzewo bibliotek play'a (ma scope provided)
public class PlayDependenciesMojo
    extends AbstractPlayMojo
{

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
            Map<Artifact, File> moduleTypeArtifacts = processModuleDependencies();
            processJarDependencies(moduleTypeArtifacts); 
        }
        catch ( ArchiverException e )
        {
            // throw new MojoExecutionException( "Error unpacking file [" + file.getAbsolutePath() + "]" + "to ["
            // + unpackDirectory.getAbsolutePath() + "]", e );
            throw new MojoExecutionException( "?", e );
        }
        catch ( NoSuchArchiverException e )
        {
            throw new MojoExecutionException( "?", e );
        }
    }

    private Map<Artifact, File> processModuleDependencies() throws ArchiverException, NoSuchArchiverException, IOException 
    {
        Map<Artifact, File> moduleTypeArtifacts = new HashMap<Artifact, File>();
        
        File baseDir = project.getBasedir();
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

                    File modulesDir = new File( baseDir, "modules" );
                    //createDir( modulesDir );
                    String moduleName = artifact.getArtifactId();
                    if ( moduleName.startsWith( "play-" ) )
                    {
                        moduleName = moduleName.substring( "play-".length() );
                    }
                    String moduleSubDir = String.format( "%s-%s", moduleName, artifact.getVersion() );
                    File toDirectory = new File( modulesDir, moduleSubDir );
                    createDir( toDirectory );
                    UnArchiver zipUnArchiver = archiverManager.getUnArchiver( "zip" );
                    zipUnArchiver.setSourceFile( zipFile );
                    zipUnArchiver.setDestDirectory( toDirectory );
                    zipUnArchiver.setOverwrite( false/* ??true */);
                    zipUnArchiver.extract();

                    moduleTypeArtifacts.put( artifact, toDirectory );
                    //System.out.println("module: " + artifact.getGroupId() + ":" + artifact.getArtifactId() + " added");
                }
            }
        }
        return moduleTypeArtifacts;
    }

    private void processJarDependencies(Map<Artifact, File> moduleTypeArtifacts) throws ArchiverException, NoSuchArchiverException, IOException 
    {
        File baseDir = project.getBasedir();
        Set<?> artifacts = project.getArtifacts();

        for ( Iterator<?> iter = artifacts.iterator(); iter.hasNext(); )
        {
            Artifact artifact = (Artifact) iter.next();
            if ( "jar".equals( artifact.getType() ) )
            {
                //System.out.println("jar: " + artifact.getGroupId() + ":" + artifact.getArtifactId());
                File jarFile = artifact.getFile();
                File libDir = new File( baseDir, "lib" );
                for (Artifact moduleArtifact: moduleTypeArtifacts.keySet())
                {
                    //System.out.println("checking module: " + moduleArtifact.getGroupId() + ":" + moduleArtifact.getArtifactId());
                    if ( artifact.getGroupId().equals( moduleArtifact.getGroupId() )
                        && artifact.getArtifactId().equals( moduleArtifact.getArtifactId() ) )
                    {
                        libDir = new File( moduleTypeArtifacts.get( moduleArtifact ), "lib" );
                        //System.out.println("checked ok - lib is " + libDir.getCanonicalPath());
                        break;
                    }
                }
                //System.out.println("jar: " + artifact.getGroupId() + ":" + artifact.getArtifactId() + " added to " + libDir);
                createDir( libDir );
                FileUtils.copyFileToDirectoryIfModified( jarFile, libDir );
            }
        }
    }
    
    private void createDir( File directory )
        throws IOException
    {
        if ( directory.isFile() )
        {
            getLog().info( String.format( "Deleting \"%s\" file", directory ) );// TODO-more descriptive message
            if ( !directory.delete() )
            {
                throw new IOException( String.format( "Cannot delete \"%s\" file", directory.getCanonicalPath() ) );
            }
        }
        if ( !directory.exists() )
        {
            if ( !directory.mkdirs() )
            {
                throw new IOException( String.format( "Cannot create \"%s\" directory", directory.getCanonicalPath() ) );
            }
        }
    }

}

// TODO-dorobic detekcje konfliktow nazw roznych artifactow (zwlaszcza modulow)
