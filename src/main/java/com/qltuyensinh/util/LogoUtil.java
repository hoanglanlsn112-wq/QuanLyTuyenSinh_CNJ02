package com.qltuyensinh.util;

import javax.swing.ImageIcon;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;

public final class LogoUtil {
    private LogoUtil() { }

    public static ImageIcon load(int size) {
        URL resource = resource();
        if (resource == null) return null;
        try {
            BufferedImage original = ImageIO.read(resource);
            if (original == null) return null;
            
            // Render dư pixel để không bị vỡ khi Windows/Java phóng DPI màn hình.
            int pixelSize = Math.max(size * 3, 270);
            BufferedImage scaled = new BufferedImage(pixelSize, pixelSize, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = scaled.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            g2d.drawImage(original, 0, 0, pixelSize, pixelSize, null);
            g2d.dispose();
            
            // Icon giữ kích thước layout cũ nhưng vẽ bitmap lớn xuống đúng kích thước đó.
            return new ImageIcon(scaled) {
                @Override public int getIconWidth() { return size; }
                @Override public int getIconHeight() { return size; }
                @Override public void paintIcon(java.awt.Component c, java.awt.Graphics g, int x, int y) {
                    Graphics2D graphics = (Graphics2D) g.create();
                    graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
                    graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    graphics.drawImage(scaled, x, y, size, size, null);
                    graphics.dispose();
                }
            };
        } catch (Exception e) {
            return null;
        }
    }

    public static URL resource() {
        URL resource = LogoUtil.class.getResource("/logo-eaut.png");
        if (resource != null) return resource;
        for (Path path : new Path[]{
                Path.of("resources", "logo-eaut.png"),
                Path.of("QuanLyTuyenSinh", "resources", "logo-eaut.png"),
                Path.of("C:/Users/Admin/Downloads/QuanLyTuyenSinh_CNJ02/QuanLyTuyenSinh/resources/logo-eaut.png")
        }) {
            if (Files.exists(path)) {
                try {
                    return path.toUri().toURL();
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }
}
