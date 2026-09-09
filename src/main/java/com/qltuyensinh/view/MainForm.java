package com.qltuyensinh.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.qltuyensinh.model.NhanVien;
import com.qltuyensinh.util.AlertUtil;
import com.qltuyensinh.util.BackupUtil;
import com.qltuyensinh.util.LogoUtil;
import com.qltuyensinh.util.UITheme;

public class MainForm extends JFrame {

    private JLabel lblClock;
    private JLabel lblStatusInfo;
    private Timer clockTimer;
    private JButton activeNavigationButton;
    private JPanel admissionActionPanel;

    public MainForm(NhanVien nhanVien) {
        setTitle("Hệ thống Quản lý Tuyển sinh – " + nhanVien.getHoTen()
                + " (" + nhanVien.getVaiTro() + ")");
        setSize(1320, 800);
        setMinimumSize(new Dimension(1150, 700));
        setResizable(true);
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(UITheme.BG_PAGE);

        // ── Header ───────────────────────────────────────────────────────────
        add(buildHeader(nhanVien), BorderLayout.NORTH);

        // ── Tab panel ────────────────────────────────────────────────────────
        NganhPanel nganhPanel  = new NganhPanel();
        ThiSinhPanel thiSinhPanel = new ThiSinhPanel();
        HoSoPanel hoSoPanel    = new HoSoPanel(nhanVien.getTaiKhoan(), nhanVien.getVaiTro());
        ThongKePanel thongKePanel = new ThongKePanel();
        AuditLogPanel auditLogPanel = "Admin".equalsIgnoreCase(nhanVien.getVaiTro())
                ? new AuditLogPanel() : null;
        DashboardPanel dashboardPanel = new DashboardPanel();
        NhanVienPanel nhanVienPanel = "Admin".equalsIgnoreCase(nhanVien.getVaiTro())
                ? new NhanVienPanel() : null;

        CardLayout cardLayout = new CardLayout();
        JPanel content = new JPanel(cardLayout);
        content.setBackground(UITheme.BG_PAGE);
        content.add(dashboardPanel, "dashboard");
        content.add(nganhPanel, "nganh");
        content.add(thiSinhPanel, "thisinh");
        content.add(hoSoPanel, "hoso");
        content.add(thongKePanel, "thongke");
        if (nhanVienPanel != null) content.add(nhanVienPanel, "nhanvien");
        if (auditLogPanel != null) content.add(auditLogPanel, "auditlog");
        add(buildNavigation(cardLayout, content, dashboardPanel, hoSoPanel, thongKePanel,
                nhanVienPanel, auditLogPanel), BorderLayout.WEST);
        add(content, BorderLayout.CENTER);

        // ── Footer / status bar ───────────────────────────────────────────────
        admissionActionPanel = hoSoPanel.getAdmissionActionPanel();
        admissionActionPanel.setVisible(false);
        JPanel bottomArea = new JPanel(new BorderLayout());
        bottomArea.setOpaque(false);
        bottomArea.add(admissionActionPanel, BorderLayout.CENTER);
        bottomArea.add(buildFooter(nhanVien, nganhPanel, thiSinhPanel, hoSoPanel), BorderLayout.SOUTH);
        add(bottomArea, BorderLayout.SOUTH);

        // ── Phân quyền ───────────────────────────────────────────────────────
        if (!"Admin".equalsIgnoreCase(nhanVien.getVaiTro())) {
            disableRestrictedButtons(nganhPanel);
            disableRestrictedButtons(thiSinhPanel);
            hoSoPanel.setAdmissionActionsEnabled("TuyenSinh".equalsIgnoreCase(nhanVien.getVaiTro()));
        }

        // ── Đồng hồ thời gian thực ───────────────────────────────────────────
        clockTimer = new Timer(1000, e -> updateClock());
        clockTimer.start();
        updateClock();
    }

