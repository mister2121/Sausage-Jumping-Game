import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GameMap extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 750;
    int boardHeight = 250;
    int groundY = boardHeight; // ustalona linia podłogi

    // obrazki
    Image dogImg;
    BufferedImage boneImg, cactus1Img, cactus2Img, cactus3Img;

    SausageDog parowka;
    ArrayList<GameObject> obstacles;
    ArrayList<GameObject> collectibles;

    //deklaracja zegara gry zeby nonstop sie odswiezala i aktualizowala sytuacje/stan gry
    Timer gameLoop;

    // zegar potrzebny do spawnowania gameobjectow (kosci, przeszkody)
    Timer spawnerTimer;

    //prędkość mapy
    int velocityX = -5;

    //wynik gracza
    int score = 0;

    //czy gra sie zakonczyla?
    boolean gameRunning = true;

    public GameMap() {
        //potrzebne zeby klawiatura byla wysluchiwana przez gre i dzialala
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);

        //tło
        setBackground(Color.LIGHT_GRAY);

        //ładuj obrazki
        loadImages();

        parowka = new SausageDog(50, groundY, 40, dogImg);
        obstacles = new ArrayList<>();
        collectibles = new ArrayList<>();

        //tworzenie zegara gry ktory tyka co ¬16ms (60fps)
        gameLoop = new Timer(1000/60, this);
        gameLoop.start();

        // zegar tworzenia obiektów (co 1.5s dodaje cos na mape)
        spawnerTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                spawnObjects();
            }
        });
        spawnerTimer.start();
    }

    private void loadImages() {
        try {
            dogImg = new ImageIcon("img/parowka.gif").getImage();
            boneImg = ImageIO.read(new File("img/bone.png"));
            cactus1Img = ImageIO.read(new File("img/cactus1.png"));
            cactus2Img = ImageIO.read(new File("img/cactus2.png"));
            cactus3Img = ImageIO.read(new File("img/cactus3.png"));
        } catch (IOException e) {
            System.out.println("Błąd ładowania obrazków!");
            e.printStackTrace();
        }
    }

    private void spawnObjects() {
        if (!gameRunning) return;

        double chance = Math.random();
        int spawnX = boardWidth + 50; // Pojawiają się poza prawą krawędzią ekranu
        int cactusHeight = 50;

        // na podstawie logiki z tutoriala kenny'ego + dodajemy kości
        if (chance > 0.85) {
            obstacles.add(new Cactus(spawnX, groundY - cactusHeight, 70, cactusHeight, cactus3Img));
        } else if (chance > 0.50) {
            obstacles.add(new Cactus(spawnX, groundY - cactusHeight, 48, cactusHeight, cactus2Img));
        } else if (chance > 0.30) {
            obstacles.add(new Cactus(spawnX, groundY - cactusHeight, 24, cactusHeight, cactus1Img));
        } else if (chance > 0.20) {

            // szansa na pojawienie się kości zamiast kaktusa
            int boneHeight = 30;

            // dajemy kość trochę wyżej, żeby czasem trzeba było po nią skoczyć
            int boneY = (Math.random() > 0.5) ? groundY - boneHeight : groundY - boneHeight - 60;
            collectibles.add(new Bone(spawnX, boneY, 50, boneHeight, boneImg));
        }
    }

    // to jest wbudowana metoda z Javy odpowiadajca za rysowanie
    @Override
    protected void paintComponent(Graphics g) {
        // tutaj najpierw czyscimy ekran a potem rysujemy tlo
        super.paintComponent(g);

        // rysowanie elementów mapy
        parowka.draw(g, this);

        for (GameObject obstacle : obstacles) {
            obstacle.draw(g, this);
        }

        for (GameObject collectible : collectibles) {
            collectible.draw(g, this);
        }

        // punkty
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("Punkty: " + score, 600, 30);

        // game over
        if (!gameRunning) {
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString("KONIEC GRY", 250, 125);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (gameRunning) {
            parowka.move();

            // zarządzanie przeszkodami
            for (int i = obstacles.size() - 1; i >= 0; i--) {
                GameObject obs = obstacles.get(i);
                obs.move(velocityX);

                // kolizja z kaktusem
                if (parowka.getBounds().intersects(obs.getBounds())) {
                    gameRunning = false;
                    gameLoop.stop();
                    spawnerTimer.stop();
                }

                // usuwamy obiekty z pamięci, gdy wylecą poza lewy ekran
                if (obs.x + obs.width < -10) {
                    obstacles.remove(i);
                }
            }

            // zarządzanie kośćmi
            for (int i = collectibles.size() - 1; i >= 0; i--) {
                GameObject col = collectibles.get(i);
                col.move(velocityX);

                // zbieranie punktów
                if (parowka.getBounds().intersects(col.getBounds())) {
                    score += 10; // 10 punktów za kość
                    collectibles.remove(i); // znika po zebraniu
                } else if (col.x + col.width < -10) {
                    collectibles.remove(i); // usuwa, jeśli gracz pominął
                }
            }
        }
        repaint();
    }

    //metoda sprawdzajaca jakie klawisze sa nacisniete
    @Override
    public void keyPressed(KeyEvent e) {
        //sprawdzamy tutaj czy spacja (lub strzalka w gore) byla nacisnieta, jezeli tak to wykonujemy jump() na parowce
        if (e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_UP) {
            if (!gameRunning) {
                gameReset();
            } else {
                parowka.jump();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    public void gameReset() {
        score = 0;
        obstacles.clear();
        collectibles.clear();
        parowka.y = groundY - parowka.height;
        parowka.velocityY = 0;
        gameRunning = true;
        gameLoop.start();
        spawnerTimer.start();
    }
}
