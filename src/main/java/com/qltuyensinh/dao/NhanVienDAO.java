package com.qltuyensinh.dao;

import com.qltuyensinh.connection.DBConnection;
import com.qltuyensinh.model.NhanVien;
import com.qltuyensinh.util.PasswordUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();

    public NhanVien login(String taiKhoan, String matKhau) {
        String sql = "SELECT * FROM nhan_vien WHERE tai_khoan = ?";
        String maNV = null, hoTen = null, taiKhoanDb = null, matKhauDb = null, vaiTro = null;
        boolean valid;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, taiKhoan);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    auditLogDAO.log(taiKhoan, "DANG_NHAP_THAT_BAI", "Khong ton tai tai khoan");
                    return null;
                }
                matKhauDb = rs.getString("mat_khau");
                valid = matKhauDb.equals(PasswordUtil.hash(matKhau)) || matKhauDb.equals(matKhau);
                if (!valid) {
                    auditLogDAO.log(taiKhoan, "DANG_NHAP_THAT_BAI", "Sai mat khau");
                    return null;
                }
                maNV = rs.getString("ma_nv");
                hoTen = rs.getString("ho_ten");
                taiKhoanDb = rs.getString("tai_khoan");
                vaiTro = rs.getString("vai_tro");
            }

            if (matKhauDb.equals(matKhau)) {
                try (PreparedStatement upgrade = conn.prepareStatement(
                        "UPDATE nhan_vien SET mat_khau=? WHERE tai_khoan=?")) {
                    upgrade.setString(1, PasswordUtil.hash(matKhau));
                    upgrade.setString(2, taiKhoan);
                    upgrade.executeUpdate();
                }
            }
            auditLogDAO.log(taiKhoan, "DANG_NHAP", "Dang nhap thanh cong");
            return new NhanVien(maNV, hoTen, taiKhoanDb, PasswordUtil.hash(matKhau), vaiTro);
        } catch (SQLException e) {
            throw new RuntimeException("Loi dang nhap: " + e.getMessage(), e);
        }
    }

    public List<NhanVien> getAll() {
        List<NhanVien> result = new ArrayList<>();
        String sql = "SELECT ma_nv, ho_ten, tai_khoan, mat_khau, vai_tro FROM nhan_vien ORDER BY ma_nv";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                result.add(new NhanVien(rs.getString("ma_nv"), rs.getString("ho_ten"),
                        rs.getString("tai_khoan"), rs.getString("mat_khau"), rs.getString("vai_tro")));
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Loi tai danh sach nhan vien: " + e.getMessage(), e);
        }
    }

    public boolean insert(NhanVien nv, String rawPassword) {
        String sql = "INSERT INTO nhan_vien (ma_nv, ho_ten, tai_khoan, mat_khau, vai_tro) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getMaNV()); ps.setString(2, nv.getHoTen()); ps.setString(3, nv.getTaiKhoan());
            ps.setString(4, PasswordUtil.hash(rawPassword)); ps.setString(5, nv.getVaiTro());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException("Loi them nhan vien: " + e.getMessage(), e); }
    }

    public boolean update(NhanVien nv, String rawPassword) {
        String sql = rawPassword == null || rawPassword.isBlank()
                ? "UPDATE nhan_vien SET ho_ten=?, tai_khoan=?, vai_tro=? WHERE ma_nv=?"
                : "UPDATE nhan_vien SET ho_ten=?, tai_khoan=?, mat_khau=?, vai_tro=? WHERE ma_nv=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nv.getHoTen()); ps.setString(2, nv.getTaiKhoan());
            if (rawPassword == null || rawPassword.isBlank()) {
                ps.setString(3, nv.getVaiTro()); ps.setString(4, nv.getMaNV());
            } else {
                ps.setString(3, PasswordUtil.hash(rawPassword)); ps.setString(4, nv.getVaiTro()); ps.setString(5, nv.getMaNV());
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException("Loi cap nhat nhan vien: " + e.getMessage(), e); }
    }

    public boolean delete(String maNV) {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM nhan_vien WHERE ma_nv=?")) {
            ps.setString(1, maNV); return ps.executeUpdate() > 0;
        } catch (SQLException e) { throw new RuntimeException("Loi xoa nhan vien: " + e.getMessage(), e); }
    }
}
