# CNJ02 — Quản lý hồ sơ xét tuyển và chỉ tiêu tuyển sinh

Ứng dụng desktop viết bằng **Java Swing**, kết nối **MySQL** qua **JDBC**.

## 1. Cây thư mục

```
QuanLyTuyenSinh/
├── lib/
│   └── mysql-connector-j-8.4.0.jar        <-- tải về, xem mục 3
│
├── resources/
│   └── db.properties                      <-- cấu hình kết nối CSDL
│
├── sql/
│   └── qltuyensinh.sql                    <-- script tạo DB + dữ liệu mẫu
│
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── qltuyensinh/
│                   ├── Main.java                      <-- điểm chạy chương trình
│                   │
│                   ├── connection/
│                   │   └── DBConnection.java          <-- kết nối JDBC (Singleton)
│                   │
│                   ├── model/
│                   │   ├── ThiSinh.java
│                   │   ├── NganhTuyenSinh.java
│                   │   ├── HoSoDangKy.java
│                   │   └── NhanVien.java
│                   │
│                   ├── dao/
│                   │   ├── ThiSinhDAO.java
│                   │   ├── NganhTuyenSinhDAO.java
│                   │   ├── HoSoDangKyDAO.java
│                   │   └── NhanVienDAO.java
│                   │
│                   ├── view/
│                   │   ├── LoginForm.java              <-- form đăng nhập
│                   │   ├── MainForm.java                <-- form chính (JTabbedPane)
│                   │   ├── NganhPanel.java              <-- tab Ngành tuyển sinh
│                   │   ├── ThiSinhPanel.java            <-- tab Thí sinh
│                   │   ├── HoSoPanel.java                <-- tab Hồ sơ + Xét duyệt
│                   │   └── ThongKePanel.java            <-- tab Thống kê
│                   │
│                   └── util/
│                       ├── ValidationUtil.java          <-- validate input
│                       └── AlertUtil.java                <-- popup thông báo
│
├── README.md
└── .gitignore
```

**Kiến trúc:** mô hình phân lớp cổ điển
`model` (dữ liệu) ← `dao` (truy vấn SQL) ← `view` (giao diện Swing gọi DAO trực tiếp).
Package `connection` dùng chung 1 kết nối JDBC (Singleton).

## 2. Cài đặt CSDL

1. Mở MySQL Workbench / mysql CLI.
2. Chạy toàn bộ file `sql/qltuyensinh.sql` — script sẽ tự tạo database `qltuyensinh`, các bảng, và dữ liệu mẫu.

## 3. Tải MySQL Connector/J

Tải file `mysql-connector-j-8.4.0.jar` (hoặc bản mới hơn) tại:
https://dev.mysql.com/downloads/connector/j/

Đặt file `.jar` vào thư mục `lib/`.

## 4. Cấu hình kết nối

Sửa file `resources/db.properties`:
```
db.url=jdbc:mysql://localhost:3306/qltuyensinh?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8
db.user=root
db.password=MAT_KHAU_MYSQL_CUA_BAN
```

## 5. Biên dịch & chạy (không dùng IDE)

Từ thư mục gốc `QuanLyTuyenSinh/`:

**Trên Windows (cmd):**
```cmd
mkdir out
javac -encoding UTF-8 -cp "lib\mysql-connector-j-8.4.0.jar" -d out (dir /s /b src\main\java\*.java)
copy resources\db.properties out\
java -cp "out;lib\mysql-connector-j-8.4.0.jar" com.qltuyensinh.Main
```

**Trên macOS/Linux:**
```bash
mkdir -p out
find src/main/java -name "*.java" > sources.txt
javac -encoding UTF-8 -cp "lib/mysql-connector-j-8.4.0.jar" -d out @sources.txt
cp resources/db.properties out/
java -cp "out:lib/mysql-connector-j-8.4.0.jar" com.qltuyensinh.Main
```

> `db.properties` phải nằm ở **gốc classpath** (cùng cấp với thư mục `com/`) để `DBConnection` đọc được qua `getResourceAsStream("/db.properties")`.

## 6. Chạy bằng IDE (IntelliJ IDEA / Eclipse / NetBeans) — khuyến nghị

1. Mở project, đánh dấu `src/main/java` là **Source Root**.
2. Đánh dấu `resources` là **Resources Root** (IntelliJ: chuột phải → Mark Directory as → Resources Root).
3. Thêm file `.jar` trong `lib/` vào **Project Structure → Libraries**.
4. Chạy class `com.qltuyensinh.Main`.

## 7. Tài khoản đăng nhập mặc định

| Tài khoản | Mật khẩu | Vai trò |
|---|---|---|
| admin | admin123 | Admin |
| nva   | 123456   | NhanVien |

## 8. Chức năng chính

- **Ngành tuyển sinh**: thêm/sửa/xóa ngành, chỉ tiêu, điểm chuẩn tạm.
- **Thí sinh**: thêm/sửa/xóa, tìm kiếm theo mã hoặc tên, validate SĐT/email/ngày sinh.
- **Hồ sơ đăng ký**: nộp hồ sơ, lọc theo ngành/trạng thái, duyệt thủ công từng hồ sơ, hoặc **tự động xét duyệt** toàn bộ theo điểm xét + chỉ tiêu còn lại của từng ngành.
- **Thống kê**: số hồ sơ chờ duyệt / trúng tuyển / không trúng tuyển theo từng ngành.
