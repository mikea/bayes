package com.mikea.bayes.util;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Lists.newArrayList;

/**
 * @author mike.aizatsky@gmail.com
 */
@Singleton
public class TempFiles {
    static {
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                shutdown();
            }
        }));
    }

    private static List<File> tempFiles = newArrayList();

    private static void shutdown() {
        for (File tempFile : tempFiles) {
            checkState(tempFile.delete());
        }
    }

    @Inject public TempFiles() {
    }

    public File newTempFile() {
        try {
            File tempFile = File.createTempFile("bayes", "tmp");
            tempFiles.add(tempFile);
            return tempFile;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
