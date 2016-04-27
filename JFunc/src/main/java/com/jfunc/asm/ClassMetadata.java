package com.jfunc.asm;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class ClassMetadata {

	private final String className;

	private final ClassNode classNode;

	private final List<MethodMetaData> methodMetadataList = new ArrayList<MethodMetaData>();

	private final List<FieldNode> fieldNodes = new ArrayList<FieldNode>();

	public ClassMetadata(ClassNode classNode) {
		this.className = classNode.sourceFile;
		this.classNode = classNode;
		List<MethodNode> methodnodes = classNode.methods;
		for (MethodNode methodNode : methodnodes) {
			this.getMethodMetadataList().add(new MethodMetaData(methodNode, classNode));
		}
		this.fieldNodes.addAll(classNode.fields);
	}

	public String getClassName() {
		return className;
	}

	public ClassNode getClassNode() {
		return classNode;
	}

	public List<MethodMetaData> getMethodMetadataList() {
		return methodMetadataList;
	}

	public List<FieldNode> getFieldNodes() {
		return fieldNodes;
	}

}
