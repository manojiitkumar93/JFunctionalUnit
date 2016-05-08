package com.jfunc.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jfunc.asm.InternalFeild;
import com.jfunc.asm.MethodMetaData;
import com.jfunc.core.Validator;
import com.jfunc.util.FieldUtil;
import com.jfunc.util.FileUtil;
import com.jfunc.util.JsonUtils;
import com.jfunc.util.VoidTypeUtil;

public class FunctionValidator implements Validator {

    private final String filePath;
    private File classFile;

    private ClassReader classReader = null;
    private ClassNode classNode = new ClassNode();
    private List<MethodNode> methodNodes;

    public FunctionValidator(String path) throws FileNotFoundException {
        this.filePath = path;
        File classFile = new File(filePath);
        if (FileUtil.isFileExists(classFile)) {
            this.classFile = classFile;
            init(this.classFile);
        } else {
            throw new FileNotFoundException();
        }
    }

    @SuppressWarnings("unchecked")
    private void init(File classFile) {
        try (InputStream fileStream = new FileInputStream(classFile)) {
            classReader = new ClassReader(fileStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        classReader.accept(classNode, 0);
        methodNodes = classNode.methods;
    }

    @Override
    public String validate(String methodName) {
        return validate(methodName, false, false);
    }

    @Override
    public String validate(String methodName, boolean skipLogStatements, boolean skipPrintStatements) {
        for (MethodNode methodNode : methodNodes) {
            if (StringUtils.equals(methodName, methodNode.name)) {
                return getReasons(methodNode, skipLogStatements, skipPrintStatements).toString();
            }
        }
        return "No method found with name" + methodName;
    }

    // At present we are checking for 1) Void 2) Print-Statements 3) Log-Statements
    private ObjectNode getReasons(MethodNode methodNode, boolean skipLogStatements, boolean skipPrintStatements) {
        ObjectMapper mapper = JsonUtils.getObjectMapper();
        ObjectNode objectNode = mapper.createObjectNode();
        ArrayNode reasonObjectNode = mapper.createArrayNode();
        MethodMetaData methodMetaData = new MethodMetaData(methodNode);

        // First check whether method type is "Void"
        if (VoidTypeUtil.isVoid(methodMetaData)) {
            reasonObjectNode.add(VoidTypeUtil.getReasons(methodMetaData));
        }

        // Check for any non final object is accessing in a method
        if (FieldUtil.doesMethodRefferedObjects(methodMetaData)) {
            reasonObjectNode.add(FieldUtil.getREasonForRefferedObjects(methodMetaData));
        }
        // Check for any "Print" and "Log" statements in a method
        if (!(skipPrintStatements && skipLogStatements)) {
            List<InternalFeild> internalFields = methodMetaData.getInternallyRefferedFields();
            for (InternalFeild internalField : internalFields) {
                if (!skipPrintStatements && FieldUtil.containsPrintStatements(internalField)) {
                    reasonObjectNode.add(FieldUtil.getReasonsForPrintStatements(internalField));
                }
                if (!skipLogStatements && FieldUtil.containsLogStatements(internalField)) {
                    reasonObjectNode.add(FieldUtil.getReasonsForLogStatements(internalField));
                }
            }
        }
        if (reasonObjectNode.size() > 0) {
            objectNode.put(JfuncConstants.ISFUNCTIONAL, false);
            objectNode.set(JfuncConstants.REASONS, reasonObjectNode);
        } else {
            objectNode.put(JfuncConstants.ISFUNCTIONAL, true);
        }

        return objectNode;
    }


}
