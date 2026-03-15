import java.awt.image.BufferedImage;

public class Cactus extends GameObject {

    public Cactus(int x, int y, int width, int height, BufferedImage img) {
        // wywołanie konstruktora klasy nadrzędnej (GameObject) za pomocą super()
        super(x, y, width, height, img);
    }
}