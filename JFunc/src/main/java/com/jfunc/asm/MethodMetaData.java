package com.jfunc.asm;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

public class MethodMetaData {

    private static Printer printer = new Textifier();
    private static TraceMethodVisitor mp = new TraceMethodVisitor(printer);

    private final MethodNode methodNode;
    private final List<String> byteCode;
    private final String returnType;
    private final Map<LineNumberNode, List<AbstractInsnNode>> lineToOperationsMap;
    private final List<InternalMethod> internallyCalledMethods = new ArrayList<>();
    private final List<InternalFeild> internallyRefferedFields = new ArrayList<>();

    public MethodMetaData(MethodNode methodNode) {
        this.methodNode = methodNode;
        this.byteCode = generateByteCodeOfMethod(methodNode);
        this.returnType = Type.getReturnType(methodNode.desc).getClassName();
        this.lineToOperationsMap = getLineToOperationsMap(methodNode.instructions);
    }

    public String getMethodNode() {
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

    public List<InternalMethod> getInternallyCalledMethods() {
        return Collections.unmodifiableList(internallyCalledMethods);
    }

    public List<InternalFeild> getInternallyRefferedFields() {
        return Collections.unmodifiableList(internallyRefferedFields);
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

    private Map<LineNumberNode, List<AbstractInsnNode>> getLineToOperationsMap(InsnList insList) {
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
                        internallyCalledMethods.add(new InternalMethod((MethodInsnNode) insList.get(i), key));
                    }
                }
            }
        }
        return lineToOperationsMap;
    }
}
