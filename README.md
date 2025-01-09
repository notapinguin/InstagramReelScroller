# Instagram Reel Scroller

InstagramReelUI is a Java-based graphical user interface (GUI) for logging into Instagram using a username and password. The application uses Flatlaf and Java Swing for modern UI styling and includes custom font support, a login form with username and password fields, and a login button. It also integrates a maze game that has a chance to be launched after the login button is clicked, the completion of this game is mandatory, after the game launches, users will not be able to use the program without completing the game. Upon completion of the game, users can try to login again.  

**Warning:**
Once the maze is launched, the exit application button will be disabled. To close the app anyways, go to task explorer and end the task called "OpenJDK Platform Binary". 


 
## Features

- Automatic login to Instagram
- Scrolls through Instagram reels automatically
- GUI

## Dependencies

### 1. **FlatLaf**
The project uses the FlatLaf look-and-feel library to style the UI.

- **Maven**:
    ```xml
    <dependency>
        <groupId>com.formdev</groupId>
        <artifactId>flatlaf</artifactId>
        <version>2.3</version> <!-- Check for the latest version -->
    </dependency>
    ```

- **Gradle**:
    ```gradle
    implementation 'com.formdev:flatlaf:2.3' // Check for the latest version
    ```

### 2. **Selenium WebDriver**
Selenium is used for web automation. Make sure to include the appropriate Selenium WebDriver dependency for interacting with Chrome.

- **Maven**:
    ```xml
    <dependency>
        <groupId>org.seleniumhq.selenium</groupId>
        <artifactId>selenium-java</artifactId>
        <version>4.9.0</version> <!-- Check for the latest version -->
    </dependency>
    ```

- **Gradle**:
    ```gradle
    implementation 'org.seleniumhq.selenium:selenium-java:4.9.0' // Check for the latest version
    ```

### 3. **Chrome WebDriver**
To interact with Chrome using Selenium, you need the Chrome WebDriver. You can download it from [here](https://sites.google.com/a/chromium.org/chromedriver/), or use a tool like [WebDriverManager](https://github.com/bonigarcia/webdrivermanager) to manage the WebDriver automatically.

- **WebDriverManager (Maven)**:
    ```xml
    <dependency>
        <groupId>io.github.bonigarcia</groupId>
        <artifactId>webdrivermanager</artifactId>
        <version>5.0.3</version> <!-- Check for the latest version -->
    </dependency>
    ```

- **WebDriverManager (Gradle)**:
    ```gradle
    implementation 'io.github.bonigarcia:webdrivermanager:5.0.3' // Check for the latest version
    ```

### 4. **Custom Font**
The project includes a custom font (`ABeeZee-Regular.ttf`). Make sure to place this font file in the `resources` folder or another appropriate directory.


