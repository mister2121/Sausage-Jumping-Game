import javax.swing.*;

public class SausageGame {
    public static void main(String[] args) {
        int boardWidth = 750;
        int boardHeight = 250;

        JFrame gameWindow = new JFrame("Parówka the Game");
        
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameWindow.setSize(boardWidth, boardHeight);
        gameWindow.setLocationRelativeTo(null);
        gameWindow.setResizable(false);
        
        GameMap gameMap = new GameMap();
        gameWindow.add(gameMap);
        gameWindow.pack();
        gameMap.requestFocus();
        gameWindow.setVisible(true);
    }
}
