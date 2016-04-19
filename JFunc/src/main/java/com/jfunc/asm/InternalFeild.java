package com.jfunc.asm;

import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.LineNumberNode;

public class InternalFeild {

    private final FieldInsnNode fieldInsnNode;
    private final LineNumberNode lineNumberNode;
    private final String owner;
    private final String description;
    private final String name;

    public InternalFeild(FieldInsnNode feildInsNode, LineNumberNode lineNumberNode) {
        this.fieldInsnNode = feildInsNode;
        this.lineNumberNode = lineNumberNode;
        this.owner = fieldInsnNode.owner;
        this.description = fieldInsnNode.desc;
        this.name = fieldInsnNode.name;
    }

    public String getName() {
        return this.name;
    }

    public String getOwner() {
        return this.owner;
    }

    public String getDescription() {
        return this.description;
    }

    public String getLineNumber() {
        return Integer.toString(lineNumberNode.line);
    }

}
