/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.sap.rfc;

import org.apache.log4j.Logger;

import java.io.Console;

public class RFCApp {

    private static Logger LOG = Logger.getLogger(RFCApp.class);

    public static void main(String[] args) {

        LOG.info("Starting RFC payload builder... ");
        String rfcFunction;

        String destination = getValueFromConsole("Please provide the SAP destination :");
        SapRFCMetadataReader metadataReader = new SapRFCMetadataReader(destination);

        // will repeatedly take RFC names and output payloads
        while (true) {
            rfcFunction = getValueFromConsole("Enter RFC Function name :");
            if (rfcFunction != null) {
                metadataReader.createBAPIPayload(rfcFunction);
            } else {
                break;
            }
        }
    }

    public static String getValueFromConsole(String message) {

        Console console = System.console();
        if (console != null) {
            String value;
            if ((value = console.readLine("[%s]", message)) != null) {
                return value;
            }
        }
        return null;
    }
}
