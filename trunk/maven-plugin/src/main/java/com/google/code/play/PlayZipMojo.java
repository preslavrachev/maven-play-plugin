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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import org.codehaus.plexus.archiver.Archiver;
import org.codehaus.plexus.archiver.ArchiverException;
import org.codehaus.plexus.archiver.zip.ZipArchiver;

/**
 * Package Play! application as a ZIP achive.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 * @goal zip
 * @phase package
 */
public class PlayZipMojo
    extends AbstractPlayMojo
{

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

    // /**
    // * Classifier to add to the generated ZIP. If given, the artifact will be an attachment instead.??? The classifier
    // * will not be applied to the jar file of the project - only to the zip file.
    // *
    // * @parameter
    // */
    // private String classifier;

    protected void internalExecute()
        throws MojoExecutionException, MojoFailureException, IOException
    {
        try
        {
            File baseDir = project.getBasedir();
            File destFile = new File( outputDirectory, zipName + ".zip" );
            // File destFile = new File( outputDirectory, getDestinationFileName() );

            ZipArchiver zipArchiver = new ZipArchiver();
            zipArchiver.setDuplicateBehavior( Archiver.DUPLICATES_FAIL );// Just in case
            zipArchiver.setDestFile( destFile );

            // application
            zipArchiver.addDirectory( new File( baseDir, "app" ), "app/", null, null );
            // conf
            zipArchiver.addDirectory( new File( baseDir, "conf" ), "conf/", null, null );
            // lib
            if ( new File( baseDir, "lib" ).isDirectory() )
            {
                zipArchiver.addDirectory( new File( baseDir, "lib" ), "lib/", null, null );
            }
            // public
            zipArchiver.addDirectory( new File( baseDir, "public" ), "public/", null, null );
            // tags
            if ( new File( baseDir, "tags" ).isDirectory() )
            {
                zipArchiver.addDirectory( new File( baseDir, "tags" ), "tags/", null, null );
            }

            zipArchiver.createArchive();

            project.getArtifact().setFile( destFile );

        }
        catch ( ArchiverException e )
        {
            throw new MojoExecutionException( "?", e );
        }
    }

    // private String getDestinationFileName()
    // {
    // StringBuffer buf = new StringBuffer();
    // buf.append( zipName );
    // if ( classifier != null && !"".equals( classifier ) )
    // {
    // if ( !classifier.startsWith( "-" ) )
    // {
    // buf.append( '-' );
    // }
    // buf.append( classifier );
    // }
    // buf.append( ".zip" );
    // return buf.toString();
    // }

}
