import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Random;
/*
made multiple prompts to chatGPT (using its code editor feature)

"Set up a JFrame for a game window with a JPanel for rendering the maze." i was not aware that i should be using JDialog
"Implement a recursive algorithm to generate a random maze of a given size" sometimes mazes weren't solvable 
"Ensure the maze has a valid path from the start to the exit"

"Add key controls to move a player through the maze using arrow keys or WASD"
"Implement logic to reset the game if the player leaves the shortest path." didn't work, so i broke down the problem 

"Use Dijkstra's algorithm to find the shortest path in the maze."
"Highlight the shortest path on the maze when a specific key is pressed"
"Include a way to reset the game when the player leaves the optimal path to solving the maze." this prompt broke previous code 
"Add logic to prevent the maze from being unsolvable." 
"The player should be blue, the end should be red, and the walls should be black"
"Ensure the maze size is always odd for proper generation." chatgpt told me the maze size should be always odd in a previous prompt 
"Add a check to regenerate the maze if no valid path exists." the previous prompt broke code previously written
"The game should close itself if it completes, there should be no other way to close it" inspired by a bug fix recommended by chatgpt (there was a bug that only happened when closing an incomplete maze game, and chatgpt
recommended me to disable closing)
chatGPT ignored the second half of the last statement, so i manually added the line "setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);"
in hindsight, i completely forgot that i added the line mentioned above, so it was accidently also added to the ui file 

there were almost no issues in getting the game to work, everything worked on the first try (almost)
there was one edge case that would cause infinite recursion when generating a maze, fixed manually 

initially, the maze game didn't integrate properly into the UI, chatgpt told me "if you're trying to use this within a JDialog,
make sure that the GameFrame is being properly created and displayed within the context of a dialog instead of a regular JFrame. 
Since JDialog is a special type of window that has different properties compared to JFrame, you might encounter issues if the 
game logic assumes the window is a JFrame."


at first, I didn't actually read the response, and just blindly told it to fix the code (obviously it didnt work) 
eventually, i read the response, and reprompted chatgpt, it worked after a ton of trial and error
it would have been faster to write the code myself, to fix it, I would have changed the gameFrame to extend JDialog instead of JFrame, this would have taken 1 hour max
although chatGPT did get by without changing JFrame to JDialog, i have no idea why it works 


*/
public class MazeSolverGame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameFrame());

    }
}

class GameFrame extends JFrame {
    private int rows;
    private int cols;
    private MazePanel mazePanel;
    private Runnable onGameComplete;
    

    public GameFrame(Runnable onGameComplete) {
        this.onGameComplete = onGameComplete;
        setTitle("Maze Solver");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        resetGame();
        add(mazePanel);
        pack(); // Ensure window is packed after adding components
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }
    public GameFrame() {
        setTitle("Maze Solver");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        resetGame(); // Initialize the first maze
        setLocationRelativeTo(null);
        setVisible(true);
    }
    public MazePanel getMazePanel() {
        return mazePanel; // Return the maze panel so it can be added to JDialog
    }



    

    private void randomizeDimensions() {
        Random rand = new Random();
        boolean feelingLucky = rand.nextInt(100) <= 75;
        int impossibleMaze = rand.nextInt(100);

        int value = 9 + rand.nextInt(9); // Generate a value between 9 and 17 (inclusive)
        if (feelingLucky) value = 9 + rand.nextInt(9); // Optionally make the maze size between 9 and 17
        if (impossibleMaze <= 2) value = 261; // "Impossible" maze case

        this.rows = value % 2 == 0 ? value + 1 : value; // Ensure rows is odd
        
        
        
        this.cols = rows; // Ensure square maze
        System.out.println("New Maze Size: " + rows + "x" + cols);
    }

    public void resetGame() {
        randomizeDimensions(); // Always randomize dimensions on reset
    
        if (mazePanel != null) {
            mazePanel.resetMaze(); // Reset maze only if the panel exists
        } else {
            // If mazePanel is null, create and initialize it
            mazePanel = new MazePanel(rows, cols, this);
            add(mazePanel);
        }
    
        // Revalidate and repaint the current panel to reflect the changes
        mazePanel.revalidate();
        mazePanel.repaint();
        
        // Adjust the window size based on the new maze dimensions
        pack();
        mazePanel.requestFocusInWindow(); // Ensure key events are captured
    }
    

    
    
    
    



class MazePanel extends JPanel {
    private int rows;
    private int cols;
    private final int cellSize;
    private final int[][] maze;
    private final MazePoint player;
    private final MazePoint exit;
    private List<MazePoint> shortestPath;
    private boolean firstMaze = true;
    private boolean showShortestPath = false; // Controls whether to show the shortest path
    private StringBuilder typedInput = new StringBuilder();
    private JLabel headerLabel;
    

