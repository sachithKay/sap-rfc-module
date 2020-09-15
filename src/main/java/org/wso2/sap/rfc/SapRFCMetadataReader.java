package org.wso2.sap.rfc;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoRepository;
import org.apache.axiom.om.OMElement;
import org.apache.log4j.Logger;

public class SapRFCMetadataReader {

    private JCoRepository repository;
    private JCoDestination rfcClient;
    private static Logger LOG = Logger.getLogger(SapRFCMetadataReader.class);

    public SapRFCMetadataReader(String destination) {

        try {

            String confLocation = System.getProperty("user.dir");
            LOG.info("Initializing jco.destinations.dir to " + confLocation);
            System.setProperty("jco.destinations.dir", confLocation);
            rfcClient = JCoDestinationManager.getDestination(destination);
            repository = rfcClient.getRepository();
        } catch (JCoException e) {
            LOG.error("Error while getting destination or repository", e);
        }
    }

    public void createBAPIPayload(String rfcName) {

        try {
            JCoFunctionTemplate functionTemplate = getRepository().getFunctionTemplate(rfcName);
            if (functionTemplate != null) {
                RFCFunctionSerializer serializer = new RFCFunctionSerializer();
                OMElement payload = serializer.serializeFunction(functionTemplate, rfcName);
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
