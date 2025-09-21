import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * FloodFillApp - versão corrigida sem classes duplicadas.
 * Use JDK 11+; cole em src/FloodFillApp.java e execute.
 */
public class FloodFillApp {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FloodFillApp().createAndShowGui());
    }

    private JFrame frame;
    private ImagePanel imagePanel;
    private BufferedImage image;
    private JButton loadBtn, runBtn;
    private JRadioButton dfsBtn, bfsBtn;
    private JLabel infoLabel;
    private Color fillColor = Color.RED;
    private volatile Point startPoint = null;

    private void createAndShowGui() {
        frame = new JFrame("FloodFill - DFS (pilha) / BFS (fila)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);
        frame.setLayout(new BorderLayout());

        imagePanel = new ImagePanel();
        imagePanel.setPreferredSize(new Dimension(800, 600));
        frame.add(new JScrollPane(imagePanel), BorderLayout.CENTER);

        JPanel controls = new JPanel();
        loadBtn = new JButton("Carregar imagem");
        runBtn = new JButton("Executar floodfill");
        dfsBtn = new JRadioButton("Pilha (DFS)");
        bfsBtn = new JRadioButton("Fila (BFS)");
        ButtonGroup g = new ButtonGroup();
        g.add(dfsBtn); g.add(bfsBtn);
        dfsBtn.setSelected(true);

        JButton chooseColorBtn = new JButton("Escolher cor");
        infoLabel = new JLabel("Clique na imagem para escolher ponto inicial.");

        controls.add(loadBtn);
        controls.add(dfsBtn);
        controls.add(bfsBtn);
        controls.add(chooseColorBtn);
        controls.add(runBtn);
        controls.add(infoLabel);

        frame.add(controls, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> loadImage());
        chooseColorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(frame, "Escolha a cor de preenchimento", fillColor);
            if (c != null) fillColor = c;
        });

        imagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (image == null) return;
                Point p = imagePanel.panelToImageCoords(e.getPoint());
                if (p != null) {
                    startPoint = p;
                    infoLabel.setText("Ponto inicial: (" + p.x + ", " + p.y + ")");
                    imagePanel.setCrosshair(p.x, p.y);
                }
            }
        });

        runBtn.addActionListener(e -> {
            if (image == null) { JOptionPane.showMessageDialog(frame, "Carregue uma imagem primeiro."); return; }
            if (startPoint == null) { JOptionPane.showMessageDialog(frame, "Clique na imagem para escolher o ponto inicial."); return; }
            runBtn.setEnabled(false);
            loadBtn.setEnabled(false);
            boolean useDFS = dfsBtn.isSelected();
            new Thread(() -> {
                try {
                    if (useDFS) floodFillDFS(image, startPoint.x, startPoint.y, fillColor.getRGB(), 20);
                    else floodFillBFS(image, startPoint.x, startPoint.y, fillColor.getRGB(), 20);
                    JOptionPane.showMessageDialog(frame, "Floodfill concluído.");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "Erro: " + ex.getMessage());
                } finally {
                    runBtn.setEnabled(true);
                    loadBtn.setEnabled(true);
                }
            }, "FloodFill-Thread").start();
        });

        frame.setVisible(true);
    }

    private void loadImage() {
        JFileChooser fc = new JFileChooser(".");
        int res = fc.showOpenDialog(frame);
        if (res == JFileChooser.APPROVE_OPTION) {
            try {
                File f = fc.getSelectedFile();
                image = ImageIO.read(f);
                if (image == null) throw new RuntimeException("Formato de imagem não suportado");
                imagePanel.setImage(image);
                startPoint = null;
                imagePanel.setCrosshair(-1, -1);
                infoLabel.setText("Clique na imagem para escolher ponto inicial.");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erro ao carregar imagem: " + ex.getMessage());
            }
        }
    }

    // ---------- FloodFill DFS (pilha) ----------
    private void floodFillDFS(BufferedImage img, int startX, int startY, int newColor, int sleepMillis) {
        int w = img.getWidth(), h = img.getHeight();
        if (!inBounds(startX, startY, w, h)) return;
        int background = img.getRGB(startX, startY);
        if (background == newColor) return;

        boolean[][] visited = new boolean[h][w];
        ArrayStack<Pixel> stack = new ArrayStack<>();
        stack.push(new Pixel(startX, startY));
        visited[startY][startX] = true;

        int filledCount = 0;
        int saveCounter = 0;
        int[][] dirs = {{0,-1},{0,1},{-1,0},{1,0}};

        while (!stack.isEmpty()) {
            Pixel p = stack.pop();
            int x = p.x, y = p.y;
            if (!inBounds(x,y,w,h)) continue;
            if (img.getRGB(x,y) != background) continue;
            img.setRGB(x,y,newColor);
            filledCount++;
            imagePanel.repaint();
            try { Thread.sleep(sleepMillis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            if (filledCount % 4 == 0) {
                saveCounter++;
                saveImageCopy(img, saveCounter);
            }

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

    // ---------- FloodFill BFS (fila) ----------
    private void floodFillBFS(BufferedImage img, int startX, int startY, int newColor, int sleepMillis) {
        int w = img.getWidth(), h = img.getHeight();
        if (!inBounds(startX, startY, w, h)) return;
        int background = img.getRGB(startX, startY);
        if (background == newColor) return;

        boolean[][] visited = new boolean[h][w];
        ArrayQueue<Pixel> queue = new ArrayQueue<>();
        queue.enqueue(new Pixel(startX, startY));
        visited[startY][startX] = true;

        int filledCount = 0;
        int saveCounter = 0;
        int[][] dirs = {{0,-1},{0,1},{-1,0},{1,0}};

        while (!queue.isEmpty()) {
            Pixel p = queue.dequeue();
            int x = p.x, y = p.y;
            if (!inBounds(x,y,w,h)) continue;
            if (img.getRGB(x,y) != background) continue;
            img.setRGB(x,y,newColor);
            filledCount++;
            imagePanel.repaint();
            try { Thread.sleep(sleepMillis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

            if (filledCount % 4 == 0) {
                saveCounter++;
                saveImageCopy(img, saveCounter);
            }

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

    private boolean inBounds(int x, int y, int w, int h) {
        return x >= 0 && x < w && y >= 0 && y < h;
    }

    private synchronized void saveImageCopy(BufferedImage img, int counter) {
        try {
            String filename = String.format("output_%04d.png", counter);
            ImageIO.write(img, "png", new File(filename));
            System.out.println("Salvou: " + filename);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ---------- UI Panel ----------
    private class ImagePanel extends JPanel {
        private BufferedImage img;
        private int crossX = -1, crossY = -1;

        public void setImage(BufferedImage b) {
            this.img = b;
            revalidate();
            repaint();
        }

        public void setCrosshair(int x, int y) {
            this.crossX = x; this.crossY = y;
            repaint();
        }

        public Point panelToImageCoords(Point p) {
            if (img == null) return null;
            double sx = (double) getWidth() / img.getWidth();
            double sy = (double) getHeight() / img.getHeight();
            double s = Math.min(sx, sy);
            int drawW = (int) (img.getWidth() * s);
            int drawH = (int) (img.getHeight() * s);
            int x0 = (getWidth() - drawW) / 2;
            int y0 = (getHeight() - drawH) / 2;
            int ix = (int) ((p.x - x0) / s);
            int iy = (int) ((p.y - y0) / s);
            if (ix < 0 || iy < 0 || ix >= img.getWidth() || iy >= img.getHeight()) return null;
            return new Point(ix, iy);
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

        @Override
        public Dimension getPreferredSize() {
            if (img != null) return new Dimension(img.getWidth(), img.getHeight());
            return super.getPreferredSize();
        }
    }

    private static class Pixel {
        final int x, y;
        Pixel(int x, int y) { this.x = x; this.y = y; }
    }

    // ---------- Stack interface and implementation (custom) ----------
    private interface StackIF<T> {
        void push(T item);
        T pop();
        boolean isEmpty();
        int size();
    }

    private static class ArrayStack<T> implements StackIF<T> {
        private Object[] data;
        private int top;

        public ArrayStack() { this(1024); }
        public ArrayStack(int capacity) {
            data = new Object[capacity];
            top = 0;
        }

        public void push(T item) {
            if (top == data.length) {
                Object[] n = new Object[data.length * 2];
                System.arraycopy(data, 0, n, 0, data.length);
                data = n;
            }
            data[top++] = item;
        }

        @SuppressWarnings("unchecked")
        public T pop() {
            if (top == 0) throw new RuntimeException("Stack vazia");
            top--;
            T item = (T) data[top];
            data[top] = null;
            return item;
        }

        public boolean isEmpty() { return top == 0; }
        public int size() { return top; }
    }

    // ---------- Queue interface and implementation (custom) ----------
    private interface QueueIF<T> {
        void enqueue(T item);
        T dequeue();
        boolean isEmpty();
        int size();
    }

    private static class ArrayQueue<T> implements QueueIF<T> {
        private Object[] data;
        private int head, tail, size;

        public ArrayQueue() { this(1024); }
        public ArrayQueue(int capacity) {
            data = new Object[capacity];
            head = 0; tail = 0; size = 0;
        }

        public void enqueue(T item) {
            if (size == data.length) {
                Object[] n = new Object[data.length * 2];
                for (int i = 0; i < size; i++) n[i] = data[(head + i) % data.length];
                data = n;
                head = 0;
                tail = size;
            }
            data[tail] = item;
            tail = (tail + 1) % data.length;
            size++;
        }

        @SuppressWarnings("unchecked")
        public T dequeue() {
            if (size == 0) throw new RuntimeException("Queue vazia");
            T item = (T) data[head];
            data[head] = null;
            head = (head + 1) % data.length;
            size--;
            return item;
        }

        public boolean isEmpty() { return size == 0; }
        public int size() { return size; }
    }
}
