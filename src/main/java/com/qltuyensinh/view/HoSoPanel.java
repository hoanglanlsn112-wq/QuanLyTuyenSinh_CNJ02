package com.qltuyensinh.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.qltuyensinh.dao.HoSoDangKyDAO;
import com.qltuyensinh.dao.AuditLogDAO;
import com.qltuyensinh.dao.NganhTuyenSinhDAO;
import com.qltuyensinh.dao.ThiSinhDAO;
import com.qltuyensinh.model.HoSoDangKy;
import com.qltuyensinh.model.NganhTuyenSinh;
import com.qltuyensinh.model.ThiSinh;
import com.qltuyensinh.util.AlertUtil;
import com.qltuyensinh.util.BackupUtil;
import com.qltuyensinh.util.PdfExportUtil;
import com.qltuyensinh.util.UITheme;
import com.qltuyensinh.util.ValidationUtil;

public class HoSoPanel extends JPanel {

    private final HoSoDangKyDAO hoSoDao     = new HoSoDangKyDAO();
    private final ThiSinhDAO    thiSinhDao  = new ThiSinhDAO();
    private final NganhTuyenSinhDAO nganhDao = new NganhTuyenSinhDAO();
    private final AuditLogDAO auditLogDAO = new AuditLogDAO();
    private final String currentAccount;
    private final boolean admissionRole;

    private JComboBox<ThiSinh>        cboThiSinh;
    private JComboBox<NganhTuyenSinh> cboNganh;
    private JComboBox<String>         cboKhoiXet;
    private JTextField txtDiemXet, txtDiemUuTien, txtDiemMon1, txtDiemMon2, txtDiemMon3, txtNgayNop, txtGhiChu;
    private JLabel lblMon1, lblMon2, lblMon3;
    private JLabel lblDiemChuanForm, lblKetQuaForm;
    private JComboBox<String> cboPhuongThuc;
    private JComboBox<String> cboFilterNganh, cboFilterTrangThai;
    private JTextField txtSearchHoSo;

    private JTable table;
    private DefaultTableModel tableModel;
    private String selectedMaHoSo = null;

    private JLabel lblDetailMaHoSo, lblDetailThiSinh, lblDetailNgaySinh, lblDetailGioiTinh,
                   lblDetailDiaChi, lblDetailSdt, lblDetailEmail, lblDetailCccd,
                   lblDetailKhoi, lblDetailNganh, lblDetailDiem, lblDetailTrangThai;
    private JLabel photoLabel;
    private final Map<String, String> photoPaths = new HashMap<>();
    private JButton btnDuyetTrung, btnDuyetKhong, btnNhapHoc, btnTuDong;
    private JPanel admissionActionPanel;

    public HoSoPanel() { this("SYSTEM", "Admin"); }

    public HoSoPanel(String currentAccount) {
        this(currentAccount, "Admin");
    }

    public HoSoPanel(String currentAccount, String role) {
        this.currentAccount = currentAccount == null || currentAccount.isBlank() ? "SYSTEM" : currentAccount;
        this.admissionRole = "Admin".equalsIgnoreCase(role) || "TuyenSinh".equalsIgnoreCase(role);
        setLayout(new BorderLayout(8, 8));
        setBackground(UITheme.BG_PAGE);
        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        add(buildFormPanel(),   BorderLayout.NORTH);
        add(buildTablePanel(),  BorderLayout.CENTER);
        JScrollPane detailScroll = new JScrollPane(buildDetailPanel());
        UITheme.styleScrollPane(detailScroll);
        // Giữ phần chi tiết đủ rộng cho ảnh nhưng trả thêm không gian cho bảng.
        detailScroll.setPreferredSize(new Dimension(470, 0));
        detailScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        detailScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        detailScroll.setBorder(null);
        add(detailScroll, BorderLayout.EAST);
        admissionActionPanel = buildActionPanel();

        reloadComboData();
        loadData();
    }

    // ── Form nộp hồ sơ ────────────────────────────────────────────────────────
    private JPanel buildFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        // Không để nền trắng phủ kín toàn bộ khối nhập liệu; hòa với nền
        // xanh nhạt của trang, giống bố cục màn hình Thống kê.
        form.setOpaque(false);
        form.setBorder(UITheme.sectionBorder("Nộp hồ sơ đăng ký xét tuyển"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cboThiSinh    = new JComboBox<>(); UITheme.styleComboBox(cboThiSinh);
        cboNganh      = new JComboBox<>(); UITheme.styleComboBox(cboNganh);
        cboKhoiXet    = new JComboBox<>(); UITheme.styleComboBox(cboKhoiXet);
        txtDiemXet    = UITheme.styledTextField(8); txtDiemXet.setEditable(false);
        txtDiemXet.setBackground(Color.WHITE);
        txtDiemXet.setDisabledTextColor(UITheme.TEXT_PRIMARY);
        txtDiemUuTien = UITheme.styledTextField(8); txtDiemUuTien.setText("0");
        txtDiemMon1   = UITheme.styledTextField(5);
        txtDiemMon2   = UITheme.styledTextField(5);
        txtDiemMon3   = UITheme.styledTextField(5);
        txtNgayNop    = UITheme.styledTextField(10);
        txtNgayNop.setText(ValidationUtil.formatDate(LocalDate.now()));
        txtGhiChu     = UITheme.styledTextField(22);
        cboPhuongThuc = new JComboBox<>(new String[]{"Thi THPT","Học bạ","Đánh giá năng lực"});
        UITheme.styleComboBox(cboPhuongThuc);
        cboPhuongThuc.addActionListener(e -> {
            updateSubjectLabels();
            updateScoreTotal();
        });
        lblMon1 = UITheme.fieldLabel("Môn 1:");
        lblMon2 = UITheme.fieldLabel("Môn 2:");
        lblMon3 = UITheme.fieldLabel("Môn 3:");
        lblDiemChuanForm = UITheme.styledValueLabel("-");
        lblKetQuaForm = UITheme.styledValueLabel("Chưa nhập điểm");
        lblKetQuaForm.setForeground(new Color(92, 99, 110));

        DocumentListener scoreListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { updateScoreTotal(); }
            public void removeUpdate(DocumentEvent e)  { updateScoreTotal(); }
            public void changedUpdate(DocumentEvent e) { updateScoreTotal(); }
        };
        txtDiemMon1.getDocument().addDocumentListener(scoreListener);
        txtDiemMon2.getDocument().addDocumentListener(scoreListener);
        txtDiemMon3.getDocument().addDocumentListener(scoreListener);
        txtDiemUuTien.getDocument().addDocumentListener(scoreListener);
        cboThiSinh.addActionListener(e -> {
            updateSubjectLabels();
            updateDetailFromForm();
        });
        cboNganh.addActionListener(e -> {
            reloadBlockOptions();
            updateSubjectLabels();
            updateDetailFromForm();
        });
        cboKhoiXet.addActionListener(e -> {
            updateSubjectLabels();
            updateDetailFromForm();
        });

        int r = 0;
        gbc.gridx=0; gbc.gridy=r; form.add(UITheme.fieldLabel("Thí sinh:"), gbc);
        gbc.gridx=1; form.add(cboThiSinh, gbc);
        gbc.gridx=2; form.add(UITheme.fieldLabel("Ngành đăng ký:"), gbc);
        gbc.gridx=3; gbc.weightx=1; form.add(cboNganh, gbc); gbc.weightx=0;

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(UITheme.fieldLabel("Điểm tổng:"), gbc);
        gbc.gridx=1; form.add(txtDiemXet, gbc);
        gbc.gridx=2; form.add(UITheme.fieldLabel("Điểm ưu tiên:"), gbc);
        gbc.gridx=3; form.add(txtDiemUuTien, gbc);

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(UITheme.fieldLabel("Ngày nộp (dd/MM/yyyy):"), gbc);
        gbc.gridx=1; form.add(txtNgayNop, gbc);
        gbc.gridx=2; form.add(UITheme.fieldLabel("Ghi chú:"), gbc);
        gbc.gridx=3; form.add(txtGhiChu, gbc);

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(UITheme.fieldLabel("Điểm chuẩn tạm:"), gbc);
        gbc.gridx=1; form.add(lblDiemChuanForm, gbc);
        gbc.gridx=2; gbc.gridy=r; form.add(UITheme.fieldLabel("Khối xét:"), gbc);
        gbc.gridx=3; form.add(cboKhoiXet, gbc);

        r++;
        gbc.gridx=0; gbc.gridy=r; form.add(UITheme.fieldLabel("Phương thức:"), gbc);
        gbc.gridx=1; form.add(cboPhuongThuc, gbc);
        gbc.gridx=2; form.add(UITheme.fieldLabel("Đánh giá sơ bộ:"), gbc);
        gbc.gridx=3; form.add(lblKetQuaForm, gbc);

        r++;
        JPanel subjectRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        subjectRow.setOpaque(false);
        subjectRow.add(lblMon1); subjectRow.add(txtDiemMon1);
        subjectRow.add(lblMon2); subjectRow.add(txtDiemMon2);
        subjectRow.add(lblMon3); subjectRow.add(txtDiemMon3);
        gbc.gridx=0; gbc.gridy=r; gbc.gridwidth=4; form.add(subjectRow, gbc); gbc.gridwidth=1;

        r++;
        JButton btnNop     = UITheme.primaryButton("Nộp hồ sơ");
        JButton btnCapNhat = UITheme.warningButton("Cập nhật hồ sơ");
        JButton btnXoa     = UITheme.dangerButton("Xóa hồ sơ");
        JButton btnLamMoi  = UITheme.secondaryButton("Làm mới");

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnNop); btnRow.add(btnCapNhat); btnRow.add(btnXoa); btnRow.add(btnLamMoi);
        gbc.gridx=0; gbc.gridy=r; gbc.gridwidth=4;
        gbc.insets = new Insets(8, 8, 8, 8);
        form.add(btnRow, gbc);

