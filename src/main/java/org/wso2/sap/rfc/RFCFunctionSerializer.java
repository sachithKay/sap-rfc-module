package org.wso2.sap.rfc;

import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoListMetaData;
import com.sap.conn.jco.JCoStructure;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.log4j.Logger;

public class RFCFunctionSerializer {

    private static Logger LOG = Logger.getLogger(RFCFunctionSerializer.class);
    private OMFactory factory = OMAbstractFactory.getOMFactory();

    protected OMElement serializeFunction(JCoFunctionTemplate functionTemplate, String functionName) {

        OMElement document = factory.createOMElement(Constants.BAPIRFC, null);
        document.addAttribute("name", functionName, null);
        handleImports(document, functionTemplate);
        LOG.info(document.toString());
        return document;
    }

    private void handleImports(OMElement document, JCoFunctionTemplate functionTemplate) {

        JCoListMetaData imports = functionTemplate.getImportParameterList();
        OMElement importsOM = factory.createOMElement(Constants.IMPORT_QNAME, null);
        document.addChild(importsOM);

        int size = (imports != null) ? imports.getFieldCount() : 0;
        for (int i = 0; i < size; i++) {

            String type = imports.getTypeAsString(i);
            String paramName = imports.getName(i);
            OMElement importEntryOM;
                switch (type) {
                    case "STRUCTURE":
                        importEntryOM = handleStructure(paramName, functionTemplate);
                        break;

                    case "TABLE":
                        importEntryOM = handleTable(paramName);
                        break;

                    default:
                        importEntryOM = handleField(paramName);
                }
            importsOM.addChild(importEntryOM);
        }
    }

    private OMElement handleStructure(String structureName, JCoFunctionTemplate functionTemplate) {
        OMElement structureOMElement = factory.createOMElement(Constants.STRUCTURE_QNAME, null);
        JCoFunction function = functionTemplate.getFunction();
        JCoStructure structure = function.getImportParameterList().getStructure(structureName);


        return structureOMElement;
    }

    private OMElement handleTable(String tableName) {
        OMElement tableOMElement = factory.createOMElement(Constants.STRUCTURE_QNAME, null);

        return tableOMElement;
    }

    private OMElement handleField(String fieldName) {

        OMElement fieldOMElement = factory.createOMElement(Constants.FIELD_QNAME, null);
        fieldOMElement.addAttribute(Constants.NAME_ATTRIBUTE, fieldName, null);
        fieldOMElement.setText(Constants.SAMPLE_VALUE);
        return fieldOMElement;
    }
}
