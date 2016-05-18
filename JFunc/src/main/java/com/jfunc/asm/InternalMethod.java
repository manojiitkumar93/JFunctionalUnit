package com.jfunc.asm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.Type;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodInsnNode;

public class InternalMethod {

    private MethodInsnNode methodInsnNode;
    private final LineNumberNode lineNumberNode;
    private final String desc;
    private final String name;
    private final String owner;
    private final String returnType;
    private final List<String> argumentTypeList = new ArrayList<>();

    public InternalMethod(MethodInsnNode methodInsNode, LineNumberNode lineNumberNode) {
        this.lineNumberNode = lineNumberNode;
        this.methodInsnNode = methodInsNode;
        this.desc = methodInsnNode.desc;
        this.name = methodInsnNode.name;
        this.owner = methodInsnNode.owner;
        this.returnType = Type.getReturnType(desc).getClassName();
        for (Type type : Type.getArgumentTypes(desc)) {
            argumentTypeList.add(type.getClassName());
        }
    }

    public int getLineNumber() {
        return this.lineNumberNode.line;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.desc;
    }

    public String getOwner() {
        return this.owner;
    }

    public String getReturnType() {
        return this.returnType;
    }

    public List<String> getArgumentTypes() {
        return Collections.unmodifiableList(argumentTypeList);
    }

    public MethodInsnNode getMethodNode() {
        return this.methodInsnNode;
    }


}
