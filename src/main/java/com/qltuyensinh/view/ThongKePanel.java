package com.qltuyensinh.view;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;

import com.qltuyensinh.dao.HoSoDangKyDAO;
import com.qltuyensinh.util.AlertUtil;
import com.qltuyensinh.util.PdfExportUtil;
import com.qltuyensinh.util.UITheme;

public class ThongKePanel extends JPanel {

    private final HoSoDangKyDAO hoSoDao = new HoSoDangKyDAO();
    private DefaultTableModel tableModel;

    // Summary card labels (cập nhật khi load data)
    private JLabel lblTongHoSo, lblChoDuyet, lblTrungTuyen, lblDaNhapHoc, lblKhongTrung;

    // Biểu đồ
    private BarChartPanel chartPanel;

    // Data hiện tại
    private List<Object[]> currentRows = new ArrayList<>();

    public ThongKePanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UITheme.BG_PAGE);
        setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        add(buildTitlePanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildFooterPanel(), BorderLayout.SOUTH);

        loadData();
    }

    // ── Tiêu đề ───────────────────────────────────────────────────────────────
    private JPanel buildTitlePanel() {
        JLabel title = new JLabel("THỐNG KÊ HỒ SƠ TUYỂN SINH THEO NGÀNH", SwingConstants.CENTER);
        title.setFont(new Font(UITheme.FONT_NAME, Font.BOLD, 17));
        title.setForeground(UITheme.PRIMARY);
        JLabel subtitle = new JLabel("Tổng quan số lượng hồ sơ và kết quả xét tuyển", SwingConstants.CENTER);
        subtitle.setFont(UITheme.FONT_SMALL);
        subtitle.setForeground(UITheme.TEXT_SECONDARY);
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.add(title, BorderLayout.CENTER);
        p.add(subtitle, BorderLayout.SOUTH);
        p.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        return p;
    }

    // ── Vùng giữa: summary cards + biểu đồ + bảng ────────────────────────────
    private JPanel buildCenterPanel() {
        JPanel center = new JPanel(new BorderLayout(0, 10));
        center.setOpaque(false);

        // Summary cards hàng trên
        center.add(buildSummaryCards(), BorderLayout.NORTH);

        // Biểu đồ + bảng (chia đôi)
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        split.setOpaque(false);
        split.setBorder(null);
        split.setResizeWeight(0.58);
        split.setDividerSize(6);

        // Biểu đồ
        chartPanel = new BarChartPanel();
        chartPanel.setBackground(UITheme.BG_CARD);
        chartPanel.setBorder(UITheme.sectionBorder("Biểu đồ hồ sơ theo ngành"));
        chartPanel.setPreferredSize(new Dimension(0, 390));
        split.setTopComponent(chartPanel);

        // Bảng
        JPanel tableWrap = buildTablePanel();
        split.setBottomComponent(tableWrap);

        center.add(split, BorderLayout.CENTER);
        return center;
    }

    // ── Summary cards ─────────────────────────────────────────────────────────
    private JPanel buildSummaryCards() {
        JPanel cards = new JPanel(new GridLayout(1, 5, 12, 0));
        cards.setOpaque(false);

        lblTongHoSo  = new JLabel("0", SwingConstants.CENTER);
        lblChoDuyet  = new JLabel("0", SwingConstants.CENTER);
        lblTrungTuyen= new JLabel("0", SwingConstants.CENTER);
        lblDaNhapHoc = new JLabel("0", SwingConstants.CENTER);
        lblKhongTrung= new JLabel("0", SwingConstants.CENTER);

        cards.add(summaryCardPanel("Tổng hồ sơ",         lblTongHoSo,  UITheme.PRIMARY));
        cards.add(summaryCardPanel("Chờ duyệt",           lblChoDuyet,  UITheme.WARNING));
        cards.add(summaryCardPanel("Trúng tuyển",         lblTrungTuyen,UITheme.SUCCESS));
        cards.add(summaryCardPanel("Đã nhập học",         lblDaNhapHoc, UITheme.ACCENT));
        cards.add(summaryCardPanel("Không trúng tuyển",   lblKhongTrung,UITheme.DANGER));

        return cards;
    }

    private JPanel summaryCardPanel(String title, JLabel valueLabel, Color accent) {
        JPanel card = new JPanel(new BorderLayout(0, 6)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                // Dải màu liền mạch, không bị tách thành hai vạch ở mép thẻ.
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, 10, getHeight(), 14, 14);
                g2.fillRect(5, 0, 5, getHeight());
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(215, 225, 240), 1, true),
            BorderFactory.createEmptyBorder(16, 20, 16, 16)
        ));

        valueLabel.setFont(new Font(UITheme.FONT_NAME, Font.BOLD, 30));
        valueLabel.setForeground(accent);
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(UITheme.FONT_SMALL);
        lblTitle.setForeground(UITheme.TEXT_SECONDARY);

        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblTitle,   BorderLayout.SOUTH);
        return card;
    }

    // ── Bảng chi tiết ────────────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(UITheme.sectionBorder("Chi tiết theo ngành"));

        String[] cols = {"Mã ngành","Tên ngành","Chỉ tiêu","Chờ duyệt","Trúng tuyển","Đã nhập học","Không trúng tuyển","Tổng hồ sơ"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        UITheme.styleTable(table);

        // Màu trạng thái giúp đọc nhanh báo cáo, nhưng vẫn giữ màu chữ khi chọn dòng.
        table.getColumnModel().getColumn(4).setCellRenderer(countRenderer(UITheme.SUCCESS_DARK));
        table.getColumnModel().getColumn(5).setCellRenderer(countRenderer(UITheme.ACCENT));
        table.getColumnModel().getColumn(6).setCellRenderer(countRenderer(UITheme.DANGER_DARK));
        for (int i = 2; i < cols.length; i++)
            if (i != 4 && i != 5 && i != 6)
                table.getColumnModel().getColumn(i).setCellRenderer(countRenderer(UITheme.TEXT_PRIMARY));

        int[] widths = {90, 230, 80, 90, 100, 105, 140, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        JScrollPane scroll = new JScrollPane(table);
        UITheme.styleScrollPane(scroll);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 215, 235), 1));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private javax.swing.table.TableCellRenderer countRenderer(Color color) {
        return new javax.swing.table.DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                setFont(UITheme.FONT_BOLD);
                setForeground(sel ? t.getSelectionForeground() : color);
                return this;
            }
        };
    }

    // ── Footer ────────────────────────────────────────────────────────────────
    private JPanel buildFooterPanel() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 4));
        footer.setOpaque(false);
        JButton btnRefresh = UITheme.primaryButton("Làm mới thống kê");
        JButton btnExport  = UITheme.secondaryButton("Xuất thống kê PDF");
        JButton btnCsv     = UITheme.secondaryButton("Xuất Excel (CSV)");
        btnRefresh.addActionListener(e -> loadData());
        btnExport.addActionListener(e  -> exportPdf());
        btnCsv.addActionListener(e -> exportCsv());
        footer.add(btnRefresh);
        footer.add(btnExport);
        footer.add(btnCsv);
        return footer;
    }

    // ── Load data ────────────────────────────────────────────────────────────
    public void refreshData() { loadData(); }

    private void loadData() {
        tableModel.setRowCount(0);
        currentRows = hoSoDao.thongKeTheoNganh();
        for (Object[] row : currentRows) tableModel.addRow(row);

        // Tính tổng cho summary cards
        int tongHoSo = 0, choDuyet = 0, trungTuyen = 0, daNhapHoc = 0, khongTrung = 0;
        for (Object[] row : currentRows) {
            choDuyet   += toInt(row[3]);
            trungTuyen += toInt(row[4]);
            daNhapHoc  += toInt(row[5]);
            khongTrung += toInt(row[6]);
            tongHoSo   += toInt(row[7]);
        }
        lblTongHoSo.setText(String.valueOf(tongHoSo));
        lblChoDuyet.setText(String.valueOf(choDuyet));
        lblTrungTuyen.setText(String.valueOf(trungTuyen));
        lblDaNhapHoc.setText(String.valueOf(daNhapHoc));
        lblKhongTrung.setText(String.valueOf(khongTrung));

        // Cập nhật biểu đồ
        chartPanel.setData(currentRows);
    }

    private int toInt(Object o) {
        if (o == null) return 0;
        try { return Integer.parseInt(o.toString()); } catch (NumberFormatException e) { return 0; }
    }

    private void exportPdf() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu báo cáo thống kê PDF");
        chooser.setSelectedFile(new File("thong-ke-tuyen-sinh.pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        try {
            PdfExportUtil.exportThongKe(chooser.getSelectedFile().toPath(), currentRows);
            AlertUtil.info(this, "Đã xuất file PDF thành công.");
        } catch (Exception ex) {
            AlertUtil.error(this, "Không thể xuất PDF: " + ex.getMessage());
        }
    }

    private void exportCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu dữ liệu thống kê Excel");
        chooser.setSelectedFile(new File("thong-ke-tuyen-sinh.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;
        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".csv")) file = new File(file.getAbsolutePath() + ".csv");
        try (java.io.Writer out = new java.io.OutputStreamWriter(new java.io.FileOutputStream(file), java.nio.charset.StandardCharsets.UTF_8)) {
            out.write("\uFEFFMã ngành,Tên ngành,Chỉ tiêu,Chờ duyệt,Trúng tuyển,Đã nhập học,Không trúng tuyển,Tổng hồ sơ\n");
            for (Object[] row : currentRows) {
                for (int i = 0; i < 8; i++) {
                    if (i > 0) out.write(",");
                    String value = row[i] == null ? "" : row[i].toString().replace("\"", "\"\"");
                    out.write("\"" + value + "\"");
                }
                out.write("\n");
            }
            AlertUtil.info(this, "Đã xuất dữ liệu Excel (CSV) thành công.");
        } catch (Exception ex) { AlertUtil.error(this, "Không thể xuất Excel: " + ex.getMessage()); }
    }

    // ════════════════════════════════════════════════════════════════════════
    // Biểu đồ cột vẽ bằng Java2D thuần (không cần thư viện ngoài)
    // ════════════════════════════════════════════════════════════════════════
    static class BarChartPanel extends JPanel {

        private List<Object[]> data = new ArrayList<>();

            // Màu nhóm cột: Chờ duyệt | Trúng tuyển | Đã nhập học | Không trúng tuyển
        private static final Color[] GROUP_COLORS = {
            UITheme.WARNING, UITheme.SUCCESS, UITheme.ACCENT, UITheme.DANGER
        };
        private static final String[] GROUP_LABELS = {"Chờ duyệt","Trúng tuyển","Đã nhập học","Không trúng tuyển"};

        void setData(List<Object[]> data) {
            this.data = data == null ? new ArrayList<>() : data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (data == null || data.isEmpty()) {
                g.setFont(UITheme.FONT_BODY); g.setColor(UITheme.TEXT_SECONDARY);
                String msg = "Không có dữ liệu để hiển thị biểu đồ";
                FontMetrics fm = g.getFontMetrics();
                g.drawString(msg, (getWidth() - fm.stringWidth(msg)) / 2, getHeight() / 2);
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Biểu đồ cột dọc: giữ cách xem quen thuộc nhưng dành nhiều không gian hơn.
            List<Object[]> chartData = new ArrayList<>(data);
            int marginLeft = 58, marginRight = 24, marginTop = 32, marginBottom = 88;
            int chartW = getWidth() - marginLeft - marginRight;
            int chartH = getHeight() - marginTop - marginBottom;
            int maxVal = 1;
            for (Object[] row : chartData) maxVal = Math.max(maxVal, toInt(row[7]));

            g2.setColor(new Color(248, 251, 255)); g2.fillRect(marginLeft, marginTop, chartW, chartH);
            int n = chartData.size();
            int groupW = chartW / Math.max(n, 1);
            // Nền xen kẽ và đường dọc giúp phân biệt rõ từng mã ngành.
            for (int i = 0; i < n; i++) {
                int groupLeft = marginLeft + i * groupW;
                if (i % 2 == 0) {
                    g2.setColor(new Color(244, 248, 253));
                    g2.fillRect(groupLeft, marginTop, groupW, chartH + 34);
                }
                if (i > 0) {
                    g2.setColor(new Color(190, 205, 223));
                    g2.setStroke(new BasicStroke(1));
                    g2.drawLine(groupLeft, marginTop, groupLeft, marginTop + chartH + 34);
                }
            }
            int gridLines = 5;
            g2.setFont(UITheme.FONT_SMALL);
            for (int i = 0; i <= gridLines; i++) {
                int y = marginTop + chartH - (int) (i * (double) chartH / gridLines);
                g2.setColor(new Color(210, 220, 235));
                g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10, new float[]{4, 4}, 0));
                g2.drawLine(marginLeft, y, marginLeft + chartW, y);
                g2.setColor(UITheme.TEXT_SECONDARY);
                String val = String.valueOf((int) (i * (double) maxVal / gridLines));
                g2.drawString(val, marginLeft - g2.getFontMetrics().stringWidth(val) - 7, y + 4);
            }
            int barGap = Math.max(4, groupW / 18);
            int sidePadding = Math.max(10, groupW / 8);
            int barW = Math.max(10, (groupW - sidePadding * 2 - barGap * 3) / 4);
            for (int i = 0; i < n; i++) {
                Object[] row = chartData.get(i);
                int groupLeft = marginLeft + i * groupW;
                int groupRight = groupLeft + groupW;
                // Căn giữa cụm 4 cột trong từng nhóm, tránh cột cuối chạm
                // đường phân cách của ngành kế bên.
                int x0 = groupLeft + sidePadding;
                int[] vals = {toInt(row[3]), toInt(row[4]), toInt(row[5]), toInt(row[6])};
                for (int gi = 0; gi < vals.length; gi++) {
                    int barHeight = vals[gi] == 0 ? 0 : Math.max(3, (int) ((double) vals[gi] / maxVal * chartH));
                    int bx = x0 + gi * (barW + barGap), by = marginTop + chartH - barHeight;
                    g2.setColor(GROUP_COLORS[gi]); g2.fillRoundRect(bx, by, barW, barHeight, 5, 5);
                    if (vals[gi] > 0) {
                        g2.setColor(UITheme.TEXT_PRIMARY); g2.setFont(UITheme.FONT_SMALL);
                        String value = String.valueOf(vals[gi]);
                        g2.drawString(value, bx + (barW - g2.getFontMetrics().stringWidth(value)) / 2, by - 4);
                    }
                }
                // Chỉ dùng mã ngành ở trục X để các nhãn không chồng lên nhau.
                g2.setColor(UITheme.PRIMARY); g2.setFont(new Font(UITheme.FONT_NAME, Font.BOLD, 11));
                String code = String.valueOf(row[0]);
                int codeX = groupLeft + (groupW - g2.getFontMetrics().stringWidth(code)) / 2;
                g2.drawString(code, Math.max(marginLeft, codeX), marginTop + chartH + 22);
                g2.setColor(new Color(190, 205, 223));
                g2.drawLine(groupRight, marginTop, groupRight, marginTop + chartH + 34);
            }
            int legendX = marginLeft + 8, legendY = marginTop + chartH + 58;
            g2.setFont(UITheme.FONT_SMALL);
            for (int gi = 0; gi < GROUP_LABELS.length; gi++) {
                int lx = legendX + gi * 155;
                g2.setColor(GROUP_COLORS[gi]);
                g2.fillRoundRect(lx, legendY, 14, 12, 4, 4);
                g2.setColor(UITheme.TEXT_PRIMARY);
                g2.drawString(GROUP_LABELS[gi], lx + 18, legendY + 11);
            }
        }


        private int toInt(Object o) {
            if (o == null) return 0;
            try { return Integer.parseInt(o.toString()); } catch (NumberFormatException e) { return 0; }
        }
    }
}
