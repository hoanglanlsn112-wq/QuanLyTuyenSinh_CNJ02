package com.qltuyensinh.dao;

import com.qltuyensinh.connection.DBConnection;
import com.qltuyensinh.model.AuditLog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class AuditLogDAO {
    public void log(String taiKhoan, String hanhDong, String chiTiet) {
        String sql = "INSERT INTO audit_log (tai_khoan, hanh_dong, chi_tiet) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, taiKhoan);
            ps.setString(2, hanhDong);
            ps.setString(3, chiTiet);
            ps.executeUpdate();
        } catch (Exception ignored) {
            // Nhat ky khong duoc lam dung ung dung neu bang audit chua ton tai.
        }
    }

    public List<AuditLog> getAll() {
        List<AuditLog> logs = new ArrayList<>();
        String sql = "SELECT id, tai_khoan, hanh_dong, chi_tiet, thoi_gian "
                + "FROM audit_log ORDER BY thoi_gian DESC, id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                logs.add(new AuditLog(
                        rs.getLong("id"),
                        rs.getString("tai_khoan"),
                        rs.getString("hanh_dong"),
                        rs.getString("chi_tiet"),
                        rs.getTimestamp("thoi_gian")));
            }
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tải nhật ký hệ thống: " + ex.getMessage(), ex);
        }
        return logs;
    }
}