        btnNop.addActionListener(e     -> nopHoSo());
        btnCapNhat.addActionListener(e -> capNhatHoSo());
        btnXoa.addActionListener(e     -> xoaHoSo());
        btnLamMoi.addActionListener(e  -> { refreshData(); clearForm(); });

        return form;
    }

    // ── Bảng hồ sơ ───────────────────────────────────────────────────────────
    private JPanel buildTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setOpaque(false);

        // Filter panel
        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        filterRow.setOpaque(false);
        filterRow.add(UITheme.fieldLabel("Tìm thí sinh:"));
        txtSearchHoSo = UITheme.styledTextField(16);
        txtSearchHoSo.setPreferredSize(new Dimension(150, 34));
        filterRow.add(txtSearchHoSo);
        filterRow.add(UITheme.fieldLabel("Ngành:"));
        cboFilterNganh = new JComboBox<>(); UITheme.styleComboBox(cboFilterNganh);
        filterRow.add(cboFilterNganh);
        filterRow.add(UITheme.fieldLabel("Trạng thái:"));
        cboFilterTrangThai = new JComboBox<>(new String[]{
            "Tất cả", HoSoDangKy.CHO_DUYET, HoSoDangKy.CAN_BO_SUNG,
            HoSoDangKy.TRUNG_TUYEN, HoSoDangKy.KHONG_TRUNG_TUYEN, HoSoDangKy.DA_NHAP_HOC
        });
        UITheme.styleComboBox(cboFilterTrangThai);
        filterRow.add(cboFilterTrangThai);
        JButton btnLoc = UITheme.primaryButton("Lọc");
        btnLoc.setMargin(new Insets(0, 6, 0, 6));
        btnLoc.setPreferredSize(new Dimension(96, 34));
        btnLoc.setMinimumSize(new Dimension(96, 34));
        filterRow.add(btnLoc);
        btnLoc.addActionListener(e -> locData());
        txtSearchHoSo.addActionListener(e -> locData());

        // Table
        String[] cols = {"Mã hồ sơ","Thí sinh","Khối xét","Ngành","Khối ngành",
                         "Điểm chuẩn","Điểm tổng","Điểm ưu tiên","Phương thức","Ngày nộp","Trạng thái","Ghi chú"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        UITheme.styleTable(table);
        // Giữ độ rộng thực của các cột để có thanh kéo ngang khi cửa sổ hẹp.
        // Nhờ vậy nội dung dài không bị ép thành dấu ... hoặc bị khuất.
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setFillsViewportHeight(true);

        // Renderer trạng thái màu sắc (badge)
        table.getColumnModel().getColumn(10).setCellRenderer(new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                String st = v == null ? "" : v.toString();
                lbl.setOpaque(true);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setFont(UITheme.FONT_BOLD);
                if (!sel) {
                    if (HoSoDangKy.TRUNG_TUYEN.equals(st)) {
                        lbl.setBackground(new Color(220, 250, 230));
                        lbl.setForeground(UITheme.SUCCESS_DARK);
                        lbl.setText(st);
                    } else if (HoSoDangKy.KHONG_TRUNG_TUYEN.equals(st)) {
                        lbl.setBackground(new Color(255, 235, 235));
                        lbl.setForeground(UITheme.DANGER_DARK);
                        lbl.setText(st);
                    } else {
                        lbl.setBackground(new Color(255, 248, 220));
                        lbl.setForeground(UITheme.WARNING_DARK);
                        lbl.setText(st);
                    }
                } else {
                    lbl.setText(st);
                }
                return lbl;
            }
        });

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1)
                fillFormFromRow(table.getSelectedRow());
        });

        int[] widths = {85, 180, 70, 180, 90, 90, 85, 90, 120, 100, 145, 180};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Hiện toàn bộ nội dung khi rê chuột lên ô, hữu ích với tên ngành,
        // ghi chú và tên thí sinh dài.
        DefaultTableCellRenderer fullTextRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v,
                    boolean sel, boolean foc, int row, int col) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, row, col);
                label.setToolTipText(v == null ? null : v.toString());
                label.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return label;
            }
        };
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 10) table.getColumnModel().getColumn(i).setCellRenderer(fullTextRenderer);
        }

        JScrollPane scroll = new JScrollPane(table);
        UITheme.styleScrollPane(scroll);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 215, 235), 1));

        panel.add(filterRow, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ── Detail panel ──────────────────────────────────────────────────────────
    private JPanel buildDetailPanel() {
        JPanel detail = new JPanel(new BorderLayout(10, 10));
        detail.setBackground(UITheme.BG_CARD);
        detail.setBorder(UITheme.sectionBorder("Thông tin chi tiết thí sinh"));
        detail.setPreferredSize(new Dimension(450, 520));

        JPanel info = new JPanel(new GridBagLayout());
        info.setBackground(UITheme.BG_CARD);

        lblDetailMaHoSo    = UITheme.valueLabel();
        lblDetailThiSinh   = UITheme.valueLabel();
        lblDetailNgaySinh  = UITheme.valueLabel();
        lblDetailGioiTinh  = UITheme.valueLabel();
        lblDetailDiaChi    = UITheme.valueLabel();
        lblDetailSdt       = UITheme.valueLabel();
        lblDetailEmail     = UITheme.valueLabel();
        lblDetailCccd      = UITheme.valueLabel();
        lblDetailKhoi      = UITheme.valueLabel();
        lblDetailNganh     = UITheme.valueLabel();
        lblDetailDiem      = UITheme.valueLabel();
        lblDetailTrangThai = UITheme.valueLabel();

        addDetail(info, "Mã hồ sơ:",        lblDetailMaHoSo);
        addDetail(info, "Thí sinh:",         lblDetailThiSinh);
        addDetail(info, "Ngày sinh:",        lblDetailNgaySinh);
        addDetail(info, "Giới tính:",        lblDetailGioiTinh);
        addDetail(info, "Địa chỉ:",          lblDetailDiaChi);
        addDetail(info, "Số điện thoại:",    lblDetailSdt);
        addDetail(info, "Email:",            lblDetailEmail);
        addDetail(info, "CCCD:",             lblDetailCccd);
        addDetail(info, "Khối dự thi:",      lblDetailKhoi);
        addDetail(info, "Ngành đăng ký:",    lblDetailNganh);
        addDetail(info, "Điểm tổng:",        lblDetailDiem);
        addDetail(info, "Trạng thái:",       lblDetailTrangThai);

        GridBagConstraints infoFiller = new GridBagConstraints();
        infoFiller.gridx = 0; infoFiller.gridy = 12; infoFiller.gridwidth = 2;
        infoFiller.weighty = 1; infoFiller.fill = GridBagConstraints.VERTICAL;
        // Ô đệm mặc định của Swing có nền xám/trắng riêng, tạo thành một
        // dải dọc thừa cạnh khu vực ảnh. Để trong suốt để hòa vào khung.
        JPanel infoFillerPanel = new JPanel();
        infoFillerPanel.setOpaque(false);
        info.add(infoFillerPanel, infoFiller);

        // Ảnh thẻ
        JPanel photoBox = new JPanel(new BorderLayout(0, 6));
        photoBox.setBackground(UITheme.BG_CARD);
        photoLabel = new JLabel("Chưa có ảnh", SwingConstants.CENTER);
        photoLabel.setPreferredSize(new Dimension(150, 150));
        photoLabel.setMinimumSize(new Dimension(150, 150));
        photoLabel.setMaximumSize(new Dimension(150, 150));
        photoLabel.setVerticalAlignment(SwingConstants.TOP);
        photoLabel.setForeground(UITheme.TEXT_SECONDARY);
        photoLabel.setFont(UITheme.FONT_SMALL);
        // Không tạo khung riêng quanh ảnh; khung thông tin bên ngoài đã đủ.
        photoLabel.setBorder(BorderFactory.createEmptyBorder());
        JButton btnChonAnh = UITheme.secondaryButton("Chọn ảnh thẻ");
        btnChonAnh.addActionListener(e -> choosePhoto());
        photoBox.add(photoLabel, BorderLayout.CENTER);
        photoBox.add(btnChonAnh, BorderLayout.SOUTH);

        detail.add(info, BorderLayout.CENTER);
        detail.add(photoBox, BorderLayout.EAST);
        clearDetail();
        return detail;
    }

    private void addDetail(JPanel p, String title, JLabel value) {
        JLabel lbl = UITheme.fieldLabel(title);
        int row = p.getComponentCount() / 2;
        GridBagConstraints labelGbc = new GridBagConstraints();
        labelGbc.gridx = 0; labelGbc.gridy = row;
        labelGbc.anchor = GridBagConstraints.WEST;
        labelGbc.fill = GridBagConstraints.NONE;
        labelGbc.insets = new Insets(4, 0, 4, 10);
        p.add(lbl, labelGbc);

        GridBagConstraints valueGbc = new GridBagConstraints();
        valueGbc.gridx = 1; valueGbc.gridy = row;
        valueGbc.weightx = 1; valueGbc.fill = GridBagConstraints.HORIZONTAL;
        valueGbc.anchor = GridBagConstraints.WEST;
        valueGbc.insets = new Insets(4, 0, 4, 0);
        p.add(value, valueGbc);
    }

    // ── Action panel (xét duyệt) ─────────────────────────────────────────────
    private JPanel buildActionPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(new Color(240, 243, 248));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 210, 225)),
            BorderFactory.createTitledBorder(
                BorderFactory.createEmptyBorder(),
                " Xét duyệt hồ sơ (dựa theo chỉ tiêu còn lại của ngành) ",
                javax.swing.border.TitledBorder.LEFT,
                javax.swing.border.TitledBorder.TOP,
                UITheme.FONT_LABEL, UITheme.PRIMARY
            )
        ));

        btnDuyetTrung   = UITheme.successButton("Duyệt: Trúng tuyển");
        btnDuyetKhong   = UITheme.dangerButton("Duyệt: Không trúng tuyển");
        btnNhapHoc      = UITheme.primaryButton("Xác nhận đã nhập học");
        btnTuDong       = UITheme.warningButton("Tự động xét duyệt theo điểm + chỉ tiêu");
        JButton btnGiayBao      = UITheme.primaryButton("Xuất giấy báo trúng tuyển PDF");

        // Chia thành 2 hàng để các nút không bị chen chúc ở màn hình nhỏ.
        // Vùng đáy hiện đủ rộng, gom các nút vào một hàng để không che bảng hồ sơ.
        JPanel buttonRow = new JPanel(new WrapLayout(FlowLayout.LEFT, 10, 0));
        buttonRow.setOpaque(false);
        buttonRow.add(btnDuyetTrung);
        buttonRow.add(btnDuyetKhong);
        buttonRow.add(btnNhapHoc);
        buttonRow.add(btnTuDong);
        buttonRow.add(btnGiayBao);
        panel.add(buttonRow, BorderLayout.CENTER);

        btnDuyetTrung.addActionListener(e  -> duyetHoSo(HoSoDangKy.TRUNG_TUYEN));
        btnDuyetKhong.addActionListener(e  -> duyetHoSo(HoSoDangKy.KHONG_TRUNG_TUYEN));
        btnNhapHoc.addActionListener(e -> xacNhanNhapHoc());
        btnTuDong.addActionListener(e      -> tuDongXetDuyet());
        btnGiayBao.addActionListener(e     -> xuatGiayBao());

        return panel;
    }

    /** Khu vực xét tuyển được MainForm đặt ở đáy cửa sổ và ẩn khi đổi trang. */
    public JPanel getAdmissionActionPanel() {
        return admissionActionPanel;
    }

    private JPanel buildDatabasePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
                new Color(200, 210, 225)));

        // Khu vực này chỉ thuộc trang Hồ sơ đăng ký nên sẽ tự ẩn khi đổi tab.
        JButton btnBackup = UITheme.secondaryButton("Sao lưu CSDL");
        JButton btnRestore = UITheme.warningButton("Khôi phục CSDL");
        btnBackup.addActionListener(e -> backupDatabase());
        btnRestore.addActionListener(e -> restoreDatabase());
        panel.add(btnBackup);
        panel.add(btnRestore);
        return panel;
    }

    private void backupDatabase() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu bản sao CSDL");
        chooser.setSelectedFile(new File("qltuyensinh-backup.sql"));
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

    /** FlowLayout tự xuống hàng khi cửa sổ hẹp, tránh cắt mất nút hoặc chữ. */
    private static class WrapLayout extends FlowLayout {
        WrapLayout(int align, int hgap, int vgap) {
            super(align, hgap, vgap);
        }

        @Override
        public Dimension preferredLayoutSize(Container target) {
            return layoutSize(target, true);
        }

        @Override
        public Dimension minimumLayoutSize(Container target) {
            Dimension size = layoutSize(target, false);
            size.width -= getHgap() + 1;
            return size;
        }

        private Dimension layoutSize(Container target, boolean preferred) {
            synchronized (target.getTreeLock()) {
                Insets insets = target.getInsets();
                int width = target.getWidth();
                if (width <= 0) width = Integer.MAX_VALUE;
                int maxWidth = width - insets.left - insets.right - getHgap() * 2;
                int rowWidth = 0;
                int rowHeight = 0;
                int totalHeight = insets.top + getVgap();
                int totalWidth = 0;

                for (Component component : target.getComponents()) {
                    if (!component.isVisible()) continue;
                    Dimension size = preferred ? component.getPreferredSize() : component.getMinimumSize();
                    if (rowWidth > 0 && rowWidth + size.width > maxWidth) {
                        totalHeight += rowHeight + getVgap();
                        totalWidth = Math.max(totalWidth, rowWidth);
                        rowWidth = 0;
                        rowHeight = 0;
                    }
                    rowWidth += size.width + getHgap();
                    rowHeight = Math.max(rowHeight, size.height);
                }
                totalWidth = Math.max(totalWidth, rowWidth);
                totalHeight += rowHeight + insets.bottom;
                return new Dimension(totalWidth + insets.left + insets.right,
                        totalHeight);
            }
        }
    }

    /** Khóa các thao tác kết quả tuyển sinh đối với nhân viên thường. */
    public void setAdmissionActionsEnabled(boolean enabled) {
        if (btnDuyetTrung != null) btnDuyetTrung.setEnabled(enabled);
        if (btnDuyetKhong != null) btnDuyetKhong.setEnabled(enabled);
        if (btnNhapHoc != null) btnNhapHoc.setEnabled(enabled);
        if (btnTuDong != null) btnTuDong.setEnabled(enabled);
    }

    // ── Subject labels ────────────────────────────────────────────────────────
    private void updateSubjectLabels() {
        String khoiThiSinh = null;
        if (cboThiSinh.getSelectedItem() instanceof ThiSinh ts) khoiThiSinh = ts.getKhoiDuThi();
        String khoiNganh = null;
        if (cboNganh.getSelectedItem() instanceof NganhTuyenSinh ng) khoiNganh = ng.getKhoiXetTuyen();

        // Ưu tiên ngành đang chọn. Nếu khối của thí sinh vẫn nằm trong ngành
        // đó thì giữ lại; nếu không, lấy khối đầu tiên của ngành mới.
        String khoi = cboKhoiXet == null ? null : (String) cboKhoiXet.getSelectedItem();
        if (khoi == null || khoi.isBlank() || "Chưa chọn".equals(khoi)) {
            khoi = chooseBlockForMajor(khoiNganh, khoiThiSinh);
        }
        if (khoi == null || khoi.isBlank()) khoi = khoiThiSinh;
        String[] s = subjectsForBlock(khoi);
        String prefix = "Học bạ".equals(cboPhuongThuc.getSelectedItem()) ? "ĐTB " : "";
        lblMon1.setText(prefix + s[0] + ":");
        lblMon2.setText(prefix + s[1] + ":");
        lblMon3.setText(prefix + s[2] + ":");
        String hint = "Học bạ".equals(cboPhuongThuc.getSelectedItem())
                ? "Nhập điểm trung bình học bạ từng môn (0 - 10). Điểm tổng = tổng 3 ĐTB."
                : "Nhập điểm từng môn theo tổ hợp xét tuyển.";
        txtDiemMon1.setToolTipText(hint);
        txtDiemMon2.setToolTipText(hint);
        txtDiemMon3.setToolTipText(hint);
        updateFormSummary();
    }

    private String chooseBlockForMajor(String majorBlocks, String candidateBlock) {
        if (majorBlocks == null || majorBlocks.isBlank() || "-".equals(majorBlocks.trim())) return candidateBlock;
        if (candidateBlock != null && !candidateBlock.isBlank() && !"-".equals(candidateBlock.trim())) {
            for (String block : majorBlocks.split(",")) {
                if (block.trim().equalsIgnoreCase(candidateBlock.trim())) return candidateBlock.trim();
            }
        }
        return majorBlocks.split(",")[0].trim();
    }

    private String[] subjectsForBlock(String khoi) {
        if ("A00".equalsIgnoreCase(khoi)) return new String[]{"Toán","Lý","Hóa"};
        if ("A01".equalsIgnoreCase(khoi)) return new String[]{"Toán","Lý","Anh"};
        if ("B00".equalsIgnoreCase(khoi)) return new String[]{"Toán","Hóa","Sinh"};
        if ("C00".equalsIgnoreCase(khoi)) return new String[]{"Văn","Sử","Địa"};
        if ("X06".equalsIgnoreCase(khoi)) return new String[]{"Toán","Lý","Tin"};
        if ("X26".equalsIgnoreCase(khoi)) return new String[]{"Toán","Anh","Tin"};
        if ("D07".equalsIgnoreCase(khoi)) return new String[]{"Toán","Hóa","Anh"};
        if (khoi != null && khoi.toUpperCase().startsWith("D")) return new String[]{"Toán","Văn","Anh"};
        return new String[]{"Môn 1","Môn 2","Môn 3"};
    }

    // ── Combo data ────────────────────────────────────────────────────────────
    private void reloadComboData() {
        cboThiSinh.removeAllItems();
        for (ThiSinh t : thiSinhDao.getAll()) cboThiSinh.addItem(t);
        cboNganh.removeAllItems();
        cboFilterNganh.removeAllItems();
        cboFilterNganh.addItem("Tất cả");
        for (NganhTuyenSinh n : nganhDao.getAll()) {
            cboNganh.addItem(n);
            cboFilterNganh.addItem(n.getMaNganh() + " - " + n.getTenNganh()
                    + " - " + (n.getKhoiXetTuyen() == null || n.getKhoiXetTuyen().isBlank()
                    ? "Chưa chọn" : n.getKhoiXetTuyen()));
        }
        // Dành đủ chiều rộng để người dùng nhìn rõ mã/tên lựa chọn trong thanh lọc.
        cboFilterNganh.setPreferredSize(new Dimension(155, 34));
        cboFilterTrangThai.setPreferredSize(new Dimension(145, 34));
        reloadBlockOptions();
    }

    private void reloadBlockOptions() {
        if (cboKhoiXet == null) return;
        String previous = (String) cboKhoiXet.getSelectedItem();
        cboKhoiXet.removeAllItems();
        if (cboNganh.getSelectedItem() instanceof NganhTuyenSinh ng
                && ng.getKhoiXetTuyen() != null && !ng.getKhoiXetTuyen().isBlank()) {
            for (String block : ng.getKhoiXetTuyen().split(",")) {
                String value = block.trim();
                if (!value.isBlank()) cboKhoiXet.addItem(value);
            }
        }
        if (cboKhoiXet.getItemCount() == 0) cboKhoiXet.addItem("Chưa chọn");
        if (previous != null) cboKhoiXet.setSelectedItem(previous);
        if (cboKhoiXet.getSelectedIndex() < 0) cboKhoiXet.setSelectedIndex(0);
    }

    public void refreshData() { reloadComboData(); loadData(); }

    private void loadData() {
        tableModel.setRowCount(0);
        for (HoSoDangKy h : hoSoDao.getAll()) fillRow(h);
    }

    private void locData() {
        String kw        = txtSearchHoSo.getText().trim().toLowerCase();
        String nganhHienThi = (String) cboFilterNganh.getSelectedItem();
        String maNganh = nganhHienThi;
        if (maNganh != null && maNganh.contains(" - ")) {
            maNganh = maNganh.substring(0, maNganh.indexOf(" - ")).trim();
        }
        String trangThai = (String) cboFilterTrangThai.getSelectedItem();
        tableModel.setRowCount(0);
        List<HoSoDangKy> list;
        boolean tatCaNganh = maNganh == null || maNganh.isBlank() || "Tất cả".equals(maNganh);
        boolean tatCaTrangThai = trangThai == null || trangThai.isBlank() || "Tất cả".equals(trangThai);
        // Khi bỏ toàn bộ bộ lọc, trả lại thứ tự mã hồ sơ HS0001, HS0002...
        list = tatCaNganh && tatCaTrangThai ? hoSoDao.getAll() : hoSoDao.filter(maNganh, trangThai);
        for (HoSoDangKy h : list) {
            if (kw.isEmpty() || h.getHoTenTS().toLowerCase().contains(kw)
                    || h.getMaTS().toLowerCase().contains(kw)) {
                fillRow(h);
            }
        }
    }

    private void fillRow(HoSoDangKy h) {
        tableModel.addRow(new Object[]{
            h.getMaHoSo(), h.getHoTenTS() + " (" + h.getMaTS() + ")",
            displayBlock(h), h.getTenNganh(), safe(h.getKhoiXetTuyen()),
            h.getKhoiXetTuyen() == null || h.getKhoiXetTuyen().isBlank() ? "-" : getDiemChuan(h),
            h.getDiemXet(), h.getDiemUuTien(), safe(h.getPhuongThuc()),
            ValidationUtil.formatDate(h.getNgayNop()), h.getTrangThai(), h.getGhiChu()
        });
    }

    private String safe(String v) { return v == null || v.isBlank() ? "-" : v; }

    private String displayBlock(HoSoDangKy hs) {
        if (hs.getKhoiDuThi() != null && !hs.getKhoiDuThi().isBlank()
                && !"-".equals(hs.getKhoiDuThi().trim())) return hs.getKhoiDuThi();
        if (hs.getKhoiDangKy() != null && !hs.getKhoiDangKy().isBlank()) return hs.getKhoiDangKy();
        if ("Học bạ".equalsIgnoreCase(hs.getPhuongThuc())) return safe(hs.getKhoiXetTuyen());
        return "-";
    }

    private String getDiemChuan(HoSoDangKy h) {
        NganhTuyenSinh n = nganhDao.getById(h.getMaNganh());
        return n == null ? "-" : String.valueOf(n.getDiemChuanTam());
    }

    // ── Fill form from table row ──────────────────────────────────────────────
    private void fillFormFromRow(int row) {
        selectedMaHoSo = tableModel.getValueAt(row, 0).toString();
        String maTS = tableModel.getValueAt(row, 1).toString();
        maTS = maTS.substring(maTS.indexOf("(") + 1, maTS.indexOf(")"));
        selectComboByCode(cboThiSinh, maTS);

        String tenNganh = tableModel.getValueAt(row, 3).toString();
        selectComboByNganhTen(tenNganh);

        txtDiemXet.setText(tableModel.getValueAt(row, 6).toString());
        txtDiemUuTien.setText(tableModel.getValueAt(row, 7).toString());
        cboPhuongThuc.setSelectedItem(tableModel.getValueAt(row, 8).toString());
        txtNgayNop.setText(tableModel.getValueAt(row, 9).toString());
        Object ghiChu = tableModel.getValueAt(row, 11);
        txtGhiChu.setText(ghiChu == null ? "" : ghiChu.toString());

        ThiSinh thiSinh = (ThiSinh) cboThiSinh.getSelectedItem();
        HoSoDangKy hoSo = hoSoDao.getAll().stream()
                .filter(item -> selectedMaHoSo.equals(item.getMaHoSo()))
                .findFirst().orElse(null);
        if (hoSo != null) {
            reloadBlockOptions();
            cboKhoiXet.setSelectedItem(hoSo.getKhoiDangKy());
        }
        updateDetail(thiSinh, hoSo);
    }

    private void selectComboByCode(JComboBox<ThiSinh> combo, String maTS) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (combo.getItemAt(i).getMaTS().equals(maTS)) { combo.setSelectedIndex(i); return; }
        }
    }

    private void selectComboByNganhTen(String tenNganh) {
        for (int i = 0; i < cboNganh.getItemCount(); i++) {
            if (cboNganh.getItemAt(i).getTenNganh().equals(tenNganh)) { cboNganh.setSelectedIndex(i); return; }
        }
    }

    // ── Detail update / clear ─────────────────────────────────────────────────
    private void updateDetail(ThiSinh ts, HoSoDangKy hs) {
        if (ts == null || hs == null) { clearDetail(); return; }
        lblDetailMaHoSo.setText(safe(hs.getMaHoSo()));
        lblDetailThiSinh.setText(safe(ts.getMaTS()) + " – " + safe(ts.getHoTen()));
        lblDetailNgaySinh.setText(ValidationUtil.formatDate(ts.getNgaySinh()));
        lblDetailGioiTinh.setText(safe(ts.getGioiTinh()));
        lblDetailDiaChi.setText(safe(ts.getDiaChi()));
        lblDetailSdt.setText(safe(ts.getSdt()));
        lblDetailEmail.setText(safe(ts.getEmail()));
        lblDetailCccd.setText(safe(ts.getCccd()));
        lblDetailKhoi.setText(displayBlock(hs));
        lblDetailNganh.setText(safe(hs.getTenNganh()) + " (" + safe(hs.getKhoiXetTuyen()) + ")");
        lblDetailDiem.setText(String.valueOf(hs.getDiemXet()));
        lblDetailTrangThai.setText(safe(hs.getTrangThai()));
        txtDiemMon1.setText(String.valueOf(hs.getDiemMon1()));
        txtDiemMon2.setText(String.valueOf(hs.getDiemMon2()));
        txtDiemMon3.setText(String.valueOf(hs.getDiemMon3()));
        txtDiemXet.setText(String.valueOf(hs.getDiemXet()));
        cboPhuongThuc.setSelectedItem(hs.getPhuongThuc() == null ? "Thi THPT" : hs.getPhuongThuc());
        reloadBlockOptions();
        if (hs.getKhoiDangKy() != null) cboKhoiXet.setSelectedItem(hs.getKhoiDangKy());
        updateSubjectLabels();
        String photoPath = ts.getAnhThe();
        if (photoPath == null || photoPath.isBlank()) photoPath = photoPaths.get(ts.getMaTS());
        loadPhoto(photoPath);
    }

    private void updateDetailFromForm() {
        ThiSinh ts = (ThiSinh) cboThiSinh.getSelectedItem();
        NganhTuyenSinh ng = (NganhTuyenSinh) cboNganh.getSelectedItem();
        if (ts == null) {
            clearDetail();
            return;
        }
        lblDetailMaHoSo.setText(selectedMaHoSo != null ? selectedMaHoSo : "-");
        lblDetailThiSinh.setText(safe(ts.getMaTS()) + " – " + safe(ts.getHoTen()));
        lblDetailNgaySinh.setText(ValidationUtil.formatDate(ts.getNgaySinh()));
        lblDetailGioiTinh.setText(safe(ts.getGioiTinh()));
        lblDetailDiaChi.setText(safe(ts.getDiaChi()));
        lblDetailSdt.setText(safe(ts.getSdt()));
        lblDetailEmail.setText(safe(ts.getEmail()));
        lblDetailCccd.setText(safe(ts.getCccd()));
        String khoiHienThi = (String) cboKhoiXet.getSelectedItem();
        if (khoiHienThi == null || khoiHienThi.isBlank() || "Chưa chọn".equals(khoiHienThi)) {
            khoiHienThi = ts.getKhoiDuThi();
        }
        lblDetailKhoi.setText(safe(khoiHienThi));
        
        if (ng != null) {
            lblDetailNganh.setText(safe(ng.getTenNganh()) + " (" + safe(ng.getKhoiXetTuyen()) + ")");
        } else {
            lblDetailNganh.setText("-");
        }
        
        lblDetailDiem.setText(txtDiemXet.getText().isBlank() ? "-" : txtDiemXet.getText());
        
        if (selectedMaHoSo != null) {
            HoSoDangKy hoSo = hoSoDao.getAll().stream()
                .filter(item -> selectedMaHoSo.equals(item.getMaHoSo()))
                .findFirst().orElse(null);
            if (hoSo != null) {
                lblDetailTrangThai.setText(safe(hoSo.getTrangThai()));
            } else {
                lblDetailTrangThai.setText("-");
            }
        } else {
            lblDetailTrangThai.setText("-");
        }

        String photoPath = ts.getAnhThe();
        if (photoPath == null || photoPath.isBlank()) photoPath = photoPaths.get(ts.getMaTS());
        loadPhoto(photoPath);
    }

    private void clearDetail() {
        if (lblDetailMaHoSo == null) return;
        for (JLabel lbl : new JLabel[]{
            lblDetailMaHoSo, lblDetailThiSinh, lblDetailNgaySinh, lblDetailGioiTinh,
            lblDetailDiaChi, lblDetailSdt, lblDetailEmail, lblDetailCccd,
            lblDetailKhoi, lblDetailNganh, lblDetailDiem, lblDetailTrangThai}) {
            lbl.setText("-");
        }
        loadPhoto(null);
    }

    // ── Ảnh thẻ ──────────────────────────────────────────────────────────────
    private void choosePhoto() {
        if (cboThiSinh.getSelectedItem() == null) {
            AlertUtil.error(this, "Vui lòng chọn hồ sơ thí sinh trước."); return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Chọn ảnh thẻ 3x4");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                BufferedImage image = ImageIO.read(file);
                if (image == null) throw new IllegalArgumentException("Không đọc được ảnh.");
                ThiSinh ts = (ThiSinh) cboThiSinh.getSelectedItem();
                photoPaths.put(ts.getMaTS(), file.getAbsolutePath());
                ts.setAnhThe(file.getAbsolutePath());
                thiSinhDao.updatePhotoPath(ts.getMaTS(), file.getAbsolutePath());
                loadPhoto(file.getAbsolutePath());
            } catch (Exception ex) {
                AlertUtil.error(this, "Ảnh không hợp lệ: " + ex.getMessage());
            }
        }
    }

    private void loadPhoto(String path) {
        if (photoLabel == null) return;
        if (path == null || path.isBlank()) {
            photoLabel.setIcon(null); photoLabel.setText("Chưa có ảnh"); return;
        }
        try {
            File imageFile = new File(path);
            if (!imageFile.isFile()) imageFile = new File("QuanLyTuyenSinh", path);
            BufferedImage src = ImageIO.read(imageFile);
            if (src == null) throw new IllegalArgumentException();
            int box = 170;
            BufferedImage canvas = new BufferedImage(box, box, BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = canvas.createGraphics();
            g2.setColor(Color.WHITE); g2.fillRect(0, 0, box, box);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(src, 0, 0, box, box, null); g2.dispose();
            photoLabel.setText(""); photoLabel.setIcon(new ImageIcon(canvas));
        } catch (Exception ex) {
            photoLabel.setIcon(null); photoLabel.setText("Không đọc được ảnh");
        }
    }

    // ── Form helpers ─────────────────────────────────────────────────────────
    private void clearForm() {
        selectedMaHoSo = null;
        if (cboThiSinh.getItemCount() > 0) cboThiSinh.setSelectedIndex(0);
        if (cboNganh.getItemCount() > 0)   cboNganh.setSelectedIndex(0);
        txtDiemXet.setText(""); txtDiemUuTien.setText("0");
        txtDiemMon1.setText(""); txtDiemMon2.setText(""); txtDiemMon3.setText("");
        cboPhuongThuc.setSelectedIndex(0);
        txtNgayNop.setText(ValidationUtil.formatDate(LocalDate.now()));
        txtGhiChu.setText(""); table.clearSelection(); clearDetail();
    }

    private boolean validateForm() {
        if (cboThiSinh.getSelectedItem() == null || cboNganh.getSelectedItem() == null) {
            AlertUtil.error(this, "Vui lòng chọn thí sinh và ngành."); return false;
        }
        if (!validSubjectScore(txtDiemMon1.getText())
                || !validSubjectScore(txtDiemMon2.getText())
                || !validSubjectScore(txtDiemMon3.getText())) {
            AlertUtil.error(this, isHocBa()
                    ? "Điểm học bạ từng môn phải là số từ 0 đến 10."
                    : "Điểm 3 môn phải là số từ 0 đến 30."); return false;
        }
        txtDiemXet.setText(formatScore(totalSubjectScore()));
        if (!ValidationUtil.isValidScore(txtDiemUuTien.getText().trim())) {
            AlertUtil.error(this, "Điểm ưu tiên phải là số từ 0 đến 30."); return false;
        }
        if (ValidationUtil.parseDate(txtNgayNop.getText().trim()) == null) {
            AlertUtil.error(this, "Ngày nộp không hợp lệ. Định dạng: dd/MM/yyyy"); return false;
        }
        ThiSinh ts = (ThiSinh) cboThiSinh.getSelectedItem();
        NganhTuyenSinh ng = (NganhTuyenSinh) cboNganh.getSelectedItem();
        if (ts != null && ng != null && hoSoDao.existsApplication(ts.getMaTS(), ng.getMaNganh(), selectedMaHoSo)) {
            AlertUtil.error(this, "Thí sinh đã có hồ sơ đăng ký ngành này."); return false;
        }
        return true;
    }

    private double totalSubjectScore() {
        double total = Double.parseDouble(txtDiemMon1.getText().trim())
             + Double.parseDouble(txtDiemMon2.getText().trim())
             + Double.parseDouble(txtDiemMon3.getText().trim());
        return Math.round(total * 100.0) / 100.0;
    }

    private boolean isHocBa() {
        return "Học bạ".equals(cboPhuongThuc.getSelectedItem());
    }

    private boolean validSubjectScore(String value) {
        try {
            double score = Double.parseDouble(value.trim());
            return score >= 0 && score <= 10;
        } catch (Exception e) {
            return false;
        }
    }

    private String formatScore(double score) {
        return String.format(java.util.Locale.US, "%.2f", score)
                .replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private void updateScoreTotal() {
        try {
            if (!txtDiemMon1.getText().isBlank() && !txtDiemMon2.getText().isBlank()
                    && !txtDiemMon3.getText().isBlank()) {
                txtDiemXet.setText(formatScore(totalSubjectScore()));
            }
        } catch (NumberFormatException ignored) {}
        updateFormSummary();
    }

    /** Cập nhật vùng thông tin tóm tắt để phần nhập hồ sơ không còn khoảng trống. */
    private void updateFormSummary() {
        if (lblDiemChuanForm == null || lblKetQuaForm == null) return;
        NganhTuyenSinh ng = cboNganh == null ? null : (NganhTuyenSinh) cboNganh.getSelectedItem();
        if (ng == null) {
            lblDiemChuanForm.setText("-");
            lblKetQuaForm.setText("Chưa chọn ngành");
            lblKetQuaForm.setForeground(new Color(92, 99, 110));
            return;
        }

        double chuan = ng.getDiemChuanTam();
        lblDiemChuanForm.setText(formatScore(chuan));
        String totalText = txtDiemXet == null ? "" : txtDiemXet.getText().trim();
        if (totalText.isBlank()) {
            lblKetQuaForm.setText("Chưa nhập điểm");
            lblKetQuaForm.setForeground(new Color(92, 99, 110));
            return;
        }
        try {
            double total = Double.parseDouble(totalText);
            double priority = Double.parseDouble(txtDiemUuTien.getText().trim().isBlank()
                    ? "0" : txtDiemUuTien.getText().trim());
            boolean passed = total + priority >= chuan;
            lblKetQuaForm.setText(passed ? "Đạt điểm chuẩn sơ bộ" : "Chưa đạt điểm chuẩn");
            lblKetQuaForm.setForeground(passed ? new Color(25, 135, 84) : new Color(190, 55, 55));
        } catch (NumberFormatException ex) {
            lblKetQuaForm.setText("Điểm chưa hợp lệ");
            lblKetQuaForm.setForeground(new Color(190, 55, 55));
        }
    }

    // ── CRUD ─────────────────────────────────────────────────────────────────
    private void nopHoSo() {
        if (!validateForm()) return;
        try {
            ThiSinh ts = (ThiSinh) cboThiSinh.getSelectedItem();
            NganhTuyenSinh ng = (NganhTuyenSinh) cboNganh.getSelectedItem();
            String maHoSo = hoSoDao.generateNextId();
            HoSoDangKy hs = new HoSoDangKy(
                maHoSo, ts.getMaTS(), ng.getMaNganh(),
                Double.parseDouble(txtDiemXet.getText().trim()),
                ValidationUtil.parseDate(txtNgayNop.getText().trim()),
                HoSoDangKy.CHO_DUYET, txtGhiChu.getText().trim()
            );
            hs.setDiemUuTien(Double.parseDouble(txtDiemUuTien.getText().trim()));
            hs.setKhoiDangKy(selectedBlock());
            hs.setDiemMon1(Double.parseDouble(txtDiemMon1.getText().trim()));
            hs.setDiemMon2(Double.parseDouble(txtDiemMon2.getText().trim()));
            hs.setDiemMon3(Double.parseDouble(txtDiemMon3.getText().trim()));
            hs.setPhuongThuc((String) cboPhuongThuc.getSelectedItem());
            if (hoSoDao.insert(hs)) {
                AlertUtil.info(this, "Nộp hồ sơ thành công.\nMã hồ sơ: " + maHoSo);
                loadData(); clearForm();
            }
        } catch (Exception ex) { AlertUtil.error(this, "Lỗi: " + ex.getMessage()); }
    }

    private void capNhatHoSo() {
        if (selectedMaHoSo == null) { AlertUtil.error(this, "Vui lòng chọn 1 hồ sơ trên bảng để cập nhật."); return; }
        if (!validateForm()) return;
        try {
            HoSoDangKy hoSoCu = hoSoDao.getAll().stream()
                    .filter(item -> selectedMaHoSo.equals(item.getMaHoSo())).findFirst().orElse(null);
            if (hoSoCu == null) {
                AlertUtil.error(this, "Không tìm thấy hồ sơ cần cập nhật.");
                return;
            }
            ThiSinh ts = (ThiSinh) cboThiSinh.getSelectedItem();
            NganhTuyenSinh ng = (NganhTuyenSinh) cboNganh.getSelectedItem();
            String trangThaiCapNhat = hoSoCu.getTrangThai();
            // Mọi hồ sơ được chỉnh sửa phải xét lại từ đầu. Hồ sơ đã nhập học
            // được giữ nguyên vì đó là trạng thái hoàn tất quy trình.
            if (!HoSoDangKy.DA_NHAP_HOC.equals(trangThaiCapNhat)) {
                trangThaiCapNhat = HoSoDangKy.CHO_DUYET;
            }
            HoSoDangKy hs = new HoSoDangKy(
                selectedMaHoSo, ts.getMaTS(), ng.getMaNganh(),
                Double.parseDouble(txtDiemXet.getText().trim()),
                ValidationUtil.parseDate(txtNgayNop.getText().trim()),
                trangThaiCapNhat, txtGhiChu.getText().trim()
            );
            hs.setDiemUuTien(Double.parseDouble(txtDiemUuTien.getText().trim()));
            hs.setKhoiDangKy(selectedBlock());
            hs.setDiemMon1(Double.parseDouble(txtDiemMon1.getText().trim()));
            hs.setDiemMon2(Double.parseDouble(txtDiemMon2.getText().trim()));
            hs.setDiemMon3(Double.parseDouble(txtDiemMon3.getText().trim()));
            hs.setPhuongThuc((String) cboPhuongThuc.getSelectedItem());
            if (hoSoDao.update(hs)) {
                if (!trangThaiCapNhat.equals(hoSoCu.getTrangThai())) {
                    hoSoDao.updateTrangThai(selectedMaHoSo, trangThaiCapNhat);
                }
                String message = HoSoDangKy.DA_NHAP_HOC.equals(hoSoCu.getTrangThai())
                        ? "Cập nhật hồ sơ thành công. Hồ sơ đã nhập học được giữ nguyên trạng thái."
                        : "Cập nhật thành công. Hồ sơ đã được đưa về trạng thái chờ duyệt.";
                AlertUtil.info(this, message);
                loadData(); clearForm();
            }
        } catch (Exception ex) { AlertUtil.error(this, "Lỗi: " + ex.getMessage()); }
    }

    private void xoaHoSo() {
        if (selectedMaHoSo == null) { AlertUtil.error(this, "Vui lòng chọn 1 hồ sơ để xóa."); return; }
        if (!AlertUtil.confirm(this, "Bạn có chắc muốn xóa hồ sơ này?")) return;
        try {
            if (hoSoDao.delete(selectedMaHoSo)) { AlertUtil.info(this, "Xóa thành công."); loadData(); clearForm(); }
        } catch (Exception ex) { AlertUtil.error(this, "Lỗi: " + ex.getMessage()); }
    }

    private void duyetHoSo(String trangThaiMoi) {
        if (!admissionRole) { AlertUtil.error(this, "Tài khoản này không có quyền duyệt tuyển sinh."); return; }
        if (selectedMaHoSo == null) { AlertUtil.error(this, "Vui lòng chọn 1 hồ sơ trên bảng để xét duyệt."); return; }
        try {
            HoSoDangKy hoSo = hoSoDao.getAll().stream()
                    .filter(item -> selectedMaHoSo.equals(item.getMaHoSo())).findFirst().orElse(null);
            if (hoSo == null) {
                AlertUtil.error(this, "Không tìm thấy hồ sơ cần xét duyệt.");
                return;
            }
            if (!HoSoDangKy.CHO_DUYET.equals(hoSo.getTrangThai())) {
                AlertUtil.error(this, "Chỉ hồ sơ đang chờ duyệt mới được xét tuyển lại.");
                return;
            }
            if (trangThaiMoi.equals(HoSoDangKy.TRUNG_TUYEN)) {
                NganhTuyenSinh ng = nganhDao.getById(hoSo.getMaNganh());
                if (ng == null) {
                    AlertUtil.error(this, "Không tìm thấy ngành của hồ sơ.");
                    return;
                }
                boolean dungKhoi = isBlockAccepted(ng.getKhoiXetTuyen(), effectiveBlock(hoSo), hoSo.getPhuongThuc());
                boolean datDiem = hoSo.getDiemXet() + hoSo.getDiemUuTien() >= ng.getDiemChuanTam();
                if (!dungKhoi || !datDiem) {
                    AlertUtil.error(this, "Hồ sơ chưa đủ điều kiện: sai khối xét tuyển hoặc chưa đạt điểm chuẩn.");
                    return;
                }
                int daTrung = nganhDao.demSoTrungTuyen(ng.getMaNganh());
                if (daTrung >= ng.getChiTieu()) {
                    AlertUtil.error(this, "Ngành " + ng.getTenNganh() + " đã đủ chỉ tiêu.");
                    return;
                }
            }
            if (hoSoDao.updateTrangThai(selectedMaHoSo, trangThaiMoi)) {
                auditLogDAO.log(currentAccount, "DUYET_HO_SO", selectedMaHoSo + " -> " + trangThaiMoi);
                AlertUtil.info(this, "Cập nhật trạng thái thành công."); loadData(); clearForm();
            }
        } catch (Exception ex) { AlertUtil.error(this, "Lỗi: " + ex.getMessage()); }
    }

    private void xacNhanNhapHoc() {
        if (!admissionRole) { AlertUtil.error(this, "Tài khoản này không có quyền xác nhận nhập học."); return; }
        if (selectedMaHoSo == null) {
            AlertUtil.error(this, "Vui lòng chọn hồ sơ cần xác nhận nhập học.");
            return;
        }
        try {
            HoSoDangKy hoSo = hoSoDao.getAll().stream()
                    .filter(item -> selectedMaHoSo.equals(item.getMaHoSo())).findFirst().orElse(null);
            if (hoSo == null || !HoSoDangKy.TRUNG_TUYEN.equals(hoSo.getTrangThai())) {
                AlertUtil.error(this, "Chỉ hồ sơ đã trúng tuyển mới được xác nhận nhập học.");
                return;
            }
            if (hoSoDao.updateTrangThai(selectedMaHoSo, HoSoDangKy.DA_NHAP_HOC)) {
                auditLogDAO.log(currentAccount, "XAC_NHAN_NHAP_HOC", selectedMaHoSo);
                AlertUtil.info(this, "Đã xác nhận thí sinh nhập học.");
                loadData();
                clearForm();
            }
        } catch (Exception ex) {
            AlertUtil.error(this, "Lỗi xác nhận nhập học: " + ex.getMessage());
        }
    }

    private void tuDongXetDuyet() {
        if (!admissionRole) { AlertUtil.error(this, "Tài khoản này không có quyền tự động xét tuyển."); return; }
        if (!AlertUtil.confirm(this,
                "Thao tác này sẽ tự động xét duyệt TẤT CẢ hồ sơ đang 'Chờ duyệt'\n"
                + "theo điểm xét và chỉ tiêu còn lại của từng ngành.\nBạn có chắc muốn tiếp tục?")) return;
        try {
            int tongTrung = 0, tongKhong = 0;
            for (NganhTuyenSinh ng : nganhDao.getAll()) {
                // Chỉ xét các hồ sơ đang chờ duyệt; không xét lại hồ sơ đã bị từ chối
                // hoặc đã có kết quả trước đó.
                List<HoSoDangKy> choDuyet = hoSoDao.filter(ng.getMaNganh(), HoSoDangKy.CHO_DUYET);
                int daTrungTuyen = (int) hoSoDao.filter(ng.getMaNganh(), "Tất cả").stream()
                        .filter(h -> HoSoDangKy.TRUNG_TUYEN.equals(h.getTrangThai())
                                || HoSoDangKy.DA_NHAP_HOC.equals(h.getTrangThai()))
                        .count();
                int conLai = Math.max(0, ng.getChiTieu() - daTrungTuyen);
                for (HoSoDangKy hs : choDuyet) {
                    boolean dungKhoi = isBlockAccepted(ng.getKhoiXetTuyen(), effectiveBlock(hs), hs.getPhuongThuc());
                    boolean datDiem = (hs.getDiemXet() + hs.getDiemUuTien()) >= ng.getDiemChuanTam();
                    if (conLai > 0 && dungKhoi && datDiem) {
                        hoSoDao.updateTrangThai(hs.getMaHoSo(), HoSoDangKy.TRUNG_TUYEN);
                        conLai--; tongTrung++;
                    } else {
                        hoSoDao.updateTrangThai(hs.getMaHoSo(), HoSoDangKy.KHONG_TRUNG_TUYEN);
                        tongKhong++;
                    }
                }
            }
            AlertUtil.info(this, "Đã xét duyệt xong.\nTrúng tuyển: " + tongTrung
                    + "\nKhông trúng tuyển: " + tongKhong);
            auditLogDAO.log(currentAccount, "TU_DONG_XET_DUYET", "Trung tuyen: " + tongTrung + ", Khong trung: " + tongKhong);
            loadData(); clearForm();
        } catch (Exception ex) { AlertUtil.error(this, "Lỗi khi tự động xét duyệt: " + ex.getMessage()); }
    }

    private boolean isBlockAccepted(String configuredBlocks, String candidateBlock, String method) {
        if (configuredBlocks == null || configuredBlocks.isBlank()) return true;
        // Hồ sơ học bạ không bắt buộc thí sinh phải khai báo khối dự thi;
        // ngành đăng ký đã cung cấp danh sách khối dùng để xét tuyển.
        if (candidateBlock == null || candidateBlock.isBlank()) {
            return "Học bạ".equalsIgnoreCase(method);
        }
        for (String block : configuredBlocks.split(",")) {
            if (block.trim().equalsIgnoreCase(candidateBlock.trim())) return true;
        }
        return false;
    }

    private String selectedBlock() {
        String block = cboKhoiXet == null ? null : (String) cboKhoiXet.getSelectedItem();
        return block == null || block.isBlank() || "Chưa chọn".equals(block) ? null : block;
    }

    private String effectiveBlock(HoSoDangKy hs) {
        if (hs.getKhoiDangKy() != null && !hs.getKhoiDangKy().isBlank()) return hs.getKhoiDangKy();
        return hs.getKhoiDuThi();
    }

    private void xuatGiayBao() {
        if (selectedMaHoSo == null) { AlertUtil.error(this, "Vui lòng chọn hồ sơ cần xuất giấy báo."); return; }
        HoSoDangKy hoSo = hoSoDao.getAll().stream()
                .filter(item -> selectedMaHoSo.equals(item.getMaHoSo())).findFirst().orElse(null);
        if (hoSo == null || (!HoSoDangKy.TRUNG_TUYEN.equals(hoSo.getTrangThai())
                && !HoSoDangKy.DA_NHAP_HOC.equals(hoSo.getTrangThai()))) {
            AlertUtil.error(this, "Chỉ có thể xuất giấy báo cho hồ sơ trúng tuyển hoặc đã nhập học."); return;
        }
        ThiSinh thiSinh = (ThiSinh) cboThiSinh.getSelectedItem();
        NganhTuyenSinh nganh = (NganhTuyenSinh) cboNganh.getSelectedItem();
        if (thiSinh == null || nganh == null) {
            AlertUtil.error(this, "Không tìm thấy thông tin thí sinh hoặc ngành."); return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu giấy báo trúng tuyển");
        chooser.setSelectedFile(new File("giay-bao-" + selectedMaHoSo + ".pdf"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                PdfExportUtil.exportGiayBaoTrungTuyen(chooser.getSelectedFile().toPath(), hoSo, thiSinh, nganh);
                AlertUtil.info(this, "Xuất giấy báo trúng tuyển thành công.");
            } catch (Exception ex) { AlertUtil.error(this, "Không thể xuất giấy báo: " + ex.getMessage()); }
        }
    }
}
