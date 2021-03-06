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

import com.sap.conn.jco.JCoField;
import com.sap.conn.jco.JCoFieldIterator;
import com.sap.conn.jco.JCoFunction;
import com.sap.conn.jco.JCoFunctionTemplate;
import com.sap.conn.jco.JCoListMetaData;
import com.sap.conn.jco.JCoParameterList;
import com.sap.conn.jco.JCoStructure;
import com.sap.conn.jco.JCoTable;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.log4j.Logger;

/**
 * This class Serializes a RFC function to provide
 * its XML representation
 */
public class RFCFunctionSerializer {

    private static Logger LOG = Logger.getLogger(RFCFunctionSerializer.class);
    private OMFactory factory = OMAbstractFactory.getOMFactory();

    protected OMElement serializeFunction(JCoFunctionTemplate functionTemplate, String functionName) {

        OMElement document = factory.createOMElement(Constants.BAPIRFC, null);
        document.addAttribute("name", functionName, null);
        addFunctionImports(document, functionTemplate);
        addFunctionTables(document, functionTemplate);
        System.out.println("\n Serialized RFC Function: \n" + document.toString());
        return document;
    }

    private void addFunctionImports(OMElement document, JCoFunctionTemplate functionTemplate) {

        JCoListMetaData imports = functionTemplate.getImportParameterList();
        OMElement importsOM = factory.createOMElement(Constants.IMPORT_QNAME, null);
        document.addChild(importsOM);
        JCoFunction function = functionTemplate.getFunction();

        OMElement tablesOMElement = factory.createOMElement(Constants.TABLES_QNAME, null);
        importsOM.addChild(tablesOMElement);

        int size = (imports != null) ? imports.getFieldCount() : 0;
        for (int i = 0; i < size; i++) {

            String type = imports.getTypeAsString(i);
            String paramName = imports.getName(i);
            boolean isOptional = imports.isOptional(i);
            OMElement importEntryOM = null;
            switch (type) {
                case "STRUCTURE":
                    JCoStructure structure = function.getImportParameterList().getStructure(paramName);
                    importEntryOM = handleStructure(paramName, structure);
                    break;

                case "TABLE":
                    JCoTable table = function.getImportParameterList().getTable(paramName);
                    //handleTables return the updated tables OM element
                    handleTables(paramName, table, tablesOMElement);
                    break;

                default:
                    importEntryOM = handleField(paramName);
            }
            importsOM.addChild(importEntryOM);
        }
    }

    private void addFunctionTables(OMElement document, JCoFunctionTemplate functionTemplate) {

        JCoParameterList tables = functionTemplate.getFunction().getTableParameterList();
        OMElement tablesOMElement = factory.createOMElement(Constants.TABLES_QNAME, null);
        if (tables != null) {
            document.addChild(tablesOMElement);
            tables.forEach((JCoField tableField) -> {
                String tableName = tableField.getName();
                String type = tableField.getTypeAsString();

                if (type.equalsIgnoreCase("TABLE")) {
                    handleTables(tableName, functionTemplate.getFunction().getTableParameterList().getTable(tableName), tablesOMElement);
                } else {
                    LOG.error("Type not table of item in table list");
                }
            });
        }
    }

    private OMElement handleStructure(String structureName, JCoStructure structure) {

        OMElement structureOMElement = factory.createOMElement(Constants.STRUCTURE_QNAME, null);
        structureOMElement.addAttribute(Constants.NAME_ATTRIBUTE, structureName, null);

        if (structure != null) {
            JCoFieldIterator fieldIterator = structure.getFieldIterator();
            while (fieldIterator.hasNextField()) {
                JCoField field = fieldIterator.nextField();
                String fieldName = field.getName();
                String fieldType = field.getTypeAsString();
                if (fieldType.equalsIgnoreCase("STRUCTURE")) {
                    // process inner structures
                    OMElement innerStructureOMElement = handleStructure(fieldName, structure.getStructure(fieldName));
                    structureOMElement.addChild(innerStructureOMElement);
                } else if (fieldType.equalsIgnoreCase("TABLE")) {
                    OMElement tableOMElement = handleTable(fieldName, structure.getTable(fieldName));
                    structureOMElement.addChild(tableOMElement);
                } else {
                    // consider as field if the type is not table or structure ??
                    OMElement fieldOMElement = handleField(fieldName);
                    structureOMElement.addChild(fieldOMElement);
                }
            }
        }
        return structureOMElement;
    }

