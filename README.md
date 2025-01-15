# Instagram Reel Scroller

InstagramReelUI is a Java-based graphical user interface (GUI) for logging into Instagram using a username and password. The application uses Flatlaf and Java Swing for modern UI styling and includes custom font support, a login form with username and password fields, and a login button. It also integrates a maze game that has a chance to be launched after the login button is clicked, the completion of this game is mandatory, after the game launches, users will not be able to use the program without completing the game. The difficulty of the mazes are randomized, with most being easy. Upon completion of the game, users can try to login again.  

**Warning:**
Once the maze is launched, the exit application button will be disabled. To close the app anyways, go to task explorer and end the task called "OpenJDK Platform Binary". 


 
## Features

- Automatic login to Instagram
- Scrolls through Instagram reels automatically
- GUI

## Dependencies

### 1. **FlatLaf**
The project uses the FlatLaf look-and-feel library to style the UI.




### 2. **Selenium WebDriver**
Selenium is used for web automation. Make sure to include the appropriate Selenium WebDriver dependency for interacting with Chrome.

### 3. **Chrome WebDriver**
To interact with Chrome using Selenium, you need the Chrome WebDriver. You can download it from [here](https://sites.google.com/chromium.org/driver/home), or use a tool like [WebDriverManager](https://github.com/bonigarcia/webdrivermanager) to manage the WebDriver automatically.


### 4. **Custom Font**
The project includes a custom font (`ABeeZee-Regular.ttf`). Make sure to place this font file in the `resources` folder or another appropriate directory.


## Future Features
- More complex minigames before allowing users to use program
- Remember me for username and password, stored and encrypted locally
- Dark mode
- Custom UI that fetches content from instagram
- Optimized code
- Better error logging
- Personal stats page and anonymized global stats
- Collect demographic data, and corrosponding for you page information via sentiment analysis with a pretrained model (which age/gender/race likes a certain type of content?)

