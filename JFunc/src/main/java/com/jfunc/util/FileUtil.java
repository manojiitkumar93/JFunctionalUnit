package com.jfunc.util;

import java.io.File;

public class FileUtil {

    public static boolean isFileExists(File file) {
        return (file.exists() && file.isFile()) ? true : false;
    }
}
