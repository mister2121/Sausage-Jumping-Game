import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

public class SausageDog {
    // wspolrzedne gdzie ma byc
    int x;
    int y;

    //tutaj przechowujemy gdzie jest podloga dla psa (dol ekranu, gry)
    int groundY;

    // rozmiary jamnika
    int width;
    int height;

    //fizyka psa
    double velocityY = 0; //szybkosc skoku
    double gravity = 0.8;

    //tutaj przechowujemy fotke parowki
    Image img;

    public SausageDog(int x, int groundY, int height, Image img) {
        this.x = x;
        this.groundY = groundY;
        this.height = height;
        this.img = img;

        // Obliczamy pozycję Y: góra psa = linia podłogi - wysokość psa
        this.y = groundY - height;

        // Obliczamy szerokość proporcjonalnie do obrazka
        this.width = height * 2;
    }

    // tutaj rysujemny psa na mapce
    public void draw(Graphics g, ImageObserver observer) {
        g.drawImage(img, x, y, width, height, observer);
    }

    public void move() {
        velocityY += gravity;
        y += (int) velocityY;

        // zatrzymanie na ziemi (podłoga)
        if (y + height >= groundY) {
            y = groundY - height;
            velocityY = 0;
        }
    }

    public void jump() {
        // skaczemy tylko jesli stoimy na ziemi
        if (y == groundY - height) {
            velocityY = -17;
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