    public MazePanel(int rows, int cols, GameFrame gameFrame) {
        this.rows = rows;
        this.cols = cols;
        this.cellSize = Math.min(600 / rows, 600 / cols);
        this.maze = new int[rows][cols];
        this.player = new MazePoint(1, 1, 0);
        this.exit = new MazePoint(rows - 2, cols - 2, 0);
        this.firstMaze = true;
        
        generateMaze();
        if (shortestPath == null) {
            findShortestPath();
        }
        headerLabel = new JLabel("Find the most optimal route to escape the maze. You may not leave the optimal path at any point.", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 12));
        headerLabel.setPreferredSize(new Dimension(cols * cellSize, 30)); // Adjust header height

        setLayout(new BorderLayout());
        add(headerLabel, BorderLayout.SOUTH);

    // Add some padding/margin to move the maze down
    setPreferredSize(new Dimension(cols * cellSize, rows * cellSize + headerLabel.getPreferredSize().height )); // Added 20px of extra height

    // Set the panel background and other configurations
    setBackground(Color.WHITE);
    addKeyListener(new PlayerControl(gameFrame)); // Pass gameFrame to the key listener
    setFocusable(true);

    }
    
    public Dimension getSize() {
        return new Dimension(mazePanel.getWidth(), mazePanel.getHeight());
    }
    
    public Container getContentPane() {
        return getContentPane();
    }
    

    private boolean isPathValid() {
        boolean[][] visited = new boolean[rows][cols];
        return dfsPathCheck(1, 1, visited);
    }
    
    private boolean dfsPathCheck(int x, int y, boolean[][] visited) {
        // Boundary check and wall check
        if (x < 0 || y < 0 || x >= rows || y >= cols || maze[x][y] == 1 || visited[x][y]) {
            return false;
        }
    
        // If we reach the exit, the path is valid
        if (x == rows - 2 && y == cols - 2) {
            return true;
        }
    
        // Mark the current cell as visited
        visited[x][y] = true;
    
        // Explore in all four directions
        return dfsPathCheck(x + 1, y, visited) || // Down
               dfsPathCheck(x - 1, y, visited) || // Up
               dfsPathCheck(x, y + 1, visited) || // Right
               dfsPathCheck(x, y - 1, visited);   // Left
    }
    
    
    
    public void resetMaze() {
        // Regenerate the maze
        
        generateMaze();
    
        // Reset player position
        player.setLocation(1, 1);
    
        // Reset exit position
        exit.setLocation(rows - 2, cols - 2);
    
        // Recalculate the shortest path
        findShortestPath();
    
        // Repaint the panel to reflect the new maze
        repaint();
    }
    
    
    

