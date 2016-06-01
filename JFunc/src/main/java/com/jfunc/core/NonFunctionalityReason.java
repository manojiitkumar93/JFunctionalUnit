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

/**
 * Constructs a non functionality reasons json.<br>
 * <br>
 * {
  "isFunctional": false,
  "Reasons": {
    "package1/class1": {
      "method1": {
        "line1": [
          "reason1",
          "reason2"
        ],
        "line2": [
          "reason1",
          "reason2"
        ]
      }
    },
    "package2/class1": {
      "method2": {
        "line1": [
          "reason1",
          "reason2"
        ],
        "line2": [
          "reason1",
          "reason2"
        ]
      }
    }
  }
}
 * 
 * @author manojk
 *
 */
public class NonFunctionalityReason {

    private ObjectMapper mapper = JsonUtils.getObjectMapper();
    private ObjectNode objectNode = mapper.createObjectNode();
    private ObjectNode reasonsNode = mapper.createObjectNode();
    private static NonFunctionalityReason instance = new NonFunctionalityReason();

    private NonFunctionalityReason() {}

    public static NonFunctionalityReason getInstance() {
        return instance;
    }

    /**
     * Constructs and adds new method object to non functionality reasons json.<br>
     * <br>
     *  {
      "method1": {
        "line1": [
          "reason1",
          "reason2"
        ],
        "line2": [
          "reason1",
          "reason2"
        ]
      }
    }
     * 
     * @param className className to which method belongs
     * @param methodName method to be added
     * @param lineToReasonsListMap non functionality reasons map
     * @param isVoid
     */
    public synchronized void addNewMethod(String className, String methodName, Map<String, List<String>> lineToReasonsListMap,
            boolean isVoid) {
        ObjectNode classNode = (ObjectNode) reasonsNode.get(className);
        if (classNode == null && !methodName.equals(JfuncConstants.INITIALIZATION_METHOD)
                && !lineToReasonsListMap.isEmpty()) {
            updateReasonNode(className, methodName, lineToReasonsListMap, isVoid, mapper.createObjectNode());
        } else if (!methodName.equals(JfuncConstants.INITIALIZATION_METHOD) && !lineToReasonsListMap.isEmpty()) {
            updateReasonNode(className, methodName, lineToReasonsListMap, isVoid, classNode);
        }
    }

    private void updateReasonNode(String className, String methodName, Map<String, List<String>> lineToReasonsListMap,
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
