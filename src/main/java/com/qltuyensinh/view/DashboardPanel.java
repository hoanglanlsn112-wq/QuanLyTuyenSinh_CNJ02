package com.qltuyensinh.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import com.qltuyensinh.dao.HoSoDangKyDAO;
import com.qltuyensinh.dao.NganhTuyenSinhDAO;
import com.qltuyensinh.dao.ThiSinhDAO;
import com.qltuyensinh.model.HoSoDangKy;
import com.qltuyensinh.util.UITheme;

/** Màn hình tổng quan để nhân viên nắm nhanh tình hình tuyển sinh. */
public class DashboardPanel extends JPanel {
    private final HoSoDangKyDAO hoSoDao = new HoSoDangKyDAO();
    private final ThiSinhDAO thiSinhDao = new ThiSinhDAO();
    private final NganhTuyenSinhDAO nganhDao = new NganhTuyenSinhDAO();
    private final JLabel total = valueLabel(), candidates = valueLabel(), waiting = valueLabel(), passed = valueLabel();
    private StatusChart statusChart;
    private StatusChart industryChart;
    private final DefaultTableModel recentModel = new DefaultTableModel(
            new String[]{"Mã hồ sơ", "Thí sinh", "Ngành", "Điểm tổng", "Trạng thái"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };

    public DashboardPanel() {
        setLayout(new BorderLayout(14, 14));
        setBackground(UITheme.BG_PAGE);
        setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        refreshData();
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout()); p.setOpaque(false);
        JLabel title = new JLabel("TỔNG QUAN TUYỂN SINH");
        title.setFont(new Font(UITheme.FONT_NAME, Font.BOLD, 22)); title.setForeground(UITheme.PRIMARY);
        JLabel sub = new JLabel("Theo dõi nhanh tình hình hồ sơ và kết quả xét tuyển");
        sub.setFont(UITheme.FONT_BODY); sub.setForeground(UITheme.TEXT_SECONDARY);
        JPanel text = new JPanel(new GridLayout(2, 1)); text.setOpaque(false); text.add(title); text.add(sub);
        p.add(text, BorderLayout.WEST); return p;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout(0, 16)); body.setOpaque(false);
        JPanel cards = new JPanel(new GridLayout(1, 4, 14, 0)); cards.setOpaque(false);
        cards.add(card("TỔNG HỒ SƠ", total, UITheme.PRIMARY));
        cards.add(card("THÍ SINH", candidates, UITheme.ACCENT));
        cards.add(card("CHỜ DUYỆT", waiting, UITheme.WARNING));
        cards.add(card("TRÚNG TUYỂN", passed, UITheme.SUCCESS));
        body.add(cards, BorderLayout.NORTH);
        JPanel analytics = new JPanel(new GridLayout(1, 2, 14, 0));
        analytics.setOpaque(false);
        statusChart = new StatusChart("Trạng thái hồ sơ", UITheme.SUCCESS, UITheme.WARNING, UITheme.DANGER);
        industryChart = new StatusChart("Hồ sơ theo ngành", UITheme.PRIMARY, UITheme.ACCENT, UITheme.WARNING);
        analytics.add(chartScroll(statusChart));
        analytics.add(chartScroll(industryChart));
        body.add(analytics, BorderLayout.CENTER);
        JPanel recent = new JPanel(new BorderLayout()); recent.setOpaque(false); recent.setBorder(UITheme.sectionBorder("Hồ sơ mới nhất"));
        JTable table = new JTable(recentModel);
        UITheme.styleTable(table);
        table.setRowHeight(32);
        table.setShowGrid(true);
        table.setGridColor(new Color(224, 231, 240));
        table.setIntercellSpacing(new java.awt.Dimension(1, 1));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        table.getColumnModel().getColumn(0).setPreferredWidth(115);
        table.getColumnModel().getColumn(1).setPreferredWidth(260);
        table.getColumnModel().getColumn(2).setPreferredWidth(350);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(230);