    private void generateMaze() {
        
        // Initialize grid with walls
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                maze[i][j] = 1; // Wall
            }
        }

        // Recursive backtracking algorithm
        Random rand = new Random();
        ArrayList<MazePoint> stack = new ArrayList<>();
        MazePoint start = new MazePoint(1, 1, 0);
        maze[start.x][start.y] = 0;
        stack.add(start);

        while (!stack.isEmpty()) {
            MazePoint current = stack.remove(stack.size() - 1);
            ArrayList<MazePoint> neighbors = new ArrayList<>();

            for (int[] dir : new int[][]{{0, 2}, {2, 0}, {0, -2}, {-2, 0}}) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];
                if (nx > 0 && ny > 0 && nx < rows - 1 && ny < cols - 1 && maze[nx][ny] == 1) {
                    neighbors.add(new MazePoint(nx, ny, 0));
                }
            }

            if (!neighbors.isEmpty()) {
                stack.add(current);
                Collections.shuffle(neighbors);
                MazePoint chosen = neighbors.get(0);
                maze[chosen.x][chosen.y] = 0;
                maze[(current.x + chosen.x) / 2][(current.y + chosen.y) / 2] = 0;
                stack.add(chosen);
            }
        }

        if(!isPathValid()&&!firstMaze) generateMaze();
        firstMaze = false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        
        
        super.paintComponent(g);

        // Draw maze
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (maze[i][j] == 1) {
                    g.setColor(Color.BLACK);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fillRect(j * cellSize, i * cellSize, cellSize, cellSize);
            }
        }
        if(showShortestPath)paintShortestPath(g);
        // Draw player
        g.setColor(Color.BLUE);
        g.fillRect(player.y * cellSize, player.x * cellSize, cellSize, cellSize);

        // Draw exit
        g.setColor(Color.RED);
        g.fillRect(exit.y * cellSize, exit.x * cellSize, cellSize, cellSize);
        
        // Paint the shortest path
        
    }

    private void paintShortestPath(Graphics g) {
        
        g.setColor(Color.GREEN);
        for (MazePoint p : shortestPath) {
            g.fillRect(p.y * cellSize, p.x * cellSize, cellSize, cellSize);
        }
    }

    private void findShortestPath() {
        PriorityQueue<MazePoint> queue = new PriorityQueue<>(Comparator.comparingInt(p -> p.distance));
        Map<MazePoint, MazePoint> prev = new HashMap<>();
        Map<MazePoint, Integer> distances = new HashMap<>();
        shortestPath = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                MazePoint p = new MazePoint(i, j, Integer.MAX_VALUE);
                distances.put(p, Integer.MAX_VALUE);
            }
        }

        MazePoint start = new MazePoint(player.x, player.y, 0);
        distances.put(start, 0);
        queue.add(start);

        while (!queue.isEmpty()) {
            MazePoint current = queue.poll();

            if (current.equals(exit)) break;

            for (int[] dir : new int[][]{{0, 1}, {1, 0}, {0, -1}, {-1, 0}}) {
                int nx = current.x + dir[0];
                int ny = current.y + dir[1];
                MazePoint neighbor = new MazePoint(nx, ny, 0);

                if (nx >= 0 && ny >= 0 && nx < rows && ny < cols && maze[nx][ny] == 0) {
                    int newDist = distances.get(current) + 1;
                    if (newDist < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                        distances.put(neighbor, newDist);
                        neighbor.distance = newDist;
                        prev.put(neighbor, current);
                        queue.add(neighbor);
                    }
                }
            }
        }

        MazePoint current = exit;
        while (current != null) {
            shortestPath.add(current);
            current = prev.get(current);
        }
        Collections.reverse(shortestPath);
    }

    private class PlayerControl extends KeyAdapter {
        
        private final GameFrame gameFrame;

        public PlayerControl(GameFrame gameFrame) {
            this.gameFrame = gameFrame;
        }
        
        @Override
        public void keyPressed(KeyEvent e) {
            int dx = 0, dy = 0;
            char typedChar = e.getKeyChar();
            String key = "cc";
            if (Character.isLetterOrDigit(typedChar)) {
                typedInput.append(typedChar); // Append the typed character
            }
    
            // Check if the typed word matches "show"
            if (typedInput.toString().equalsIgnoreCase(key)) {
                showShortestPath = !showShortestPath; // Display the shortest path
                System.out.println("Shortest path is now visible!");
                typedInput.setLength(0); // Clear the typed input after triggering
            } else if (typedInput.length() > key.length()) {
                typedInput.setLength(0); // Reset if the word exceeds "show"
            }
            

            switch (e.getKeyCode()) {
                case KeyEvent.VK_UP, KeyEvent.VK_W -> dx = -1;
                case KeyEvent.VK_DOWN, KeyEvent.VK_S -> dx = 1;
                case KeyEvent.VK_LEFT, KeyEvent.VK_A -> dy = -1;
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> dy = 1;
            }
            
            
            
            int newX = player.x + dx;
            int newY = player.y + dy;
            
            if (newX >= 0 && newY >= 0 && newX < rows && newY < cols && maze[newX][newY] == 0) {
                player.setLocation(newX, newY);
                if (shortestPath != null && !shortestPath.contains(player)) {
                    gameFrame.resetGame(); // Reset the game if player leaves the optimal path
                }
                repaint();
            }
        
            if (player.equals(exit)) {
                
                if (onGameComplete != null) {
                    onGameComplete.run();
                }
            }
        
    

                
                
    
                repaint();
            

    
            if (player.equals(exit)) {
                
                if (onGameComplete != null) {
                    onGameComplete.run(); // Notify the main program
                }
                gameFrame.dispose();  // Close the game window
                       
            }
            
        }
    }
    
}

class MazePoint {
    int x, y, distance;

    public MazePoint(int x, int y, int distance) {
        this.x = x;
        this.y = y;
        this.distance = distance;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MazePoint)) return false;
        MazePoint other = (MazePoint) obj;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
public interface GameResetListener {
    void resetGame();
}

}
