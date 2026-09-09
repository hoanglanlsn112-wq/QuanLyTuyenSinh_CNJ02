package com.qltuyensinh.model;

import java.time.LocalDate;

public class HoSoDangKy {

    public static final String CHO_DUYET = "Cho duyet";
    public static final String CAN_BO_SUNG = "Can bo sung";
    public static final String TRUNG_TUYEN = "Trung tuyen";
    public static final String KHONG_TRUNG_TUYEN = "Khong trung tuyen";
    public static final String DA_NHAP_HOC = "Da nhap hoc";

    private String maHoSo;
    private String maTS;
    private String maNganh;
    private String khoiDangKy;
    private double diemXet;
    private double diemUuTien;
    private double diemMon1;
    private double diemMon2;
    private double diemMon3;
    private String phuongThuc;
    private LocalDate ngayNop;
    private LocalDate ngayNhapHoc;
    private String trangThai;
    private String ghiChu;

    // Các trường bổ sung khi JOIN để hiển thị lên bảng
    private String hoTenTS;
    private String khoiDuThi;
    private String tenNganh;
    private String khoiXetTuyen;

    public HoSoDangKy() {
    }

    public HoSoDangKy(String maHoSo, String maTS, String maNganh, double diemXet,
                       LocalDate ngayNop, String trangThai, String ghiChu) {
        this.maHoSo = maHoSo;
        this.maTS = maTS;
        this.maNganh = maNganh;
        this.diemXet = diemXet;
        this.ngayNop = ngayNop;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    public String getMaHoSo() { return maHoSo; }
    public void setMaHoSo(String maHoSo) { this.maHoSo = maHoSo; }

    public String getMaTS() { return maTS; }
    public void setMaTS(String maTS) { this.maTS = maTS; }

    public String getMaNganh() { return maNganh; }
    public void setMaNganh(String maNganh) { this.maNganh = maNganh; }

    public String getKhoiDangKy() { return khoiDangKy; }
    public void setKhoiDangKy(String khoiDangKy) { this.khoiDangKy = khoiDangKy; }

    public double getDiemXet() { return diemXet; }
    public void setDiemXet(double diemXet) { this.diemXet = diemXet; }

    public double getDiemUuTien() { return diemUuTien; }
    public void setDiemUuTien(double diemUuTien) { this.diemUuTien = diemUuTien; }

    public double getDiemMon1() { return diemMon1; }
    public void setDiemMon1(double diemMon1) { this.diemMon1 = diemMon1; }
    public double getDiemMon2() { return diemMon2; }
    public void setDiemMon2(double diemMon2) { this.diemMon2 = diemMon2; }
    public double getDiemMon3() { return diemMon3; }
    public void setDiemMon3(double diemMon3) { this.diemMon3 = diemMon3; }

    public String getPhuongThuc() { return phuongThuc; }
    public void setPhuongThuc(String phuongThuc) { this.phuongThuc = phuongThuc; }

    public LocalDate getNgayNop() { return ngayNop; }
    public void setNgayNop(LocalDate ngayNop) { this.ngayNop = ngayNop; }

    public LocalDate getNgayNhapHoc() { return ngayNhapHoc; }
    public void setNgayNhapHoc(LocalDate ngayNhapHoc) { this.ngayNhapHoc = ngayNhapHoc; }

    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public String getHoTenTS() { return hoTenTS; }
    public void setHoTenTS(String hoTenTS) { this.hoTenTS = hoTenTS; }

    public String getKhoiDuThi() { return khoiDuThi; }
    public void setKhoiDuThi(String khoiDuThi) { this.khoiDuThi = khoiDuThi; }

    public String getTenNganh() { return tenNganh; }
    public void setTenNganh(String tenNganh) { this.tenNganh = tenNganh; }

    public String getKhoiXetTuyen() { return khoiXetTuyen; }
    public void setKhoiXetTuyen(String khoiXetTuyen) { this.khoiXetTuyen = khoiXetTuyen; }
}
