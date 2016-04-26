package com.jfunc.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
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
	// Global variables
	private List<FieldNode> fieldNodes;

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
		classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
		methodNodes = classNode.methods;
		fieldNodes = classNode.fields;
	}

	private Set<String> getNonFinalFieldNodes() {
		return fieldNodes.stream().filter(
				field -> (Type.getType(field.desc).getSort() != Type.OBJECT && field.access != Opcodes.ACC_FINAL)
						|| (Type.getType(field.desc).getSort() == Type.OBJECT
								&& !field.desc.getClass().getName().equals(JfuncConstants.STRING_CLASS))
				|| (Type.getType(field.desc).getSort() == Type.OBJECT
						&& field.desc.getClass().getName().equals(JfuncConstants.STRING_CLASS)
						&& field.access != Opcodes.ACC_FINAL))
				.map(field -> field.name).collect(Collectors.toSet());
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

	// At present we are checking for 1) Void 2) Print-Statements 3)
	// Log-Statements
	private ObjectNode getReasons(MethodNode methodNode, boolean skipLogStatements, boolean skipPrintStatements) {
		ObjectMapper mapper = JsonUtils.getObjectMapper();
		ObjectNode objectNode = mapper.createObjectNode();
		MethodMetaData methodMetaData = new MethodMetaData(methodNode);

		// First check whether method type is "Void"
		if (VoidTypeUtil.isVoid(methodMetaData)) {
			objectNode.put(JfuncConstants.ISFUNCTIONAL, false);
			objectNode.set(JfuncConstants.REASONS, VoidTypeUtil.getReasons(methodMetaData));
			return objectNode;
		}

		// Check for any "Print" and "Log" statements in a method
		if (!(skipPrintStatements && skipLogStatements)) {
			ArrayNode reasonObjectNode = mapper.createArrayNode();
			boolean isFunctionSet = false;
			List<InternalFeild> internalFields = methodMetaData.getInternallyRefferedFields();
			Set<String> nonFinalFieldSet = getNonFinalFieldNodes();
			for (InternalFeild internalField : internalFields) {
				if (!skipPrintStatements && FieldUtil.containsPrintStatements(internalField)) {
					if (!isFunctionSet) {
						objectNode.put(JfuncConstants.ISFUNCTIONAL, false);
						isFunctionSet = true;
					}
					reasonObjectNode.add(FieldUtil.getReasonsForPrintStatements(internalField));
				}
				if (!skipLogStatements && FieldUtil.containsLogStatements(internalField)) {
					if (!isFunctionSet) {
						objectNode.put(JfuncConstants.ISFUNCTIONAL, false);
						isFunctionSet = true;
					}
					reasonObjectNode.add(FieldUtil.getReasonsForLogStatements(internalField));
				}
				if (nonFinalFieldSet.contains(internalField.getName())) {
					isFunctionSet = true;
					objectNode.put(JfuncConstants.ISFUNCTIONAL, false);
					reasonObjectNode.add(FieldUtil.getReasonsForGlobalFields(internalField));
				}
			}
			if (!isFunctionSet) {
				objectNode.put(JfuncConstants.ISFUNCTIONAL, true);
			} else {
				objectNode.set(JfuncConstants.REASONS, reasonObjectNode);
			}
		}
		return objectNode;
	}

}
