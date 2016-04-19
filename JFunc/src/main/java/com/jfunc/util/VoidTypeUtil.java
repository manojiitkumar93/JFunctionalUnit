package com.jfunc.util;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jfunc.asm.MethodMetaData;
import com.jfunc.validator.JfuncConstants;

public class VoidTypeUtil {

    public static boolean isVoid(MethodMetaData methodMetaData) {
        return (StringUtils.equals(JfuncConstants.VOID, methodMetaData.getMethodReturnType())) ? true : false;
    }

    public static ObjectNode getReasons(MethodMetaData methodMetaData) {
        ObjectMapper mapper = JsonUtils.getObjectMapper();
        ObjectNode resonsNode = mapper.createObjectNode();
        resonsNode.put(JfuncConstants.METHODRETURNTYPE, JfuncConstants.VOID);
        return resonsNode;
    }

}
