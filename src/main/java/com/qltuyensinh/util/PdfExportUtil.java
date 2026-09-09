package com.qltuyensinh.util;

import com.qltuyensinh.model.HoSoDangKy;
import com.qltuyensinh.model.NganhTuyenSinh;
import com.qltuyensinh.model.ThiSinh;
import java.io.IOException;
import java.io.File;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Tao bao cao PDF dang bang, khong can thu vien ben ngoai. */
public final class PdfExportUtil {
    private PdfExportUtil() { }

    public static void exportThongKe(Path file, List<Object[]> rows) throws IOException {
        writeImagePdf(file, renderStatisticsReport(rows));
    }

    /** Báo cáo thống kê dạng dashboard, giữ nguyên tiếng Việt có dấu. */
    private static BufferedImage renderStatisticsReport(List<Object[]> sourceRows) {
        List<Object[]> rows = sourceRows == null ? new ArrayList<>() : sourceRows;
        int width = 1600, height = 1250, margin = 60;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(new Color(246, 249, 253)); g.fillRect(0, 0, width, height);

        g.setColor(new Color(25, 76, 130)); g.fillRect(0, 0, width, 170);
        try {
            URL logoUrl = LogoUtil.resource();
            if (logoUrl != null) {
                BufferedImage logo = javax.imageio.ImageIO.read(logoUrl);
                if (logo != null) g.drawImage(logo, margin, 25, 115, 115, null);
            }
        } catch (IOException ignored) { }
        g.setColor(Color.WHITE); g.setFont(new Font("Segoe UI", Font.BOLD, 30));
        g.drawString("TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á", 205, 65);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 21));
        g.drawString("HỘI ĐỒNG TUYỂN SINH  •  BÁO CÁO QUẢN TRỊ", 205, 105);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        g.drawString("Ngày xuất báo cáo: " + ValidationUtil.formatDate(LocalDate.now()), 205, 138);

        int total = 0, waiting = 0, passed = 0, enrolled = 0, failed = 0;
        for (Object[] r : rows) { total += intValue(r, 7); waiting += intValue(r, 3); passed += intValue(r, 4); enrolled += intValue(r, 5); failed += intValue(r, 6); }
        String[] cardTitles = {"TỔNG HỒ SƠ", "CHỜ DUYỆT", "TRÚNG TUYỂN", "ĐÃ NHẬP HỌC", "KHÔNG TRÚNG TUYỂN"};
        int[] cardValues = {total, waiting, passed, enrolled, failed};
        Color[] cardColors = {new Color(25, 76, 130), new Color(211, 137, 0), new Color(35, 145, 76), new Color(34, 126, 184), new Color(194, 51, 55)};
        int cardY = 205, cardW = 280, cardH = 120, gap = 20;
        for (int i = 0; i < cardTitles.length; i++) {
            int x = margin + i * (cardW + gap);
            g.setColor(Color.WHITE); g.fillRoundRect(x, cardY, cardW, cardH, 16, 16);
            // Một nét duy nhất cho dải màu; vẽ thêm hình chữ nhật thứ hai
            // khiến mép khử răng cưa bị lộ thành hai vạch chồng lên nhau.
            g.setColor(cardColors[i]); g.fillRoundRect(x, cardY, 14, cardH, 16, 16);
            g.setColor(new Color(85, 98, 115)); g.setFont(new Font("Segoe UI", Font.BOLD, 15)); g.drawString(cardTitles[i], x + 25, cardY + 34);
            g.setColor(cardColors[i]); g.setFont(new Font("Segoe UI", Font.BOLD, 36)); g.drawString(String.valueOf(cardValues[i]), x + 25, cardY + 84);
        }

        int tableX = margin, tableY = 370, tableW = width - margin * 2, rowH = 40, headerH = 48;
        g.setColor(Color.WHITE); g.fillRoundRect(tableX, tableY, tableW, headerH + Math.max(1, rows.size()) * rowH + 20, 14, 14);
        g.setColor(new Color(25, 76, 130)); g.fillRoundRect(tableX, tableY, tableW, headerH, 14, 14); g.fillRect(tableX, tableY + 24, tableW, 24);
        String[] headers = {"Mã ngành", "Tên ngành", "Chỉ tiêu", "Chờ duyệt", "Trúng tuyển", "Đã nhập học", "Không trúng tuyển", "Tổng hồ sơ"};
        int[] colW = {120, 360, 110, 150, 150, 150, 220, 150};
        int x = tableX;
        g.setColor(Color.WHITE); g.setFont(new Font("Segoe UI", Font.BOLD, 15));
        for (int i = 0; i < headers.length; i++) { g.drawString(headers[i], x + 14, tableY + 30); x += colW[i]; }
        for (int rIndex = 0; rIndex < rows.size(); rIndex++) {
            Object[] row = rows.get(rIndex); int y = tableY + headerH + rIndex * rowH;
            g.setColor(rIndex % 2 == 0 ? Color.WHITE : new Color(241, 246, 252)); g.fillRect(tableX, y, tableW, rowH);
            x = tableX; g.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            for (int i = 0; i < headers.length; i++) {
                Color textColor = (i == 4 ? new Color(25, 125, 65) : i == 5 ? new Color(25, 105, 165) : i == 6 ? new Color(180, 40, 45) : new Color(35, 50, 70));
                if (i == 0 || i >= 2) g.setFont(new Font("Segoe UI", Font.BOLD, 15));
                g.setColor(textColor);
                String cell = value(row, i);
                if (i >= 2) { int sw = g.getFontMetrics().stringWidth(cell); g.drawString(cell, x + (colW[i] - sw) / 2, y + 26); }
                else g.drawString(fitToWidth(g, cell, colW[i] - 24), x + 12, y + 26);
                x += colW[i];
            }
            g.setColor(new Color(222, 230, 239)); g.drawLine(tableX, y + rowH - 1, tableX + tableW, y + rowH - 1);
        }
        int footerY = Math.min(height - 45, tableY + headerH + rows.size() * rowH + 38);
        g.setColor(new Color(95, 108, 125)); g.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        g.drawString("Báo cáo gồm " + rows.size() + " ngành tuyển sinh  •  Dữ liệu được lấy từ hệ thống quản lý tuyển sinh", margin, footerY);
        g.dispose(); return image;
    }

    private static int intValue(Object[] row, int index) {
        try { return Integer.parseInt(value(row, index)); } catch (NumberFormatException e) { return 0; }
    }

    private static String fitToWidth(Graphics2D g, String value, int width) {
        String s = value == null ? "" : value;
        if (g.getFontMetrics().stringWidth(s) <= width) return s;
        while (s.length() > 3 && g.getFontMetrics().stringWidth(s + "…") > width) s = s.substring(0, s.length() - 1);
        return s + "…";
    }

    public static void exportGiayBaoTrungTuyen(Path file, HoSoDangKy hoSo, ThiSinh thiSinh, NganhTuyenSinh nganh) throws IOException {
        if (!HoSoDangKy.TRUNG_TUYEN.equals(hoSo.getTrangThai())
                && !HoSoDangKy.DA_NHAP_HOC.equals(hoSo.getTrangThai())) {
            throw new IOException("Chỉ có thể xuất giấy báo cho hồ sơ trúng tuyển hoặc đã nhập học.");
        }
        writeImagePdf(file, renderAdmissionNotice(hoSo, thiSinh, nganh));
    }

    /** Giấy báo được vẽ bằng font hệ thống để giữ nguyên tiếng Việt có dấu. */
    private static BufferedImage renderAdmissionNotice(HoSoDangKy hs, ThiSinh ts, NganhTuyenSinh ng) {
        int width = 1100, height = 1555, margin = 75;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setColor(Color.WHITE); g.fillRect(0, 0, width, height);
        g.setColor(new Color(25, 76, 130)); g.setStroke(new java.awt.BasicStroke(5));
        g.drawRoundRect(25, 25, width - 50, height - 50, 22, 22);
        g.setStroke(new java.awt.BasicStroke(1));

        int y = 92;
        try {
            URL logoUrl = LogoUtil.resource();
            if (logoUrl != null) {
                BufferedImage logo = javax.imageio.ImageIO.read(logoUrl);
                g.drawImage(logo, margin, 55, 125, 125, null);
            }
        } catch (IOException ignored) { }
        g.setColor(new Color(25, 76, 130));
        g.setFont(new Font("Segoe UI", Font.BOLD, 25));
        center(g, "TRƯỜNG ĐẠI HỌC CÔNG NGHỆ ĐÔNG Á", width / 2, y);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        center(g, "HỘI ĐỒNG TUYỂN SINH", width / 2, y + 34);
        g.setColor(new Color(210, 120, 0)); g.setStroke(new java.awt.BasicStroke(3));
        g.drawLine(margin, 190, width - margin, 190);
        g.setColor(new Color(25, 76, 130));
        g.setFont(new Font("Segoe UI", Font.BOLD, 31));
        center(g, "GIẤY BÁO TRÚNG TUYỂN", width / 2, 250);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        center(g, "Năm tuyển sinh " + (hs.getNgayNop() == null ? LocalDate.now().getYear() : hs.getNgayNop().getYear()), width / 2, 285);

        y = 355;
        g.setColor(new Color(45, 45, 45));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 19));
        y = drawWrapped(g, "Hội đồng tuyển sinh Trường Đại học Công nghệ Đông Á trân trọng thông báo:", margin, y, width - margin * 2, 29);
        y += 18;
        g.setFont(new Font("Segoe UI", Font.BOLD, 21));
        y = drawWrapped(g, "Thí sinh " + safe(ts.getHoTen()).toUpperCase() + " đã đủ điều kiện trúng tuyển vào trường.", margin, y, width - margin * 2, 31);
        y += 18;

        int tableTop = y;
        int tableHeight = 470;
        g.setColor(new Color(244, 248, 253)); g.fillRoundRect(margin, tableTop, width - margin * 2, tableHeight, 12, 12);
        g.setColor(new Color(175, 198, 222)); g.drawRoundRect(margin, tableTop, width - margin * 2, tableHeight, 12, 12);

        // Ảnh thí sinh được lấy từ trường anh_the trong hồ sơ và đặt ở bên
        // phải bảng thông tin như ảnh thẻ trên giấy báo tuyển sinh.
        int photoW = 170, photoH = 215;
        int photoX = width - margin - photoW - 24;
        int photoY = tableTop + 32;
        BufferedImage candidatePhoto = loadCandidatePhoto(ts.getAnhThe());
        g.setColor(Color.WHITE); g.fillRect(photoX, photoY, photoW, photoH);
        g.setColor(new Color(145, 166, 190)); g.drawRect(photoX, photoY, photoW, photoH);
        if (candidatePhoto != null) drawPhotoCover(g, candidatePhoto, photoX + 1, photoY + 1, photoW - 2, photoH - 2);
        else {
            g.setColor(new Color(105, 120, 140));
            g.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            center(g, "Chưa có ảnh", photoX + photoW / 2, photoY + photoH / 2);
        }

        int rowY = tableTop + 43;
        rowY = infoRow(g, "Mã hồ sơ", hs.getMaHoSo(), margin + 22, rowY);
        rowY = infoRow(g, "Mã thí sinh", ts.getMaTS(), margin + 22, rowY);
        rowY = infoRow(g, "Ngày sinh", ValidationUtil.formatDate(ts.getNgaySinh()), margin + 22, rowY);
        rowY = infoRow(g, "Giới tính", dash(ts.getGioiTinh()), margin + 22, rowY);
        rowY = infoRow(g, "CCCD", dash(ts.getCccd()), margin + 22, rowY);
        rowY = infoRow(g, "Địa chỉ", dash(ts.getDiaChi()), margin + 22, rowY);
        rowY = infoRow(g, "Ngành trúng tuyển", safe(ng.getTenNganh()), margin + 22, rowY);
        rowY = infoRow(g, "Mã ngành / Khối", safe(ng.getMaNganh()) + " / " + dash(ng.getKhoiXetTuyen()), margin + 22, rowY);
        rowY = infoRow(g, "Phương thức", dash(hs.getPhuongThuc()), margin + 22, rowY);
        rowY = infoRow(g, "Điểm các môn", formatScores(hs), margin + 22, rowY);
        rowY = infoRow(g, "Điểm xét tuyển", String.valueOf(hs.getDiemXet()) + "   (Ưu tiên: " + hs.getDiemUuTien() + ")", margin + 22, rowY);
        if (hs.getNgayNhapHoc() != null)
            rowY = infoRow(g, "Ngày nhập học", ValidationUtil.formatDate(hs.getNgayNhapHoc()), margin + 22, rowY);
        infoRow(g, "Trạng thái", HoSoDangKy.DA_NHAP_HOC.equals(hs.getTrangThai()) ? "Đã nhập học" : "Trúng tuyển", margin + 22, rowY);

        y = tableTop + tableHeight + 48;
        g.setColor(new Color(25, 76, 130)); g.setFont(new Font("Segoe UI", Font.BOLD, 20));
        g.drawString("HƯỚNG DẪN NHẬP HỌC", margin, y); y += 34;
        g.setColor(new Color(45, 45, 45)); g.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        y = drawWrapped(g, "1. Mang theo giấy báo này, CCCD bản gốc và bản sao các giấy tờ trong hồ sơ.", margin, y, width - margin * 2, 27);
        y = drawWrapped(g, "2. Kiểm tra lịch nhập học và hoàn tất học phí theo thông báo của nhà trường.", margin, y + 7, width - margin * 2, 27);
        y = drawWrapped(g, "3. Giấy báo chỉ có giá trị khi thông tin trên giấy trùng khớp với hồ sơ tại trường.", margin, y + 7, width - margin * 2, 27);
        y = drawWrapped(g, "Liên hệ Phòng Tuyển sinh để được hướng dẫn nếu thông tin trên giấy báo chưa chính xác.", margin, y + 7, width - margin * 2, 27);
        g.setFont(new Font("Segoe UI", Font.ITALIC, 15));
        g.drawString("Ngày xuất: " + ValidationUtil.formatDate(LocalDate.now()), margin, height - 120);
        g.setFont(new Font("Segoe UI", Font.BOLD, 17));
        center(g, "HỘI ĐỒNG TUYỂN SINH", width - 245, height - 160);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        center(g, "(Ký và xác nhận)", width - 245, height - 130);
        g.dispose();
        return image;
    }

    private static int infoRow(Graphics2D g, String label, String value, int x, int y) {
        g.setColor(new Color(25, 76, 130)); g.setFont(new Font("Segoe UI", Font.BOLD, 17));
        g.drawString(label + ":", x, y);
        g.setColor(new Color(45, 45, 45)); g.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        g.drawString(value == null ? "-" : value, x + 235, y);
        return y + 34;
    }

    private static String formatScores(HoSoDangKy hs) {
        return "Môn 1: " + hs.getDiemMon1() + "   Môn 2: " + hs.getDiemMon2() + "   Môn 3: " + hs.getDiemMon3();
    }

    private static BufferedImage loadCandidatePhoto(String path) {
        if (path == null || path.isBlank()) return null;
        File file = new File(path);
        if (!file.isFile()) file = new File("QuanLyTuyenSinh", path);
        try { return javax.imageio.ImageIO.read(file); }
        catch (IOException ignored) { return null; }
    }

    private static void drawPhotoCover(Graphics2D g, BufferedImage source, int x, int y, int width, int height) {
        double scale = Math.max((double) width / source.getWidth(), (double) height / source.getHeight());
        int drawW = (int) Math.ceil(source.getWidth() * scale);
        int drawH = (int) Math.ceil(source.getHeight() * scale);
        int drawX = x + (width - drawW) / 2;
        int drawY = y + (height - drawH) / 2;
        java.awt.Shape oldClip = g.getClip();
        g.clipRect(x, y, width, height);
        g.drawImage(source, drawX, drawY, drawW, drawH, null);
        g.setClip(oldClip);
    }

    private static int drawWrapped(Graphics2D g, String value, int x, int y, int maxWidth, int lineHeight) {
        FontMetrics fm = g.getFontMetrics(); StringBuilder line = new StringBuilder();
        for (String word : value.split(" ")) {
            String test = line.length() == 0 ? word : line + " " + word;
            if (fm.stringWidth(test) > maxWidth && line.length() > 0) {
                g.drawString(line.toString(), x, y); y += lineHeight; line.setLength(0); line.append(word);
            } else line = new StringBuilder(test);
        }
        if (line.length() > 0) { g.drawString(line.toString(), x, y); y += lineHeight; }
        return y;
    }

    private static void center(Graphics2D g, String text, int x, int y) {
        FontMetrics fm = g.getFontMetrics(); g.drawString(text, x - fm.stringWidth(text) / 2, y);
    }

    private static String safe(String s) { return s == null || s.isBlank() ? "-" : s; }

    private static void writeImagePdf(Path file, BufferedImage image) throws IOException {
        int w = image.getWidth(), h = image.getHeight(); byte[] rgb = new byte[w * h * 3]; int p = 0;
        for (int y = 0; y < h; y++) for (int x = 0; x < w; x++) {
            int c = image.getRGB(x, y); rgb[p++] = (byte)(c >> 16); rgb[p++] = (byte)(c >> 8); rgb[p++] = (byte)c;
        }
        byte[] content = bytes("q " + w + " 0 0 " + h + " 0 0 cm /Im1 Do Q\n");
        StringBuilder pdf = new StringBuilder("%PDF-1.4\n"); List<Integer> offsets = new ArrayList<>();
        byte[][] objects = {
            bytes("<< /Type /Catalog /Pages 2 0 R >>"),
            bytes("<< /Type /Pages /Kids [3 0 R] /Count 1 >>"),
            bytes("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + w + " " + h + "] /Resources << /XObject << /Im1 5 0 R >> >> /Contents 4 0 R >>"),
            bytes("<< /Length " + content.length + " >>\nstream\n" + new String(content, StandardCharsets.ISO_8859_1) + "endstream"),
            imageObject(new LogoImage(w, h, rgb))
        };
        for (int i = 0; i < objects.length; i++) { offsets.add(pdf.toString().getBytes(StandardCharsets.ISO_8859_1).length); pdf.append(i + 1).append(" 0 obj\n").append(new String(objects[i], StandardCharsets.ISO_8859_1)).append("\nendobj\n"); }
        int xref = pdf.toString().getBytes(StandardCharsets.ISO_8859_1).length; pdf.append("xref\n0 6\n0000000000 65535 f \n");
        for (int offset : offsets) pdf.append(String.format("%010d 00000 n \n", offset));
        pdf.append("trailer\n<< /Size 6 /Root 1 0 R >>\nstartxref\n").append(xref).append("\n%%EOF\n");
        Files.write(file, pdf.toString().getBytes(StandardCharsets.ISO_8859_1));
    }

    private static void writePdf(Path file, String stream, float w, float h, LogoImage logo) throws IOException {
        writePdf(file, stream, w, h, logo, null);
    }

    private static void writePdf(Path file, String stream, float w, float h, LogoImage logo, LogoImage photo) throws IOException {
        List<byte[]> o = new ArrayList<>(); byte[] c = bytes(stream);
        o.add(bytes("<< /Type /Catalog /Pages 2 0 R >>"));
        o.add(bytes("<< /Type /Pages /Kids [3 0 R] /Count 1 >>"));
        String xobjects = "/Im1 6 0 R" + (photo == null ? "" : " /Im2 7 0 R");
        o.add(bytes("<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + w + " " + h + "] /Resources << /Font << /F1 4 0 R >> /XObject << " + xobjects + " >> >> /Contents 5 0 R >>"));
        o.add(bytes("<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>"));
        o.add(bytes("<< /Length " + c.length + " >>\nstream\n" + stream + "endstream"));
        o.add(imageObject(logo));
        if (photo != null) o.add(imageObject(photo));
        StringBuilder pdf = new StringBuilder("%PDF-1.4\n"); List<Integer> offsets = new ArrayList<>();
        for (int i = 0; i < o.size(); i++) { offsets.add(pdf.toString().getBytes(StandardCharsets.ISO_8859_1).length); pdf.append(i + 1).append(" 0 obj\n").append(new String(o.get(i), StandardCharsets.ISO_8859_1)).append("\nendobj\n"); }
        int xref = pdf.toString().getBytes(StandardCharsets.ISO_8859_1).length; pdf.append("xref\n0 ").append(o.size() + 1).append("\n0000000000 65535 f \n");
        for (int offset : offsets) pdf.append(String.format("%010d 00000 n \n", offset));
        pdf.append("trailer\n<< /Size ").append(o.size() + 1).append(" /Root 1 0 R >>\nstartxref\n").append(xref).append("\n%%EOF\n"); Files.write(file, pdf.toString().getBytes(StandardCharsets.ISO_8859_1));
    }

    private static void text(StringBuilder s, float x, float y, float size, String value) { s.append("0.10 0.10 0.10 rg BT /F1 ").append(size).append(" Tf ").append(x).append(' ').append(y).append(" Td (").append(escape(toAscii(value))).append(") Tj ET\n"); }
    private static String value(Object[] r, int i) { return i < r.length && r[i] != null ? r[i].toString() : ""; }
    private static String fit(String v, int max) { String s = toAscii(v); return s.length() <= max ? s : s.substring(0, Math.max(0, max - 3)) + "..."; }
    private static String dash(String v) { return v == null || v.isBlank() ? "-" : v; }
    private static float sum(float[] a, int n) { float s = 0; for (int i = 0; i < n; i++) s += a[i]; return s; }
    private static String toAscii(String v) { return Normalizer.normalize(v == null ? "" : v, Normalizer.Form.NFD).replaceAll("\\p{M}", "").replace("đ", "d").replace("Đ", "D"); }
    private static String escape(String v) { return v.replace("\\", "\\\\").replace("(", "\\(").replace(")", "\\)"); }
    private static byte[] bytes(String s) { return s.getBytes(StandardCharsets.ISO_8859_1); }

    private static byte[] imageObject(LogoImage logo) {
        byte[] head = bytes("<< /Type /XObject /Subtype /Image /Width " + logo.width + " /Height " + logo.height + " /ColorSpace /DeviceRGB /BitsPerComponent 8 /Length " + logo.rgb.length + " >>\nstream\n");
        byte[] tail = bytes("\nendstream"); byte[] result = new byte[head.length + logo.rgb.length + tail.length];
        System.arraycopy(head, 0, result, 0, head.length); System.arraycopy(logo.rgb, 0, result, head.length, logo.rgb.length); System.arraycopy(tail, 0, result, head.length + logo.rgb.length, tail.length); return result;
    }

    private static LogoImage logoRgb() {
        try {
            URL url = LogoUtil.resource();
            if (url == null) return null;
            BufferedImage source = javax.imageio.ImageIO.read(url);
            int size = 600;
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();
            g.setColor(Color.WHITE); g.fillRect(0, 0, size, size);
            g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(source, 0, 0, size, size, null); g.dispose();
            byte[] rgb = new byte[size * size * 3]; int p = 0;
            for (int y = 0; y < size; y++) for (int x = 0; x < size; x++) {
                int c = image.getRGB(x, y); rgb[p++] = (byte)(c >> 16); rgb[p++] = (byte)(c >> 8); rgb[p++] = (byte)c;
            }
            return new LogoImage(size, size, rgb);
        } catch (IOException e) { return null; }
    }

    private static LogoImage photoRgb(String path) {
        if (path == null || path.isBlank()) return null;
        try {
            BufferedImage source = javax.imageio.ImageIO.read(new java.io.File(path));
            if (source == null) return null;
            int width = 240, height = 300;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics(); g.setColor(Color.WHITE); g.fillRect(0, 0, width, height);
            g.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.drawImage(source, 0, 0, width, height, null); g.dispose();
            byte[] rgb = new byte[width * height * 3]; int p = 0;
            for (int y = 0; y < height; y++) for (int x = 0; x < width; x++) { int c = image.getRGB(x, y); rgb[p++] = (byte)(c >> 16); rgb[p++] = (byte)(c >> 8); rgb[p++] = (byte)c; }
            return new LogoImage(width, height, rgb);
        } catch (IOException e) { return null; }
    }

    private record LogoImage(int width, int height, byte[] rgb) { }
}
