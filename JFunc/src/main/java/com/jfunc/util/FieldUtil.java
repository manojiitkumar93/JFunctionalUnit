package com.jfunc.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jfunc.asm.InternalFeild;
import com.jfunc.asm.MethodMetaData;
import com.jfunc.validator.JfuncConstants;

public class FieldUtil {

    public static boolean containsPrintStatements(InternalFeild internalField) {
        return (StringUtils.contains(internalField.getDescription(), JfuncConstants.SYSTEMS_PRINTLN)) ? true : false;
    }

    public static ObjectNode getReasonsForPrintStatements(InternalFeild internalField) {
        ObjectMapper mapper = JsonUtils.getObjectMapper();
        ObjectNode resonsNode = mapper.createObjectNode();
        resonsNode.put(JfuncConstants.LINENUMBER + internalField.getLineNumber(), JfuncConstants.SYSTEMS_PRINTLN);
        return resonsNode;
    }

    public static boolean containsLogStatements(InternalFeild internalField) {
        return (StringUtils.contains(internalField.getDescription(), JfuncConstants.LOGGER)) ? true : false;
    }

    public static ObjectNode getReasonsForLogStatements(InternalFeild internalField) {
        ObjectMapper mapper = JsonUtils.getObjectMapper();
        ObjectNode resonsNode = mapper.createObjectNode();
        resonsNode.put(JfuncConstants.LINENUMBER + internalField.getLineNumber(), JfuncConstants.LOGGER);
        return resonsNode;
    }

    public static boolean doesMethodRefferedObjects(MethodMetaData methodMetaData) {
        List<InternalFeild> refferedFields = methodMetaData.getInternallyRefferedFields();
        return !refferedFields.isEmpty();
    }
    
    public static ObjectNode getREasonForRefferedObjects(MethodMetaData methodMetaData){
        ObjectMapper mapper = JsonUtils.getObjectMapper();
        ObjectNode resonsNode = mapper.createObjectNode();
        List<InternalFeild> refferedFields = methodMetaData.getInternallyRefferedFields();
        for(InternalFeild internalField : refferedFields){
            resonsNode.put(JfuncConstants.LINENUMBER + internalField.getLineNumber(), JfuncConstants.REFFERED_OBJECT);
        }
        return resonsNode;
    }

}
