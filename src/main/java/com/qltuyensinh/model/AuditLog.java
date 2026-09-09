package com.qltuyensinh.model;

import java.sql.Timestamp;

public class AuditLog {
    private long id;
    private String taiKhoan;
    private String hanhDong;
    private String chiTiet;
    private Timestamp thoiGian;

    public AuditLog(long id, String taiKhoan, String hanhDong, String chiTiet, Timestamp thoiGian) {
        this.id = id;
        this.taiKhoan = taiKhoan;
        this.hanhDong = hanhDong;
        this.chiTiet = chiTiet;
        this.thoiGian = thoiGian;
    }

    public long getId() { return id; }
    public String getTaiKhoan() { return taiKhoan; }
    public String getHanhDong() { return hanhDong; }
    public String getChiTiet() { return chiTiet; }
    public Timestamp getThoiGian() { return thoiGian; }
}
