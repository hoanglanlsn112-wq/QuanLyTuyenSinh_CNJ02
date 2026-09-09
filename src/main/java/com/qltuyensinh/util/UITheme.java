package com.qltuyensinh.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.AbstractBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

/**
 * UITheme – class tập trung mọi màu sắc, font, và factory method tạo component đẹp.
 * Dùng các method tĩnh ở bất kỳ đâu trong ứng dụng.
 */
public class UITheme {

    // ── Bảng màu chủ đạo ─────────────────────────────────────────────────────
    public static final Color PRIMARY       = new Color(26, 77, 130);   // xanh navy đậm
    public static final Color PRIMARY_DARK  = new Color(18, 54, 95);    // xanh navy tối
    public static final Color PRIMARY_LIGHT = new Color(52, 120, 185);  // xanh lam nhạt
    public static final Color ACCENT        = new Color(0, 150, 199);   // xanh cyan accent
    public static final Color SUCCESS       = new Color(34, 139, 68);   // xanh lá
    public static final Color SUCCESS_DARK  = new Color(22, 101, 48);
    public static final Color DANGER        = new Color(192, 50, 50);   // đỏ
    public static final Color DANGER_DARK   = new Color(145, 30, 30);
    public static final Color WARNING       = new Color(210, 120, 0);   // cam
    public static final Color WARNING_DARK  = new Color(160, 88, 0);
    public static final Color SECONDARY     = new Color(100, 110, 120); // xám
    public static final Color SECONDARY_DARK= new Color(70, 80, 90);

    public static final Color BG_PAGE       = new Color(236, 241, 247); // nền trang
    public static final Color BG_CARD       = Color.WHITE;
    public static final Color BG_HEADER     = PRIMARY;
    public static final Color BG_TABLE_ODD  = new Color(248, 251, 255);
    public static final Color BG_TABLE_EVEN = Color.WHITE;
    public static final Color BG_TABLE_SEL  = new Color(173, 214, 255);
    public static final Color FG_TABLE_SEL  = new Color(10, 40, 80);

    public static final Color TEXT_PRIMARY   = new Color(30, 40, 55);
    public static final Color TEXT_SECONDARY = new Color(90, 100, 115);
    public static final Color TEXT_HEADER    = Color.WHITE;

    // ── Font ─────────────────────────────────────────────────────────────────
    public static final String FONT_NAME  = "Segoe UI";
    public static final Font FONT_BODY    = new Font(FONT_NAME, Font.PLAIN, 13);
    public static final Font FONT_BOLD    = new Font(FONT_NAME, Font.BOLD, 13);
    public static final Font FONT_SMALL   = new Font(FONT_NAME, Font.PLAIN, 11);
    public static final Font FONT_TITLE   = new Font(FONT_NAME, Font.BOLD, 18);
    public static final Font FONT_HEADING = new Font(FONT_NAME, Font.BOLD, 15);
    public static final Font FONT_LABEL   = new Font(FONT_NAME, Font.BOLD, 12);
    public static final Font FONT_TABLE   = new Font(FONT_NAME, Font.PLAIN, 12);
    public static final Font FONT_HEADER  = new Font(FONT_NAME, Font.BOLD, 12);

