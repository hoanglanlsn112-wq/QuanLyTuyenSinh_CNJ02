-- Nâng cấp CSDL hiện tại, không xóa dữ liệu.
USE qltuyensinh;

ALTER TABLE nganh_tuyen_sinh
    MODIFY COLUMN khoi_xet_tuyen VARCHAR(150) NULL;

ALTER TABLE ho_so_dang_ky
    ADD COLUMN ngay_nhap_hoc DATE NULL AFTER ngay_nop;

ALTER TABLE ho_so_dang_ky
    ADD COLUMN khoi_dang_ky VARCHAR(10) NULL AFTER ma_nganh;

-- Mỗi thí sinh chỉ được đăng ký một lần cho cùng một ngành.
-- Nếu đã có dữ liệu trùng, xử lý dữ liệu trùng trước khi chạy lệnh này.
ALTER TABLE ho_so_dang_ky
    ADD UNIQUE KEY uq_ho_so_thi_sinh_nganh (ma_ts, ma_nganh);

-- CCCD đã có trong thi_sinh và được ràng buộc UNIQUE từ script khởi tạo.
SELECT ma_ts, ho_ten, cccd FROM thi_sinh ORDER BY ma_ts;
