package org.wso2.sap.rfc;

import org.apache.log4j.Logger;

public class RFCApp {

    private static Logger LOG = Logger.getLogger(RFCApp.class);

    public static void main(String[] args) {

        LOG.info("Starting RFC payload builder... ");
        String operation = null;
        String destination  = null;
        String rfcFunction = null;

        if (args.length == 3) {
            operation = args[0];
            destination = args[1];
            rfcFunction = args[2];
        } else {
            LOG.error("Invalid arguments provided.");
            System.exit(1);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Operation: " + operation + "\n Destination: " + destination + "\n RFCFunction: " + rfcFunction);
        }

        SapRFCMetadataReader metadataReader = new SapRFCMetadataReader(destination);
    }
}
