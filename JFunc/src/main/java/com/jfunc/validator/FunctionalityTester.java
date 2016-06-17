package com.jfunc.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    private JFuncQueueImpl queue = JFuncQueueImpl.getInstance();

    public FunctionalityTester(String packagePath) throws Exception {
        URL filePath = classLoader.getResource(packagePath);
        if (filePath == null) {
            throw new FileNotFoundException();
        } else {
            this.filePath = filePath.toString().replace(JfuncConstants.FILE, JfuncConstants.EMPTY_STRING);
            File file = new File(this.filePath);
            this.projectPath = filterProjectPath(file);
            ProjectDetailsProvider.setProjectPath(this.projectPath);
            classMetaData = constructClassMetaData(file);
        }
    }

    public String testMethod(String methodName, boolean skipLogging, boolean skipPrintStatements)
            throws JfuncException {
        int count = 0;

        // classMethodMetaDataCache is to avoid adding same MethodMetaData more than once in a Queue
        Map<String, List<Integer>> methodMetaDataCache = new HashMap<>();
        // classMetaDataCache is to avoid multiple creation of ClassMetaData for same class
        Map<String, ClassMetaDada> classMetaDataCache = new HashMap<>();

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
        queue.enqueue(new RequirementsWrapper(requiredMethod, skipLogging, skipPrintStatements));
        initializeQueue(requiredMethod, classMetaData, methodMetaDataCache, classMetaDataCache);
        NonFunctionalityReason nonFunctionalityReason = new NonFunctionalityReason();
        JFuncExecutorImpl.getInstnace().startService(queue, nonFunctionalityReason);
        return nonFunctionalityReason.getReasonsJson().toString();
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

        // classMethodMetaDataCache is to avoid adding same MethodMetaData more than once in a Queue
        Map<String, List<Integer>> methodMetaDataCache = new HashMap<>();
        // classMetaDataCache is to avoid multiple creation of ClassMetaData for same class
        Map<String, ClassMetaDada> classMetaDataCache = new HashMap<>();

        List<MethodMetaData> methodMethodDataList = classMetaData.getMethodMetadaList();
        MethodMetaData requiredMethod = null;
        for (MethodMetaData methodMetaData : methodMethodDataList) {
            if (methodMetaData.getMethodName().equals(methodName)) {
                List<String> methodMetaDataArgumentTypes = methodMetaData.getArgumetsClassName();
                if (argumentTypes.equals(methodMetaDataArgumentTypes)) {
                    requiredMethod = methodMetaData;
                    count = count + 1;
                }
            }
            if (count > 1) {
                throw new Exception("Exists two methods which satisfies input arguments");
            }
        }
        if (requiredMethod == null) {
            throw new JfuncException("No method exists with Methtod Name : " + methodName);
        }
        queue.enqueue(new RequirementsWrapper(requiredMethod, skipLogging, skipPrintStatements));
        initializeQueue(requiredMethod, classMetaData, methodMetaDataCache, classMetaDataCache);
        System.out.println("Queue Size" + queue.size());
        NonFunctionalityReason nonFunctionalityReason = new NonFunctionalityReason();
        JFuncExecutorImpl.getInstnace().startService(queue, nonFunctionalityReason);
        return nonFunctionalityReason.getReasonsJson().toString();
    }

    public String testMethod(String methodName, List<String> argumentTypes) throws Exception {
        return testMethod(methodName, argumentTypes, false, false);
    }

    /**
     * A recursive method to add all the internally called methods to {@link JFuncQueueImpl} by
     * 
     * @param requiredMethod
     * @param classMetaData
     * @throws JfuncException
     */
    private void initializeQueue(MethodMetaData requiredMethod, ClassMetaDada classMetaData,
            Map<String, List<Integer>> methodMetaDataCache, Map<String, ClassMetaDada> classMetaDataCache)
            throws JfuncException {
        List<InternalMethod> internallyCalledLocalMethods = requiredMethod.getClassOwnedInternallyCalledMethods();
        List<MethodMetaData> methodMethodDataList = classMetaData.getMethodMetadaList();
        for (MethodMetaData methodMetaData : methodMethodDataList) {
            for (InternalMethod internalMethod : internallyCalledLocalMethods) {
                if (checkForMethodMatch(internalMethod, methodMetaData)
                        && !checkIfMethodMetaDataIsAlreadyAddedInQueue(classMetaData, methodMetaData,
                                methodMetaDataCache)) {
                    // add this methodMetada in queue
                    queue.enqueue(new RequirementsWrapper(methodMetaData, false, false));
                    // there is a chance where this method may call some other methods
                    initializeQueue(methodMetaData, classMetaData, methodMetaDataCache, classMetaDataCache);
                    break;
                }
            }
        }

        List<InternalMethod> projectOwnedMethods = requiredMethod.getProjectOwnedInternallyCalledMethods();
        for (InternalMethod internalMethod : projectOwnedMethods) {
            String className = internalMethod.getOwner() + JfuncConstants.CLASS;
            ClassMetaDada newClassMetaData = checkAndConstructClassMetaData(className, classMetaDataCache);
            if (newClassMetaData != null) {
                List<MethodMetaData> newMethodMethodDataList = newClassMetaData.getMethodMetadaList();
                for (MethodMetaData methodMetaData : newMethodMethodDataList) {
                    if (checkForMethodMatch(internalMethod, methodMetaData)
                            && !checkIfMethodMetaDataIsAlreadyAddedInQueue(classMetaData, methodMetaData,
                                    methodMetaDataCache)) {
                        // add this methodMetada in queue
                        queue.enqueue(new RequirementsWrapper(methodMetaData, false, false));
                        initializeQueue(methodMetaData, classMetaData, methodMetaDataCache, classMetaDataCache);
                    }
                }
            }
        }
    }

    /**
     * Checks whether provided {@link InternalMethod} matches with {@link MethodMetaData}
     * 
     * @param internalMethod
     * @param methodMetaData
     * @return
     */
    private boolean checkForMethodMatch(InternalMethod internalMethod, MethodMetaData methodMetaData) {
        String internalMethodName = internalMethod.getName();
        String methodMetaDataName = methodMetaData.getMethodName();
        List<String> internalMethodArgumetTypes = internalMethod.getArgumentTypes();
        List<String> methodMetaDataArgumentTypes = methodMetaData.getArgumetsClassName();
        return (internalMethodName.equals(methodMetaDataName)
                && internalMethodArgumetTypes.equals(methodMetaDataArgumentTypes)) ? true : false;
    }

    /**
     * Checks whether {@link MethodMetaData} already added to the queue
     * 
     * @param classMetaData
     * @param methodMetaData
     * @return
     */
    private boolean checkIfMethodMetaDataIsAlreadyAddedInQueue(ClassMetaDada classMetaData,
            MethodMetaData methodMetaData, Map<String, List<Integer>> methodMetaDataCache) {
        // we are using method name and method params to create an hashcode because there may be
        // overloaded methods. And one overloaded method can internally call other overloaded
        // method
        int methodMetaDataHashKey =
                (methodMetaData.getMethodName() + methodMetaData.getMethodReturnType().toString()).hashCode();
        boolean contains = false;
        String classMetaDataName = classMetaData.getName();
        if (methodMetaDataCache.containsKey(classMetaDataName)) {
            List<Integer> methodMetaDataKeys = methodMetaDataCache.get(classMetaDataName);
            if (!methodMetaDataKeys.contains(methodMetaDataHashKey)) {
                // if does not contains add this to the list
                methodMetaDataKeys.add(methodMetaDataHashKey);
            } else {
                contains = true;
            }
        } else {
            // methodMetaDataCache does not contain classMetaData then add to it
            List<Integer> methodMetaDataKeys = new ArrayList<>();
            methodMetaDataKeys.add(methodMetaDataHashKey);
            methodMetaDataCache.put(classMetaDataName, methodMetaDataKeys);
        }
        return contains;
    }

    /**
     * Constructs a {@link ClassMetaDada} object for the input class
     * 
     * @param classFile
     * @return {@link ClassMetaDada}
     * @throws JfuncException
     */
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

    /**
     * Filters the project path.<br>
     * <br>
     * <b>Example:</b> If input file path is
     * <ul>
     * <li>"/home/user/Project/build/classes/test/com/jfunc/validator/SomeClass.class"</li>
     * <li>"/home/user/Project/bin/functionalExamples/SomeClass.class"</li>
     * </ul>
     * returns Project path as <b>"/home/user/Project/build/"</b> or
     * <b>"/home/user/Project/bin/"</b>
     * 
     * 
     * @param filePath
     * @return
     */
    private String filterProjectPath(File filePath) {
        String absolutePath = filePath.getAbsolutePath();
        String[] strings = absolutePath.split(JfuncConstants.SLASH);
        StringBuilder builder = new StringBuilder();
        for (String string : strings) {
            builder.append(string).append(JfuncConstants.SLASH);
            if (string.equals(JfuncConstants.BIN) || string.equals(JfuncConstants.BUILD)) {
                break;
            }
        }
        return builder.toString();
    }

    /**
     * Check whether if a {@link ClassMetaDada} object is already constructed for a respective class
     * if not constructs a new
     * 
     * @param className
     * @return {@link ClassMetaDada}
     * @throws JfuncException
     */
    private ClassMetaDada checkAndConstructClassMetaData(String classMetaDataName,
            Map<String, ClassMetaDada> classMetaDataCache) throws JfuncException {
        ClassMetaDada requiredClassMetaData = null;
        String classFilePath = classLoader.getResource(classMetaDataName).toString();
        classFilePath = classFilePath.replace(JfuncConstants.FILE, JfuncConstants.EMPTY_STRING);
        if (classMetaDataCache.containsKey(classMetaDataName)) {
            requiredClassMetaData = classMetaDataCache.get(classMetaDataName);
        }
        File classFile = new File(classFilePath);
        if (FileUtil.isFileExists(classFile)) {
            requiredClassMetaData = constructClassMetaData(classFile);
            classMetaDataCache.put(classMetaDataName, requiredClassMetaData);
        }
        return requiredClassMetaData;
    }
}