    private void handleTables(String tableName, JCoTable table, OMElement tablesOMElement) {

        // are all import tables grouped together in the tables OM element??
        OMElement tableOMElement = handleTable(tableName, table);
        tablesOMElement.addChild(tableOMElement);

    }

    /**
     * Creates a tables OM object based if the the table structure and adds a single
     * row as a reference.
     *
     * @param tableName JCo table Name
     * @param table JCoTable Object
     * @return tables OM element
     */
    private OMElement handleTable(String tableName, JCoTable table) {
        OMElement tableOMElement = factory.createOMElement(Constants.TABLE_QNAME, null);
        tableOMElement.addAttribute(Constants.NAME_ATTRIBUTE, tableName, null);
        // fill the table here if possible?
        OMElement rowOMElement = factory.createOMElement(Constants.ROW_QNAME, null);
        rowOMElement.addAttribute("id", "0", null);
        if (table.getNumColumns() > 0) {
            // add a row to the table because there are columns.
            // we will add a sample row into the skeleton with all columns.
            // user can add more rows be referring the sample row.
            tableOMElement.addChild(rowOMElement);
        }

            JCoFieldIterator recordFields = table.getFieldIterator();
            while (recordFields.hasNextField()) {
                JCoField record = recordFields.nextField();
                String recordName = record.getName();
                String recordType = record.getTypeAsString();

                if (recordType.equalsIgnoreCase("STRUCTURE")) {
                    if (table.getNumRows() > 0) {
                        rowOMElement.addChild(handleStructure(recordName, record.getStructure()));
                    } else {
                        // since we cannot access the structure of the inner structure
                        // let's add the structure OM element only
                        OMElement innerStructureOMElement = factory.createOMElement(Constants.STRUCTURE_QNAME, null);
                        innerStructureOMElement.addAttribute(Constants.NAME_ATTRIBUTE, recordName, null);
                        rowOMElement.addChild(innerStructureOMElement);

                        LOG.warn("Structure " + recordName + " inside table " + tableName + " cannot be processed.");
                    }
                } else if (recordType.equalsIgnoreCase("TABLE")) {
                    if (table.getNumRows() > 0) {
                        rowOMElement.addChild(handleTable(recordName, record.getTable()));
                    } else {
                        // since we cannot process the structure of the inner table
                        // let's add the table OM only
                        OMElement innerTableOMElement = factory.createOMElement(Constants.TABLE_QNAME, null);
                        innerTableOMElement.addAttribute(Constants.NAME_ATTRIBUTE, recordName, null);
                        OMElement innerRowOMElement = factory.createOMElement(Constants.ROW_QNAME, null);
                        innerRowOMElement.addAttribute("id", "", null);
                        rowOMElement.addChild(innerTableOMElement);

                        LOG.warn("Table " + recordName + " inside table " + tableName + " cannot be processed.");
                    }
                } else {
                    rowOMElement.addChild(handleField(recordName));
                }
            }
        return tableOMElement;
    }

    private OMElement handleField(String fieldName) {

        OMElement fieldOMElement = factory.createOMElement(Constants.FIELD_QNAME, null);
        fieldOMElement.addAttribute(Constants.NAME_ATTRIBUTE, fieldName, null);
        fieldOMElement.setText(Constants.SAMPLE_VALUE);
        return fieldOMElement;
    }
}
