package com.jfunc.core;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.jfunc.exception.JfuncException;
import com.jfunc.validator.JfuncConstants;

public class ProjectDetailsProvider {
    private static String projectPath;
    private Set<String> projectSet = new HashSet<>();
    private static ProjectDetailsProvider instance;

    private ProjectDetailsProvider() {
        constructAllPackages(new File(projectPath));

    }

    public static ProjectDetailsProvider getInstance() throws JfuncException {
        if (projectPath == null) {
            throw new JfuncException("First set ProjectPath");
        }
        if (instance == null) {
            instance = new ProjectDetailsProvider();
        }
        return instance;
    }

    private void constructAllPackages(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File subFolder : files) {
                constructAllPackages(subFolder);
            }
        } else
            projectSet.add(file.getParent().replace(projectPath, "") + JfuncConstants.SLASH + file.getName());
    }

    public Set<String> getAllClasses() {
        return Collections.unmodifiableSet(projectSet);
    }

    public String getProjectPath() {
        return projectPath;
    }

    public static void setProjectPath(String path) {
        if (projectPath == null) {
            projectPath = path;
        }
    }

}
