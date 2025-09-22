import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * de forma geral, essa classe aqui vai ser o responsável por fazer com que as imagens apareçam já com tamanho definido
 * entao tudo q envolver essa imagem (centralização e tamanho principalmente)
 */

public class ImagePanel extends JPanel {
    private BufferedImage img; //atributo da foto que vai ser moestrada (no caso a gente já definiu o dir dela no código mas tem aqui mesmo assim

    public void setImage(BufferedImage b) { //não é esse método que atualiza de fato (ou seja, pinta os pixels) a imagem, quem faz isso é o floodfill!!
        this.img = b; //b sempre vai ser a mesma imagem de referencia (estrada.png)
        revalidate(); //é uma forma de verificar as dimensões da imagem
        repaint(); //pede pra que a tela seja alterada pra mostrar os pixels que foram alterados no floodfill

        /**
         * aqui é engraçado, o setImage é um mensageiro que vai atualizar a
         * imagem que aparece pro usuário (ou seja, a cara 4 pinturas de pixels como definido no fooldfill
         * ele é quem manda a imagem ser alterada na tela so usuário a partir da mensagem recebida do app.refresh()
         */
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); //define a janela do swing
        if (img == null) { //um tratamento de erro basiquinho
            g.setColor(Color.DARK_GRAY);
            g.drawString("Nenhuma imagem carregada", 20, 20);
            return;
        }

        //trata do redimensionamento da imagem na janela
        double sx = (double) getWidth() / img.getWidth(); //320/16 = 20 vezes maior
        double sy = (double) getHeight() / img.getHeight();//400/16 = 25 ficou bonitinho assim rs
        double s = Math.min(sx, sy); //evita que a imagem fique distorcida nessa nova escala definida no App
        int drawW = (int) (img.getWidth() * s);
        int drawH = (int) (img.getHeight() * s);
        int x0 = (getWidth() - drawW) / 2; //especifica onde a imagem vai ter q ser desenhada
        int y0 = (getHeight() - drawH) / 2;
        g.drawImage(img, x0, y0, drawW, drawH, null); //coloca a imagem na dimensao definida

    }
}
