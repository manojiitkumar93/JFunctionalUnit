package com.jfunc.asm;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import com.jfunc.core.ProjectDetailsProvider;
import com.jfunc.exception.JfuncException;
import com.jfunc.validator.JfuncConstants;

/**
 * Class to store all required metadata of a method.
 * 
 * @author manojk
 *
 */
public class MethodMetaData {

    private static Printer printer = new Textifier();
    private static TraceMethodVisitor mp = new TraceMethodVisitor(printer);

    private final MethodNode methodNode;
    private final List<String> byteCode;
    private final String returnType;
    private final String parentClass;
    private final Map<LineNumberNode, List<AbstractInsnNode>> lineToOperationsMap;
    private final List<InternalFeild> internallyRefferedFields = new ArrayList<>();
    private final List<String> argumentClassNameList;

    // List of all internally called method
    private final List<InternalMethod> allInternallyCalledMethods = new ArrayList<>();
    // List of internally called methods related to the class of input method.
    private final List<InternalMethod> classOwnedInternallyCalledMethods = new ArrayList<>();
    // List of internally called methods related to the user's project.
    private final List<InternalMethod> projectOwnedInternallyCalledMethods = new ArrayList<>();
    // List of internally called methods related to other dependencies.
    private final List<InternalMethod> otherInternallyCalledMethods = new ArrayList<>();

    public MethodMetaData(MethodNode methodNode, String parentClass) throws JfuncException {
        this.methodNode = methodNode;
        this.parentClass = parentClass;
        this.byteCode = generateByteCodeOfMethod(methodNode);
        this.returnType = Type.getReturnType(methodNode.desc).getClassName();
        this.lineToOperationsMap = getLineToOperationsMap(methodNode.instructions);
        this.argumentClassNameList = getArgumetsClassNames(methodNode);
    }

    public String getClassName() {
        return this.parentClass;
    }

    public String getMethodName() {
        return this.methodNode.name;
    }

    public String getMethodReturnType() {
        return this.returnType;
    }

    public List<String> getByteCode() {
        return Collections.unmodifiableList(this.byteCode);
    }

    public Map<LineNumberNode, List<AbstractInsnNode>> getLineToOperationsMap() {
        return Collections.unmodifiableMap(this.lineToOperationsMap);
    }

    public List<InternalMethod> getAllInternallyCalledMethods() {
        return Collections.unmodifiableList(allInternallyCalledMethods);
    }

    public List<InternalMethod> getClassOwnedInternallyCalledMethods() {
        return Collections.unmodifiableList(classOwnedInternallyCalledMethods);
    }

    public List<InternalMethod> getProjectOwnedInternallyCalledMethods() {
        return Collections.unmodifiableList(projectOwnedInternallyCalledMethods);
    }

    public List<InternalMethod> getOtherInternallyCalledMethodss() {
        return Collections.unmodifiableList(otherInternallyCalledMethods);
    }

    public List<InternalFeild> getInternallyRefferedFields() {
        return Collections.unmodifiableList(internallyRefferedFields);
    }

    public List<String> getArgumetsClassName() {
        return Collections.unmodifiableList(argumentClassNameList);
    }

    private List<String> getArgumetsClassNames(MethodNode methodNode) {
        List<String> argumetClassNameList = new ArrayList<>();
        for (Type type : Type.getArgumentTypes(methodNode.desc)) {
            argumetClassNameList.add(type.getClassName());
        }
        return argumetClassNameList;
    }

    private List<String> generateByteCodeOfMethod(MethodNode methodNode) {
        List<String> byteCodeList = new ArrayList<>();
        InsnList inList = methodNode.instructions;
        for (int i = 0; i < inList.size(); i++) {
            byteCodeList.add(insnToString(inList.get(i)));
        }
        return byteCodeList;
    }

    private String insnToString(AbstractInsnNode insn) {
        insn.accept(mp);
        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();
        return sw.toString();
    }

    private Map<LineNumberNode, List<AbstractInsnNode>> getLineToOperationsMap(InsnList insList) throws JfuncException {
        ProjectDetailsProvider projectDetaisInstance = ProjectDetailsProvider.getInstance();
        Map<LineNumberNode, List<AbstractInsnNode>> lineToOperationsMap = new LinkedHashMap<>();
        LineNumberNode key = null;
        for (int i = 0; i < insList.size(); i++) {
            if (insList.get(i) instanceof LineNumberNode) {
                key = (LineNumberNode) insList.get(i);
                lineToOperationsMap.put(key, new ArrayList<AbstractInsnNode>());
            } else {
                if (key != null && lineToOperationsMap.containsKey(key)) {
                    lineToOperationsMap.get(key).add(insList.get(i));
                    if (insList.get(i) instanceof FieldInsnNode) {
                        internallyRefferedFields.add(new InternalFeild((FieldInsnNode) insList.get(i), key));
                    }
                    if (insList.get(i) instanceof MethodInsnNode) {
                        MethodInsnNode methodInsNode = (MethodInsnNode) insList.get(i);
                        // we wont care for the constructors (Constructors also be MethodInsnNode
                        // with method name <init>)
                        if (!methodInsNode.name.equals(JfuncConstants.INITIALIZATION_METHOD)) {
                            allInternallyCalledMethods.add(new InternalMethod(methodInsNode, key));
                            Set<String> packageDetailsSet = projectDetaisInstance.getAllClasses();
                            if (StringUtils.equals(methodInsNode.owner, parentClass)) {
                                classOwnedInternallyCalledMethods.add(new InternalMethod(methodInsNode, key));
                            } else if (packageDetailsSet.contains(methodInsNode.owner + JfuncConstants.CLASS)) {
                                projectOwnedInternallyCalledMethods.add(new InternalMethod(methodInsNode, key));
                            } else {
                                otherInternallyCalledMethods.add(new InternalMethod(methodInsNode, key));
                            }
                        }

                    }
                }
            }
        }
        return lineToOperationsMap;
    }
}
