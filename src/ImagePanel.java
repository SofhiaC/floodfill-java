import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImagePanel extends JPanel {
    private BufferedImage img;
    private int crossX = -1, crossY = -1;

    public void setImage(BufferedImage b) {
        this.img = b;
        revalidate();
        repaint();
    }

    public void setCrosshair(int x, int y) {
        this.crossX = x;
        this.crossY = y;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (img == null) {
            g.setColor(Color.DARK_GRAY);
            g.drawString("Nenhuma imagem carregada", 20, 20);
            return;
        }
        double sx = (double) getWidth() / img.getWidth();
        double sy = (double) getHeight() / img.getHeight();
        double s = Math.min(sx, sy);
        int drawW = (int) (img.getWidth() * s);
        int drawH = (int) (img.getHeight() * s);
        int x0 = (getWidth() - drawW) / 2;
        int y0 = (getHeight() - drawH) / 2;
        g.drawImage(img, x0, y0, drawW, drawH, null);

        if (crossX >= 0 && crossY >= 0) {
            int cx = x0 + (int) (crossX * s);
            int cy = y0 + (int) (crossY * s);
            g.setColor(Color.YELLOW);
            g.drawLine(cx - 6, cy, cx + 6, cy);
            g.drawLine(cx, cy - 6, cx, cy + 6);
        }
    }
}
