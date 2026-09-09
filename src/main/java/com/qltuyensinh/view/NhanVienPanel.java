package com.qltuyensinh.view;

import com.qltuyensinh.dao.NhanVienDAO;
import com.qltuyensinh.model.NhanVien;
import com.qltuyensinh.util.AlertUtil;
import com.qltuyensinh.util.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Màn hình quản lý tài khoản, chỉ được mở bởi Admin. */
public class NhanVienPanel extends JPanel {
    private final NhanVienDAO dao = new NhanVienDAO();
    private JTextField txtMa, txtHoTen, txtTaiKhoan;
    private JPasswordField txtMatKhau;
    private JComboBox<String> cboVaiTro;
    private JTable table;
    private DefaultTableModel model;

    public NhanVienPanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(UITheme.BG_PAGE);
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        add(buildForm(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        loadData();
    }

    private JPanel buildForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(UITheme.sectionBorder("Quản lý tài khoản nhân viên"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8); c.fill = GridBagConstraints.HORIZONTAL;

        txtMa = UITheme.styledTextField(10);
        txtHoTen = UITheme.styledTextField(22);
        txtTaiKhoan = UITheme.styledTextField(16);
        txtMatKhau = UITheme.styledPasswordField(16);
        cboVaiTro = new JComboBox<>(new String[]{"NhanVien", "TuyenSinh", "Admin"});
        UITheme.styleComboBox(cboVaiTro);

        addField(form, c, 0, 0, "Mã nhân viên:", txtMa);
        addField(form, c, 2, 0, "Họ tên:", txtHoTen);
        addField(form, c, 0, 1, "Tài khoản:", txtTaiKhoan);
        addField(form, c, 2, 1, "Mật khẩu:", txtMatKhau);
        addField(form, c, 0, 2, "Vai trò:", cboVaiTro);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        buttons.setOpaque(false);
        JButton add = UITheme.primaryButton("Thêm");
        JButton edit = UITheme.warningButton("Sửa");
        JButton delete = UITheme.dangerButton("Xóa");
        JButton clear = UITheme.secondaryButton("Làm mới");
        buttons.add(add); buttons.add(edit); buttons.add(delete); buttons.add(clear);
        c.gridx = 0; c.gridy = 3; c.gridwidth = 4; c.insets = new Insets(8, 8, 8, 8);
        form.add(buttons, c);

        add.addActionListener(e -> insert());
        edit.addActionListener(e -> update());
        delete.addActionListener(e -> delete());
        clear.addActionListener(e -> clearForm());
        return form;
    }

    private void addField(JPanel panel, GridBagConstraints c, int x, int y, String label, Component field) {
        c.gridx = x; c.gridy = y; c.gridwidth = 1; c.weightx = 0;
        panel.add(UITheme.fieldLabel(label), c);
        c.gridx = x + 1; c.weightx = 1;
        panel.add(field, c);
    }

    private JScrollPane buildTable() {
        model = new DefaultTableModel(new String[]{"Mã NV", "Họ tên", "Tài khoản", "Vai trò"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        UITheme.styleTable(table);
        // Chỉ các dòng dữ liệu có nền zebra; phần còn lại dùng nền trang,
        // tránh cảm giác bảng trắng bị kéo dài đến cuối cửa sổ.
        table.setBackground(UITheme.BG_PAGE);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) fillForm(table.getSelectedRow());
        });
        JScrollPane scroll = new JScrollPane(table);
        UITheme.styleScrollPane(scroll);
        // Phần trống của bảng phải hòa vào nền màn hình, không tạo một mảng
        // trắng lớn bao quanh toàn bộ màn hình Nhân viên.
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(true);
        scroll.getViewport().setBackground(UITheme.BG_PAGE);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 215, 235)));
        return scroll;
    }

    private void loadData() {
        model.setRowCount(0);
        for (NhanVien nv : dao.getAll()) model.addRow(new Object[]{nv.getMaNV(), nv.getHoTen(), nv.getTaiKhoan(), nv.getVaiTro()});
    }

    private void fillForm(int row) {
        txtMa.setText(String.valueOf(model.getValueAt(row, 0)));
        txtMa.setEnabled(false);
        txtHoTen.setText(String.valueOf(model.getValueAt(row, 1)));
        txtTaiKhoan.setText(String.valueOf(model.getValueAt(row, 2)));
        cboVaiTro.setSelectedItem(String.valueOf(model.getValueAt(row, 3)));
        txtMatKhau.setText("");
    }

    private void clearForm() {
        txtMa.setText(""); txtMa.setEnabled(true); txtHoTen.setText("");
        txtTaiKhoan.setText(""); txtMatKhau.setText(""); cboVaiTro.setSelectedIndex(0);
        table.clearSelection();
    }

    private boolean validateForm(boolean passwordRequired) {
        if (txtMa.getText().trim().isEmpty() || txtHoTen.getText().trim().isEmpty()
                || txtTaiKhoan.getText().trim().isEmpty()
                || (passwordRequired && txtMatKhau.getPassword().length == 0)) {
            AlertUtil.error(this, "Vui lòng nhập đầy đủ thông tin bắt buộc."); return false;
        }
        return true;
    }

    private void insert() {
        if (!validateForm(true)) return;
        try {
            NhanVien nv = readForm();
            if (dao.insert(nv, new String(txtMatKhau.getPassword()))) {
                AlertUtil.info(this, "Thêm tài khoản thành công."); loadData(); clearForm();
            }
        } catch (Exception ex) { AlertUtil.error(this, "Không thể thêm tài khoản: " + ex.getMessage()); }
    }

    private void update() {
        if (!validateForm(false)) return;
        try {
            NhanVien nv = readForm();
            String password = new String(txtMatKhau.getPassword());
            if (dao.update(nv, password)) {
                AlertUtil.info(this, "Cập nhật tài khoản thành công."); loadData(); clearForm();
            }
        } catch (Exception ex) { AlertUtil.error(this, "Không thể cập nhật tài khoản: " + ex.getMessage()); }
    }

    private void delete() {
        if (table.getSelectedRow() < 0) { AlertUtil.error(this, "Vui lòng chọn tài khoản cần xóa."); return; }
        if (!AlertUtil.confirm(this, "Bạn có chắc muốn xóa tài khoản này?")) return;
        try {
            if (dao.delete(txtMa.getText().trim())) { AlertUtil.info(this, "Xóa tài khoản thành công."); loadData(); clearForm(); }
        } catch (Exception ex) { AlertUtil.error(this, "Không thể xóa tài khoản: " + ex.getMessage()); }
    }

    private NhanVien readForm() {
        return new NhanVien(txtMa.getText().trim(), txtHoTen.getText().trim(),
                txtTaiKhoan.getText().trim(), "", String.valueOf(cboVaiTro.getSelectedItem()));
    }
}
