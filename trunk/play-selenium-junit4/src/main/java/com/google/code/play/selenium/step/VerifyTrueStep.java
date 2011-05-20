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

import com.thoughtworks.selenium.SeleneseTestCase;

import com.google.code.play.selenium.Step;

public class VerifyTrueStep
    implements Step
{

    private SeleneseTestCase seleneseTestCase;

    private BooleanSeleniumCommand innerCommand;

    public VerifyTrueStep( SeleneseTestCase seleneseTestCase, BooleanSeleniumCommand innerCommand )
    {
        this.seleneseTestCase = seleneseTestCase;
        this.innerCommand = innerCommand;
    }

    public void execute()
        throws Exception
    {
        boolean innerCommandResult = innerCommand.getBoolean();
        seleneseTestCase.verifyTrue( innerCommandResult );
    }

    public String toString()
    {
        String cmd = innerCommand.command.substring( "is".length() );

        StringBuffer buf = new StringBuffer();
        buf.append( "verify" ).append( cmd ).append( "('" );
        buf.append( innerCommand.param1 ).append( "')" );
        return buf.toString();
    }

}
