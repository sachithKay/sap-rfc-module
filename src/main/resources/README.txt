================================================================================
                        WSO2 SAP RFC Accelerator Module 1.0.0
================================================================================

This is an utility tool which aims to generate the structure of the BAPI payloads
required by WSO2 SAP adapter. You can invoke the provided the .jar with the destination
name and the RFC function name, in order to get the skeleton of the BAPI payload expected
by the WSO2 server.


Instructions to use
==================================
1. Extract the sap-rfc-module.zip to a desired location.
2. Let's call this ,location <TOOL_HOME>.
3. Add the destination files (.jcoDestination files) into the TOOL_HOME/sap directory.
4. Add the sapjco3.jar into the TOOL_HOME/libs directory.
5. Run the tool with java -jar sap-rfc-module-1.0.0
