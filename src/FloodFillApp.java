import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class FloodFillApp {

    private JFrame frame;
    private ImagePanel imagePanel;
    private BufferedImage image;
    private JButton loadBtn, runBtn;
    private JRadioButton dfsBtn, bfsBtn;
    private JLabel infoLabel;

    // Cor e ponto inicial fixos
    private final Color fillColor = Color.RED;
    private final Point startPoint = new Point(0, 0);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FloodFillApp().createAndShowGui());
    }

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
        g.add(dfsBtn);
        g.add(bfsBtn);
        dfsBtn.setSelected(true);

        infoLabel = new JLabel("Ponto fixo: (0,0) | Cor: Vermelho");

        controls.add(loadBtn);
        controls.add(dfsBtn);
        controls.add(bfsBtn);
        controls.add(runBtn);
        controls.add(infoLabel);

        frame.add(controls, BorderLayout.SOUTH);

        loadBtn.addActionListener(e -> loadImage());

        runBtn.addActionListener(e -> {
            if (image == null) {
                JOptionPane.showMessageDialog(frame, "Carregue uma imagem primeiro.");
                return;
            }
            runBtn.setEnabled(false);
            loadBtn.setEnabled(false);
            boolean useDFS = dfsBtn.isSelected();
            new Thread(() -> {
                try {
                    if (useDFS) {
                        FloodFill.floodFillDFS(image, startPoint.x, startPoint.y, fillColor.getRGB(), 20, imagePanel);
                    } else {
                        FloodFill.floodFillBFS(image, startPoint.x, startPoint.y, fillColor.getRGB(), 20, imagePanel);
                    }
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
                imagePanel.setCrosshair(startPoint.x, startPoint.y);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Erro ao carregar imagem: " + ex.getMessage());
            }
        }
    }
}