    // ── Header gradient ───────────────────────────────────────────────────────
    private JPanel buildHeader(NhanVien nhanVien) {
        JPanel header = new JPanel(new BorderLayout(16, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setPaint(new GradientPaint(0, 0, UITheme.PRIMARY_DARK, getWidth(), 0, UITheme.PRIMARY_LIGHT));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setOpaque(false);
        header.setBorder(BorderFactory.createEmptyBorder(10, 22, 10, 22));

        // Logo
        ImageIcon logo = LogoUtil.load(82);
        if (logo != null) {
            JLabel logoLbl = new JLabel(logo);
            logoLbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            header.add(logoLbl, BorderLayout.WEST);
        }

        // Tiêu đề
        JPanel titles = new JPanel();
        titles.setOpaque(false);
        titles.setLayout(new BoxLayout(titles, BoxLayout.Y_AXIS));
        JLabel lblSchool = new JLabel("TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á");
        lblSchool.setForeground(Color.WHITE);
        lblSchool.setFont(new Font(UITheme.FONT_NAME, Font.BOLD, 19));
        JLabel lblSys = new JLabel("HỆ THỐNG QUẢN LÝ TUYỂN SINH  •  Xin chào, " + nhanVien.getHoTen()
                + "  [" + nhanVien.getVaiTro() + "]");
        lblSys.setForeground(new Color(200, 220, 245));
        lblSys.setFont(new Font(UITheme.FONT_NAME, Font.PLAIN, 12));
        titles.add(lblSchool);
        titles.add(Box.createVerticalStrut(4));
        titles.add(lblSys);
        header.add(titles, BorderLayout.CENTER);

        // Đồng hồ bên phải
        lblClock = new JLabel("", SwingConstants.RIGHT);
        lblClock.setFont(new Font(UITheme.FONT_NAME, Font.BOLD, 13));
        lblClock.setForeground(new Color(190, 215, 245));
        header.add(lblClock, BorderLayout.EAST);

        return header;
    }

    // ── Menu điều hướng dọc ─────────────────────────────────────────────────
    private JPanel buildNavigation(CardLayout cardLayout, JPanel content, DashboardPanel dashboardPanel,
                                   HoSoPanel hoSoPanel, ThongKePanel thongKePanel,
                                   NhanVienPanel nhanVienPanel, AuditLogPanel auditLogPanel) {
        JPanel nav = new JPanel(new BorderLayout());
        // Thu gọn menu để dành thêm không gian cho bảng hồ sơ và phần xét duyệt.
        nav.setPreferredSize(new Dimension(205, 0));
        nav.setBackground(new Color(31, 57, 116));
        nav.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(20, 42, 91)));

        JLabel title = new JLabel("DANH MỤC", SwingConstants.CENTER);
        title.setText("QUẢN LÝ TUYỂN SINH");
        title.setFont(new Font(UITheme.FONT_NAME, Font.BOLD, 13));
        title.setForeground(Color.WHITE);
        title.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(70, 98, 160)),
                BorderFactory.createEmptyBorder(22, 8, 20, 8)));
        nav.add(title, BorderLayout.NORTH);

        JPanel buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.Y_AXIS));
        buttons.setOpaque(false);
        buttons.setBorder(BorderFactory.createEmptyBorder(18, 10, 12, 10));
        JButton dashboardButton = navigationButton("Trang chủ", "dashboard", cardLayout, content, dashboardPanel);
        addNavigationItem(buttons, dashboardButton);
        setActiveNavigationButton(dashboardButton);
        addNavigationItem(buttons, navigationButton("Ngành tuyển sinh", "nganh", cardLayout, content));
        addNavigationItem(buttons, navigationButton("Thí sinh", "thisinh", cardLayout, content));
        addNavigationItem(buttons, navigationButton("Hồ sơ đăng ký", "hoso", cardLayout, content, hoSoPanel));
        addNavigationItem(buttons, navigationButton("Thống kê", "thongke", cardLayout, content, thongKePanel));
        if (nhanVienPanel != null) addNavigationItem(buttons, navigationButton("Nhân viên", "nhanvien", cardLayout, content));
        if (auditLogPanel != null) {
            addNavigationItem(buttons, navigationButton("Nhật ký hệ thống", "auditlog",
                    cardLayout, content, auditLogPanel));
        }
        nav.add(buttons, BorderLayout.CENTER);
        return nav;
    }

    private void addNavigationItem(JPanel panel, JButton button) {
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(button);
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        separator.setForeground(new Color(70, 98, 160));
        separator.setBackground(new Color(70, 98, 160));
        separator.setOpaque(true);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        panel.add(separator);
        panel.add(Box.createVerticalStrut(4));
    }

    private JButton navigationButton(String text, String card, CardLayout layout, JPanel content, Object... refreshTarget) {
        JButton button = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean active = Boolean.TRUE.equals(getClientProperty("active"));
                boolean hover = getModel().isRollover();
                if (active || hover) {
                    g2.setColor(active ? new Color(52, 88, 155) : new Color(42, 73, 137));
                    g2.fillRoundRect(2, 2, getWidth() - 10, getHeight() - 4, 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setFont(new Font(UITheme.FONT_NAME, Font.BOLD, 13));
        button.setForeground(new Color(235, 242, 255));
        button.setBackground(new Color(31, 57, 116));
        button.setFocusPainted(false);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(13, 18, 13, 10));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        button.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            setActiveNavigationButton(button);
            layout.show(content, card);
            if (admissionActionPanel != null) {
                admissionActionPanel.setVisible("hoso".equals(card));
                admissionActionPanel.getParent().revalidate();
                admissionActionPanel.getParent().repaint();
            }
            if (refreshTarget.length > 0 && refreshTarget[0] instanceof HoSoPanel)
                ((HoSoPanel) refreshTarget[0]).refreshData();
            if (refreshTarget.length > 0 && refreshTarget[0] instanceof ThongKePanel)
                ((ThongKePanel) refreshTarget[0]).refreshData();
            if (refreshTarget.length > 0 && refreshTarget[0] instanceof DashboardPanel)
                ((DashboardPanel) refreshTarget[0]).refreshData();
            if (refreshTarget.length > 0 && refreshTarget[0] instanceof AuditLogPanel)
                ((AuditLogPanel) refreshTarget[0]).refreshData();
        });
        return button;
    }

    private void setActiveNavigationButton(JButton button) {
        if (activeNavigationButton != null) {
            activeNavigationButton.putClientProperty("active", Boolean.FALSE);
            activeNavigationButton.repaint();
        }
        activeNavigationButton = button;
        if (button != null) {
            button.putClientProperty("active", Boolean.TRUE);
            button.repaint();
        }
    }

    // ── Footer / status bar ──────────────────────────────────────────────────
    private JPanel buildFooter(NhanVien nhanVien,
                               NganhPanel nganhPanel, ThiSinhPanel thiSinhPanel, HoSoPanel hoSoPanel) {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(new Color(240, 243, 248));
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 210, 225)),
            BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));

        // Nút (phải)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        if ("Admin".equalsIgnoreCase(nhanVien.getVaiTro())) {
            JButton btnBackup  = UITheme.secondaryButton("Sao lưu CSDL");
            JButton btnRestore = UITheme.warningButton("Khôi phục CSDL");
            btnBackup.addActionListener(e  -> backupDatabase());
            btnRestore.addActionListener(e -> restoreDatabase());
            btnPanel.add(btnBackup);
            btnPanel.add(btnRestore);
        }

        JButton btnLogout = UITheme.dangerButton("Đăng xuất");
        btnLogout.addActionListener(e -> {
            clockTimer.stop();
            dispose();
            new LoginForm().setVisible(true);
        });
        btnPanel.add(btnLogout);
        footer.add(btnPanel, BorderLayout.EAST);

        return footer;
    }

    private void updateClock() {
        String now = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("HH:mm:ss  |  dd/MM/yyyy"));
        if (lblClock != null) lblClock.setText(now + "  ");
    }

    // ── Phân quyền ───────────────────────────────────────────────────────────
    private void disableRestrictedButtons(Container container) {
        for (Component c : container.getComponents()) {
            if (c instanceof JButton btn) {
                String t = btn.getText().toLowerCase();
                if (t.contains("xóa") || t.contains("xoa") || t.contains("duyệt")
                        || t.contains("duyet") || t.contains("tự động") || t.contains("tu dong")) {
                    btn.setEnabled(false);
                }
            }
            if (c instanceof Container child) disableRestrictedButtons(child);
        }
    }

    // ── Backup / Restore ─────────────────────────────────────────────────────
    private void backupDatabase() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu bản sao CSDL");
        chooser.setSelectedFile(new File("qltuyensinh-backup.sql"));
        chooser.setFileFilter(new FileNameExtensionFilter("SQL backup (*.sql)", "sql"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                BackupUtil.backup(chooser.getSelectedFile());
                AlertUtil.info(this, "Sao lưu CSDL thành công.");
            } catch (Exception ex) {
                AlertUtil.error(this, "Không thể sao lưu: " + ex.getMessage());
            }
        }
    }

    private void restoreDatabase() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn file backup để khôi phục");
        chooser.setFileFilter(new FileNameExtensionFilter("SQL backup (*.sql)", "sql"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION
                && AlertUtil.confirm(this, "Khôi phục sẽ ghi đè dữ liệu hiện tại. Tiếp tục?")) {
            try {
                BackupUtil.restore(chooser.getSelectedFile());
                AlertUtil.info(this, "Khôi phục CSDL thành công. Hãy tải lại các tab.");
            } catch (Exception ex) {
                AlertUtil.error(this, "Không thể khôi phục: " + ex.getMessage());
            }
        }
    }
}
