import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {

    // Define constants for the screen dimensions and unit size
    static final int SCREEN_WIDTH = 800;
    static final int SCREEN_HEIGHT = 800;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;

    // Arrays to store the x and y coordinates of snake body parts
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];

    // Variables for tracking the number of body parts, food eaten, food position, direction, and game state
    int bodyParts = 6;
    int foodEaten;
    int foodX;
    int foodY;
    char direction = 'D';
    boolean running = false;

    // Timer for controlling game speed
    Timer timer;

    // Random object for generating food position and color
    Random random;

    // Variables for food color and play again button
    private Color foodColor;
    private JButton playAgainButton;

    // GamePanel constructor
    GamePanel() {
        random = new Random();

        // Set panel dimensions and background color
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.gray);

        // Set panel focus and add key listener
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());

        // Start the game and initialize the play again button
        startGame();
        initializePlayAgainButton();
        restartGame();
    }

    // Method to start the game
    public void startGame() {
        // Generate initial food position
        newFood();

        // Set running state to true and initialize the timer
        running = true;
        int dynamicDelay = 70;
        timer = new Timer(dynamicDelay, this);
    }

    // Method to restart the game
    private void restartGame() {
        // Reset variables for body parts, food eaten, direction, and running state
        bodyParts = 6;
        foodEaten = 0;
        direction = 'D';
        running = true;

        // Hide the play again button
        playAgainButton.setVisible(false);

        // Generate new food and set dynamic delay
        newFood();
        int dynamicDelay = 70;
        timer.setDelay(dynamicDelay);
        timer.start();

        // Reset the snake's position
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
    }

    // Method to initialize the play again button
    private void initializePlayAgainButton() {
        // Set FlowLayout with vertical gap of 500 pixels
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 550));

        // Create play again button and add action listener
        playAgainButton = new JButton("Play Again");
        playAgainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        // Set font and initially hide the button
        playAgainButton.setFont(new Font("Monaco", Font.BOLD, 25));
        playAgainButton.setVisible(false);

        // Add the play again button to the panel
        add(playAgainButton);
    }

    // Override the paintComponent method to draw graphics on the panel
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);

        // Stop the timer if the game is not running
        if (!running) {
            timer.stop();
        }
    }

    // Method to draw graphics on the panel
    public void draw(Graphics g) {
        if (running) {
            // Draw grid lines
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            // Draw the food with the generated color
            g.setColor(foodColor);
            g.fillRect(foodX, foodY, UNIT_SIZE, UNIT_SIZE);

            // Draw the snake's body parts
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.magenta);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(Color.cyan);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            // Draw the score on the screen
            g.setColor(Color.red);
            g.setFont(new Font("Monaco", Font.BOLD, 25));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + foodEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + foodEaten)) / 2, g.getFont().getSize());
        } else {
            // Draw the game over screen
            gameOver(g);
        }
    }

    // Method to generate new food position and color
    public void newFood() {
        foodX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        foodY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
        foodColor = new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }

    // Method to move the snake
    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        // Update the snake's head position based on the current direction
        switch (direction) {
            case 'W':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'S':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'D':
                x[0] = x[0] + UNIT_SIZE;
                break;
            case 'A':
                x[0] = x[0] - UNIT_SIZE;
                break;
        }
    }

    // Method to check if the snake has eaten the food
    public void checkFood() {
        if ((x[0] == foodX) && (y[0] == foodY)) {
            bodyParts++;
            foodEaten++;
            newFood();

            // Adjust the dynamic delay based on the number of food eaten
            int dynamicDelay = Math.max(30, 70 - foodEaten * 2);
            timer.setDelay(dynamicDelay);
        }
    }

    // Method to check collisions with the snake's body and borders
    public void checkCollisions() {
        // Check if the head collides with the body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }

        // Check if the head collides with the borders
        if (x[0] < 0 || x[0] >= SCREEN_WIDTH || y[0] < 0 || y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        // Stop the timer and show the play again button if the game is over
        if (!running) {
            timer.stop();
            playAgainButton.setVisible(true);
        }
    }

    // Method to display the game over screen
    public void gameOver(Graphics g) {
        // Display game over message
        g.setColor(Color.red);
        g.setFont(new Font("Monaco", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2 - 100);

        // Display final score
        g.setColor(Color.red);
        g.setFont(new Font("Monaco", Font.BOLD, 40));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + foodEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: " + foodEaten)) / 2, SCREEN_HEIGHT / 2);

        // Display play again message
        g.setColor(Color.red);
        g.setFont(new Font("Monaco", Font.BOLD, 40));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        g.drawString("Press 'Play Again' to Restart", (SCREEN_WIDTH - metrics3.stringWidth("Press 'Play Again' to Restart")) / 2, SCREEN_HEIGHT / 2 + 100);
    }

    // ActionListener implementation for the timer
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollisions();
        }
        repaint();
    }

    // KeyAdapter class to handle keyboard input
    public class MyKeyAdapter extends KeyAdapter {
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_W:
                    if (direction != 'S') {
                        direction = 'W';
                    }
                    break;
                case KeyEvent.VK_S:
                    if (direction != 'W') {
                        direction = 'S';
                    }
                    break;
                case KeyEvent.VK_D:
                    if (direction != 'A') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_A:
                    if (direction != 'D') {
                        direction = 'A';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'S') {
                        direction = 'W';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'W') {
                        direction = 'S';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'A') {
                        direction = 'D';
                    }
                    break;
                case KeyEvent.VK_LEFT:
                    if (direction != 'D') {
                        direction = 'A';
                    }
                    break;
            }
        }
    }
}