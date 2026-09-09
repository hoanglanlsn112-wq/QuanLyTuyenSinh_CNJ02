package com.qltuyensinh.view;

import com.qltuyensinh.dao.NhanVienDAO;
import com.qltuyensinh.model.NhanVien;
import com.qltuyensinh.util.AlertUtil;
import com.qltuyensinh.util.LogoUtil;
import com.qltuyensinh.util.UITheme;

import javax.swing.*;
import java.awt.*;
import java.util.prefs.Preferences;

public class LoginForm extends JFrame {
    private final JTextField txtTaiKhoan = new JTextField();
    private final JPasswordField txtMatKhau = new JPasswordField();
    private final JCheckBox chkGhiNho = new JCheckBox("Ghi nhớ mật khẩu");
    private JButton loginButton;
    private final Preferences loginPreferences =
            Preferences.userNodeForPackage(LoginForm.class).node("login");

    public LoginForm() {
        setTitle("Đăng nhập - Hệ thống quản lý tuyển sinh");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));
        setSize(1120, 700);
        setLocationRelativeTo(null);
        setResizable(true);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UITheme.BG_PAGE);
        setContentPane(root);
        root.add(buildBrandPanel(), BorderLayout.WEST);
        root.add(buildFormPanel(), BorderLayout.CENTER);

        txtTaiKhoan.addActionListener(e -> txtMatKhau.requestFocusInWindow());
        txtMatKhau.addActionListener(e -> doLogin());
        loadRememberedLogin();
    }

    private JPanel buildBrandPanel() {
        JPanel brand = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0, 0, UITheme.PRIMARY_DARK,
                        getWidth(), getHeight(), UITheme.PRIMARY_LIGHT));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(255, 255, 255, 22));
                g2.fillOval(-120, getHeight() - 260, 360, 360);
                g2.fillOval(getWidth() - 130, -100, 260, 260);
                g2.dispose();
            }
        };
        brand.setOpaque(false);
        brand.setPreferredSize(new Dimension(390, 0));
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.weightx = 1; c.anchor = GridBagConstraints.CENTER;

        JLabel logo = new JLabel(LogoUtil.load(132));
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 0; c.insets = new Insets(0, 20, 30, 20); brand.add(logo, c);

        JLabel school = text("TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á", 15, Font.BOLD, Color.WHITE);
        school.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 1; c.insets = new Insets(0, 28, 10, 28); brand.add(school, c);

        JLabel line = text("HỆ THỐNG QUẢN LÝ TUYỂN SINH", 13, Font.PLAIN, new Color(220, 240, 255));
        line.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 2; c.insets = new Insets(0, 20, 26, 20); brand.add(line, c);

        JLabel description = text("Quản lý hồ sơ xét tuyển nhanh chóng, chính xác và hiệu quả", 12,
                Font.PLAIN, new Color(225, 238, 250));
        description.setHorizontalAlignment(SwingConstants.CENTER);
        description.setPreferredSize(new Dimension(330, 30));
        c.gridy = 3; c.insets = new Insets(0, 25, 0, 25); brand.add(description, c);
        return brand;
    }

    private JPanel buildFormPanel() {
        JPanel area = new JPanel(new GridBagLayout());
        area.setBackground(new Color(247, 249, 252));
        RoundedPanel card = new RoundedPanel(Color.WHITE, 22);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createEmptyBorder(42, 48, 38, 48));
        card.setPreferredSize(new Dimension(470, 500));

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL; c.anchor = GridBagConstraints.WEST;

        c.gridy = 0; c.insets = new Insets(0, 0, 10, 0); card.add(text("CỔNG NHÂN VIÊN", 12, Font.BOLD, UITheme.ACCENT), c);
        c.gridy = 1; c.insets = new Insets(0, 0, 8, 0); card.add(text("Chào mừng trở lại", 29, Font.BOLD, UITheme.TEXT_PRIMARY), c);
        c.gridy = 2; c.insets = new Insets(0, 0, 30, 0); card.add(text("Đăng nhập để tiếp tục làm việc với hệ thống", 13, Font.PLAIN, UITheme.TEXT_SECONDARY), c);
        c.gridy = 3; c.insets = new Insets(0, 0, 8, 0); card.add(text("Tài khoản", 13, Font.BOLD, UITheme.TEXT_PRIMARY), c);

        styleField(txtTaiKhoan, "Nhập tài khoản");
        c.gridy = 4; c.insets = new Insets(0, 0, 18, 0); card.add(txtTaiKhoan, c);
        c.gridy = 5; c.insets = new Insets(0, 0, 8, 0); card.add(text("Mật khẩu", 13, Font.BOLD, UITheme.TEXT_PRIMARY), c);

        styleField(txtMatKhau, "Nhập mật khẩu");
        c.gridy = 6; c.insets = new Insets(0, 0, 8, 0); card.add(txtMatKhau, c);

        JPanel options = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        options.setOpaque(false);
        chkGhiNho.setFont(new Font(UITheme.FONT_NAME, Font.PLAIN, 12));
        chkGhiNho.setForeground(UITheme.TEXT_SECONDARY);
        chkGhiNho.setOpaque(false);
        chkGhiNho.setFocusPainted(false);

        JCheckBox show = new JCheckBox("Hiện mật khẩu");
        show.setFont(new Font(UITheme.FONT_NAME, Font.PLAIN, 12));
        show.setForeground(UITheme.TEXT_SECONDARY); show.setOpaque(false); show.setFocusPainted(false);
        show.addActionListener(e -> txtMatKhau.setEchoChar(show.isSelected() ? (char) 0 : '•'));
        options.add(chkGhiNho);
        options.add(Box.createHorizontalStrut(18));
        options.add(show);
        c.gridy = 7; c.insets = new Insets(0, -4, 18, 0); card.add(options, c);

        loginButton = new RoundedButton("Đăng nhập", UITheme.PRIMARY, UITheme.PRIMARY_DARK);
        loginButton.setFont(new Font(UITheme.FONT_NAME, Font.BOLD, 15));
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(0, 46));
        loginButton.addActionListener(e -> doLogin());
        c.gridy = 8; c.insets = new Insets(0, 0, 18, 0); card.add(loginButton, c);

        JLabel hint = text("Tài khoản mẫu: admin  •  Mật khẩu: admin123", 11, Font.PLAIN, new Color(145, 153, 165));
        hint.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridy = 9; c.insets = new Insets(0, 0, 0, 0); card.add(hint, c);

        GridBagConstraints wrapper = new GridBagConstraints();
        wrapper.weightx = 1; wrapper.weighty = 1; wrapper.anchor = GridBagConstraints.CENTER;
        area.add(card, wrapper);
        return area;
    }

    private void styleField(JTextField field, String tooltip) {
        field.setFont(new Font(UITheme.FONT_NAME, Font.PLAIN, 14));
        field.setForeground(UITheme.TEXT_PRIMARY); field.setBackground(new Color(250, 252, 255));
        field.setCaretColor(UITheme.PRIMARY); field.setToolTipText(tooltip);
        field.setPreferredSize(new Dimension(0, 44));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(208, 220, 235), 1, true),
                BorderFactory.createEmptyBorder(6, 13, 6, 13)));
        if (field instanceof JPasswordField) ((JPasswordField) field).setEchoChar('•');
    }

    private JLabel text(String value, int size, int style, Color color) {
        String html = value.contains("\n") ? "<html><div style='text-align:center'>" + value.replace("\n", "<br>") + "</div></html>" : value;
        JLabel label = new JLabel(html);
        label.setFont(new Font(UITheme.FONT_NAME, style, size)); label.setForeground(color);
        return label;
    }

    private void doLogin() {
        if (loginButton != null && !loginButton.isEnabled()) return;
        String taiKhoan = txtTaiKhoan.getText().trim();
        String matKhau = new String(txtMatKhau.getPassword());
        if (taiKhoan.isEmpty() || matKhau.isEmpty()) {
            AlertUtil.error(this, "Vui lòng nhập đầy đủ tài khoản và mật khẩu."); return;
        }
        if (loginButton != null) {
            loginButton.setEnabled(false);
            loginButton.setText("Đang đăng nhập...");
        }

        SwingWorker<NhanVien, Void> worker = new SwingWorker<>() {
            @Override
            protected NhanVien doInBackground() {
                return new NhanVienDAO().login(taiKhoan, matKhau);
            }

            @Override
            protected void done() {
                try {
                    NhanVien nv = get();
                    if (nv != null) {
                        saveRememberedLogin(taiKhoan, matKhau);
                        new MainForm(nv).setVisible(true);
                        dispose();
                    } else {
                        AlertUtil.error(LoginForm.this, "Sai tài khoản hoặc mật khẩu.");
                    }
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
                    cause.printStackTrace();
                    String message = cause.getClass().getSimpleName() + ": " + cause.getMessage();
                    AlertUtil.error(LoginForm.this, "Không thể mở giao diện chính:\n" + message);
                } finally {
                    if (loginButton != null) {
                        loginButton.setEnabled(true);
                        loginButton.setText("Đăng nhập");
                    }
                }
            }
        };
        worker.execute();
    }

    private void loadRememberedLogin() {
        String rememberedAccount = loginPreferences.get("taiKhoan", "");
        String rememberedPassword = loginPreferences.get("matKhau", "");
        boolean remembered = !rememberedAccount.isBlank() && !rememberedPassword.isBlank();
        txtTaiKhoan.setText(rememberedAccount);
        txtMatKhau.setText(rememberedPassword);
        chkGhiNho.setSelected(remembered);
    }

    private void saveRememberedLogin(String taiKhoan, String matKhau) {
        if (chkGhiNho.isSelected()) {
            loginPreferences.put("taiKhoan", taiKhoan);
            loginPreferences.put("matKhau", matKhau);
        } else {
            loginPreferences.remove("taiKhoan");
            loginPreferences.remove("matKhau");
        }
    }

    private static class RoundedPanel extends JPanel {
        private final Color color; private final int radius;
        RoundedPanel(Color color, int radius) { this.color = color; this.radius = radius; setOpaque(false); }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(30, 60, 90, 18)); g2.fillRoundRect(4, 7, getWidth() - 8, getHeight() - 7, radius, radius);
            g2.setColor(color); g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 8, radius, radius);
            g2.dispose(); super.paintComponent(g);
        }
    }

    private static class RoundedButton extends JButton {
        private final Color normal, hover; private boolean hovered;
        RoundedButton(String text, Color normal, Color hover) {
            super(text); this.normal = normal; this.hover = hover;
            setBorderPainted(false); setContentAreaFilled(false); setFocusPainted(false); setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true; repaint(); }
                @Override public void mouseExited(java.awt.event.MouseEvent e) { hovered = false; repaint(); }
            });
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(hovered ? hover : normal); g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            g2.dispose(); super.paintComponent(g);
        }
    }
}
