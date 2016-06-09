package com.jfunc.validator;

import com.jfunc.asm.MethodMetaData;

public class RequirementsWrapper {

    private final MethodMetaData methodMetaData;
    private final boolean skipLogStatements;
    private final boolean skipPrintStatements;

    public RequirementsWrapper(MethodMetaData classMetaData, boolean skipLogStatements, boolean skipPrintStatements) {
        this.methodMetaData = classMetaData;
        this.skipLogStatements = skipLogStatements;
        this.skipPrintStatements = skipPrintStatements;
    }

    public MethodMetaData getClassMetaData() {
        return this.methodMetaData;
    }

    public boolean skipLogStatements() {
        return this.skipLogStatements;
    }

    public boolean skipPrintStatements() {
        return this.skipPrintStatements;
    }

}
