package com.google.code.play.surefire.junit4;

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

import org.apache.maven.surefire.testset.TestSetFailedException;
import org.junit.runner.Request;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;

import play.Play;
import play.PlayPlugin;

public class PlayJUnit4TestSet
{

    public static void execute( Class testClass, RunNotifier fNotifier )
        throws TestSetFailedException
    {
        for ( PlayPlugin playPlugin : Play.plugins )
        {
            playPlugin.beforeInvocation();
        }
        try
        {
            Runner junitTestRunner = Request.aClass( testClass ).getRunner();

            junitTestRunner.run( fNotifier );
        }
        finally
        {
            for ( PlayPlugin playPlugin : Play.plugins )
            {
                playPlugin.afterInvocation();
            }
            for ( PlayPlugin playPlugin : Play.plugins )
            {
                playPlugin.invocationFinally();
            }

        }
    }
}
