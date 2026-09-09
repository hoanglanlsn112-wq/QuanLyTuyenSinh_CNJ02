package com.qltuyensinh.util;

import java.io.File;
import java.io.IOException;

public final class BackupUtil {
    private static final String MYSQL_BIN = "C:/xampp/mysql/bin/";
    private static final String MYSQL_PORT = "3307";
    private BackupUtil() { }

    public static void backup(File output) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(MYSQL_BIN + "mysqldump.exe", "-uroot", "--port=" + MYSQL_PORT,
                "--default-character-set=utf8mb4", "qltuyensinh");
        pb.redirectOutput(output);
        pb.redirectError(ProcessBuilder.Redirect.PIPE);
        run(pb, "Sao luu that bai");
    }

    public static void restore(File input) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(MYSQL_BIN + "mysql.exe", "-uroot", "--port=" + MYSQL_PORT,
                "--default-character-set=utf8mb4", "qltuyensinh");
        pb.redirectInput(input);
        pb.redirectError(ProcessBuilder.Redirect.PIPE);
        run(pb, "Khoi phuc that bai");
    }

    private static void run(ProcessBuilder pb, String message) throws IOException, InterruptedException {
        Process process = pb.start();
        int code = process.waitFor();
        if (code != 0) throw new IOException(message + " (ma loi " + code + ")");
    }
}
