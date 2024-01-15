import javax.swing.JFrame;

public class GameFrame extends JFrame {
    GameFrame() {
        // Create a new instance of the GamePanel and add it to the frame
        this.add(new GamePanel());

        // Set the title of the frame
        this.setTitle("Snake");

        // Specify the default close operation
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Disable frame resizing
        this.setResizable(false);

        // Pack the components of the frame tightly
        this.pack();

        // Set the frame to be visible
        this.setVisible(true);

        // Center the frame on the screen
        this.setLocationRelativeTo(null);
    }
}