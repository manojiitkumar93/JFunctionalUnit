package com.jfunc.validator;

import java.io.FileNotFoundException;

public class MainApp {

	public static void main(String[] args) throws FileNotFoundException {
		FunctionValidator fv = new FunctionValidator(
				"/home/rajarajang/Workspace/eclipseWorkspace/JFunctionalUnit/JFunc/build/classes/");
		fv.showResults();
	}

}
