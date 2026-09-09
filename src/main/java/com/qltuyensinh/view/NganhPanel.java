package com.qltuyensinh.view;

import java.awt.BorderLayout;
import java.awt.Color;
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

import com.qltuyensinh.dao.NganhTuyenSinhDAO;
import com.qltuyensinh.model.NganhTuyenSinh;
import com.qltuyensinh.util.AlertUtil;
import com.qltuyensinh.util.UITheme;
import com.qltuyensinh.util.ValidationUtil;

public class NganhPanel extends JPanel {

    private final NganhTuyenSinhDAO dao = new NganhTuyenSinhDAO();

    private JTextField txtMaNganh, txtTenNganh, txtChiTieu, txtDiemChuan, txtKhoiXetTuyen, txtMoTa, txtSearch;
    private JTable table;
    private DefaultTableModel tableModel;

    public NganhPanel() {
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
        form.setOpaque(false);
        form.setBorder(UITheme.sectionBorder("Thông tin ngành tuyển sinh"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtMaNganh     = UITheme.styledTextField(10);
        txtTenNganh    = UITheme.styledTextField(22);
        txtChiTieu     = UITheme.styledTextField(8);
        txtDiemChuan   = UITheme.styledTextField(8);
        txtKhoiXetTuyen = UITheme.styledTextField(22);
        txtKhoiXetTuyen.setToolTipText("Ví dụ: A00, A01, B00, D01");
        txtMoTa        = UITheme.styledTextField(30);

        // Hàng 0
        gbc.gridx=0; gbc.gridy=0; form.add(UITheme.fieldLabel("Mã ngành:"), gbc);
        gbc.gridx=1; form.add(txtMaNganh, gbc);
        gbc.gridx=2; form.add(UITheme.fieldLabel("Tên ngành:"), gbc);
        gbc.gridx=3; gbc.weightx=1; form.add(txtTenNganh, gbc); gbc.weightx=0;

        // Hàng 1
        gbc.gridx=0; gbc.gridy=1; form.add(UITheme.fieldLabel("Chỉ tiêu:"), gbc);
        gbc.gridx=1; form.add(txtChiTieu, gbc);
        gbc.gridx=2; form.add(UITheme.fieldLabel("Điểm chuẩn tạm:"), gbc);
        gbc.gridx=3; form.add(txtDiemChuan, gbc);

        // Hàng 2
        gbc.gridx=0; gbc.gridy=2; form.add(UITheme.fieldLabel("Khối xét tuyển:"), gbc);
        gbc.gridx=1; form.add(txtKhoiXetTuyen, gbc);

        // Hàng 3
        gbc.gridx=0; gbc.gridy=3; form.add(UITheme.fieldLabel("Mô tả:"), gbc);
        gbc.gridx=1; gbc.gridwidth=3; form.add(txtMoTa, gbc); gbc.gridwidth=1;

        // Hàng 4 – nút
        JButton btnThem    = UITheme.primaryButton("Thêm");
        JButton btnSua     = UITheme.warningButton("Sửa");
        JButton btnXoa     = UITheme.dangerButton("Xóa");
        JButton btnLamMoi  = UITheme.secondaryButton("Làm mới");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnThem); btnRow.add(btnSua); btnRow.add(btnXoa); btnRow.add(btnLamMoi);

        gbc.gridx=0; gbc.gridy=4; gbc.gridwidth=4;
        gbc.insets = new Insets(8, 8, 8, 8);
        form.add(btnRow, gbc);

        btnThem.addActionListener(e   -> themNganh());
        btnSua.addActionListener(e    -> suaNganh());
        btnXoa.addActionListener(e    -> xoaNganh());
        btnLamMoi.addActionListener(e -> clearForm());

        // Thanh tìm kiếm
        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchRow.setOpaque(false);
        searchRow.add(UITheme.fieldLabel("Tìm kiếm:"));
        txtSearch = UITheme.styledTextField(20);
        searchRow.add(txtSearch);
        JButton btnSearch = UITheme.primaryButton("Tìm");
        JButton btnReset  = UITheme.secondaryButton("Tất cả");
        searchRow.add(btnSearch);
        searchRow.add(btnReset);
        btnSearch.addActionListener(e -> searchData());
        btnReset.addActionListener(e  -> loadData());
        txtSearch.addActionListener(e -> searchData());

        wrap.add(form, BorderLayout.CENTER);
        wrap.add(searchRow, BorderLayout.SOUTH);
        return wrap;
    }

    // ── Bảng ─────────────────────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        String[] cols = {"Mã ngành","Tên ngành","Chỉ tiêu","Đã trúng / Chỉ tiêu",
                "Điểm chuẩn tạm","Khối xét tuyển","Mô tả"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1)
                fillFormFromRow(table.getSelectedRow());
        });

        // Độ rộng cột
        int[] widths = {90, 250, 80, 115, 120, 120, 200};
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
        for (NganhTuyenSinh n : dao.getAll()) addRow(n);
    }

    private void searchData() {
        String kw = txtSearch.getText().trim().toLowerCase();
        tableModel.setRowCount(0);
        for (NganhTuyenSinh n : dao.getAll()) {
            if (n.getMaNganh().toLowerCase().contains(kw)
                    || n.getTenNganh().toLowerCase().contains(kw)) {
                addRow(n);
            }
        }
    }

    private void addRow(NganhTuyenSinh n) {
        tableModel.addRow(new Object[]{
            n.getMaNganh(), n.getTenNganh(), n.getChiTieu(),
            n.getSoTrungTuyen() + " / " + n.getChiTieu(),
            n.getDiemChuanTam(), n.getKhoiXetTuyen(), n.getMoTa()
        });
    }

    private void fillFormFromRow(int row) {
        txtMaNganh.setText(tableModel.getValueAt(row, 0).toString());
        txtMaNganh.setEditable(false);
        txtMaNganh.setBackground(Color.WHITE);
        txtMaNganh.setDisabledTextColor(UITheme.TEXT_PRIMARY);
        txtTenNganh.setText(tableModel.getValueAt(row, 1).toString());
        txtChiTieu.setText(tableModel.getValueAt(row, 2).toString());
        txtDiemChuan.setText(tableModel.getValueAt(row, 4).toString());
        Object khoi = tableModel.getValueAt(row, 5);
        txtKhoiXetTuyen.setText(khoi == null ? "" : khoi.toString());
        Object moTa = tableModel.getValueAt(row, 6);
        txtMoTa.setText(moTa == null ? "" : moTa.toString());
    }

    private void clearForm() {
        txtMaNganh.setText(""); txtMaNganh.setEnabled(true);
        txtTenNganh.setText(""); txtChiTieu.setText(""); txtDiemChuan.setText("");
        txtKhoiXetTuyen.setText(""); txtMoTa.setText("");
        table.clearSelection();
    }

    // ── Validate ─────────────────────────────────────────────────────────────
    private boolean validateForm() {
        if (ValidationUtil.isEmpty(txtMaNganh.getText()) || ValidationUtil.isEmpty(txtTenNganh.getText())) {
            AlertUtil.error(this, "Mã ngành và Tên ngành không được để trống."); return false;
        }
        if (!ValidationUtil.isNonNegativeInteger(txtChiTieu.getText())) {
            AlertUtil.error(this, "Chỉ tiêu phải là số nguyên không âm."); return false;
        }
        try { Integer.parseInt(txtChiTieu.getText().trim()); }
        catch (NumberFormatException ex) { AlertUtil.error(this, "Chỉ tiêu phải là số nguyên."); return false; }
        if (!ValidationUtil.isNonNegativeNumber(txtDiemChuan.getText())) {
            AlertUtil.error(this, "Điểm chuẩn tạm phải là số không âm."); return false;
        }
        try { Double.parseDouble(txtDiemChuan.getText().trim()); }
        catch (NumberFormatException ex) { AlertUtil.error(this, "Điểm chuẩn tạm phải là số."); return false; }
        return true;
    }

    private String getKhoiXetTuyen() {
        String k = txtKhoiXetTuyen.getText().trim().replace(';', ',');
        return k.equalsIgnoreCase("Chưa chọn") ? "" : k;
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────
    private void themNganh() {
        if (!validateForm()) return;
        try {
            NganhTuyenSinh n = new NganhTuyenSinh(
                txtMaNganh.getText().trim(), txtTenNganh.getText().trim(),
                Integer.parseInt(txtChiTieu.getText().trim()),
                Double.parseDouble(txtDiemChuan.getText().trim()),
                getKhoiXetTuyen(), txtMoTa.getText().trim()
            );
            if (dao.insert(n)) { AlertUtil.info(this, "Thêm ngành thành công."); loadData(); clearForm(); }
        } catch (Exception ex) { AlertUtil.error(this, "Lỗi: " + ex.getMessage()); }
    }

    private void suaNganh() {
        if (table.getSelectedRow() == -1) { AlertUtil.error(this, "Vui lòng chọn 1 ngành để sửa."); return; }
        if (!validateForm()) return;
        try {
            NganhTuyenSinh n = new NganhTuyenSinh(
                txtMaNganh.getText().trim(), txtTenNganh.getText().trim(),
                Integer.parseInt(txtChiTieu.getText().trim()),
                Double.parseDouble(txtDiemChuan.getText().trim()),
                getKhoiXetTuyen(), txtMoTa.getText().trim()
            );
            if (dao.update(n)) { AlertUtil.info(this, "Cập nhật thành công."); loadData(); clearForm(); }
        } catch (Exception ex) { AlertUtil.error(this, "Lỗi: " + ex.getMessage()); }
    }

    private void xoaNganh() {
        if (table.getSelectedRow() == -1) { AlertUtil.error(this, "Vui lòng chọn 1 ngành để xóa."); return; }
        if (dao.hasApplications(txtMaNganh.getText().trim())) {
            AlertUtil.error(this, "Không thể xóa ngành đang có hồ sơ đăng ký. Hãy ngừng sử dụng ngành thay vì xóa.");
            return;
        }
        if (!AlertUtil.confirm(this, "Bạn có chắc muốn xóa ngành này?")) return;
        try {
            if (dao.delete(txtMaNganh.getText().trim())) {
                AlertUtil.info(this, "Xóa thành công."); loadData(); clearForm();
            }
        } catch (Exception ex) {
            AlertUtil.error(this, "Không thể xóa (ngành đang được sử dụng trong hồ sơ):\n" + ex.getMessage());
        }
    }
}