    // ── Border helper ─────────────────────────────────────────────────────────
    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(185, 202, 224), 1, true),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        );
    }

    public static Border sectionBorder(String title) {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(145, 174, 211), 1),
                " " + title + " ",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                FONT_LABEL, PRIMARY
            ),
            BorderFactory.createEmptyBorder(4, 8, 6, 8)
        );
    }

    // ── Nút bấm factory ──────────────────────────────────────────────────────

    /** Nút chính – xanh navy */
    public static JButton primaryButton(String text) {
        return styledButton(text, PRIMARY, PRIMARY_DARK, Color.WHITE);
    }

    /** Nút nguy hiểm – đỏ */
    public static JButton dangerButton(String text) {
        return styledButton(text, DANGER, DANGER_DARK, Color.WHITE);
    }

    /** Nút thành công – xanh lá */
    public static JButton successButton(String text) {
        return styledButton(text, SUCCESS, SUCCESS_DARK, Color.WHITE);
    }

    /** Nút cảnh báo – cam */
    public static JButton warningButton(String text) {
        return styledButton(text, WARNING, WARNING_DARK, Color.WHITE);
    }

    /** Nút phụ – xám */
    public static JButton secondaryButton(String text) {
        return styledButton(text, SECONDARY, SECONDARY_DARK, Color.WHITE);
    }

    private static JButton styledButton(String text, Color bg, Color hover, Color fg) {
        JButton btn = new JButton(text) {
            private boolean hovered = false;
            {
                addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true; repaint(); }
                    public void mouseExited(java.awt.event.MouseEvent e)  { hovered = false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isEnabled() ? (hovered ? hover : bg) : new Color(180, 185, 192));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(fg);
        btn.setFont(FONT_BOLD);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0, 14, 0, 14));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 28, 38));
        btn.setMinimumSize(btn.getPreferredSize());
        return btn;
    }

    // ── Bảng (JTable) ────────────────────────────────────────────────────────

    /** Áp dụng style zebra stripe + header đẹp cho JTable. */
    public static void styleTable(JTable table) {
        table.setFont(FONT_TABLE);
        table.setRowHeight(30);
        table.setFillsViewportHeight(true);
        // Giữ thứ tự do DAO trả về; các màn hình hồ sơ cần thứ tự mã HS ổn định.
        table.setAutoCreateRowSorter(false);
        // Đường kẻ mảnh giúp phân biệt rõ các cột và dòng, đặc biệt với
        // những bảng có nhiều trường dữ liệu dài.
        table.setShowGrid(true);
        table.setGridColor(new Color(218, 227, 238));
        table.setIntercellSpacing(new Dimension(1, 1));
        table.setSelectionBackground(BG_TABLE_SEL);
        table.setSelectionForeground(FG_TABLE_SEL);
        table.setBackground(BG_TABLE_EVEN);
        table.getTableHeader().setDefaultRenderer(headerRenderer());
        table.getTableHeader().setFont(FONT_HEADER);
        table.getTableHeader().setBackground(PRIMARY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 34));
        table.getTableHeader().setReorderingAllowed(false);
        table.setDefaultRenderer(Object.class, zebraRenderer());
    }

    /** Thanh cuộn phẳng, không có nút tam giác mặc định của Swing. */
    public static void styleScrollPane(JScrollPane scroll) {
        scroll.setBackground(Color.WHITE);
        scroll.getViewport().setBackground(Color.WHITE);
        styleScrollBar(scroll.getVerticalScrollBar());
        styleScrollBar(scroll.getHorizontalScrollBar());
    }

    private static void styleScrollBar(JScrollBar bar) {
        bar.setPreferredSize(new Dimension(12, 12));
        bar.setBackground(new Color(239, 244, 250));
        bar.setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                thumbColor = new Color(120, 164, 205);
                trackColor = new Color(239, 244, 250);
            }
            @Override protected JButton createDecreaseButton(int orientation) { return emptyScrollButton(); }
            @Override protected JButton createIncreaseButton(int orientation) { return emptyScrollButton(); }
            private JButton emptyScrollButton() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                b.setBorder(BorderFactory.createEmptyBorder());
                b.setOpaque(false);
                return b;
            }
        });
    }

    /** Đồng bộ combobox với ô nhập và nút trong toàn bộ ứng dụng. */
    public static void styleComboBox(JComboBox<?> combo) {
        combo.setFont(FONT_BODY);
        combo.setOpaque(false);
        combo.setBackground(Color.WHITE);
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 34));
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean selected, boolean focused) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, selected, focused);
                label.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
                label.setOpaque(false);
                label.setForeground(TEXT_PRIMARY);
                return label;
            }
        });
        combo.setUI(new BasicComboBoxUI() {
            @Override protected JButton createArrowButton() {
                // Chỉ vẽ mũi tên, không vẽ nền hoặc đường kẻ ngăn phía sau.
                JButton arrow = new JButton() {
                    @Override protected void paintComponent(Graphics g) {
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(PRIMARY_DARK);
                        int cx = getWidth() / 2;
                        int cy = getHeight() / 2;
                        g2.fillPolygon(new int[]{cx - 5, cx + 5, cx}, new int[]{cy - 2, cy - 2, cy + 4}, 3);
                        g2.dispose();
                    }
                };
                arrow.setPreferredSize(new Dimension(24, 1));
                arrow.setMinimumSize(new Dimension(24, 1));
                arrow.setMaximumSize(new Dimension(24, Integer.MAX_VALUE));
                arrow.setOpaque(false);
                arrow.setContentAreaFilled(false);
                arrow.setBorder(BorderFactory.createEmptyBorder());
                return arrow;
            }
        });
        combo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedFieldBorder(new Color(180, 200, 225), 1, 10),
                BorderFactory.createEmptyBorder(1, 6, 1, 6)));
    }

    public static DefaultTableCellRenderer headerRenderer() {
        DefaultTableCellRenderer r = new DefaultTableCellRenderer();
        r.setOpaque(true);
        r.setBackground(PRIMARY);
        r.setForeground(Color.WHITE);
        r.setFont(FONT_HEADER);
        // Căn trái để tiêu đề thẳng hàng với nội dung bên dưới.
        r.setHorizontalAlignment(SwingConstants.LEFT);
        r.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 1, PRIMARY_DARK));
        return r;
    }

    public static DefaultTableCellRenderer zebraRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? BG_TABLE_EVEN : BG_TABLE_ODD);
                    setForeground(TEXT_PRIMARY);
                }
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                return this;
            }
        };
    }

    // ── TextField đẹp ────────────────────────────────────────────────────────
    public static JTextField styledTextField(int cols) {
        JTextField tf = new JTextField(cols);
        tf.setFont(FONT_BODY);
        tf.setOpaque(false);
        tf.setBorder(BorderFactory.createCompoundBorder(
            new RoundedFieldBorder(new Color(180, 200, 225), 1, 10),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return tf;
    }

    public static JPasswordField styledPasswordField(int cols) {
        JPasswordField pf = new JPasswordField(cols);
        pf.setFont(FONT_BODY);
        pf.setOpaque(false);
        pf.setBorder(BorderFactory.createCompoundBorder(
            new RoundedFieldBorder(new Color(180, 200, 225), 1, 10),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        return pf;
    }

    /** Viền bo tròn thật, không phụ thuộc vào Look and Feel của Swing. */
    private static final class RoundedFieldBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int radius;

        private RoundedFieldBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override public Insets getBorderInsets(Component c) {
            return new Insets(thickness + 1, thickness + 1, thickness + 1, thickness + 1);
        }

        @Override public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = insets.left = insets.bottom = insets.right = thickness + 1;
            return insets;
        }

        @Override public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x + thickness / 2, y + thickness / 2,
                    width - thickness, height - thickness, radius, radius);
            g2.dispose();
        }
    }

    // ── Label helper ─────────────────────────────────────────────────────────
    public static JLabel sectionTitle(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_HEADING);
        lbl.setForeground(PRIMARY);
        return lbl;
    }

    public static JLabel fieldLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_LABEL);
        lbl.setForeground(TEXT_PRIMARY);
        return lbl;
    }

    /** Nhãn hiển thị giá trị trong biểu mẫu, dùng cùng viền bo tròn với ô nhập liệu. */
    public static JLabel styledValueLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_BODY);
        lbl.setForeground(TEXT_PRIMARY);
        lbl.setOpaque(true);
        lbl.setBackground(Color.WHITE);
        lbl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 200, 225), 1, true),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return lbl;
    }

    public static JLabel valueLabel() {
        JLabel lbl = new JLabel("-");
        lbl.setFont(FONT_BODY);
        lbl.setForeground(PRIMARY);
        return lbl;
    }

    // ── Summary Card (dùng trong ThongKePanel) ───────────────────────────────
    public static JPanel summaryCard(String title, String value, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 6)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 6, getHeight(), 12, 12);
                g2.fillRect(0, 0, 6, getHeight());
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 228, 240), 1, true),
            BorderFactory.createEmptyBorder(12, 18, 12, 18)
        ));
        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font(FONT_NAME, Font.BOLD, 28));
        lblValue.setForeground(accent);
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(FONT_SMALL);
        lblTitle.setForeground(TEXT_SECONDARY);
        card.add(lblValue, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.SOUTH);
        return card;
    }

    /** Áp dụng Look and Feel Nimbus nếu có, fallback về System. */
    public static void applyLookAndFeel() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    // Ghi đè màu Nimbus
                    UIManager.put("nimbusBase", PRIMARY);
                    UIManager.put("nimbusBlueGrey", new Color(180, 195, 215));
                    UIManager.put("control", BG_PAGE);
                    UIManager.put("Component.arc", 10);
                    UIManager.put("TextField.arc", 10);
                    UIManager.put("ComboBox.arc", 10);
                    UIManager.put("Button.arc", 10);
                    return;
                }
            }
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Giữ nguyên L&F mặc định nếu lỗi
        }
    }
}