        // Căn dữ liệu theo đúng cột, tránh điểm/trạng thái bị lệch so với
        // tiêu đề khi cửa sổ thay đổi kích thước.
        DefaultTableCellRenderer centered = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean selected, boolean focused, int row, int column) {
                super.getTableCellRendererComponent(t, value, selected, focused, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!selected) setBackground(row % 2 == 0 ? UITheme.BG_TABLE_EVEN : UITheme.BG_TABLE_ODD);
                setBorder(BorderFactory.createEmptyBorder(0, 6, 0, 6));
                return this;
            }
        };
        table.getColumnModel().getColumn(3).setCellRenderer(centered);
        table.getColumnModel().getColumn(4).setCellRenderer(centered);

        JScrollPane scroll = new JScrollPane(table);
        UITheme.styleScrollPane(scroll);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(205, 218, 235)));
        recent.add(scroll, BorderLayout.CENTER);
        recent.setPreferredSize(new java.awt.Dimension(0, 245));
        body.add(recent, BorderLayout.SOUTH);
        return body;
    }

    private JScrollPane chartScroll(StatusChart chart) {
        JScrollPane scroll = new JScrollPane(chart);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        // Vẫn cuộn được bằng con lăn chuột nhưng không chiếm diện tích bằng
        // thanh cuộn dọc nổi ở mép phải của từng khung biểu đồ.
        scroll.getVerticalScrollBar().setPreferredSize(new java.awt.Dimension(0, 0));
        return scroll;
    }

    private JPanel card(String title, JLabel value, Color color) {
        JPanel p = new JPanel(new BorderLayout(4, 4)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE); g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.setColor(new Color(215, 225, 240)); g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
                g2.setColor(color); g2.fillRoundRect(0, 0, 7, getHeight(), 18, 18); g2.fillRect(3, 0, 4, getHeight());
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBorder(BorderFactory.createEmptyBorder(12, 22, 12, 18));
        JLabel name = new JLabel(title); name.setFont(UITheme.FONT_SMALL); name.setForeground(UITheme.TEXT_SECONDARY);
        p.add(name, BorderLayout.NORTH); p.add(value, BorderLayout.CENTER); value.setForeground(color); return p;
    }

    private JLabel valueLabel() { JLabel l = new JLabel("0"); l.setFont(new Font(UITheme.FONT_NAME, Font.BOLD, 30)); return l; }

    public void refreshData() {
        List<HoSoDangKy> applications = hoSoDao.getAll();
        total.setText(String.valueOf(applications.size())); candidates.setText(String.valueOf(thiSinhDao.getAll().size()));
        waiting.setText(String.valueOf(applications.stream().filter(h -> HoSoDangKy.CHO_DUYET.equals(h.getTrangThai())).count()));
        passed.setText(String.valueOf(applications.stream().filter(h -> HoSoDangKy.TRUNG_TUYEN.equals(h.getTrangThai()) || HoSoDangKy.DA_NHAP_HOC.equals(h.getTrangThai())).count()));
        if (statusChart != null) {
            Map<String, Integer> statuses = new LinkedHashMap<>();
            statuses.put("Trúng tuyển", (int) applications.stream().filter(h -> HoSoDangKy.TRUNG_TUYEN.equals(h.getTrangThai()) || HoSoDangKy.DA_NHAP_HOC.equals(h.getTrangThai())).count());
            statuses.put("Chờ duyệt", (int) applications.stream().filter(h -> HoSoDangKy.CHO_DUYET.equals(h.getTrangThai())).count());
            statuses.put("Không trúng", (int) applications.stream().filter(h -> HoSoDangKy.KHONG_TRUNG_TUYEN.equals(h.getTrangThai())).count());
            statusChart.setData(statuses);

            Map<String, Integer> industries = new LinkedHashMap<>();
            applications.forEach(h -> industries.merge(h.getTenNganh(), 1, Integer::sum));
            industryChart.setData(industries);
        }
        recentModel.setRowCount(0);
        for (int i = Math.max(0, applications.size() - 8); i < applications.size(); i++) {
            HoSoDangKy h = applications.get(i);
            recentModel.addRow(new Object[]{h.getMaHoSo(), h.getHoTenTS(), h.getTenNganh(), h.getDiemXet(), h.getTrangThai()});
        }
    }

    private static class StatusChart extends JPanel {
        private final String title;
        private final Color[] colors;
        private Map<String, Integer> data = new LinkedHashMap<>();

        StatusChart(String title, Color... colors) {
            this.title = title;
            this.colors = colors;
            setOpaque(false);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(215, 225, 240), 1, true),
                    BorderFactory.createEmptyBorder(12, 16, 10, 16)));
        }

        void setData(Map<String, Integer> data) {
            this.data = new LinkedHashMap<>(data);
            setPreferredSize(new java.awt.Dimension(0, Math.max(155, 70 + data.size() * 42)));
            revalidate();
            repaint();
        }

        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
            g2.setColor(UITheme.TEXT_PRIMARY);
            g2.setFont(UITheme.FONT_BOLD);
            g2.drawString(title, 16, 24);
            int max = data.values().stream().mapToInt(Integer::intValue).max().orElse(1);
            int y = 52;
            int index = 0;
            for (Map.Entry<String, Integer> item : data.entrySet()) {
                g2.setColor(index < colors.length ? colors[index] : UITheme.PRIMARY);
                g2.fillRoundRect(16, y - 11, 9, 9, 4, 4);
                g2.setColor(UITheme.TEXT_SECONDARY);
                g2.setFont(UITheme.FONT_SMALL);
                g2.drawString(item.getKey(), 34, y - 2);
                int barWidth = Math.max(4, (getWidth() - 100) * item.getValue() / max);
                g2.setColor(new Color(232, 238, 247));
                g2.fillRoundRect(34, y + 8, getWidth() - 76, 7, 4, 4);
                g2.setColor(index < colors.length ? colors[index] : UITheme.PRIMARY);
                g2.fillRoundRect(34, y + 8, barWidth, 7, 4, 4);
                g2.setColor(UITheme.TEXT_PRIMARY);
                g2.setFont(UITheme.FONT_BOLD);
                g2.drawString(String.valueOf(item.getValue()), getWidth() - 34, y - 2);
                y += 42;
                index++;
            }
            g2.dispose();
        }
    }
}
