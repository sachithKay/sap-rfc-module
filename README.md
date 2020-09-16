# sap-rfc-module
This module aims to accelerate the WSO2 SAP executions by creating a skeleton of the payload required by 
the WSO2 SAP adapter.

## Building from source.

1. Clone this repo into your local machine.
2. Download the [sapjco3-3.0.14.jar](http://maven.mit.edu/nexus/content/repositories/public/com/sap/conn/jco/sapjco3/3.0.14/) from the maven archives and install it to your local m2 repository.

`
mvn install:install-file -DgroupId=com.sap.conn.jco -DartifactId=sapjco3 -Dversion=3.0.14 -Dpackaging=jar -Dfile=sapjco3-3.0.14.jar
`
3. Navigate into the project-home directory (sap-rfc-module) and perform a maven build with `mvn clwean install`

## Running the tool.

1. Unzip the .zip distribution in the target directory.
2. Copy the sapjco3.jar into the libs directory of the distribution.
3. Add a SAP destination file into the TOOL_HOME (ECC.jcoDestination).
4. Open a terminal and run the jar as follows with 3 arguments.

``
java -jar sap-rfc-module-1.0-SNAPSHOT.jar OPERATION DESTINATION RFC_FUNCTION_NAME
``

Given below are sample values for the arguments required by the tool.

- OPERATION - payload
- DESTINATION - ECC
- RFC_FUNCTION_NAME - RFC_GET_LOCAL_SERVERS