package com.qltuyensinh.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.qltuyensinh.dao.AuditLogDAO;
import com.qltuyensinh.model.AuditLog;
import com.qltuyensinh.util.AlertUtil;
import com.qltuyensinh.util.UITheme;

public class AuditLogPanel extends JPanel {
    private final AuditLogDAO dao = new AuditLogDAO();
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Mã", "Tài khoản", "Hành động", "Chi tiết", "Thời gian"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public AuditLogPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BG_PAGE);
        setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JLabel title = new JLabel("NHẬT KÝ HỆ THỐNG", SwingConstants.CENTER);
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.PRIMARY);
        add(title, BorderLayout.NORTH);

        table.setFont(UITheme.FONT_TABLE);
        table.setRowHeight(30);
        table.setAutoCreateRowSorter(true);
        table.setSelectionBackground(UITheme.BG_TABLE_SEL);
        table.setSelectionForeground(UITheme.FG_TABLE_SEL);
        table.getTableHeader().setDefaultRenderer(UITheme.headerRenderer());
        table.setDefaultRenderer(Object.class, UITheme.zebraRenderer());
        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(1).setPreferredWidth(140);
        table.getColumnModel().getColumn(2).setPreferredWidth(190);
        table.getColumnModel().getColumn(3).setPreferredWidth(480);
        table.getColumnModel().getColumn(4).setPreferredWidth(180);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(180, 200, 225)));
        add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        footer.setOpaque(false);
        JButton refresh = UITheme.primaryButton("Làm mới nhật ký");
        refresh.addActionListener(e -> refreshData());
        footer.add(refresh);
        add(footer, BorderLayout.SOUTH);

        refreshData();
    }

    public void refreshData() {
        try {
            List<AuditLog> logs = dao.getAll();
            tableModel.setRowCount(0);
            for (AuditLog log : logs) {
                tableModel.addRow(new Object[]{
                        log.getId(),
                        log.getTaiKhoan() == null ? "-" : log.getTaiKhoan(),
                        log.getHanhDong(),
                        log.getChiTiet() == null ? "" : log.getChiTiet(),
                        log.getThoiGian()
                });
            }
        } catch (RuntimeException ex) {
            AlertUtil.error(this, ex.getMessage());
        }
    }
}
