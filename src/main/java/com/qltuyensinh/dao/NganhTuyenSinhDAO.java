package com.qltuyensinh.dao;

import com.qltuyensinh.connection.DBConnection;
import com.qltuyensinh.model.NganhTuyenSinh;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NganhTuyenSinhDAO {

    private static final String SELECT_WITH_COUNT =
            "SELECT n.*, "
          + "(SELECT COUNT(*) FROM ho_so_dang_ky h "
          + " WHERE h.ma_nganh=n.ma_nganh "
          + " AND h.trang_thai IN ('Trung tuyen', 'Da nhap hoc')) AS so_trung_tuyen "
          + "FROM nganh_tuyen_sinh n ";

    public List<NganhTuyenSinh> getAll() {
        List<NganhTuyenSinh> list = new ArrayList<>();
        String sql = SELECT_WITH_COUNT + "ORDER BY n.ma_nganh";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi truy van danh sach nganh: " + e.getMessage(), e);
        }
        return list;
    }

    public NganhTuyenSinh getById(String maNganh) {
        String sql = SELECT_WITH_COUNT + "WHERE n.ma_nganh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNganh);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi truy van nganh: " + e.getMessage(), e);
        }
        return null;
    }

    /** Số lượng hồ sơ đã trúng tuyển hoặc đã nhập học của một ngành. */
    public int demSoTrungTuyen(String maNganh) {
        String sql = "SELECT COUNT(*) FROM ho_so_dang_ky WHERE ma_nganh = ? "
                   + "AND trang_thai IN ('Trung tuyen', 'Da nhap hoc')";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNganh);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi dem so trung tuyen: " + e.getMessage(), e);
        }
        return 0;
    }

    public boolean insert(NganhTuyenSinh nganh) {
        String sql = "INSERT INTO nganh_tuyen_sinh (ma_nganh, ten_nganh, chi_tieu, diem_chuan_tam, khoi_xet_tuyen, mo_ta) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nganh.getMaNganh());
            ps.setString(2, nganh.getTenNganh());
            ps.setInt(3, nganh.getChiTieu());
            ps.setDouble(4, nganh.getDiemChuanTam());
            ps.setString(5, nganh.getKhoiXetTuyen());
            ps.setString(6, nganh.getMoTa());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi them nganh: " + e.getMessage(), e);
        }
    }

    public boolean update(NganhTuyenSinh nganh) {
        String sql = "UPDATE nganh_tuyen_sinh SET ten_nganh=?, chi_tieu=?, diem_chuan_tam=?, khoi_xet_tuyen=?, mo_ta=? " +
                     "WHERE ma_nganh=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nganh.getTenNganh());
            ps.setInt(2, nganh.getChiTieu());
            ps.setDouble(3, nganh.getDiemChuanTam());
            ps.setString(4, nganh.getKhoiXetTuyen());
            ps.setString(5, nganh.getMoTa());
            ps.setString(6, nganh.getMaNganh());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat nganh: " + e.getMessage(), e);
        }
    }

    public boolean delete(String maNganh) {
        String sql = "DELETE FROM nganh_tuyen_sinh WHERE ma_nganh = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNganh);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi xoa nganh (co the dang duoc su dung trong ho so): " + e.getMessage(), e);
        }
    }

    public boolean hasApplications(String maNganh) {
        String sql = "SELECT COUNT(*) FROM ho_so_dang_ky WHERE ma_nganh = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maNganh);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra hồ sơ của ngành: " + e.getMessage(), e);
        }
    }

    private NganhTuyenSinh map(ResultSet rs) throws SQLException {
        NganhTuyenSinh nganh = new NganhTuyenSinh(
                rs.getString("ma_nganh"),
                rs.getString("ten_nganh"),
                rs.getInt("chi_tieu"),
                rs.getDouble("diem_chuan_tam"),
                rs.getString("khoi_xet_tuyen"),
                rs.getString("mo_ta")
        );
        nganh.setSoTrungTuyen(rs.getInt("so_trung_tuyen"));
        return nganh;
    }
}
