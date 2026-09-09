package com.qltuyensinh.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import com.qltuyensinh.dao.ThiSinhDAO;
import com.qltuyensinh.model.ThiSinh;
import com.qltuyensinh.util.AlertUtil;
import com.qltuyensinh.util.UITheme;
import com.qltuyensinh.util.ValidationUtil;

public class ThiSinhPanel extends JPanel {

    private final ThiSinhDAO dao = new ThiSinhDAO();

    private JTextField txtMaTS, txtHoTen, txtNgaySinh, txtDiaChi, txtSdt, txtEmail, txtCccd, txtSearch;
    private JComboBox<String> cboKhoiDuThi, cboGioiTinh, cboFilterKhoi;
    private JTable table;
    private DefaultTableModel tableModel;

    public ThiSinhPanel() {
        setLayout(new BorderLayout(8, 8));
        setBackground(UITheme.BG_PAGE);
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        add(buildFormPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        loadData();
    }

    // ── Form nhập liệu ────────────────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel wrap = new JPanel(new BorderLayout(0, 8));
        wrap.setOpaque(false);

        JPanel form = new JPanel(new GridBagLayout());
        // Không phủ một mảng trắng ra toàn bộ vùng nội dung; phần form hòa
        // cùng nền trang như các khối trên màn hình Thống kê.
        form.setOpaque(false);
        form.setBorder(UITheme.sectionBorder("Thông tin thí sinh"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaTS     = UITheme.styledTextField(10);
        txtMaTS.setEditable(false);
        txtMaTS.setBackground(Color.WHITE);
        txtMaTS.setDisabledTextColor(UITheme.TEXT_PRIMARY);
        txtHoTen    = UITheme.styledTextField(20);
        txtNgaySinh = UITheme.styledTextField(10);
        txtDiaChi   = UITheme.styledTextField(22);
        txtSdt      = UITheme.styledTextField(12);
        txtEmail    = UITheme.styledTextField(20);
        txtCccd     = UITheme.styledTextField(15);
        cboGioiTinh = new JComboBox<>(new String[]{"Nam","Nữ"});
        UITheme.styleComboBox(cboGioiTinh);
        cboKhoiDuThi = new JComboBox<>(new String[]{
            "Chưa chọn","A00","A01","A02","B00","B03","C00","C01",
            "D01","D07","D08","D09","D10"
        });
        UITheme.styleComboBox(cboKhoiDuThi);

        int r = 0;
        gbc.gridx=0; gbc.gridy=r; form.add(UITheme.fieldLabel("Mã TS (tự động):"), gbc);
        gbc.gridx=1; form.add(txtMaTS, gbc);
        gbc.gridx=2; form.add(UITheme.fieldLabel("Họ tên:"), gbc);
        gbc.gridx=3; gbc.weightx=1; form.add(txtHoTen, gbc); gbc.weightx=0;

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(UITheme.fieldLabel("Ngày sinh (dd/MM/yyyy):"), gbc);
        gbc.gridx=1; form.add(txtNgaySinh, gbc);
        gbc.gridx=2; form.add(UITheme.fieldLabel("Giới tính:"), gbc);
        gbc.gridx=3; form.add(cboGioiTinh, gbc);

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(UITheme.fieldLabel("Địa chỉ:"), gbc);
        gbc.gridx=1; form.add(txtDiaChi, gbc);
        gbc.gridx=2; form.add(UITheme.fieldLabel("Số điện thoại:"), gbc);
        gbc.gridx=3; form.add(txtSdt, gbc);

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(UITheme.fieldLabel("Email:"), gbc);
        gbc.gridx=1; form.add(txtEmail, gbc);
        gbc.gridx=2; form.add(UITheme.fieldLabel("CCCD:"), gbc);
        gbc.gridx=3; form.add(txtCccd, gbc);

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(UITheme.fieldLabel("Khối dự thi:"), gbc);
        gbc.gridx=1; form.add(cboKhoiDuThi, gbc);

        r++;
        JButton btnThem   = UITheme.primaryButton("Thêm");
        JButton btnSua    = UITheme.warningButton("Sửa");
        JButton btnXoa    = UITheme.dangerButton("Xóa");
        JButton btnLamMoi = UITheme.secondaryButton("Làm mới");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnThem); btnRow.add(btnSua); btnRow.add(btnXoa); btnRow.add(btnLamMoi);

        gbc.gridx=0; gbc.gridy=r; gbc.gridwidth=4;
        gbc.insets = new Insets(8, 8, 8, 8);
        form.add(btnRow, gbc);

        btnThem.addActionListener(e   -> themThiSinh());
        btnSua.addActionListener(e    -> suaThiSinh());
        btnXoa.addActionListener(e    -> xoaThiSinh());
        btnLamMoi.addActionListener(e -> clearForm());

        // Thanh tìm kiếm + lọc khối
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchRow.setOpaque(false);
        searchRow.add(UITheme.fieldLabel("Tìm kiếm:"));
        txtSearch = UITheme.styledTextField(20);
        searchRow.add(txtSearch);
        searchRow.add(UITheme.fieldLabel("Khối:"));
        cboFilterKhoi = new JComboBox<>(new String[]{
            "Tất cả","A00","A01","A02","B00","B03","C00","C01",
            "D01","D07","D08","D09","D10"
        });
        UITheme.styleComboBox(cboFilterKhoi);
        // Đủ rộng để hiển thị đầy đủ "Tất cả", không bị rút gọn thành "T...".
        cboFilterKhoi.setPreferredSize(new Dimension(105, 34));
        searchRow.add(cboFilterKhoi);
        JButton btnSearch = UITheme.primaryButton("Tìm");
        JButton btnReset  = UITheme.secondaryButton("Tất cả");
        searchRow.add(btnSearch);
        searchRow.add(btnReset);
        btnSearch.addActionListener(e -> searchData());
        btnReset.addActionListener(e  -> { txtSearch.setText(""); cboFilterKhoi.setSelectedIndex(0); loadData(); });
        txtSearch.addActionListener(e -> searchData());

        wrap.add(form, BorderLayout.CENTER);
        wrap.add(searchRow, BorderLayout.SOUTH);
        return wrap;
    }

    // ── Bảng ─────────────────────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] cols = {"Mã TS","Họ tên","Ngày sinh","Giới tính","Địa chỉ","SĐT","Email","CCCD","Khối dự thi"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1)
                fillFormFromRow(table.getSelectedRow());
        });

        int[] widths = {80, 160, 100, 80, 180, 105, 160, 130, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        UITheme.styleScrollPane(scroll);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 215, 235), 1));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ── Data ─────────────────────────────────────────────────────────────────
    private void loadData() {
        tableModel.setRowCount(0);
        for (ThiSinh t : dao.getAll()) addRow(t);
    }

    private void searchData() {
        String kw    = txtSearch.getText().trim().toLowerCase();
        String khoi  = (String) cboFilterKhoi.getSelectedItem();
        boolean allKhoi = "Tất cả".equals(khoi);
        tableModel.setRowCount(0);
        for (ThiSinh t : dao.getAll()) {
            boolean matchKw = kw.isEmpty()
                    || t.getMaTS().toLowerCase().contains(kw)
                    || t.getHoTen().toLowerCase().contains(kw);
            boolean matchKhoi = allKhoi
                    || khoi.equals(t.getKhoiDuThi());
            if (matchKw && matchKhoi) addRow(t);
        }
    }

    private void addRow(ThiSinh t) {
        tableModel.addRow(new Object[]{
            t.getMaTS(), t.getHoTen(), ValidationUtil.formatDate(t.getNgaySinh()),
            t.getGioiTinh(), t.getDiaChi(), t.getSdt(), t.getEmail(), t.getCccd(), t.getKhoiDuThi()
        });
    }

    private void fillFormFromRow(int row) {
        txtMaTS.setText(s(tableModel.getValueAt(row, 0)));
        txtHoTen.setText(s(tableModel.getValueAt(row, 1)));
        txtNgaySinh.setText(s(tableModel.getValueAt(row, 2)));
        cboGioiTinh.setSelectedItem(s(tableModel.getValueAt(row, 3)));
        txtDiaChi.setText(s(tableModel.getValueAt(row, 4)));
        txtSdt.setText(s(tableModel.getValueAt(row, 5)));
        txtEmail.setText(s(tableModel.getValueAt(row, 6)));
        txtCccd.setText(s(tableModel.getValueAt(row, 7)));
        String kd = s(tableModel.getValueAt(row, 8));
        cboKhoiDuThi.setSelectedItem(kd.isEmpty() ? "Chưa chọn" : kd);
    }

    private String s(Object o) { return o == null ? "" : o.toString(); }

    private void clearForm() {
        txtMaTS.setText(""); txtHoTen.setText(""); txtNgaySinh.setText("");
        cboGioiTinh.setSelectedIndex(0); txtDiaChi.setText(""); txtSdt.setText("");
        txtEmail.setText(""); txtCccd.setText(""); cboKhoiDuThi.setSelectedIndex(0);
        table.clearSelection();
    }

    private String getKhoiDuThi() {
        String k = (String) cboKhoiDuThi.getSelectedItem();
        return k == null || k.equals("Chưa chọn") ? "" : k;
    }

    // ── Validate ─────────────────────────────────────────────────────────────
    private boolean validateForm() {
        if (ValidationUtil.isEmpty(txtHoTen.getText())) {
            AlertUtil.error(this, "Họ tên không được để trống."); return false;
        }
        if (ValidationUtil.parseDate(txtNgaySinh.getText().trim()) == null) {
            AlertUtil.error(this, "Ngày sinh không hợp lệ. Định dạng: dd/MM/yyyy"); return false;
        }
        if (ValidationUtil.parseDate(txtNgaySinh.getText().trim()).isAfter(java.time.LocalDate.now())) {
            AlertUtil.error(this, "Ngày sinh không được lớn hơn ngày hiện tại."); return false;
        }
        if (!ValidationUtil.isValidPhone(txtSdt.getText().trim())) {
            AlertUtil.error(this, "Số điện thoại không hợp lệ."); return false;
        }
        if (!ValidationUtil.isValidEmail(txtEmail.getText().trim())) {
            AlertUtil.error(this, "Email không hợp lệ."); return false;
        }
        if (!ValidationUtil.isValidCccd(txtCccd.getText().trim())) {
            AlertUtil.error(this, "CCCD phải gồm đúng 12 chữ số."); return false;
        }
        String maTS = txtMaTS.getText().trim();
        if (dao.existsCccd(txtCccd.getText().trim(), maTS)) {
            AlertUtil.error(this, "CCCD đã tồn tại trong hệ thống."); return false;
        }
        if (dao.existsEmail(txtEmail.getText().trim(), maTS)) {
            AlertUtil.error(this, "Email đã tồn tại trong hệ thống."); return false;
        }
        return true;
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────
    private void themThiSinh() {
        if (!validateForm()) return;
        try {
            String maTS = dao.generateNextId();
            ThiSinh t = new ThiSinh(
                maTS, txtHoTen.getText().trim(),
                ValidationUtil.parseDate(txtNgaySinh.getText().trim()),
                (String) cboGioiTinh.getSelectedItem(),
                txtDiaChi.getText().trim(), txtSdt.getText().trim(),
                txtEmail.getText().trim(), txtCccd.getText().trim(), getKhoiDuThi()
            );
            if (dao.insert(t)) { AlertUtil.info(this, "Thêm thí sinh thành công. Mã TS: " + maTS); loadData(); clearForm(); }
        } catch (Exception ex) { AlertUtil.error(this, "Lỗi: " + ex.getMessage()); }
    }

    private void suaThiSinh() {
        if (table.getSelectedRow() == -1) { AlertUtil.error(this, "Vui lòng chọn 1 thí sinh để sửa."); return; }
        if (!validateForm()) return;
        try {
            ThiSinh t = new ThiSinh(
                txtMaTS.getText().trim(), txtHoTen.getText().trim(),
                ValidationUtil.parseDate(txtNgaySinh.getText().trim()),
                (String) cboGioiTinh.getSelectedItem(),
                txtDiaChi.getText().trim(), txtSdt.getText().trim(),
                txtEmail.getText().trim(), txtCccd.getText().trim(), getKhoiDuThi()
            );
            if (dao.update(t)) { AlertUtil.info(this, "Cập nhật thành công."); loadData(); clearForm(); }
        } catch (Exception ex) { AlertUtil.error(this, "Lỗi: " + ex.getMessage()); }
    }

    private void xoaThiSinh() {
        if (table.getSelectedRow() == -1) { AlertUtil.error(this, "Vui lòng chọn 1 thí sinh để xóa."); return; }
        if (dao.hasApplications(txtMaTS.getText().trim())) {
            AlertUtil.error(this, "Không thể xóa thí sinh đã có hồ sơ đăng ký.");
            return;
        }
        if (!AlertUtil.confirm(this, "Bạn có chắc muốn xóa thí sinh này?")) return;
        try {
            if (dao.delete(txtMaTS.getText().trim())) {
                AlertUtil.info(this, "Xóa thành công."); loadData(); clearForm();
            }
        } catch (Exception ex) {
            AlertUtil.error(this, "Không thể xóa (thí sinh có thể đã có hồ sơ đăng ký):\n" + ex.getMessage());
        }
    }
}
