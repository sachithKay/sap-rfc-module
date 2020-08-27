package org.wso2.sap.rfc;

import com.sap.conn.jco.JCoDestination;
import com.sap.conn.jco.JCoDestinationManager;
import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoRepository;
import org.apache.log4j.Logger;

public class SapRFCMetadataReader {

    private JCoRepository repository;
    private JCoDestination rfcClient;
    private static Logger LOG = Logger.getLogger(SapRFCMetadataReader.class);

    public SapRFCMetadataReader(String destination) {
        try {

            String confLocation = System.getProperty("user.dir");
            LOG.info("Program started...");
            System.out.println("Initializing jco.destinations.dir to " + confLocation);
            System.setProperty("jco.destinations.dir", confLocation);
            rfcClient = JCoDestinationManager.getDestination(destination);
            System.out.println("reached here");
            repository = rfcClient.getRepository();
            LOG.info("Program ended");
        } catch (JCoException e) {
            LOG.error("Error while getting destination or repository", e);
        }
    }
}
