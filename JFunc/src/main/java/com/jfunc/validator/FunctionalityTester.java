package com.jfunc.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import com.jfunc.asm.ClassMetaDada;
import com.jfunc.asm.InternalMethod;
import com.jfunc.asm.MethodMetaData;
import com.jfunc.core.NonFunctionalityReason;
import com.jfunc.core.ProjectDetailsProvider;
import com.jfunc.exception.JfuncException;
import com.jfunc.model.impl.JFuncExecutorImpl;
import com.jfunc.model.impl.JFuncQueueImpl;
import com.jfunc.util.FileUtil;

public class FunctionalityTester {

    private final String filePath;
    private final String projectPath;
    private ClassMetaDada classMetaData;
    private ClassReader classReader = null;
    private JFuncQueueImpl queue = JFuncQueueImpl.getInstance();

    public FunctionalityTester(String path) throws Exception {
        this.projectPath = filterProjectPath(path);
        ProjectDetailsProvider.setProjectPath(this.projectPath);
        this.filePath = path;
        File classFile = new File(filePath);
        if (FileUtil.isFileExists(classFile)) {
            classMetaData = constructClassMetaData(classFile);
        } else {
            throw new FileNotFoundException();
        }
    }

    public String testMethod(String methodName, boolean skipLogging, boolean skipPrintStatements)
            throws JfuncException {
        int count = 0;
        List<MethodMetaData> methodMethodDataList = classMetaData.getMethodMetadaList();
        MethodMetaData requiredMethod = null;
        for (MethodMetaData methodMetaData : methodMethodDataList) {
            if (methodMetaData.getMethodName().equals(methodName)) {
                requiredMethod = methodMetaData;
                count = count + 1;
            }
            if (count > 1) {
                throw new JfuncException("Exists two methods with same name : " + methodMetaData.getMethodName());
            }
        }
        if (requiredMethod == null) {
            throw new JfuncException("No method exists with Methtod Name : " + methodName);
        }
        queue.enqueue(requiredMethod);
        initializeQueue(requiredMethod, classMetaData);
        NonFunctionalityReason nonFunctionalityReason =
                ValidatorUtil.validate(requiredMethod, skipLogging, skipPrintStatements);
        JFuncExecutorImpl.getInstnace().startService();
        return (requiredMethod == null) ? "No method exists with Methtod Name : " + methodName
                : nonFunctionalityReason.getReasonsJson().toString();
    }

    public String testMethod(String methodName) throws Exception {
        return testMethod(methodName, false, false);
    }

    /**
     * Tests whether the implemented method follows all the rules of functionality..<br>
     * <b>argumentTypes : </b> input "argumentTypes" is a list of argument class names.<br>
     * <b>Example : </b> someMethod(int arg1,List<String> arg2){....}. argumentTypes =
     * ["int","java.util.List"]
     * 
     * @param methodName
     * @param argumentTypes
     * @return
     * @throws Exception
     */
    public String testMethod(String methodName, List<String> argumentTypes, boolean skipLogging,
            boolean skipPrintStatements) throws Exception {
        int count = 0;
        List<MethodMetaData> methodMethodDataList = classMetaData.getMethodMetadaList();
        MethodMetaData requiredMethod = null;
        for (MethodMetaData methodMetaData : methodMethodDataList) {
            if (methodMetaData.getMethodName().equals(methodName)
                    && argumentTypes.equals(methodMetaData.getArgumetsClassName())) {
                requiredMethod = methodMetaData;
                count = count + 1;
            }
            if (count > 1) {
                throw new Exception("Exists two methods which satisfies input arguments");
            }
        }
        queue.enqueue(requiredMethod);
        initializeQueue(requiredMethod, classMetaData);
        NonFunctionalityReason nonFunctionalityReason =
                ValidatorUtil.validate(requiredMethod, skipLogging, skipPrintStatements);
        JFuncExecutorImpl.getInstnace().startService();
        return (requiredMethod == null) ? "No method exists with Methtod Name : " + methodName
                + " or method does not exists with Argumnets : " + argumentTypes
                : nonFunctionalityReason.getReasonsJson().toString();
    }

    public String testMethod(String methodName, List<String> argumentTypes) throws Exception {
        return testMethod(methodName, argumentTypes, false, false);
    }

    private void initializeQueue(MethodMetaData requiredMethod, ClassMetaDada classMetaData) throws JfuncException {
        List<InternalMethod> internallyCalledLocalMethods = requiredMethod.getClassOwnedInternallyCalledMethods();
        List<MethodMetaData> methodMethodDataList = classMetaData.getMethodMetadaList();
        for (MethodMetaData methodMetaData : methodMethodDataList) {
            for (InternalMethod internalMethod : internallyCalledLocalMethods) {
                if (checkForMethodMatch(internalMethod, methodMetaData)) {
                    // add this methodMetada in queue
                    queue.enqueue(methodMetaData);
                    break;
                }
            }
        }
        List<InternalMethod> projectOwnedMethods = requiredMethod.getProjectOwnedInternallyCalledMethods();
        for (InternalMethod internalMethod : projectOwnedMethods) {
            String classFilePath = projectPath + internalMethod.getOwner() + JfuncConstants.CLASS;
            File classFile = new File(classFilePath);
            if (FileUtil.isFileExists(classFile)) {
                ClassMetaDada newClassMetaData = constructClassMetaData(classFile);
                List<MethodMetaData> newMethodMethodDataList = newClassMetaData.getMethodMetadaList();
                for (MethodMetaData methodMetaData : newMethodMethodDataList) {
                    if (checkForMethodMatch(internalMethod, methodMetaData)) {
                        queue.enqueue(methodMetaData);
                        // add this methodMetada in queue
                        initializeQueue(methodMetaData, classMetaData);
                    }
                }
            }

        }
    }

    private boolean checkForMethodMatch(InternalMethod internalMethod, MethodMetaData methodMetaData) {
        String internalMethodName = internalMethod.getName();
        String methodMetaDataName = methodMetaData.getMethodName();
        List<String> internalMethodArgumetTypes = internalMethod.getArgumentTypes();
        List<String> methodMetaDataArgumentTypes = methodMetaData.getArgumetsClassName();
        return (internalMethodName.equals(methodMetaDataName)
                && internalMethodArgumetTypes.equals(methodMetaDataArgumentTypes)) ? true : false;
    }

    private ClassMetaDada constructClassMetaData(File classFile) throws JfuncException {
        try (InputStream fileStream = new FileInputStream(classFile)) {
            classReader = new ClassReader(fileStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ClassNode classNode = new ClassNode();
        classReader.accept(classNode, 0);
        return new ClassMetaDada(classNode);
    }

    private String filterProjectPath(String filePath) {
        String[] strings = filePath.split(JfuncConstants.SLASH);
        StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            builder.append(string).append(JfuncConstants.SLASH);
            if (string.equals(JfuncConstants.BIN)) {
                break;
            }
        }
        return builder.toString();
    }

}
