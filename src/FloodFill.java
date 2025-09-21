import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FloodFill {

    private final BufferedImage image;
    private final int width, height;
    private int frameCount = 0;
    private int pixelCounter = 0;
    private final boolean useStack;

    public FloodFill(BufferedImage image, boolean useStack) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.useStack = useStack;

        // cria a pasta Images se não existir
        File dir = new File("Images");
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public void fill(int startX, int startY, Color newColor, FloodFillApp app) {
        int targetColor = image.getRGB(startX, startY);
        if (targetColor == newColor.getRGB()) return;

        FloodFillApp.Stack<Point> stack = new FloodFillApp.ArrayStack<>(1000);
        FloodFillApp.Queue<Point> queue = new FloodFillApp.ArrayQueue<>(1000);

        if (useStack) {
            stack.push(new Point(startX, startY));
        } else {
            queue.enqueue(new Point(startX, startY));
        }

        while ((useStack && !stack.isEmpty()) || (!useStack && !queue.isEmpty())) {
            Point p = useStack ? stack.pop() : queue.dequeue();
            int x = p.x;
            int y = p.y;

            if (x < 0 || x >= width || y < 0 || y >= height) continue;
            if (image.getRGB(x, y) != targetColor) continue;

            image.setRGB(x, y, newColor.getRGB());
            pixelCounter++;

            // salva imagem a cada 4 pixels
            if (pixelCounter % 4 == 0) {
                saveFrame();
                app.refresh();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ignored) {}
            }

            if (useStack) {
                stack.push(new Point(x + 1, y));
                stack.push(new Point(x - 1, y));
                stack.push(new Point(x, y + 1));
                stack.push(new Point(x, y - 1));
            } else {
                queue.enqueue(new Point(x + 1, y));
                queue.enqueue(new Point(x - 1, y));
                queue.enqueue(new Point(x, y + 1));
                queue.enqueue(new Point(x, y - 1));
            }
        }

        // salva imagem final
        saveFrame();
        app.refresh();
    }

    private void saveFrame() {
        try {
            String filename = String.format("Images/frame_%04d.png", frameCount++);
            ImageIO.write(image, "png", new File(filename));
            System.out.println("Imagem salva: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
