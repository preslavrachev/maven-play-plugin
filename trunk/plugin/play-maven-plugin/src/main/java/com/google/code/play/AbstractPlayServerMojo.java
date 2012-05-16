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
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.types.Path;

/**
 * Base class for Play! Server starting mojos.
 */
public abstract class AbstractPlayServerMojo
    extends AbstractAntJavaBasedPlayMojo
{
    /**
     * Alternative server port.
     * 
     * @parameter expression="${play.httpPort}" default-value=""
     * @since 1.0.0
     */
    private String httpPort;

    /**
     * Alternative server port for secure connection (https protocol).
     * 
     * @parameter expression="${play.httpsPort}" default-value=""
     * @since 1.0.0
     */
    private String httpsPort;

    /**
     * Disable the JPDA port checking and force the jpda.port value (Play!'s "-f" option equivalent).
     * 
     * @parameter expression="${play.disableCheckJpda}" default-value="false"
     * @since 1.0.0
     */
    private boolean disableCheckJpda;

    protected Java prepareAntJavaTask( ConfigurationParser configParser, String playId, boolean fork )
        throws MojoExecutionException, IOException
    {
        File playHome = getPlayHome();
        File baseDir = project.getBasedir();

        Project antProject = createProject();
        Path classPath = getProjectClassPath( antProject, playId );

        Java javaTask = new Java();
        javaTask.setTaskName( "play" );
        javaTask.setProject( antProject );
        javaTask.setClassname( "com.google.code.play.PlayServerBooter" );
        javaTask.setClasspath( classPath );
        javaTask.setFork( fork );
        if ( fork )
        {
            javaTask.setDir( baseDir );

            String jvmMemory = configParser.getProperty( "jvm.memory" );
            if ( jvmMemory != null )
            {
                jvmMemory = jvmMemory.trim();
                if ( !jvmMemory.isEmpty() )
                {
                    String[] args = jvmMemory.split( " " );
                    for ( String arg : args )
                    {
                        javaTask.createJvmarg().setValue( arg );
                        getLog().debug( "  Adding jvmarg '" + arg + "'" );
                    }
                }
            }
            
            String jpdaPortStr = configParser.getProperty( "jpda.port", "8000" );
            int jpdaPort = Integer.parseInt( jpdaPortStr );
            
            String applicationMode = configParser.getProperty( "application.mode", "dev" );
            
            if ( "prod".equalsIgnoreCase( applicationMode ) )
            {
                javaTask.createJvmarg().setValue( "-server" );
            }

            // JDK 7 compat
            javaTask.createJvmarg().setValue( "-XX:-UseSplitVerifier" );

            String javaPolicy = configParser.getProperty( "java.policy" );
            if ( javaPolicy != null && !javaPolicy.isEmpty() )
            {
                File confDir = new File( baseDir, "conf" );
                File policyFile = new File ( confDir, javaPolicy );
                if ( policyFile.isFile() )
                {
                    getLog().info( String.format( "~ using policy file \"%s\"", policyFile.getPath() ) );
                    addSystemProperty( javaTask, "java.security.manager", "" );
                    addSystemProperty( javaTask, "java.security.policy", policyFile );
                }
            }

            if ( httpPort != null && !httpPort.isEmpty() )
            {
                javaTask.createArg().setValue( "--http.port=" + httpPort );
            }
            if ( httpsPort != null && !httpsPort.isEmpty() )
            {
                javaTask.createArg().setValue( "--https.port=" + httpsPort );
            }

            if ( "dev".equalsIgnoreCase( applicationMode ) )
            {
                if ( !disableCheckJpda )
                {
                    jpdaPort = checkJpda( jpdaPort );
                }
                javaTask.createJvmarg().setValue( "-Xdebug" );
                javaTask.createJvmarg().setValue( String.format( "-Xrunjdwp:transport=dt_socket,address=%d,server=y,suspend=n", jpdaPort ) );
                //from Java5 should be: -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=1044 instead of the two above
                addSystemProperty( javaTask, "play.debug", "yes" ); // what is it for?
            }

            Artifact frameworkJarArtifact = getFrameworkJarArtifact();
            
            javaTask.createJvmarg().setValue( String.format( "-javaagent:%s",
                                                             frameworkJarArtifact.getFile().getAbsoluteFile() ) );
        }
        addSystemProperty( javaTask, "play.home", playHome.getAbsolutePath() );
        addSystemProperty( javaTask, "play.id", ( playId != null ? playId : "" ) );
        addSystemProperty( javaTask, "application.path", baseDir.getAbsolutePath() );
        
        return javaTask;
    }
    
    protected Collection<Artifact> getExcludedArtifacts( Set<?> classPathArtifacts, String playId )
        throws IOException
    {
        List<Artifact> result = new ArrayList<Artifact>();

        // Get "application.conf" modules active in "playId" profile
        Collection<String> providedModuleNames = getProvidedModuleNames( playId );

        Map<String, Artifact> moduleArtifacts = findAllModuleArtifacts( true );

        for ( Iterator<?> iter = classPathArtifacts.iterator(); iter.hasNext(); )
        {
            Artifact artifact = (Artifact) iter.next();
            if ( Artifact.SCOPE_PROVIDED.equals( artifact.getScope() ) )
            {
                for ( Map.Entry<String, Artifact> moduleArtifactEntry : moduleArtifacts.entrySet() )
                {
                    Artifact moduleArtifact = moduleArtifactEntry.getValue();
                    if ( Artifact.SCOPE_PROVIDED.equals( moduleArtifact.getScope() ) )
                    {
                        if ( artifact.getGroupId().equals( moduleArtifact.getGroupId() )
                            && artifact.getArtifactId().equals( moduleArtifact.getArtifactId() ) )
                        {
                            String moduleName = moduleArtifactEntry.getKey();
                            if ( !providedModuleNames.contains( moduleName ) )
                            {
                                result.add( artifact );
                            }
                            break;
                        }
                    }
                }
            }
        }

        return result;
    }
    
    protected Path getProjectClassPath( Project antProject, String playId )
        throws MojoExecutionException, IOException
    {
        Path classPath = new Path( antProject );

        classPath.createPathElement().setLocation( new File( project.getBasedir(), "conf" ) );
        Set<?> classPathArtifacts = project.getArtifacts();
        Collection<Artifact> excludedArtifacts = getExcludedArtifacts( classPathArtifacts, playId );
        for ( Iterator<?> iter = classPathArtifacts.iterator(); iter.hasNext(); )
        {
            Artifact artifact = (Artifact) iter.next();
            if ( !excludedArtifacts.contains( artifact ) )
            {
                getLog().debug( String.format( "CP: %s:%s:%s (%s)", artifact.getGroupId(),
                                               artifact.getArtifactId(), artifact.getType(), artifact.getScope() ) );
                classPath.createPathElement().setLocation( artifact.getFile() );
            }
        }
        classPath.createPathElement().setLocation( getPluginArtifact( "com.google.code.maven-play-plugin",
                                                                      "play-server-booter", "jar" ).getFile() );
        return classPath;
    }    

    protected Artifact getFrameworkJarArtifact()
    {
        Artifact result = null;
        
        Artifact frameworkZipArtifact = findFrameworkArtifact( true );
        //TODO-validate not null
        
        Set<?> artifacts = project.getArtifacts();
        for ( Iterator<?> iter = artifacts.iterator(); iter.hasNext(); )
        {
            Artifact artifact = (Artifact) iter.next();
            if ( frameworkZipArtifact.getGroupId().equals( artifact.getGroupId() )
                && frameworkZipArtifact.getArtifactId().equals( artifact.getArtifactId() )
                && "jar".equals( artifact.getType() ) )
            {
                result = artifact;
                break;
            }
        }
        
        //TODO-validate not null?
        return result;
    }

    protected int checkJpda( int confJpdaPort )
    {
        int result = confJpdaPort;
        try
        {
            ServerSocket serverSocket = new ServerSocket( confJpdaPort );
            serverSocket.close();
        }
        catch ( IOException e )
        {
            getLog().info( String.format( "JPDA port %d is already used. Will try to use any free port for debugging",
                                          confJpdaPort ) );
            result = 0;
        }
        return result;
    }
    
    protected String getHttpPort()
    {
        return httpPort;
    }

    protected String getHttpsPort()
    {
        return httpsPort;
    }

}