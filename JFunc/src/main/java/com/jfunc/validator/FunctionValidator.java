package com.jfunc.validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
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
import com.jfunc.asm.AppMetadata;
import com.jfunc.asm.ClassMetadata;
import com.jfunc.asm.InternalFeild;
import com.jfunc.asm.MethodMetaData;
import com.jfunc.core.Validator;
import com.jfunc.util.FieldUtil;
import com.jfunc.util.FileUtil;
import com.jfunc.util.JsonUtils;
import com.jfunc.util.VoidTypeUtil;

public class FunctionValidator implements Validator {

	private final String filePath;
	private final Collection<File> classFiles;
	private ClassReader classReader = null;
	private ClassNode classNode;
	private final List<FieldNode> fieldNodes = new ArrayList<FieldNode>();
	private final AppMetadata appNode = new AppMetadata();

	private List<ObjectNode> results = new ArrayList<ObjectNode>();

	private final boolean skipLogStatements = false;
	private final boolean skipPrintStatements = false;

	public FunctionValidator(String path) throws FileNotFoundException {
		this.filePath = path;
		classFiles = new ArrayList<File>();
		File file = new File(filePath);
		if (file.isDirectory()) {
			classFiles.addAll(FileUtils.listFiles(file, JfuncConstants.EXTENSIONS, true));
		} else if (FileUtil.isFileExists(file)) {
			classFiles.add(file);
		} else {
			throw new FileNotFoundException();
		}

		for (File classFile : classFiles) {
			init(classFile);
		}
	}

	private void init(File classFile) {

		try (InputStream fileStream = new FileInputStream(classFile)) {
			classReader = new ClassReader(fileStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		classNode = new ClassNode();
		classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
		appNode.getClassMetaData().add(new ClassMetadata(classNode));
	}

	private Set<String> getNonFinalFieldNodes() {
		return fieldNodes.stream().parallel().filter(
				field -> (Type.getType(field.desc).getSort() != Type.OBJECT && field.access != Opcodes.ACC_FINAL)
						|| (Type.getType(field.desc).getSort() == Type.OBJECT
								&& !field.desc.getClass().getName().equals(JfuncConstants.STRING_CLASS))
				|| (Type.getType(field.desc).getSort() == Type.OBJECT
						&& field.desc.getClass().getName().equals(JfuncConstants.STRING_CLASS)
						&& field.access != Opcodes.ACC_FINAL))
				.map(field -> field.name).collect(Collectors.toSet());
	}

	// At present we are checking for 1) Void 2) Print-Statements 3)
	// Log-Statements
	private ObjectNode getReasons(MethodNode methodNode, boolean skipLogStatements, boolean skipPrintStatements) {
		ObjectMapper mapper = JsonUtils.getObjectMapper();
		ObjectNode objectNode = mapper.createObjectNode();
		MethodMetaData methodMetaData = new MethodMetaData(methodNode, null);

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
			}
			if (!isFunctionSet) {
				objectNode.put(JfuncConstants.ISFUNCTIONAL, true);
			} else {
				objectNode.set(JfuncConstants.REASONS, reasonObjectNode);
			}
		}
		return objectNode;
	}

	public List<ObjectNode> validate() {
		for (ClassMetadata classMetadata : appNode.getClassMetaData()) {
			List<MethodMetaData> methodMetaDatas = classMetadata.getMethodMetadataList();
			for (MethodMetaData methodMetaData : methodMetaDatas) {
				results.add(getReasons(methodMetaData, skipLogStatements, skipPrintStatements));
			}
		}
		return results;
	}

	public void showResults() {
		results = validate();
		for (ObjectNode node : results) {
			System.out.println(node.toString());
		}
	}

	@Override
	public String validate(String methodName, boolean skipLogStatements, boolean skipPrintStatements) {
		for (ClassMetadata classMetadata : appNode.getClassMetaData()) {
			List<MethodMetaData> methodMetaDatas = classMetadata.getMethodMetadataList();
			for (MethodMetaData methodMetaData : methodMetaDatas) {
				if (StringUtils.equals(methodName, methodMetaData.getMethodNode().name)) {
					return getReasons(new MethodMetaData(methodMetaData.getMethodNode(), classMetadata.getClassNode()),
							skipLogStatements, skipPrintStatements).toString();
				}
			}
		}
		return "No method found with name" + methodName;
	}

	// At present we are checking for 1) Void 2) Print-Statements 3)
	// Log-Statements
	private ObjectNode getReasons(MethodMetaData methodMetaData, boolean skipLogStatements,
			boolean skipPrintStatements) {
		ObjectMapper mapper = JsonUtils.getObjectMapper();
		ObjectNode objectNode = mapper.createObjectNode();
		MethodNode methodNode = methodMetaData.getMethodNode();

		// First check whether method type is "Void"
		if (VoidTypeUtil.isVoid(methodMetaData)) {
			objectNode.put(JfuncConstants.ISFUNCTIONAL, false);
			objectNode.put(JfuncConstants.CLASS, methodMetaData.getClassName());
			objectNode.put(JfuncConstants.METHOD, methodNode.name);
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
						objectNode.put(JfuncConstants.CLASS, methodMetaData.getClassName());
						objectNode.put(JfuncConstants.METHOD, methodNode.name);
						isFunctionSet = true;
					}
					reasonObjectNode.add(FieldUtil.getReasonsForPrintStatements(internalField));
				}
				if (!skipLogStatements && FieldUtil.containsLogStatements(internalField)) {
					if (!isFunctionSet) {
						objectNode.put(JfuncConstants.ISFUNCTIONAL, false);
						objectNode.put(JfuncConstants.CLASS, methodMetaData.getClassName());
						objectNode.put(JfuncConstants.METHOD, methodNode.name);
						isFunctionSet = true;
					}
					reasonObjectNode.add(FieldUtil.getReasonsForLogStatements(internalField));
				}
				if (nonFinalFieldSet.contains(internalField.getName())) {
					isFunctionSet = true;
					objectNode.put(JfuncConstants.ISFUNCTIONAL, false);
					objectNode.put(JfuncConstants.CLASS, methodMetaData.getClassName());
					objectNode.put(JfuncConstants.METHOD, methodNode.name);
					reasonObjectNode.add(FieldUtil.getReasonsForGlobalFields(internalField));
				}
			}
			if (!isFunctionSet) {
				objectNode.put(JfuncConstants.ISFUNCTIONAL, true);
				objectNode.put(JfuncConstants.CLASS, methodMetaData.getClassName());
				objectNode.put(JfuncConstants.METHOD, methodNode.name);
			} else {
				objectNode.set(JfuncConstants.REASONS, reasonObjectNode);
			}
		}
		return objectNode;
	}

	@Override
	public String validate(String methodName) {
		return validate(methodName, false, false);
	}

}
