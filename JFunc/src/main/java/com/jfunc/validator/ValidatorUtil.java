package com.jfunc.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.jfunc.asm.InternalFeild;
import com.jfunc.asm.MethodMetaData;
import com.jfunc.core.NonFunctionalityReason;

public class ValidatorUtil {

    public static NonFunctionalityReason validate(MethodMetaData methodMetaData) {
        return validate(methodMetaData, false, false);
    }

    public static NonFunctionalityReason validate(MethodMetaData methodMetaData, boolean skipLogStatements,
            boolean skipPrintStatements) {
        return getReasons(methodMetaData, skipLogStatements, skipPrintStatements);
    }

    // At present we are checking for 1) Void 2) Print-Statements 3) Log-Statements
    private static NonFunctionalityReason getReasons(MethodMetaData methodMetaData, boolean skipLogStatements,
            boolean skipPrintStatements) {

        Map<String, List<String>> lineToReasonsList = new HashMap<>();

        // Check for any non final object is accessing in a method
        if (!methodMetaData.getInternallyRefferedFields().isEmpty()) {
            List<InternalFeild> refferedFields = methodMetaData.getInternallyRefferedFields();
            for (InternalFeild internalField : refferedFields) {
                updateLineToReasonsListMap(lineToReasonsList, JfuncConstants.REFFERED_OBJECT, internalField);
            }
        }

        // Check for any "Print" and "Log" statements in a method
        if (!(skipPrintStatements && skipLogStatements)) {
            List<InternalFeild> internalFields = methodMetaData.getInternallyRefferedFields();
            for (InternalFeild internalField : internalFields) {
                if (!skipPrintStatements && containsPrintStatements(internalField)) {
                    updateLineToReasonsListMap(lineToReasonsList, JfuncConstants.SYSTEMS_PRINTLN, internalField);
                }
                if (!skipLogStatements && containsLogStatements(internalField)) {
                    updateLineToReasonsListMap(lineToReasonsList, JfuncConstants.LOGGER, internalField);
                }
            }
        }
        NonFunctionalityReason instance = NonFunctionalityReason.getInstance();
        instance.addNewMethod(methodMetaData.getClassName(),methodMetaData.getMethodName(), lineToReasonsList, isVoid(methodMetaData));
        return instance;
    }

    private static void updateLineToReasonsListMap(Map<String, List<String>> lineToReasonsList, String reason,
            InternalFeild internalField) {
        String lineNumber = internalField.getLineNumber();
        if (lineToReasonsList.containsKey(lineNumber)) {
            lineToReasonsList.get(lineNumber).add(reason);
        } else {
            List<String> reasonsList = new ArrayList<>();
            reasonsList.add(reason);
            lineToReasonsList.put(internalField.getLineNumber(), reasonsList);
        }
    }

    private static boolean containsPrintStatements(InternalFeild internalField) {
        return (StringUtils.contains(internalField.getDescription(), JfuncConstants.SYSTEMS_PRINTLN)) ? true : false;
    }

    private static boolean containsLogStatements(InternalFeild internalField) {
        return (StringUtils.contains(internalField.getDescription(), JfuncConstants.LOGGER)) ? true : false;
    }

    private static boolean isVoid(MethodMetaData methodMetaData) {
        return (StringUtils.equals(JfuncConstants.VOID, methodMetaData.getMethodReturnType())) ? true : false;
    }
}


