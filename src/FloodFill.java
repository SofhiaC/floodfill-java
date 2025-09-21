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

    /*
    Esse é o construtor da classe:
    - Recebe a imagem e a escolha entre pilha ou fila.
    - Calcula as dimensões da imagem.
     */
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

    /*
      Esse é o metodo principal (fill), que faz o preenchimento:
        - Define a cor original do ponto de partida (targetColor).
        - Se a cor já for igual à nova cor desejada, não faz nada (evita trabalho desnecessário).
     */
    public void fill(int startX, int startY, Color newColor, FloodFillApp app) {
        int targetColor = image.getRGB(startX, startY);
        if (targetColor == newColor.getRGB()) return;


        /*
        - Cria duas estruturas auxiliares: pilha e fila.
        - Dependendo da escolha (useStack), o algoritmo começa com uma delas.
            - Stack (DFS) → percorre em profundidade, indo até o fim antes de voltar.
            - Queue (BFS) → percorre em largura, camada por camada.
         */
        FloodFillApp.Stack<Point> stack = new FloodFillApp.ArrayStack<>(1000);
        FloodFillApp.Queue<Point> queue = new FloodFillApp.ArrayQueue<>(1000);

        if (useStack) {
            stack.push(new Point(startX, startY));
        } else {
            queue.enqueue(new Point(startX, startY));
        }


        /*
        Esse é o loop principal:
            - Continua enquanto houver pixels na estrutura escolhida.
            - Pega um ponto da pilha/fila e checa:
            - Se está dentro da imagem.
            - Se a cor ainda é a original (targetColor).
            - Caso positivo, pinta o pixel com a nova cor e aumenta o contador.
         */
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
                    Thread.sleep(50); // pequena pausa para o usuario visualizar a progressão
                } catch (InterruptedException ignored) {}
            }

            /*
            Aqui o algoritmo adiciona os vizinhos do pixel atual: direita, esquerda, baixo e cima.
                - Se for pilha → empilha os vizinhos (ordem importa).
                - Se for fila → enfileira os vizinhos.
                - Isso garante que todo o "território conectado" seja preenchido.
             */
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
        app.refresh(); // atualiza a interface da aplicação
    }


    /*
    Esse metodo cuida de salvar as imagens:
       - Usa frameCount para numerar os arquivos (frame_0000.png, frame_0001.png, ...).
       -  Escreve a imagem no formato PNG dentro da pasta Images.
       - Caso ocorra algum erro, mostra a exceção no console.
     */
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
