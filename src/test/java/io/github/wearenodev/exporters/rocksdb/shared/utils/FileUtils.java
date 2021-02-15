package io.github.wearenodev.exporters.rocksdb.shared.utils;

import java.io.File;

public class FileUtils {

    public static void ensureDir(String directoryPath) {
        File dir = new File(directoryPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

}
