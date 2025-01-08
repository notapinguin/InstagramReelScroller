
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class InstagramReelScroller {

    private static WebDriver driver;
    private static WebDriverWait wait;

    public static void main(String[] args) {
        InstagramReelUI[] ui = new InstagramReelUI[1]; // Use an array to allow modification in the lambda
        ui[0] = new InstagramReelUI(e -> {});
    }
    

    static void startScrolling(String username, String password){
        initializeDriver();
            navigateToInstagram();
            performLogin(username, password);
            saveLoginInfo();  // Press "Save Info"
            waitForXSeconds(5);
            navigateToReels();  // Navigate to Reels section
            waitForXSeconds(5);
            while(true){
                scrollReels(10);
            }
    }

    // Method to initialize the WebDriver
    private static void initializeDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // Method to navigate to Instagram
    private static void navigateToInstagram() {
        driver.get("https://www.instagram.com");
    }

    // Method to log in to Instagram
    private static void performLogin(String username, String password) {
        try {
            if (username == null || password == null) {
                throw new IllegalArgumentException("Username and password cannot be null.");
            }

            WebElement usernameField = wait.until(ExpectedConditions.presenceOfElementLocated(By.name("username")));
            usernameField.sendKeys(username);

            WebElement passwordField = driver.findElement(By.name("password"));
            passwordField.sendKeys(password);

            WebElement loginButton = driver.findElement(By.xpath("//button[@type='submit']"));
            loginButton.click();

            System.out.println("Login successful.");
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
            throw new RuntimeException("Error during login process.");
        }
    }

    // Method to save login information
  
private static void saveLoginInfo() {
    try {
        // Wait for the "Save Info" button to be clickable using its class
        WebElement saveInfoButton = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button._acan._acap._acas._aj1-._ap30")));  // Selecting button based on the class name

        // Click the "Save Info" button
        saveInfoButton.click();
        System.out.println("Login info saved.");
    } catch (TimeoutException e) {
        System.out.println("Save Info button not found or not clickable.");
    } catch (Exception e) {
        System.out.println("Error clicking Save Info button: " + e.getMessage());
    }
}



    // Method to navigate to the Reels page
    private static void navigateToReels() {
        driver.get("https://www.instagram.com/reels/");
    }
    
    // Method to wait for a specified number of seconds
private static void waitForXSeconds(int seconds) {
    try {
        Thread.sleep(seconds * 1000);  // Convert seconds to milliseconds
        System.out.println("Waited for " + seconds + " seconds.");
    } catch (InterruptedException e) {
        System.out.println("Error during sleep: " + e.getMessage());
    }
}

public static void scrollReels(int reels) {
    AtomicBoolean stopReelDetection = new AtomicBoolean(false);

    Actions actions = new Actions(driver);

    for (int i = 0; i < reels; i++) {
        // Locate all video elements on the page
        List<WebElement> videos = driver.findElements(By.tagName("video"));
        WebElement currentVideo = null;

        // Check each video for visibility and playback status
        for (WebElement video : videos) {
            long currentTime = ((Number) ((JavascriptExecutor) driver)
                    .executeScript("return arguments[0].currentTime;", video)).longValue();
            boolean isVisible = video.isDisplayed() && ((JavascriptExecutor) driver)
                    .executeScript("var rect = arguments[0].getBoundingClientRect(); return rect.top >= 0 && rect.left >= 0 && rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) && rect.right <= (window.innerWidth || document.documentElement.clientWidth);", video).equals(true);

            if (currentTime > 0 && isVisible) {
                currentVideo = video;
                break;
            }
        }

        // Clear the videos list to free memory
        videos.clear();
        videos = null;

        if (currentVideo != null && currentVideo.isDisplayed()) {
            String videoUrl = currentVideo.getDomAttribute("src");
            System.out.println("Playing video with src: " + videoUrl);

            // Get the duration of the current video
            long duration = ((Number) ((JavascriptExecutor) driver)
                    .executeScript("return arguments[0].duration;", currentVideo)).longValue();

            // Wait until the video finishes playing
            boolean isVideoPlaying = true;
            while (isVideoPlaying) {
                if (stopReelDetection.get()) {
                    System.out.println("Stopping reel detection.");
                    isVideoPlaying = false;
                    break;
                }

                long currentTime = ((Number) ((JavascriptExecutor) driver)
                        .executeScript("return arguments[0].currentTime;", currentVideo)).longValue();
                boolean isEnded = Math.abs(currentTime - duration) < 2  ;

                // Constantly print video details
                System.out.println("Video Length: " + duration + " seconds");
                System.out.println("Elapsed Time: " + currentTime + " seconds");

                List<WebElement> loadedVideos = driver.findElements(By.tagName("video"));
                System.out.println("Number of Loaded Videos: " + loadedVideos.size());

                // Clear the list of loaded videos to free memory
                loadedVideos.clear();
                loadedVideos = null;

                if (isEnded) {
                    isVideoPlaying = false;
                } else {
                    // Handle user scrolling to a new video
                    List<WebElement> newVideos = driver.findElements(By.tagName("video"));
                    WebElement newCurrentVideo = null;
                    for (WebElement video : newVideos) {
                        long newCurrentTime = ((Number) ((JavascriptExecutor) driver)
                                .executeScript("return arguments[0].currentTime;", video)).longValue();
                        boolean isNewVisible = video.isDisplayed() && ((JavascriptExecutor) driver)
                                .executeScript("var rect = arguments[0].getBoundingClientRect(); return rect.top >= 0 && rect.left >= 0 && rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) && rect.right <= (window.innerWidth || document.documentElement.clientWidth);", video).equals(true);

                        if (newCurrentTime > 0 && isNewVisible && !video.equals(currentVideo)) {
                            newCurrentVideo = video;
                            break;
                        }
                    }

                    newVideos.clear();
                    newVideos = null;

                    if (newCurrentVideo != null) {
                        System.out.println("User manually scrolled to a new video.");
                        currentVideo = newCurrentVideo;
                        duration = ((Number) ((JavascriptExecutor) driver)
                                .executeScript("return arguments[0].duration;", currentVideo)).longValue();
                    }
                }

                try {
                    Thread.sleep(1000); // Pause to print details every second
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (!isVideoPlaying) {
                System.out.println("Scrolling to next reel");
                actions.sendKeys(Keys.ARROW_DOWN).perform();
                try {
                    Thread.sleep(2000); // Allow the next reel to load
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("No video currently playing.");
        }
    }
}





}




