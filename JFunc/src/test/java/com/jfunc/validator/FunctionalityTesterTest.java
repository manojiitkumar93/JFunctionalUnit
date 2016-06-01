package com.jfunc.validator;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jfunc.util.JsonUtils;

import static org.junit.Assert.*;

public class FunctionalityTesterTest {
    FunctionalityTester functionalityTester;
    String filePath =
            "com/jfunc/validator/Example1.class";

    @Before
    public void setUp() throws Exception {
        functionalityTester = new FunctionalityTester(filePath);
    }

    @Test
    public void test() throws Exception {
        ObjectMapper mapper = JsonUtils.getObjectMapper();
        String reasonsString = functionalityTester.testMethod("example");
        System.out.println(reasonsString);
        assertNotNull(reasonsString);
        JsonNode reasonsNode = mapper.readTree(reasonsString);
        assertEquals("false", reasonsNode.get(JfuncConstants.ISFUNCTIONAL).toString());
        ObjectNode node = (ObjectNode) reasonsNode.get("Reasons");
        assertEquals(3, node.get("com/jfunc/validator/Example1").size());

    }
}
