import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
/*
Create a Java Swing-based user interface for an Instagram login screen with a custom font, username and password fields, 
and a button that either leads to a maze game or starts scrolling content depending on a random chance. Use FlatLaf, and 
handle the UI interactions accordingly
this prompt didn't work (too much at once)

"How do I create a login screen in Java Swing?"
"How to set up a JFrame with custom panels and layouts?"
"How to style JTextField and JPasswordField with custom colors and borders in Swing?"
"How to load and use a custom TTF font in a Java Swing application?"

"Create a basic Java Swing app that is meant to be a login screen" 
"Add fields for username and password in the login screen."
"Use FlatLaf for theming and ensure the UI looks professional."
"Include a custom TTF font for the UI and ensure there's a fallback font if it fails to load."
"Style the text fields and buttons with custom colors, borders, and fonts. Use the following colors: 282f39, 111518, 1a80e6, 000000."
"Make a login button, it should be consistent with the color palette."
"There is a modular file meant to integrate with the current UI file, (pasted entire InstagramReelScroller.java file), when logging in, it should input the user's information into the helper methods" 
this step took manual debugging, most likely because it was a unique issue/not many people have asked chatgpt this in the past
this step took way too long, it would have been equally as fast if not faster to write the whole class manually

"Ensure 80% chance of the maze game launching and 20% chance of starting Instagram reels. Each time the login button is pressed, half the probability of getting the maze game."
this step took manual debugging, most likely because it was a unique issue/not many people have asked chatgpt this in the past
maze game was not running, and when it did, it would crash when resetting - some adjustments were made to the maze class to make it work with jdialog 
this step also took way too long
i had to read documentation :(

"Make the button disabled during processing and re-enable it after actions are completed."
this step took manual debugging, the prompt was most likely too general and not clear enough
it was intended to disable the login button if the maze game or instagram reels was running (to avoid bugs)

"Add error messages for scenarios like failed font loading or invalid actions."

*/
public class InstagramReelUI {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private Font customFont;
    private double mazeChance = 0.8;
    private boolean startScrolling = false;
    
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
            // chatGPT tried to use a different line of code that overcomplicated the solution and didnt work, manually added, found through java documentation
            loginButton.setEnabled(false);
            // Generate a random number between 0 and 1
            //the chance code was manually written
            double randomChance = Math.random();

            if (randomChance < mazeChance) {
                // 75% chance (initial) to go to the maze
                boolean gameCompleted = runMazeGame(frame); // Block until the game finishes
                if (gameCompleted) {
                    // Decrease the maze chance by half after completing the maze
                    mazeChance = mazeChance == 0 ? mazeChance : mazeChance/2.0;
                }
            } //AI generated with some prompting
            else {
                // 20% chance (initial) to go to the Instagram login screen
                startScrolling = true; // Set flag to true to trigger scrolling
                loginButton.setEnabled(false);
            }

            loginButton.setEnabled(true); // Re-enable after action is finished

            // Trigger the scrolling after the login button action
            if (startScrolling) {
                new Thread(() -> {
                    loginButton.setEnabled(false);
                    // Start scrolling when the flag is set to true
                    InstagramReelScroller.startScrolling(getUsername(), getPassword());
                }).start();
            }
        });



        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.decode("#000000"));
        buttonPanel.add(loginButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);


        // Add to Frame
        frame.add(mainPanel);
        frame.setVisible(true);
    }
    //written initially with AI, "write a method to start the maze game", manual editing was needed 
    private static boolean runMazeGame(JFrame parent) {
        final boolean[] gameCompleted = {false}; // Track if the game was solved
    
        // Create a modal dialog to host the game
        JDialog gameDialog = new JDialog(parent, "Maze Solver", true); //manually made it modal (unable to interact with parent frame until gameDialog was closed). Found through reading java documentation
        gameDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE); //manual code, suggested bug fix by chatgpt, found through java documentation 
    
        // Create a GameFrame with a callback for when the game is completed
        GameFrame gameFrame = new GameFrame(() -> {
            gameCompleted[0] = true;
            gameDialog.dispose(); // Close the dialog when the game is solved
        });
    
        // Add the MazePanel from the GameFrame to the JDialog (extract MazePanel)
        gameDialog.getContentPane().add(gameFrame.getMazePanel(), BorderLayout.CENTER); // Access MazePanel and add to JDialog
    
        // Adjust dialog size and position
        gameDialog.pack();
        gameDialog.setLocationRelativeTo(parent);
    
        // for some reason, chatgpt didn't automatically add this, making the gamedialog invisible, manually added, found through java documentation
        gameDialog.setVisible(true);
    
        return gameCompleted[0];
    }
    
    
    
    


//worked first try
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
