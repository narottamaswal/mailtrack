package com.mailtrack.config;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.List;
import java.util.Random;

@Component
public class AvtarComponent {
    private static final List<String> DARK_COLORS = List.of("#1a1a2e", "#16213e", "#0f3460", "#1b262c", "#2d132c", "#1c3144", "#2b2d42", "#3d2b1f", "#1f3a3d", "#2c2c54");
    private static final List<String> LIGHT_COLORS = List.of("#e8f4fd", "#fef9e7", "#eafaf1", "#fdf2f8", "#f0f4f8", "#fffde7", "#f3e5f5", "#e8f5e9", "#fff8e1", "#fce4ec");

    public String generateAvatar(String name) {
        String[] parts = name.trim().split("\\s+");
        String initials = parts.length >= 2
                ? String.valueOf(parts[0].charAt(0)) + parts[parts.length - 1].charAt(0)
                : name.substring(0, Math.min(2, name.length()));
        initials = initials.toUpperCase();

        Random random = new Random();
        String bgHex = DARK_COLORS.get(random.nextInt(DARK_COLORS.size()));
        String textHex = LIGHT_COLORS.get(random.nextInt(LIGHT_COLORS.size()));

        int size = 80;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g.setColor(Color.decode(bgHex));
        g.fillRect(0, 0, size, size);

        g.setColor(Color.decode(textHex));
        g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 30));

        FontMetrics fm = g.getFontMetrics();
        int x = (size - fm.stringWidth(initials)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(initials, x, y);
        g.dispose();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(img, "png", out);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(out.toByteArray());
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate avatar", e);
        }
    }
}
