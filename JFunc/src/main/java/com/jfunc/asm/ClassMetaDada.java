package com.jfunc.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.jfunc.exception.JfuncException;
import com.jfunc.validator.JfuncConstants;

/**
 * Class to store all the required details of a method
 * @author manojk
 *
 */
public class ClassMetaDada {
    private final ClassNode classNode;
    private final String className;
    private final List<MethodNode> methodNodes;
    private List<MethodMetaData> methodMethodDataList = new ArrayList<>();;
    private List<MethodMetaData> constructorMethodDataList = new ArrayList<>();
    private List<FieldNode> fieldNodes;
    private List<FieldNode> finalFieldNodes;
    private List<FieldNode> nonFinalPublicFieldNodes;
    private List<FieldNode> finalPrimitiveAndStringFieldNodes;
    private List<FieldNode> nonFinalPrivateFeildNodes;

    @SuppressWarnings("unchecked")
    public ClassMetaDada(ClassNode classNode) throws JfuncException {
        this.classNode = classNode;
        this.className = classNode.name;
        this.methodNodes = this.classNode.methods;
        this.fieldNodes = this.classNode.fields;
        finalFieldNodes = getAllFinalFields(fieldNodes);
        nonFinalPublicFieldNodes = getAllNonFinalPublicFields(fieldNodes);
        finalPrimitiveAndStringFieldNodes = getAllFinalPrimitiveAndStringFields(fieldNodes);
        createConstructorAndMethodMetaDataList(methodNodes);
        nonFinalPrivateFeildNodes = getAllNonFinalPrivateFields(fieldNodes);
    }

    private List<FieldNode> getAllFinalFields(List<FieldNode> filedNodes) {
        return filedNodes.stream().filter(field -> (field.access & Opcodes.ACC_FINAL) != 0).collect(Collectors.toList());
    }

    private List<FieldNode> getAllNonFinalPublicFields(List<FieldNode> filedNodes) {
        return filedNodes.stream().filter(field -> ((field.access & Opcodes.ACC_PUBLIC) != 0) && ((field.access & Opcodes.ACC_FINAL) == 0))
                .collect(Collectors.toList());
    }
    
    private List<FieldNode> getAllNonFinalPrivateFields(List<FieldNode> filedNodes){
        return filedNodes.stream().filter(field -> ((field.access & Opcodes.ACC_PRIVATE) != 0) && ((field.access & Opcodes.ACC_FINAL) == 0))
                .collect(Collectors.toList());
    }

    private List<FieldNode> getAllFinalPrimitiveAndStringFields(List<FieldNode> filedNodes) {
        return filedNodes.stream().filter(field -> ((Type.getType(field.desc).getSort() != Type.OBJECT)
                        && ((field.access & Opcodes.ACC_FINAL) != 0)) || ((Type.getType(field.desc).getSort() == Type.OBJECT)
                        && ((field.access & Opcodes.ACC_FINAL) != 0) && (Type.getType(field.desc).getClassName().equals(JfuncConstants.STRING_CLASS))))
                        .collect(Collectors.toList());
    }
    
    private void createConstructorAndMethodMetaDataList(List<MethodNode> methodNodes) throws JfuncException{
        for(MethodNode methodNode : methodNodes){
            if(methodNode.name.equals(JfuncConstants.INITIALIZATION_METHOD)){
                constructorMethodDataList.add(new MethodMetaData(methodNode, className));
            }
            else{
                methodMethodDataList.add(new MethodMetaData(methodNode, className));
            }
        }
    }
    
    public List<FieldNode> getFinalFieldNodes(){
        return finalFieldNodes;
    }
    
    public List<FieldNode> getNonFinalPublicFieldNodes(){
        return nonFinalPublicFieldNodes;
    }
    
    public List<FieldNode> getFinalPrimitiveAndStringFieldNodes(){
        return finalPrimitiveAndStringFieldNodes;
    }
    
    public List<FieldNode> getNonFinalPrivateFieldNodes(){
        return nonFinalPrivateFeildNodes;
    }
    
    public List<MethodMetaData> getMethodMetadaList(){
        return methodMethodDataList;
    }
    
    public String getName(){
        return className;
    }
    
}
