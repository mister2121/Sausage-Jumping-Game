import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public abstract class GameObject {
    int x, y, width, height;
    Image img;

    public GameObject(int x, int y, int width, int height, Image img) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.img = img;
    }

    // wspólna metoda rysowania
    public void draw(Graphics g, ImageObserver observer) {
        if (img != null) {
            g.drawImage(img, x, y, width, height, observer);
        }
    }

    // wspólna metoda przesuwania w lewo (wszystkie przeszkody i kości tak działają)
    public void move(int velocityX) {
        x += velocityX;
    }

    // wspólna metoda do sprawdzania kolizji (hitbox)
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
