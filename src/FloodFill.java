import java.awt.image.BufferedImage;

public class FloodFill {

    public static void floodFillDFS(BufferedImage img, int startX, int startY, int newColor, int sleepMillis, ImagePanel panel) {
        int w = img.getWidth(), h = img.getHeight();
        if (!inBounds(startX, startY, w, h)) return;
        int background = img.getRGB(startX, startY);
        if (background == newColor) return;

        boolean[][] visited = new boolean[h][w];
        ArrayStack<Pixel> stack = new ArrayStack<>();
        stack.push(new Pixel(startX, startY));
        visited[startY][startX] = true;

        int[][] dirs = {{0,-1},{0,1},{-1,0},{1,0}};

        while (!stack.isEmpty()) {
            Pixel p = stack.pop();
            int x = p.x, y = p.y;
            if (!inBounds(x,y,w,h)) continue;
            if (img.getRGB(x,y) != background) continue;
            img.setRGB(x,y,newColor);
            panel.repaint();
            try { Thread.sleep(sleepMillis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            for (int[] d : dirs) {
                int nx = x + d[0], ny = y + d[1];
                if (inBounds(nx,ny,w,h) && !visited[ny][nx]) {
                    if (img.getRGB(nx,ny) == background) {
                        stack.push(new Pixel(nx,ny));
                        visited[ny][nx] = true;
                    }
                }
            }
        }
    }

    public static void floodFillBFS(BufferedImage img, int startX, int startY, int newColor, int sleepMillis, ImagePanel panel) {
        int w = img.getWidth(), h = img.getHeight();
        if (!inBounds(startX, startY, w, h)) return;
        int background = img.getRGB(startX, startY);
        if (background == newColor) return;

        boolean[][] visited = new boolean[h][w];
        ArrayQueue<Pixel> queue = new ArrayQueue<>();
        queue.enqueue(new Pixel(startX, startY));
        visited[startY][startX] = true;

        int[][] dirs = {{0,-1},{0,1},{-1,0},{1,0}};

        while (!queue.isEmpty()) {
            Pixel p = queue.dequeue();
            int x = p.x, y = p.y;
            if (!inBounds(x,y,w,h)) continue;
            if (img.getRGB(x,y) != background) continue;
            img.setRGB(x,y,newColor);
            panel.repaint();
            try { Thread.sleep(sleepMillis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            for (int[] d : dirs) {
                int nx = x + d[0], ny = y + d[1];
                if (inBounds(nx,ny,w,h) && !visited[ny][nx]) {
                    if (img.getRGB(nx,ny) == background) {
                        queue.enqueue(new Pixel(nx,ny));
                        visited[ny][nx] = true;
                    }
                }
            }
        }
    }

    private static boolean inBounds(int x, int y, int w, int h) {
        return x >= 0 && x < w && y >= 0 && y < h;
    }
}
