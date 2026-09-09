package com.qltuyensinh.model;

public class NganhTuyenSinh {
    private String maNganh;
    private String tenNganh;
    private int chiTieu;
    private int soTrungTuyen;
    private double diemChuanTam;
    private String khoiXetTuyen;
    private String moTa;

    public NganhTuyenSinh() {
    }

    public NganhTuyenSinh(String maNganh, String tenNganh, int chiTieu, double diemChuanTam,
                          String khoiXetTuyen, String moTa) {
        this.maNganh = maNganh;
        this.tenNganh = tenNganh;
        this.chiTieu = chiTieu;
        this.diemChuanTam = diemChuanTam;
        this.khoiXetTuyen = khoiXetTuyen;
        this.moTa = moTa;
    }

    public String getMaNganh() { return maNganh; }
    public void setMaNganh(String maNganh) { this.maNganh = maNganh; }

    public String getTenNganh() { return tenNganh; }
    public void setTenNganh(String tenNganh) { this.tenNganh = tenNganh; }

    public int getChiTieu() { return chiTieu; }
    public void setChiTieu(int chiTieu) { this.chiTieu = chiTieu; }

    public int getSoTrungTuyen() { return soTrungTuyen; }
    public void setSoTrungTuyen(int soTrungTuyen) { this.soTrungTuyen = soTrungTuyen; }

    public double getDiemChuanTam() { return diemChuanTam; }
    public void setDiemChuanTam(double diemChuanTam) { this.diemChuanTam = diemChuanTam; }

    public String getKhoiXetTuyen() { return khoiXetTuyen; }
    public void setKhoiXetTuyen(String khoiXetTuyen) { this.khoiXetTuyen = khoiXetTuyen; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    @Override
    public String toString() {
        String khoi = khoiXetTuyen == null || khoiXetTuyen.isBlank() ? "Chưa chọn" : khoiXetTuyen;
        return tenNganh + " - " + khoi + " (" + maNganh + ")";
    }
}
