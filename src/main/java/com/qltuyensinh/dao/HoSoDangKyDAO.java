package com.qltuyensinh.dao;

import com.qltuyensinh.connection.DBConnection;
import com.qltuyensinh.model.HoSoDangKy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoSoDangKyDAO {

    private static final String JOIN_SQL =
        "SELECT h.*, t.ho_ten AS ho_ten_ts, t.khoi_du_thi AS khoi_du_thi, " +
        "n.ten_nganh AS ten_nganh, n.khoi_xet_tuyen AS khoi_xet_tuyen " +
        "FROM ho_so_dang_ky h " +
        "JOIN thi_sinh t ON h.ma_ts = t.ma_ts " +
        "JOIN nganh_tuyen_sinh n ON h.ma_nganh = n.ma_nganh ";

    public List<HoSoDangKy> getAll() {
        List<HoSoDangKy> list = new ArrayList<>();
        String sql = JOIN_SQL + "ORDER BY h.ma_ho_so";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi truy van danh sach ho so: " + e.getMessage(), e);
        }
        return list;
    }

    public List<HoSoDangKy> filter(String maNganh, String trangThai) {
        StringBuilder sql = new StringBuilder(JOIN_SQL + "WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (maNganh != null && !maNganh.isEmpty()
                && !maNganh.equals("Tất cả") && !maNganh.equalsIgnoreCase("Tat ca")) {
            sql.append("AND h.ma_nganh = ? ");
            params.add(maNganh);
        }
        if (trangThai != null && !trangThai.isEmpty()
                && !trangThai.equals("Tất cả") && !trangThai.equalsIgnoreCase("Tat ca")) {
            sql.append("AND h.trang_thai = ? ");
            params.add(trangThai);
        }
        sql.append("ORDER BY (h.diem_xet + h.diem_uu_tien) DESC, h.ngay_nop ASC");

        List<HoSoDangKy> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi loc ho so: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean insert(HoSoDangKy hs) {
        String sql = "INSERT INTO ho_so_dang_ky (ma_ho_so, ma_ts, ma_nganh, khoi_dang_ky, diem_xet, diem_uu_tien, diem_mon_1, diem_mon_2, diem_mon_3, phuong_thuc, ngay_nop, trang_thai, ghi_chu) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hs.getMaHoSo());
            ps.setString(2, hs.getMaTS());
            ps.setString(3, hs.getMaNganh());
            ps.setString(4, hs.getKhoiDangKy());
            ps.setDouble(5, hs.getDiemXet());
            ps.setDouble(6, hs.getDiemUuTien());
            ps.setDouble(7, hs.getDiemMon1());
            ps.setDouble(8, hs.getDiemMon2());
            ps.setDouble(9, hs.getDiemMon3());
            ps.setString(10, hs.getPhuongThuc());
            ps.setDate(11, Date.valueOf(hs.getNgayNop()));
            ps.setString(12, hs.getTrangThai());
            ps.setString(13, hs.getGhiChu());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi them ho so: " + e.getMessage(), e);
        }
    }

    public boolean update(HoSoDangKy hs) {
        // Cập nhật thông tin hồ sơ không được tự ý thay đổi trạng thái xét tuyển.
        String sql = "UPDATE ho_so_dang_ky SET ma_ts=?, ma_nganh=?, khoi_dang_ky=?, diem_xet=?, diem_uu_tien=?, diem_mon_1=?, diem_mon_2=?, diem_mon_3=?, phuong_thuc=?, ngay_nop=?, ghi_chu=? " +
                     "WHERE ma_ho_so=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hs.getMaTS());
            ps.setString(2, hs.getMaNganh());
            ps.setString(3, hs.getKhoiDangKy());
            ps.setDouble(4, hs.getDiemXet());
            ps.setDouble(5, hs.getDiemUuTien());
            ps.setDouble(6, hs.getDiemMon1());
            ps.setDouble(7, hs.getDiemMon2());
            ps.setDouble(8, hs.getDiemMon3());
            ps.setString(9, hs.getPhuongThuc());
            ps.setDate(10, Date.valueOf(hs.getNgayNop()));
            ps.setString(11, hs.getGhiChu());
            ps.setString(12, hs.getMaHoSo());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat ho so: " + e.getMessage(), e);
        }
    }

    public boolean updateTrangThai(String maHoSo, String trangThaiMoi) {
        String sql = "UPDATE ho_so_dang_ky SET trang_thai=?, ngay_nhap_hoc="
                + "CASE WHEN ?=? THEN CURRENT_DATE ELSE NULL END WHERE ma_ho_so=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trangThaiMoi);
            ps.setString(2, trangThaiMoi);
            ps.setString(3, HoSoDangKy.DA_NHAP_HOC);
            ps.setString(4, maHoSo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat trang thai ho so: " + e.getMessage(), e);
        }
    }

    public boolean delete(String maHoSo) {
        String sql = "DELETE FROM ho_so_dang_ky WHERE ma_ho_so = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maHoSo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi xoa ho so: " + e.getMessage(), e);
        }
    }

    public boolean existsApplication(String maTS, String maNganh, String excludeMaHoSo) {
        String sql = "SELECT COUNT(*) FROM ho_so_dang_ky WHERE ma_ts=? AND ma_nganh=? AND ma_ho_so<>?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTS); ps.setString(2, maNganh); ps.setString(3, excludeMaHoSo == null ? "" : excludeMaHoSo);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        } catch (SQLException e) { throw new RuntimeException("Loi kiem tra ho so trung: " + e.getMessage(), e); }
    }

    /** Sinh mã hồ sơ tự động dạng HS0001, HS0002... */
    public String generateNextId() {
        String sql = "SELECT ma_ho_so FROM ho_so_dang_ky ORDER BY ma_ho_so DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String last = rs.getString(1);
                int num = Integer.parseInt(last.replace("HS", "")) + 1;
                return String.format("HS%04d", num);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi sinh ma ho so: " + e.getMessage(), e);
        }
        return "HS0001";
    }

    /** Thống kê: số hồ sơ theo từng ngành + trạng thái (dùng cho tab Thống kê). */
    public List<Object[]> thongKeTheoNganh() {
        String sql =
            "SELECT n.ma_nganh, n.ten_nganh, n.chi_tieu, " +
            "  SUM(CASE WHEN h.trang_thai='Cho duyet' THEN 1 ELSE 0 END) AS cho_duyet, " +
            "  SUM(CASE WHEN h.trang_thai='Trung tuyen' THEN 1 ELSE 0 END) AS trung_tuyen, " +
            "  SUM(CASE WHEN h.trang_thai='Da nhap hoc' THEN 1 ELSE 0 END) AS da_nhap_hoc, " +
            "  SUM(CASE WHEN h.trang_thai='Khong trung tuyen' THEN 1 ELSE 0 END) AS khong_tt, " +
            "  COUNT(h.ma_ho_so) AS tong_ho_so " +
            "FROM nganh_tuyen_sinh n " +
            "LEFT JOIN ho_so_dang_ky h ON n.ma_nganh = h.ma_nganh " +
            "GROUP BY n.ma_nganh, n.ten_nganh, n.chi_tieu " +
            "ORDER BY n.ma_nganh";

        List<Object[]> result = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new Object[]{
                        rs.getString("ma_nganh"),
                        rs.getString("ten_nganh"),
                        rs.getInt("chi_tieu"),
                        rs.getInt("cho_duyet"),
                        rs.getInt("trung_tuyen"),
                        rs.getInt("da_nhap_hoc"),
                        rs.getInt("khong_tt"),
                        rs.getInt("tong_ho_so")
                });
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi thong ke: " + e.getMessage(), e);
        }
        return result;
    }

    private HoSoDangKy map(ResultSet rs) throws SQLException {
        HoSoDangKy hs = new HoSoDangKy();
        hs.setMaHoSo(rs.getString("ma_ho_so"));
        hs.setMaTS(rs.getString("ma_ts"));
        hs.setMaNganh(rs.getString("ma_nganh"));
        hs.setKhoiDangKy(rs.getString("khoi_dang_ky"));
        hs.setDiemXet(rs.getDouble("diem_xet"));
        hs.setDiemUuTien(rs.getDouble("diem_uu_tien"));
        hs.setDiemMon1(rs.getDouble("diem_mon_1"));
        hs.setDiemMon2(rs.getDouble("diem_mon_2"));
        hs.setDiemMon3(rs.getDouble("diem_mon_3"));
        hs.setPhuongThuc(rs.getString("phuong_thuc"));
        Date ngayNop = rs.getDate("ngay_nop");
        hs.setNgayNop(ngayNop != null ? ngayNop.toLocalDate() : null);
        Date ngayNhapHoc = rs.getDate("ngay_nhap_hoc");
        hs.setNgayNhapHoc(ngayNhapHoc != null ? ngayNhapHoc.toLocalDate() : null);
        hs.setTrangThai(rs.getString("trang_thai"));
        hs.setGhiChu(rs.getString("ghi_chu"));
        hs.setHoTenTS(rs.getString("ho_ten_ts"));
        hs.setKhoiDuThi(rs.getString("khoi_du_thi"));
        hs.setTenNganh(rs.getString("ten_nganh"));
        hs.setKhoiXetTuyen(rs.getString("khoi_xet_tuyen"));
        return hs;
    }
}
