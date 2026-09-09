package com.qltuyensinh.model;

import java.time.LocalDate;

public class ThiSinh {
    private String maTS;
    private String hoTen;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String diaChi;
    private String sdt;
    private String email;
    private String cccd;
    private String khoiDuThi;
    private String anhThe;

    public ThiSinh() {
    }

    public ThiSinh(String maTS, String hoTen, LocalDate ngaySinh, String gioiTinh,
                    String diaChi, String sdt, String email, String cccd, String khoiDuThi) {
        this.maTS = maTS;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.sdt = sdt;
        this.email = email;
        this.cccd = cccd;
        this.khoiDuThi = khoiDuThi;
    }

    public String getMaTS() { return maTS; }
    public void setMaTS(String maTS) { this.maTS = maTS; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public LocalDate getNgaySinh() { return ngaySinh; }
    public void setNgaySinh(LocalDate ngaySinh) { this.ngaySinh = ngaySinh; }

    public String getGioiTinh() { return gioiTinh; }
    public void setGioiTinh(String gioiTinh) { this.gioiTinh = gioiTinh; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCccd() { return cccd; }
    public void setCccd(String cccd) { this.cccd = cccd; }

    public String getKhoiDuThi() { return khoiDuThi; }
    public void setKhoiDuThi(String khoiDuThi) { this.khoiDuThi = khoiDuThi; }

    public String getAnhThe() { return anhThe; }
    public void setAnhThe(String anhThe) { this.anhThe = anhThe; }

    @Override
    public String toString() {
        return maTS + " - " + hoTen;
    }
}
