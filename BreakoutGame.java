import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;

public class BreakoutGame extends JPanel implements ActionListener, KeyListener {
    private int playerScore = 0;
    private int playerLives = 3;
    private int ballX = 400;
    private int ballY = 300;
    private double ballSpeed = 2.0; // Magnitud de la velocidad de la pelota
    private double ballDirectionX = 1.0; // Dirección de la velocidad en el eje X
    private double ballDirectionY = 1.0; // Dirección de la velocidad en el eje Y
    private int paddleX = 350;
    private int paddleSpeed = 10;
    private int brickRows = 6;
    private int brickCols = 10;
    private int brickWidth = 70;
    private int brickHeight = 20;
    private int brickGap = 5; // Separación entre ladrillos
    private int brickStartX = (800 - brickCols * (brickWidth + brickGap)) / 2; // Centrar en X
    private int brickStartY = 50; // Posición Y inicial de los ladrillos
    private int[][] bricks;
    private Timer timer;
    private int timeSeconds = 0;
    private int timeMinutes = 0;
    private boolean isGamePaused = false;
    private boolean spacePressed = false;

    public BreakoutGame() {
        timer = new Timer(10, this);
        timer.start();
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);

        // Inicializa la matriz de ladrillos
        bricks = new int[brickRows][brickCols];
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickCols; j++) {
                bricks[i][j] = 1;
            }
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Dibuja el fondo
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Dibuja la raqueta
        g.setColor(Color.WHITE);
        g.fillRect(paddleX, 550, 100, 10);

        // Dibuja la pelota
        g.setColor(Color.WHITE);
        g.fillOval((int) ballX, (int) ballY, 20, 20);

        // Dibuja los ladrillos
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickCols; j++) {
                if (bricks[i][j] == 1) {
                    g.setColor(Color.BLUE);
                    int brickX = brickStartX + j * (brickWidth + brickGap);
                    int brickY = brickStartY + i * (brickHeight + brickGap);
                    g.fillRect(brickX, brickY, brickWidth, brickHeight);
                }
            }
        }

        // Dibuja el cronómetro
        g.setColor(Color.WHITE);
        g.drawString("Score: " + playerScore, 10, 20);
        g.drawString("Lives: " + playerLives, 700, 20);
        g.drawString("Time: " + formatTime(), 350, 20);
        g.drawString("Press 'P' or SPACE to pause/unpause", 10, 40);
        g.drawString("Press '+' or '-' to change ball speed", 600, 40);
    }

    public void actionPerformed(ActionEvent e) {
        if (!isGamePaused) {
            timeSeconds++;
            if (timeSeconds == 60) {
                timeSeconds = 0;
                timeMinutes++;
            }

            // Mueve la pelota y actualiza la pantalla
            ballX += ballSpeed * ballDirectionX;
            ballY += ballSpeed * ballDirectionY;

            // Agrega la lógica para rebotes y colisiones aquí
            checkCollisions();
        }

        repaint();
    }

    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) {
            // Mueve la raqueta a la izquierda
            if (paddleX > 0 && !isGamePaused) {
                paddleX -= paddleSpeed;
            }
        }
        if (key == KeyEvent.VK_RIGHT) {
            // Mueve la raqueta a la derecha
            if (paddleX < 700 && !isGamePaused) {
                paddleX += paddleSpeed;
            }
        }
        if (key == KeyEvent.VK_P) {
            // Pausar o reanudar el juego con la tecla "P"
            isGamePaused = !isGamePaused;
        }
        if (key == KeyEvent.VK_SPACE) {
            // Pausar o reanudar el juego con la barra espaciadora
            spacePressed = !spacePressed;
            isGamePaused = spacePressed;
        }
        if (key == KeyEvent.VK_PLUS || key == KeyEvent.VK_ADD) {
            // Aumenta la magnitud de la velocidad de la pelota
            ballSpeed = Math.min(ballSpeed + 1.0, 20.0);
        }
        if (key == KeyEvent.VK_MINUS || key == KeyEvent.VK_SUBTRACT) {
            // Disminuye la magnitud de la velocidad de la pelota
            ballSpeed = Math.max(ballSpeed - 1.0, 1.0);
        }
    }

    public void keyTyped(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {
    }

    public void checkCollisions() {
        // Colisión con los bordes de la pantalla
        if (ballX <= 0 || ballX + 20 >= 800) {
            ballDirectionX = -ballDirectionX; // Rebote en los bordes izquierdo y derecho
        }
        if (ballY <= 0) {
            ballDirectionY = -ballDirectionY; // Rebote en el borde superior
        }

        // Colisión con la raqueta
        if (ballY + 20 >= 550 && ballX + 20 >= paddleX && ballX <= paddleX + 100) {
            double relativeIntersectX = (ballX + 20 / 2) - (paddleX + 100 / 2);
            double normalizedIntersectX = relativeIntersectX / (100 / 2);
            double bounceAngle = normalizedIntersectX * Math.toRadians(60); // Ángulo de rebote
            ballDirectionX = Math.sin(bounceAngle);
            ballDirectionY = -Math.cos(bounceAngle);
            playerScore++;
        }

        // Colisión con los ladrillos
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickCols; j++) {
                if (bricks[i][j] == 1) {
                    int brickX = brickStartX + j * (brickWidth + brickGap);
                    int brickY = brickStartY + i * (brickHeight + brickGap);

                    if (ballX + 20 >= brickX && ballX <= brickX + brickWidth &&
                            ballY + 20 >= brickY && ballY <= brickY + brickHeight) {
                        ballDirectionX = -ballDirectionX; // Rebote en el ladrillo
                        ballDirectionY = -ballDirectionY; // Rebote en el ladrillo
                        bricks[i][j] = 0; // Marcar el ladrillo como destruido
                        playerScore++;
                    }
                }
            }
        }

        // Verificar si el jugador ha perdido
        if (ballY > 600) {
            playerLives--;
            if (playerLives == 0) {
                // Mensaje de "Perdiste"
                JOptionPane.showMessageDialog(this, "¡Perdiste!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
                resetGame();
            } else {
                // Reiniciar la posición de la pelota y la raqueta
                ballX = 400;
                ballY = 300;
                paddleX = 350;
            }
        }
    }

    public void resetGame() {
        playerScore = 0;
        playerLives = 3;
        timeSeconds = 0;
        timeMinutes = 0;
        isGamePaused = false;

        // Restaurar la matriz de ladrillos
        for (int i = 0; i < brickRows; i++) {
            for (int j = 0; j < brickCols; j++) {
                bricks[i][j] = 1;
            }
        }

        // Reiniciar la posición de la pelota y la raqueta
        ballX = 400;
        ballY = 300;
        paddleX = 350;

        // Reiniciar el juego
        timer.start();
    }

    public String formatTime() {
        DecimalFormat df = new DecimalFormat("00");
        return df.format(timeMinutes) + ":" + df.format(timeSeconds);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Breakout Game");
        BreakoutGame game = new BreakoutGame();
        frame.add(game);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
