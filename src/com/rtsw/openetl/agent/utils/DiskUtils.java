package com.rtsw.openetl.agent.utils;

import java.io.File;

/**
 * @author RT Software Studio
 */
public class DiskUtils {

    /**
     *
     * @param path
     * @param directory
     * @param read
     * @param write
     * @return
     */
    public static File pathToFile(String path, boolean directory, boolean read, boolean write) {
        if (path == null) {
            return (null);
        }
        File file = new File(path);
        if (!file.exists()) {
            return (null);
        }
        if (directory && !file.isDirectory()) {
            return (null);
        }
        if (read && !file.canRead()) {
            return (null);
        }
        if (write && !file.canWrite()) {
            return (null);
        }
        return (file);
    }

}
