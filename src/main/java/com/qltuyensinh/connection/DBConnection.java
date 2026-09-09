package com.qltuyensinh.connection;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Quản lý kết nối tới MySQL.
 * Đọc thông tin cấu hình từ resources/db.properties.
 */
public class DBConnection {

    private static final String CONFIG_FILE = "/db.properties";
    private DBConnection() {
    }

    public static synchronized Connection getConnection() {
        try {
            Properties props = new Properties();
            try (InputStream is = openConfigStream()) {
                if (is == null) {
                    throw new RuntimeException(
                        "Khong tim thay db.properties. Hay dat file trong resources/ "
                        + "hoac danh dau resources la Resources Root trong VS Code.");
                }
                props.load(is);
            }

            String url = props.getProperty("db.url");
            String user = props.getProperty("db.user");
            String pass = props.getProperty("db.password");

            if (url == null || url.isBlank() || user == null || user.isBlank()
                    || pass == null) {
                throw new IllegalStateException(
                        "Thieu cau hinh db.url, db.user hoac db.password trong db.properties.");
            }

            Class.forName("com.mysql.cj.jdbc.Driver");
            // Mỗi DAO tự đóng kết nối của mình bằng try-with-resources.
            return DriverManager.getConnection(url, user, pass);
        } catch (ClassNotFoundException | SQLException | IOException e) {
            throw new RuntimeException("Loi ket noi CSDL: " + e.getMessage(), e);
        }
    }

    /**
     * Maven/IDE sẽ đọc từ classpath. Khi chạy trực tiếp bằng cấu hình Java của
     * VS Code, resources đôi khi không được thêm vào classpath nên dùng thêm
     * đường dẫn dự phòng tương đối với thư mục chạy chương trình.
     */
    private static InputStream openConfigStream() throws IOException {
        InputStream classpathStream = DBConnection.class.getResourceAsStream(CONFIG_FILE);
        if (classpathStream != null) {
            return classpathStream;
        }

        Path[] candidates = {
            Path.of("resources", "db.properties"),
            Path.of("QuanLyTuyenSinh", "resources", "db.properties")
        };
        for (Path candidate : candidates) {
            if (Files.isRegularFile(candidate)) {
                return Files.newInputStream(candidate);
            }
        }
        return null;
    }

    public static void closeConnection() {
        // Các DAO hiện tự quản lý kết nối bằng try-with-resources.
    }
}
