package com.jfunc.core;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jfunc.util.JsonUtils;
import com.jfunc.validator.JfuncConstants;

public class NonFunctionalityReason {

    private ObjectMapper mapper = JsonUtils.getObjectMapper();
    private ObjectNode objectNode = mapper.createObjectNode();
    private ObjectNode reasonsNode = mapper.createObjectNode();
    private static NonFunctionalityReason instance = new NonFunctionalityReason();

    private NonFunctionalityReason() {}

    public static NonFunctionalityReason getInstance() {
        return instance;
    }

    public void addNewMethod(String className, String methodName, Map<String, List<String>> lineToReasonsListMap,
            boolean isVoid) {
        ObjectNode classNode = (ObjectNode) reasonsNode.get(className);
        if (classNode == null && !methodName.equals(JfuncConstants.INITIALIZATION_METHOD)
                && !lineToReasonsListMap.isEmpty()) {
            updatereasonNode(className, methodName, lineToReasonsListMap, isVoid, mapper.createObjectNode());
        } else if (!methodName.equals(JfuncConstants.INITIALIZATION_METHOD) && !lineToReasonsListMap.isEmpty()) {
            updatereasonNode(className, methodName, lineToReasonsListMap, isVoid, classNode);
        }
    }

    private void updatereasonNode(String className, String methodName, Map<String, List<String>> lineToReasonsListMap,
            boolean isVoid, ObjectNode classNode) {
        ObjectNode lineNode = mapper.createObjectNode();
        Set<Entry<String, List<String>>> elements = lineToReasonsListMap.entrySet();
        for (Entry<String, List<String>> element : elements) {
            ArrayNode reasons = mapper.createArrayNode();
            List<String> reasonsList = (List<String>) element.getValue();
            for (String reason : reasonsList) {
                reasons.add(reason);
            }
            lineNode.set(element.getKey(), reasons);
            if (isVoid) {
                lineNode.put(JfuncConstants.METHODRETURNTYPE, JfuncConstants.VOID);
            }
        }
        classNode.set(methodName, lineNode);
        reasonsNode.set(className, classNode);
    }

    public ObjectNode getReasonsJson() {
        if (reasonsNode.size() > 0) {
            objectNode.set(JfuncConstants.REASONS, reasonsNode);
            objectNode.put(JfuncConstants.ISFUNCTIONAL, false);
        } else {
            objectNode.put(JfuncConstants.ISFUNCTIONAL, true);
        }
        return objectNode;
    }

}
