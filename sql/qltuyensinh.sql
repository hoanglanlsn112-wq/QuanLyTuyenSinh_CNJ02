-- =========================================================
-- CSDL: Quản lý hồ sơ xét tuyển và chỉ tiêu tuyển sinh (CNJ02)
-- =========================================================

DROP DATABASE IF EXISTS qltuyensinh;
CREATE DATABASE qltuyensinh CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE qltuyensinh;

-- ---------------------------------------------------------
-- Bảng: nganh_tuyen_sinh
-- ---------------------------------------------------------
CREATE TABLE nganh_tuyen_sinh (
    ma_nganh        VARCHAR(10)     PRIMARY KEY,
    ten_nganh       VARCHAR(150)    NOT NULL,
    chi_tieu        INT             NOT NULL DEFAULT 0,
    diem_chuan_tam  DOUBLE          DEFAULT 0,
    khoi_xet_tuyen  VARCHAR(150),
    mo_ta           VARCHAR(255)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- Bảng: thi_sinh
-- ---------------------------------------------------------
CREATE TABLE thi_sinh (
    ma_ts       VARCHAR(15)     PRIMARY KEY,
    ho_ten      VARCHAR(100)    NOT NULL,
    ngay_sinh   DATE,
    gioi_tinh   VARCHAR(5),
    dia_chi     VARCHAR(255),
    sdt         VARCHAR(15),
    email       VARCHAR(100),
    cccd        VARCHAR(20)     UNIQUE,
    khoi_du_thi VARCHAR(10),
    anh_the     VARCHAR(500)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- Bảng: ho_so_dang_ky
-- ---------------------------------------------------------
CREATE TABLE ho_so_dang_ky (
    ma_ho_so        VARCHAR(15)     PRIMARY KEY,
    ma_ts           VARCHAR(15)     NOT NULL,
    ma_nganh        VARCHAR(10)     NOT NULL,
    khoi_dang_ky    VARCHAR(10),
    diem_xet        DOUBLE          NOT NULL DEFAULT 0,
    diem_uu_tien    DOUBLE          NOT NULL DEFAULT 0,
    diem_mon_1      DOUBLE          NOT NULL DEFAULT 0,
    diem_mon_2      DOUBLE          NOT NULL DEFAULT 0,
    diem_mon_3      DOUBLE          NOT NULL DEFAULT 0,
    phuong_thuc     VARCHAR(30)     NOT NULL DEFAULT 'Thi THPT',
    ngay_nop        DATE            NOT NULL,
    ngay_nhap_hoc   DATE            NULL,
    trang_thai      VARCHAR(20)     NOT NULL DEFAULT 'Cho duyet',
        -- Cho duyet | Trung tuyen | Khong trung tuyen
    ghi_chu         VARCHAR(255),
    FOREIGN KEY (ma_ts) REFERENCES thi_sinh(ma_ts) ON DELETE RESTRICT,
    FOREIGN KEY (ma_nganh) REFERENCES nganh_tuyen_sinh(ma_nganh) ON DELETE RESTRICT,
    UNIQUE KEY uq_ho_so_thi_sinh_nganh (ma_ts, ma_nganh)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- Bảng: nhan_vien (tài khoản đăng nhập)
-- ---------------------------------------------------------
CREATE TABLE nhan_vien (
    ma_nv       VARCHAR(15)     PRIMARY KEY,
    ho_ten      VARCHAR(100)    NOT NULL,
    tai_khoan   VARCHAR(50)     UNIQUE NOT NULL,
    mat_khau    VARCHAR(100)    NOT NULL,
    vai_tro     VARCHAR(20)     NOT NULL DEFAULT 'NhanVien' -- Admin | NhanVien | TuyenSinh
) ENGINE=InnoDB;

CREATE TABLE audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    tai_khoan VARCHAR(50),
    hanh_dong VARCHAR(100) NOT NULL,
    chi_tiet VARCHAR(500),
    thoi_gian TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TRIGGER audit_thi_sinh_insert AFTER INSERT ON thi_sinh FOR EACH ROW
    INSERT INTO audit_log(hanh_dong, chi_tiet) VALUES ('THEM_THI_SINH', NEW.ma_ts);
CREATE TRIGGER audit_thi_sinh_update AFTER UPDATE ON thi_sinh FOR EACH ROW
    INSERT INTO audit_log(hanh_dong, chi_tiet) VALUES ('SUA_THI_SINH', NEW.ma_ts);
CREATE TRIGGER audit_thi_sinh_delete AFTER DELETE ON thi_sinh FOR EACH ROW
    INSERT INTO audit_log(hanh_dong, chi_tiet) VALUES ('XOA_THI_SINH', OLD.ma_ts);
CREATE TRIGGER audit_ho_so_insert AFTER INSERT ON ho_so_dang_ky FOR EACH ROW
    INSERT INTO audit_log(hanh_dong, chi_tiet) VALUES ('THEM_HO_SO', NEW.ma_ho_so);
CREATE TRIGGER audit_ho_so_update AFTER UPDATE ON ho_so_dang_ky FOR EACH ROW
    INSERT INTO audit_log(hanh_dong, chi_tiet) VALUES ('SUA_HO_SO', NEW.ma_ho_so);
CREATE TRIGGER audit_ho_so_delete AFTER DELETE ON ho_so_dang_ky FOR EACH ROW
    INSERT INTO audit_log(hanh_dong, chi_tiet) VALUES ('XOA_HO_SO', OLD.ma_ho_so);

-- =========================================================
-- Dữ liệu mẫu
-- =========================================================

INSERT INTO nhan_vien (ma_nv, ho_ten, tai_khoan, mat_khau, vai_tro) VALUES
('NV01', 'Quản trị viên', 'admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'Admin'),
('NV02', 'Nguyễn Văn A', 'nva', '8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92', 'NhanVien');

INSERT INTO nganh_tuyen_sinh (ma_nganh, ten_nganh, chi_tieu, diem_chuan_tam) VALUES
('CNTT',  'Công nghệ thông tin',        100, 24.0),
('QTKD',  'Quản trị kinh doanh',        80,  20.0),
('KTOAN', 'Kế toán',                    70,  19.5),
('NNANH', 'Ngôn ngữ Anh',               60,  21.0),
('DTVT',  'Điện tử viễn thông',         50,  22.0);

INSERT INTO thi_sinh (ma_ts, ho_ten, ngay_sinh, gioi_tinh, dia_chi, sdt, email, cccd) VALUES
('TS0001', 'Trần Văn Bình',  '2008-05-12', 'Nam', 'Hà Nội',     '0901111111', 'binhtv@gmail.com', '001200000001'),
('TS0002', 'Lê Thị Hoa',     '2008-08-20', 'Nữ',  'Hải Phòng',  '0902222222', 'hoalt@gmail.com',  '001200000002'),
('TS0003', 'Phạm Minh Đức',  '2008-01-15', 'Nam', 'Nam Định',   '0903333333', 'ducpm@gmail.com',  '001200000003'),
('TS0004', 'Ngô Thị Lan',    '2008-11-03', 'Nữ',  'Thái Bình',  '0904444444', 'lannt@gmail.com',  '001200000004'),
('TS0005', 'Vũ Quang Huy',   '2008-03-27', 'Nam', 'Bắc Ninh',   '0905555555', 'huyvq@gmail.com',  '001200000005');

UPDATE thi_sinh SET khoi_du_thi = CASE ma_ts
    WHEN 'TS0001' THEN 'A00'
    WHEN 'TS0002' THEN 'D01'
    WHEN 'TS0003' THEN 'A01'
    WHEN 'TS0004' THEN 'D01'
    WHEN 'TS0005' THEN 'A00'
END;

UPDATE nganh_tuyen_sinh SET khoi_xet_tuyen = CASE ma_nganh
    WHEN 'CNTT' THEN 'A00'
    WHEN 'QTKD' THEN 'D01'
    WHEN 'KTOAN' THEN 'D01'
    WHEN 'NNANH' THEN 'D01'
    WHEN 'DTVT' THEN 'A01'
END;

INSERT INTO ho_so_dang_ky
    (ma_ho_so, ma_ts, ma_nganh, diem_xet, diem_uu_tien,
     diem_mon_1, diem_mon_2, diem_mon_3, ngay_nop, trang_thai) VALUES
('HS0001', 'TS0001', 'CNTT',  25.5, 0, 8.5, 8.0, 9.0, '2026-06-01', 'Cho duyet'),
('HS0002', 'TS0002', 'QTKD',  21.0, 0, 7.0, 6.5, 7.5, '2026-06-02', 'Cho duyet'),
('HS0003', 'TS0003', 'CNTT',  23.0, 0, 7.5, 8.0, 7.5, '2026-06-03', 'Cho duyet'),
('HS0004', 'TS0004', 'KTOAN', 18.5, 0, 6.0, 6.0, 6.5, '2026-06-04', 'Cho duyet'),
('HS0005', 'TS0005', 'DTVT',  22.5, 0, 7.5, 7.0, 8.0, '2026-06-05', 'Cho duyet');

-- Danh mục tham khảo từ các đề án tuyển sinh chính thức của UET/VNU và NEU.
-- Đây là dữ liệu mẫu để minh họa, cần điều chỉnh theo đề án của trường.
INSERT INTO nganh_tuyen_sinh (ma_nganh, ten_nganh, chi_tieu, diem_chuan_tam, khoi_xet_tuyen) VALUES
('KHDL',   'Khoa học dữ liệu',                         120, 24.0, 'A00, A01, X06, X26, D01, D07'),
('AI',     'Trí tuệ nhân tạo',                         100, 25.0, 'A00, A01, X06, X26, D01'),
('HTTT',   'Hệ thống thông tin',                       100, 23.5, 'A00, A01, D01, D07'),
('KTMT',   'Kỹ thuật máy tính',                        100, 23.5, 'A00, A01, X06, X26'),
('CDT',    'Công nghệ kỹ thuật cơ điện tử',             100, 23.0, 'A00, A01'),
('TDH',    'Kỹ thuật điều khiển và tự động hóa',        100, 23.0, 'A00, A01'),
('KTXD',   'Công nghệ kỹ thuật xây dựng',               100, 20.0, 'A00, A01, D01'),
('CKD',    'Cơ kỹ thuật',                                60, 22.0, 'A00, A01'),
('CNSH',   'Công nghệ sinh học',                         80, 20.0, 'A00, B00'),
('CNNN',   'Công nghệ nông nghiệp',                      60, 20.0, 'A00, B00'),
('LUAT',   'Luật',                                       80, 21.0, 'A00, C00, D01'),
('KHOI',   'Kinh tế',                                    80, 21.0, 'A00, A01, D01, D07')
ON DUPLICATE KEY UPDATE
    ten_nganh=VALUES(ten_nganh), chi_tieu=VALUES(chi_tieu),
    diem_chuan_tam=VALUES(diem_chuan_tam), khoi_xet_tuyen=VALUES(khoi_xet_tuyen);

UPDATE nganh_tuyen_sinh SET khoi_xet_tuyen = CASE ma_nganh
    WHEN 'CNTT' THEN 'A00, A01, B00, X06, X26, D01'
    WHEN 'QTKD' THEN 'A00, A01, D01, D07'
    WHEN 'KTOAN' THEN 'A00, A01, D01, D07'
    WHEN 'NNANH' THEN 'D01, D07'
    WHEN 'DTVT' THEN 'A00, A01, X06, X26, D01'
    ELSE khoi_xet_tuyen
END;

-- Sửa dữ liệu tiếng Việt mẫu sau khi import bằng công cụ không nhận UTF-8.
UPDATE nhan_vien SET ho_ten = 'Quản trị viên' WHERE ma_nv = 'NV01';
UPDATE nhan_vien SET ho_ten = 'Nguyễn Văn A' WHERE ma_nv = 'NV02';

UPDATE nganh_tuyen_sinh SET ten_nganh = CASE ma_nganh
    WHEN 'CNTT' THEN 'Công nghệ thông tin'
    WHEN 'QTKD' THEN 'Quản trị kinh doanh'
    WHEN 'KTOAN' THEN 'Kế toán'
    WHEN 'NNANH' THEN 'Ngôn ngữ Anh'
    WHEN 'DTVT' THEN 'Điện tử viễn thông'
END;

UPDATE thi_sinh SET ho_ten = CASE ma_ts
    WHEN 'TS0001' THEN 'Trần Văn Bình'
    WHEN 'TS0002' THEN 'Lê Thị Hoa'
    WHEN 'TS0003' THEN 'Phạm Minh Đức'
    WHEN 'TS0004' THEN 'Ngô Thị Lan'
    WHEN 'TS0005' THEN 'Vũ Quang Huy'
END,
dia_chi = CASE ma_ts
    WHEN 'TS0001' THEN 'Hà Nội'
    WHEN 'TS0002' THEN 'Hải Phòng'
    WHEN 'TS0003' THEN 'Nam Định'
    WHEN 'TS0004' THEN 'Thái Bình'
    WHEN 'TS0005' THEN 'Bắc Ninh'
END,
gioi_tinh = CASE ma_ts
    WHEN 'TS0001' THEN 'Nam'
    WHEN 'TS0002' THEN 'Nữ'
    WHEN 'TS0003' THEN 'Nam'
    WHEN 'TS0004' THEN 'Nữ'
    WHEN 'TS0005' THEN 'Nam'
END;
