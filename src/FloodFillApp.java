import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FloodFillApp extends JFrame {

    private BufferedImage image;
    private FloodFill floodFill;
    private int scale = 20; // fator de zoom da imagem

    public FloodFillApp(String inputImagePath, boolean useStack) {
        try {
            image = ImageIO.read(new File(inputImagePath));
            floodFill = new FloodFill(image, useStack);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        setTitle("Flood Fill Animation (" + (useStack ? "Stack/DFS" : "Queue/BFS") + ")");
        setSize(340, 360);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        add(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // desenha a imagem escalada
                g.drawImage(image, 0, 0, image.getWidth() * scale, image.getHeight() * scale, null);
            }
        });

        new Thread(() -> {
            try {
                floodFill.fill(1, 1, Color.RED, this); // floodfill começa em (1,1)
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void refresh() {
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // menu para escolher pilha ou fila
            String[] options = {"Stack (DFS)", "Queue (BFS)"};
            int choice = JOptionPane.showOptionDialog(null,
                    "Escolha o modelo de flood fill:",
                    "Flood Fill",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);

            boolean useStack = (choice == 0);

            new FloodFillApp("entrada.png", useStack).setVisible(true);
        });
    }

    // ---------------- Interface da Pilha ----------------
    interface Stack<T> {
        void push(T item);
        T pop();
        boolean isEmpty();
    }

    // ---------------- Implementação da Pilha ----------------
    static class ArrayStack<T> implements Stack<T> {
        private Object[] data;
        private int top;

        public ArrayStack(int capacity) {
            data = new Object[capacity];
            top = -1;
        }

        public void push(T item) {
            if (top == data.length - 1) expand();
            data[++top] = item;
        }

        public T pop() {
            if (isEmpty()) return null;
            return (T) data[top--];
        }

        public boolean isEmpty() {
            return top == -1;
        }

        private void expand() {
            Object[] newData = new Object[data.length * 2];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }
    }

    // ---------------- Interface da Fila ----------------
    interface Queue<T> {
        void enqueue(T item);
        T dequeue();
        boolean isEmpty();
    }

    // ---------------- Implementação da Fila ----------------
    static class ArrayQueue<T> implements Queue<T> {
        private Object[] data;
        private int front, rear, size;

        public ArrayQueue(int capacity) {
            data = new Object[capacity];
            front = 0;
            rear = -1;
            size = 0;
        }

        public void enqueue(T item) {
            if (size == data.length) expand();
            rear = (rear + 1) % data.length;
            data[rear] = item;
            size++;
        }

        public T dequeue() {
            if (isEmpty()) return null;
            T item = (T) data[front];
            front = (front + 1) % data.length;
            size--;
            return item;
        }

        public boolean isEmpty() {
            return size == 0;
        }

        private void expand() {
            Object[] newData = new Object[data.length * 2];
            for (int i = 0; i < size; i++) {
                newData[i] = data[(front + i) % data.length];
            }
            front = 0;
            rear = size - 1;
            data = newData;
        }
    }
}
