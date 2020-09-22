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

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoRepository;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;
import org.wso2.sap.rfc.utils.IOUtils;

import java.nio.file.Paths;
import java.util.Optional;

public class SapRFCMetadataReader {

    private JCoRepository repository;
    private JCoDestination rfcClient;
    private static Logger LOG = Logger.getLogger(SapRFCMetadataReader.class);

    public SapRFCMetadataReader(String destination) {

        try {

            String confLocation = Paths.get(System.getProperty("user.dir"), "sap").toString();
            LOG.info("Initializing jco.destinations.dir to " + confLocation);
            System.setProperty("jco.destinations.dir", confLocation);
            rfcClient = JCoDestinationManager.getDestination(destination);
            repository = rfcClient.getRepository();
            LOG.info("Repository initialized for destination " + destination);
        } catch (JCoException e) {
            LOG.error("Error while getting destination or repository", e);
            System.exit(1);
        }
    }

    public void createBAPIPayload(String rfcName) {

        try {
            JCoFunctionTemplate functionTemplate = getRepository().getFunctionTemplate(rfcName);
            if (functionTemplate != null) {
                RFCFunctionSerializer serializer = new RFCFunctionSerializer();
                OMElement payload = serializer.serializeFunction(functionTemplate, rfcName);
                IOUtils.writeXMLFile(rfcName + ".xml", Optional.empty(), payload);
            } else {
                LOG.error("Function template could not be retrieved for RFC: " + rfcName);
            }
        } catch (JCoException e) {
            LOG.error("Error occurred while creating BAPI payload ", e);
        }
    }

    public JCoRepository getRepository() {
        return this.repository;
    }
}
