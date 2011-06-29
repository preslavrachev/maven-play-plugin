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

/**
 * Stop Play! server.
 * Based on <a href="http://mojo.codehaus.org/selenium-maven-plugin/stop-server-mojo.html">selenium:stop-server mojo</a>
 *
 * @author <a href="mailto:gslowikowski@gmail.com">Grzegorz Slowikowski</a>
 * @goal stop-server
 */
class StopServerMojo
    extends GroovyMojo
{
    /**
     * The port number of the server to connect to.
     *
     * @parameter expression="${port}" default-value="9000"
     */
    int port
    
    /**
     * Skip goal execution
     *
     * @parameter expression="${play.selenium.skip}" default-value="false"
     */
    boolean seleniumSkip

    //
    // Mojo
    //

    void execute() {
        if (seleniumSkip) {
            log.info('Skipping execution')
            return
        }
        
        println('Stopping Play! server...')
        
        def url = new URL("http://localhost:$port/@kill")
        
        println("Stop request URL: $url")
        log.debug("Stop request URL: $url")
        
        try {
            url.openConnection().content
        }
        catch (java.net.SocketException e) {
            //ignore
        }
        
        println('Stop request sent')
    }
}
