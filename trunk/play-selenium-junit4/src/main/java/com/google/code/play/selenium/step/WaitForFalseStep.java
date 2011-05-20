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

package com.google.code.play.selenium.step;

import junit.framework.Assert;

import com.google.code.play.selenium.Step;

public class WaitForFalseStep
    implements Step
{

    private BooleanSeleniumCommand innerCommand;

    public WaitForFalseStep( BooleanSeleniumCommand innerCommand )
    {
        this.innerCommand = innerCommand;
    }

    public void execute()
        throws Exception
    {
        for ( int second = 0;; second++ )
        {
            if ( second >= 60 )
                Assert.fail( "timeout" );
            try
            {
                boolean innerCommandResult = !innerCommand.getBoolean();
                if ( innerCommandResult )
                    break;
            }
            catch ( Exception e )
            {
            }
            Thread.sleep( 1000 );
        }
    }

    public String toString()
    {
        String cmd = innerCommand.command.substring( "is".length() );

        StringBuffer buf = new StringBuffer();
        buf.append( "waitFor" );
        if ( cmd.endsWith( "Present" ) )
        {
            buf.append( cmd.replace( "Present", "NotPresent" ) );
        }
        else
        {
            buf.append( "Not" ).append( cmd );
        }
        buf.append( "('" ).append( innerCommand.param1 ).append( "')" );
        return buf.toString();
    }

}
