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

import com.google.code.play.selenium.Step;
import com.google.code.play.selenium.StoredVars;

public class EchoStep
    implements Step
{

    private StoredVars storedVars;

    private String param;

    public EchoStep( StoredVars storedVars, String param )
    {
        this.storedVars = storedVars;
        this.param = param;
    }

    public void execute()
        throws Exception
    {
        String result = storedVars.fillValues( param );
        System.out.println( "echo:" + result );
    }

    public long getExecutionTimeMillis()
    {
        return 0L;
    }

    public String toString()
    {
        StringBuffer buf = new StringBuffer();
        buf.append( "echo('" ).append( param ).append( "')" );
        return buf.toString();
    }

}