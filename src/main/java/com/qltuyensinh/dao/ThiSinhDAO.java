package com.qltuyensinh.dao;

import com.qltuyensinh.connection.DBConnection;
import com.qltuyensinh.model.ThiSinh;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ThiSinhDAO {

    public List<ThiSinh> getAll() {
        List<ThiSinh> list = new ArrayList<>();
        String sql = "SELECT * FROM thi_sinh ORDER BY ma_ts";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi truy van danh sach thi sinh: " + e.getMessage(), e);
        }
        return list;
    }

    public List<ThiSinh> search(String keyword) {
        List<ThiSinh> list = new ArrayList<>();
        String sql = "SELECT * FROM thi_sinh WHERE ma_ts LIKE ? OR ho_ten LIKE ? ORDER BY ma_ts";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            String kw = "%" + keyword + "%";
            ps.setString(1, kw);
            ps.setString(2, kw);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi tim kiem thi sinh: " + e.getMessage(), e);
        }
        return list;
    }

    public boolean insert(ThiSinh ts) {
        String sql = "INSERT INTO thi_sinh (ma_ts, ho_ten, ngay_sinh, gioi_tinh, dia_chi, sdt, email, cccd, khoi_du_thi, anh_the) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ts.getMaTS());
            ps.setString(2, ts.getHoTen());
            ps.setDate(3, ts.getNgaySinh() != null ? Date.valueOf(ts.getNgaySinh()) : null);
            ps.setString(4, ts.getGioiTinh());
            ps.setString(5, ts.getDiaChi());
            ps.setString(6, ts.getSdt());
            ps.setString(7, ts.getEmail());
            ps.setString(8, ts.getCccd());
            ps.setString(9, ts.getKhoiDuThi());
            ps.setString(10, ts.getAnhThe());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi them thi sinh: " + e.getMessage(), e);
        }
    }

    public boolean update(ThiSinh ts) {
        String sql = "UPDATE thi_sinh SET ho_ten=?, ngay_sinh=?, gioi_tinh=?, dia_chi=?, sdt=?, email=?, cccd=?, khoi_du_thi=? " +
                     "WHERE ma_ts=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ts.getHoTen());
            ps.setDate(2, ts.getNgaySinh() != null ? Date.valueOf(ts.getNgaySinh()) : null);
            ps.setString(3, ts.getGioiTinh());
            ps.setString(4, ts.getDiaChi());
            ps.setString(5, ts.getSdt());
            ps.setString(6, ts.getEmail());
            ps.setString(7, ts.getCccd());
            ps.setString(8, ts.getKhoiDuThi());
            ps.setString(9, ts.getMaTS());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi cap nhat thi sinh: " + e.getMessage(), e);
        }
    }

    public boolean delete(String maTS) {
        String sql = "DELETE FROM thi_sinh WHERE ma_ts = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTS);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Loi xoa thi sinh (co the da co ho so dang ky): " + e.getMessage(), e);
        }
    }

    public boolean hasApplications(String maTS) {
        String sql = "SELECT COUNT(*) FROM ho_so_dang_ky WHERE ma_ts = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, maTS);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi kiểm tra hồ sơ của thí sinh: " + e.getMessage(), e);
        }
    }

    public boolean existsCccd(String cccd, String excludeMaTS) {
        return exists("cccd", cccd, excludeMaTS);
    }

    public boolean existsEmail(String email, String excludeMaTS) {
        return exists("email", email, excludeMaTS);
    }

    private boolean exists(String column, String value, String excludeMaTS) {
        if (value == null || value.isBlank()) return false;
        String sql = "SELECT COUNT(*) FROM thi_sinh WHERE " + column + "=? AND ma_ts<>?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, value.trim());
            ps.setString(2, excludeMaTS == null ? "" : excludeMaTS);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() && rs.getInt(1) > 0; }
        } catch (SQLException e) { throw new RuntimeException("Loi kiem tra trung du lieu: " + e.getMessage(), e); }
    }

    public boolean updatePhotoPath(String maTS, String path) {
        String sql = "UPDATE thi_sinh SET anh_the=? WHERE ma_ts=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, path);
            ps.setString(2, maTS);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException("Loi luu anh the: " + e.getMessage(), e); }
    }

    /** Sinh mã thí sinh tự động dạng TS0001, TS0002... */
    public String generateNextId() {
        String sql = "SELECT ma_ts FROM thi_sinh ORDER BY ma_ts DESC LIMIT 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                String last = rs.getString(1);
                int num = Integer.parseInt(last.replace("TS", "")) + 1;
                return String.format("TS%04d", num);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Loi sinh ma thi sinh: " + e.getMessage(), e);
        }
        return "TS0001";
    }

    private ThiSinh map(ResultSet rs) throws SQLException {
        Date ns = rs.getDate("ngay_sinh");
        ThiSinh thiSinh = new ThiSinh(
                rs.getString("ma_ts"),
                rs.getString("ho_ten"),
                ns != null ? ns.toLocalDate() : null,
                rs.getString("gioi_tinh"),
                rs.getString("dia_chi"),
                rs.getString("sdt"),
                rs.getString("email"),
                rs.getString("cccd"),
                rs.getString("khoi_du_thi")
        );
        thiSinh.setAnhThe(rs.getString("anh_the"));
        return thiSinh;
    }
}
