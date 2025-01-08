import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class InstagramReelUI {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Font customFont;

    public InstagramReelUI(ActionListener onLoginClick) {
        // Set the FlatLaf look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.err.println("Failed to initialize FlatLaf: " + e.getMessage());
        }

        // Load custom font
        loadCustomFont();

        // Frame setup
        frame = new JFrame("Instagram Reel Scroller");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 350);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Main Panel (consistent background)
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.decode("#000000"));
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JLabel headerLabel = new JLabel("Enter Instagram Username and Password");
        headerLabel.setFont(customFont.deriveFont(Font.BOLD, 20));
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setForeground(Color.WHITE);
        mainPanel.add(headerLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        formPanel.setBackground(Color.decode("#000000")); // Match main background
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Username Field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(customFont.deriveFont(Font.PLAIN, 14));
        usernameLabel.setForeground(Color.WHITE);
        usernameField = new JTextField();
        usernameField.setFont(customFont.deriveFont(Font.PLAIN, 14));
        usernameField.setBackground(Color.decode("#111518"));
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(Color.WHITE);
        usernameField.setBorder(BorderFactory.createLineBorder(Color.decode("#282F39"), 1));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#282F39"), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Password Field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(customFont.deriveFont(Font.PLAIN, 14));
        passwordLabel.setForeground(Color.WHITE);
        passwordField = new JPasswordField();
        passwordField.setFont(customFont.deriveFont(Font.PLAIN, 14));
        passwordField.setBackground(Color.decode("#111518"));
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#282F39"), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Login Button
        JButton loginButton = new JButton("Log in");
        loginButton.setFont(customFont.deriveFont(Font.BOLD, 14));
        loginButton.setBackground(Color.decode("#1A80E6"));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(100, 40));
        loginButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.decode("#1A80E6"), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        // Disable the button after one click
        loginButton.addActionListener(e -> {
            loginButton.setEnabled(false); // Disable button during execution
            boolean gameCompleted = runMazeGame(frame); // Block until the game finishes
            if (gameCompleted) {
                JOptionPane.showMessageDialog(frame, "Maze solved! Resuming application.");
            } else {
                JOptionPane.showMessageDialog(frame, "Game exited early.");
            }
            loginButton.setEnabled(true); // Re-enable after game finishes
        });


        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.decode("#000000"));
        buttonPanel.add(loginButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);


        // Add to Frame
        frame.add(mainPanel);
        frame.setVisible(true);
    }
    private static boolean runMazeGame(JFrame parent) {
        final boolean[] gameCompleted = {false}; // Track if the game was solved
    
        // Create a modal dialog to host the game
        JDialog gameDialog = new JDialog(parent, "Maze Solver", true);
        gameDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
    
        // Create a GameFrame with a callback for when the game is completed
        GameFrame gameFrame = new GameFrame(() -> {
            gameCompleted[0] = true;
            gameDialog.dispose(); // Close the dialog when the game is solved
        });
    
        // Use the MazePanel from GameFrame as the main content
        gameDialog.getContentPane().add(gameFrame.getContentPane());
    
        // Adjust dialog size and position
        gameDialog.pack();
        gameDialog.setLocationRelativeTo(parent);
    
        // Show the dialog (blocks the parent thread)
        gameDialog.setVisible(true);
    
        return gameCompleted[0];
    }
    
    
    



    private void loadCustomFont() {
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/ABeeZee-Regular.ttf"));
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (FontFormatException | IOException e) {
            System.err.println("Failed to load custom font: " + e.getMessage());
            customFont = new Font("SansSerif", Font.PLAIN, 14); // Fallback font
        }
    }

    public String getUsername() {
        return usernameField.getText();
    }

    public String getPassword() {
        return new String(passwordField.getPassword());
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void close() {
        frame.dispose();
    }
}
