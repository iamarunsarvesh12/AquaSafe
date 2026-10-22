package com.aquasafe.config;

import java.io.File;

public class FilePathResolver {

    public static File resolve(String path) {
        if (path == null) return null;
        File direct = new File(path);
        if (direct.exists()) return direct;

        File parentRelative = new File("../" + path);
        if (parentRelative.exists()) return parentRelative;

        return direct;
    }

    public static String resolvePath(String path) {
        return resolve(path).getPath();
    }
}
