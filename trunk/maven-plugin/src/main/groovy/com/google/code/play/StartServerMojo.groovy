/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License") you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package com.google.code.play

import org.codehaus.gmaven.mojo.GroovyMojo
import org.apache.maven.project.MavenProject
import org.codehaus.gmaven.mojo.support.ProcessLauncher

/**
 * Start Play! server.
 * Based on <a href="http://mojo.codehaus.org/selenium-maven-plugin/start-server-mojo.html">selenium:start-server mojo</a>
 *
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 * @goal start-server
 * @requiresDependencyResolution test
 */
class StartServerMojo
    extends GroovyMojo
{
    /**
     * The port number of the server to connect to.
     *
     * @parameter expression="${port}" default-value="9000"
     */
    int port
    
    /**
     * Timeout for the server in seconds.
     *
     * @parameter expression="${timeout}" default-value="-1"
     */
    int timeout

    /**
     * Working directory where Play! server will be started from.
     *
     * @parameter expression="${project.build.directory}/play"
     * @required
     */
    File workingDirectory

    /**
     * Enable logging mode.
     *
     * @parameter expression="${logOutput}" default-value="false"
     */
    boolean logOutput

    /**
     * The file that Play! server logs will be written to.
     *
     * @parameter expression="${logFile}" default-value="${project.build.directory}/play/server.log"
     * @required
     */
    File logFile

    /**
     * Flag to control if we background the server or block Maven execution.
     *
     * @parameter expression="${background}" default-value="true"
     * @required
     */
    boolean background

    /**
     * ...
     *
     * @parameter expression="${playHome}"
     * @required
     */
    String playHome

    /**
     * ...
     *
     * @parameter expression="${applicationPath}" default-value="${basedir}"
     */
    String applicationPath

    /**
     * ...
     *
     * @parameter expression="${playId}" default-value="test"
     * @required
     */
    String playTestProfile

    /**
     * Allows the server startup to be skipped.
     *
     * @parameter expression="${maven.test.skip}" default-value="false"
     */
    boolean skip
    
    /**
     * Allows the server startup to be skipped.
     *
     * @parameter expression="${play.skipSeleniumTests}" default-value="false"
     */
    boolean skipSeleniumTests
    
    /**
     * Arbitrary JVM options to set on the command line.
     *
     * @parameter expression="${play.forked.argLine}"
     * @since 1.0
     */
    private String forkedProcessArgLine;
    
    //
    // Components
    //
    
    /**
     * The enclosing project.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    MavenProject project;

    /**
     * List of artifacts this plugin depends on. Used for resolving the Findbugs coreplugin.
     *
     * @parameter expression="${plugin.artifacts}"
     * @required
     * @readonly
     */
    ArrayList pluginArtifacts

     /**
     * @parameter expression="${plugin.artifactMap}"
     * @required
     * @readonly
     */
    Map pluginArtifactMap

    //
    // Mojo
    //

    void execute() {
        if (skip || skipSeleniumTests) {
            log.info('Skipping startup')
            return
        }
        
        ant.mkdir(dir: workingDirectory)
        
        if (logOutput) {
            ant.mkdir(dir: logFile.parentFile)
        }
        
        def pluginArifact = { id ->
            def artifact = pluginArtifactMap[id]
            if (!artifact) {
                fail("Unable to locate '$id' in the list of plugin artifacts")
            }
            
            log.debug("Using plugin artifact: ${artifact.file}")
            
            return artifact.file
        }
        
//System.out.println("playHome:"+playHome);
//System.out.println("applicationPath:"+applicationPath);
//System.out.println("playId:"+playTestProfile);
        
        def launcher = new ProcessLauncher(name: 'Play! Server', background: background)
        
        launcher.process = {
            ant.java(classname: 'com.google.code.play.PlayServerBooter',
                     fork: true,
                     dir: workingDirectory,
                     failonerror: true)
            {
                classpath() {
                    def projectClasspathElements = project.testClasspathElements
                    def projectClasspathList = projectClasspathElements.findAll{project.build.outputDirectory != it.toString()}

                    projectClasspathList.each() {projectClasspathElement ->
                        log.debug("  Adding to projectArtifact ->" + projectClasspathElement.toString())
                        pathelement(location: projectClasspathElement.toString())
                    }
                    pathelement(location: pluginArifact('com.google.code.maven-play-plugin:play-server-booter'))
                }
                /*classpath() {
                    // Add our plugin artifact to pick up log4j configuration
                    //GS pathelement(location: getClass().protectionDomain.codeSource.location.file)
                    //GS pathelement(location: pluginArifact('log4j:log4j'))
                }*/
                
                if (logOutput) {
                    log.info("Redirecting output to: $logFile")
                    redirector(output: logFile)
                }
                
                sysproperty(key: 'play.home', value: playHome)
                sysproperty(key: 'play.id', value: playTestProfile)
                sysproperty(key: 'application.path', value: applicationPath)
                
                if (forkedProcessArgLine != null) {
                    String argLine = forkedProcessArgLine.trim();
                    if (!"".equals(argLine)) {
                        String[] args = argLine.split( " " );
                        for (String arg: args) {
                            jvmarg(value: arg);
                            //System.out.println("jvmarg:'"+arg+"'");
                        }
                    }
                }
            }
        }
        
        URL url = new URL("http://localhost:$port")
        
        launcher.verifier = {
            log.debug("Trying connection to: $url")
            
            try {
                url.openConnection().content
                return true
            }
            catch (Exception e) {
                return false
            }
        }
        
        launcher.launch()
    }

}
