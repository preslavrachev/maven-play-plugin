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

//import java.io.File;
//import java.io.IOException;
//import java.util.List;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

/**
 * Abstract Base for Play! Mojos.
 * 
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 */
public abstract class AbstractPlayMojo
    extends AbstractMojo
{

    /**
     * ...
     * 
     * @parameter
     */
    protected String playId;

    /**
     * The directory for the generated ZIP.
     * 
     * @parameter expression="${playHome}"
     * @required
     */
    protected File playHome;

    /**
     * <i>Maven Internal</i>: Project to interact with.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    protected abstract void internalExecute()
        throws MojoExecutionException, MojoFailureException, IOException;

    public void execute()
        throws MojoExecutionException, MojoFailureException
    {
        // if ( check == null )
        // {
        // throw new MojoExecutionException( "The Check configuration is missing." );
        // }

        if ( playHome == null || "".equals( playHome ) )
        {
            throw new MojoExecutionException(
                                              "Play! home directory (\"playHome\" plugin configuration parameter) not set" );
        }
        if ( !playHome.exists() )
        {
            throw new MojoExecutionException( "Play! home directory " + playHome + " does not exist" );
        }
        if ( !playHome.isDirectory() )
        {
            throw new MojoExecutionException( "Play! home directory " + playHome + " is not a directory" );
        }

        ArtifactHandler artifactHandler = project.getArtifact().getArtifactHandler();
        if ( !"java".equals( artifactHandler.getLanguage() ) )
        {
            getLog().info( "Not executing cobertura:instrument as the project is not a Java classpath-capable package" );
        }
        else
        {
            try
            {
                resolvePlayId();
                internalExecute();
            }
            catch ( IOException e )
            {
                throw new MojoExecutionException( "?", e );
            }
        }
    }

    protected final BufferedReader createBufferedFileReader( File file, String encoding )
        throws FileNotFoundException, UnsupportedEncodingException
    {
        return new BufferedReader( new InputStreamReader( new FileInputStream( file ), encoding ) );
    }

    protected final BufferedWriter createBufferedFileWriter( File file, String encoding )
        throws FileNotFoundException, UnsupportedEncodingException
    {
        return new BufferedWriter( new OutputStreamWriter( new FileOutputStream( file ), encoding ) );
    }

    // a co z buforowaniem?
    protected final PrintWriter createPrintFileWriter( File file, String encoding )
        throws FileNotFoundException, UnsupportedEncodingException
    {
        return new PrintWriter( new OutputStreamWriter( new FileOutputStream( file ), encoding ) );
    }

    protected/* String */void resolvePlayId()
        throws IOException
    {
        // String result = playId;

        if ( playId == null || "".equals( playId ) )
        {
            playId = readFrameworkPlayId();
        }
        // return result;
    }

    private String readFrameworkPlayId()
        throws IOException
    {
        String playId = null;
        File idFile = new File( playHome, "id" );
        if ( idFile.isFile() )
        {
            // a moze InputStream, nie Reader?
            BufferedReader is = new BufferedReader( new InputStreamReader( new FileInputStream( idFile ), "UTF-8" ) );
            try
            {
                playId = is.readLine();
            }
            finally
            {
                is.close();
            }
        }
        return playId;
    }

    /*
     * protected String rawInput( String prompt, Object... args ) throws IOException { print( prompt, args ); String
     * result = null; BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) ); try { result =
     * br.readLine(); } finally { // nie br.close(); } return result; }
     */

    /*
     * protected void print( String text, Object... args ) { System.out.printf( text, args ); System.out.println(); }
     */

    protected void checkApplication()
    {

    }

    protected List<File> loadModules()
        throws MojoExecutionException, IOException
    {
        return loadModules( true );
    }

    protected List<File> loadModules( boolean failOnMissing )
        throws MojoExecutionException, IOException
    {
        List<File> result = new ArrayList<File>();

        File baseDir = project.getBasedir();
        // if os.environ.has_key('MODULES'):
        // if os.name == 'nt':
        // modules = os.environ['MODULES'].split(';')
        // else:
        // modules = os.environ['MODULES'].split(':')
        // else:
        // modules = []
        List<String> pm = readConfs( "module." );
        for ( String path : pm )
        {
            String filteredPath = path;
            if ( filteredPath.contains( "${play.path}" ) )
            {
                filteredPath = filteredPath.replace( "${play.path}", playHome.getAbsolutePath() );
            }
            if ( !new File( filteredPath ).isAbsolute() )
            {
                filteredPath = new File( baseDir, filteredPath ).getCanonicalPath();
            }
            if ( !new File( filteredPath ).exists() && failOnMissing )
            {
                // FIXME-polish info output and exceptions throwing
                // print( "~ Oops," );
                // print( "~ Module not found: %s", filteredPath );
                // print( "~" );
                if ( path.startsWith( "${play.path}/modules" ) )
                {
                    // print( "~ You can try to install the missing module using 'play install %s'", path.substring( 21
                    // ) );
                    // print( "~" );
                }
                throw new MojoExecutionException( "Module not found" );
            }
            result.add( new File( filteredPath ) );
        }
        if ( "test".equals( playId ) )
        {
            result.add( new File( playHome, "modules/testrunner" ) );
        }
        return result;
    }

    // Copied from original Play! Python scripts, but I don't like it because it returns double modules when:
    // module.<name>=path1
    // %<playId>.module.<name>=path2
    protected List<String> readConfs( String key )
        throws IOException
    {
        List<String> result = new ArrayList<String>();
        File baseDir = project.getBasedir();
        File configFile = new File( baseDir, "conf/application.conf" );
        BufferedReader reader =
            new BufferedReader( new InputStreamReader( new FileInputStream( configFile ), "UTF-8" ) );
        try
        {
            List<String> tmp = new ArrayList<String>();
            String line = reader.readLine();
            while ( line != null )
            {
                if ( line.startsWith( key ) )
                {
                    String value = line.substring( line.indexOf( '=' ) + 1 ).trim();
                    result.add( value );
                }
                else if ( line.startsWith( '%' + playId + '.' + key ) )
                {
                    String value = line.substring( line.indexOf( '=' ) + 1 ).trim();
                    tmp.add( value );
                }
                line = reader.readLine();
            }
            result.addAll( tmp );
        }
        finally
        {
            reader.close();
        }
        return result;
    }

    protected List<File> getClasspath( List<File> modules )
    {
        List<File> result = new ArrayList<File>();

        File baseDir = project.getBasedir();

        // ?File agentPath = new File(playHome, "framework/play.jar");

        // The default
        result.add( new File( baseDir, "conf" ) );
        result.add( new File( playHome, "framework/play.jar" ) );

        // The application
        File libDir = new File( baseDir, "lib" );
        if ( libDir.isDirectory() )
        {
            File[] files = libDir.listFiles();
            for ( int i = 0; i < files.length; i++ )
            {
                if ( files[i].getName().endsWith( ".jar" ) )
                {
                    result.add( files[i] );
                }
            }
        }

        // The modules
        for ( File module : modules )
        {
            libDir = new File( module, "lib" );
            if ( libDir.isDirectory() )
            {
                File[] files = libDir.listFiles();
                for ( int i = 0; i < files.length; i++ )
                {
                    if ( files[i].getName().endsWith( ".jar" ) )
                    {
                        result.add( files[i] );
                    }
                }
            }
        }

        // The framework
        libDir = new File( playHome, "framework/lib" );
        File[] files = libDir.listFiles();
        for ( int i = 0; i < files.length; i++ )
        {
            if ( files[i].getName().endsWith( ".jar" ) )
            {
                result.add( files[i] );
            }
        }

        return result;
    }

}
