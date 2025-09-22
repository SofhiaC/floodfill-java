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

    // Construtor usado para preparar a aplicação gráfica do FloodFill
    public FloodFillApp(String inputImagePath, boolean useStack) {
        try {
            image = ImageIO.read(new File(inputImagePath)); // Lê a imagem do arquivo
            floodFill = new FloodFill(image, useStack); // Cria o objeto FloodFill indicando a escolha do usuário (fila ou pilha)
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        setTitle("Flood Fill Animation (" + (useStack ? "Stack/DFS" : "Queue/BFS") + ")");
        // Define o tamanho da janela
        setSize(340, 360);
        // Centraliza a janela na tela
        setLocationRelativeTo(null);

        // Cria um painel que sobrescreve paintComponent para definir como o componente será desenhado
        add(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Desenha a imagem ampliada: cada pixel é aumentado pelo fator definido em 'scale'
                g.drawImage(image, 0, 0, image.getWidth() * scale, image.getHeight() * scale, null);
            }
        });

        // Cria uma thread para executar o algoritmo de Flood Fill
        // O preenchimento começará no ponto (x=1, y=1) usando a cor vermelha
        new Thread(() -> {
            try {
                floodFill.fill(1, 1, Color.RED, this); // O "this" é passado para permitir o refresh e atualizar a tela
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void refresh() {
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { // Criação da interface gráfica
            // Opções para escolher entre pilha ou fila
            String[] options = {"Fila", "Pilha"};

            // Cria um pop-up para o usuário escolher o método de execução
            int choice = JOptionPane.showOptionDialog(null,
                    "Escolha o modelo de flood fill:",
                    "Flood Fill",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);

            boolean useStack = (choice == 0);

            // Cria o objeto principal passando o caminho da imagem e a opção escolhida
            new FloodFillApp("entrada.png", useStack).setVisible(true);
        });
    }

    // ---------------- Interface da Pilha ----------------
    interface Stack<T> { // Define o contrato de uma pilha genérica
        void push(T item); // Adiciona um item no topo da pilha
        T pop();           // Remove e retorna o item do topo
        boolean isEmpty(); // Verifica se a pilha está vazia
    }

    // ---------------- Implementação da Pilha ----------------
    static class ArrayStack<T> implements Stack<T> {
        private Object[] data; // Array para armazenar os elementos
        private int top;       // Índice do topo da pilha

        public ArrayStack(int capacity) {
            data = new Object[capacity];
            top = -1;
        }

        // Caso o array esteja cheio, chama expand() para dobrar sua capacidade
        public void push(T item) {
            if (top == data.length - 1) expand();
            data[++top] = item;
        }

        // Se a pilha estiver vazia retorna null, caso contrário retorna o item do topo e decrementa o índice
        public T pop() {
            if (isEmpty()) return null;
            return (T) data[top--];
        }

        // Retorna true se a pilha não possuir elementos
        public boolean isEmpty() {
            return top == -1;
        }

        // Dobra a capacidade do array copiando os elementos existentes para um novo array maior
        private void expand() {
            Object[] newData = new Object[data.length * 2];
            System.arraycopy(data, 0, newData, 0, data.length);
            data = newData;
        }
    }

    // ---------------- Interface da Fila ----------------

    // Define o contrato de uma fila genérica
    interface Queue<T> {
        void enqueue(T item); // Insere um item no fim da fila
        T dequeue();          // Remove e retorna o item do início da fila
        boolean isEmpty();    // Verifica se a fila está vazia
    }

    // ---------------- Implementação da Fila ----------------
    static class ArrayQueue<T> implements Queue<T> {
        private Object[] data;     // Array onde os elementos serão armazenados
        private int front, rear;   // Índices do primeiro e último elementos
        private int size;          // Quantidade de elementos na fila

        // Inicializa a fila sem elementos
        public ArrayQueue(int capacity) {
            data = new Object[capacity];
            front = 0;
            rear = -1;
            size = 0;
        }

        // Se a fila estiver cheia, chama expand() para aumentar a capacidade
        // Em seguida, move o índice 'rear' circularmente e adiciona o novo item
        public void enqueue(T item) {
            if (size == data.length) expand();
            rear = (rear + 1) % data.length;
            data[rear] = item;
            size++;
        }

        // Se a fila estiver vazia retorna null, caso contrário retorna o item da frente
        // e move o índice 'front' circularmente
        public T dequeue() {
            if (isEmpty()) return null;
            T item = (T) data[front];
            front = (front + 1) % data.length;
            size--;
            return item;
        }

        // Retorna true se a fila não possuir elementos
        public boolean isEmpty() {
            return size == 0;
        }

        // Dobra a capacidade do array copiando os elementos na ordem correta
        // e ajusta os índices 'front' e 'rear'
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
