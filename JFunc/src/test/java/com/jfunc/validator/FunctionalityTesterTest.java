package com.jfunc.validator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jfunc.util.JsonUtils;

public class FunctionalityTesterTest {
    FunctionalityTester functionalityTester;
    String filePath = "com/jfunc/validator/Example1.class";

    @Before
    public void setUp() throws Exception {
        functionalityTester = new FunctionalityTester(filePath);
    }

    @Test
    public void test_byJustPassingMethodName() throws Exception {
        ObjectMapper mapper = JsonUtils.getObjectMapper();
        String reasonsString = functionalityTester.testMethod("example", false, false);
        System.out.println(reasonsString);
        assertNotNull(reasonsString);
        JsonNode reasonsNode = mapper.readTree(reasonsString);
        assertEquals("false", reasonsNode.get(JfuncConstants.ISFUNCTIONAL).toString());
        ObjectNode node = (ObjectNode) reasonsNode.get("Reasons");
        assertEquals(2, node.get("com/jfunc/validator/Example1").size());

    }

    @Test
    public void test_byPassingMethodNameAlongWithArguments() throws Exception {
        List<String> argumentsClassNameList = new ArrayList<>();
        argumentsClassNameList.add("int");
        argumentsClassNameList.add("java.util.List");
        ObjectMapper mapper = JsonUtils.getObjectMapper();
        String reasonsString = functionalityTester.testMethod("example", argumentsClassNameList,false,true);
        System.out.println(reasonsString);
        assertNotNull(reasonsString);
        JsonNode reasonsNode = mapper.readTree(reasonsString);
        assertEquals("false", reasonsNode.get(JfuncConstants.ISFUNCTIONAL).toString());
        ObjectNode node = (ObjectNode) reasonsNode.get("Reasons");
        assertEquals(2, node.get("com/jfunc/validator/Example1").size());

    }
}
