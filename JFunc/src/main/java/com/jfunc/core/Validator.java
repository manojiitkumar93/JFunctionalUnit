package com.jfunc.core;

public interface Validator {

    public String validate(String methodName, boolean skipLogStatements, boolean skipPrintStatements);

    public String validate(String methodName);
}
