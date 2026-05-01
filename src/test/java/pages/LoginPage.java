package pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class LoginPage {
    private WebDriver driver;
    private WebDriverWait wait;

    // stable locators mapped directly to the saucedemo.com HTML
    private final By usernameField = By.id("user-name");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("login-button");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void open(String url) {
        driver.get(url);
    }

    // groups the login steps into one reusable action
    // returns the InventoryPage object because that's where a successful login takes us
    public InventoryPage loginAs(String username, String password) {

        // wait for the username field to actually be visible before typing
        wait.until(ExpectedConditions.visibilityOfElementLocated(usernameField)).sendKeys(username);

        // password field is definitely there if username is, so standard findElement is fine
        driver.findElement(passwordField).sendKeys(password);

        // wait for the login button to be clickable
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();

        return new InventoryPage(driver);
    }
}