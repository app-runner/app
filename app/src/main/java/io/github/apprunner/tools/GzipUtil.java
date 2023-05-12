package io.github.apprunner.tools;

import lombok.SneakyThrows;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author songyinyin
 * @since 2023/5/9 18:54
 */
public class GzipUtil {

    private static final int BUFFER_SIZE = 1024;

    /**
     * 解压 tar.gz 文件到指定目录
     *
     * @param tarGzFile  tar.gz 文件路径
     * @param destDir  解压到 destDir 目录，如果没有则自动创建
     *
     */
    @SneakyThrows
    public static void extractTarGZ(File tarGzFile, String destDir) {

        GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(new FileInputStream(tarGzFile));
        try (TarArchiveInputStream tarIn = new TarArchiveInputStream(gzipIn)) {
            TarArchiveEntry entry;

            while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    File f = new File(destDir + "/" + entry.getName());
                    boolean created = f.mkdirs();
                    if (!created) {
                        System.out.printf("Unable to create directory '%s', during extraction of archive contents.\n",
                            f.getAbsolutePath());
                    }
                } else {
                    int count;
                    byte [] data = new byte[BUFFER_SIZE];
                    FileOutputStream fos = new FileOutputStream(destDir + "/" + entry.getName(), false);
                    try (BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER_SIZE)) {
                        while ((count = tarIn.read(data, 0, BUFFER_SIZE)) != -1) {
                            dest.write(data, 0, count);
                        }
                    }
                }
            }
        }
    }
}
