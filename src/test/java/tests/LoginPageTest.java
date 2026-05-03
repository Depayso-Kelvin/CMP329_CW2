package tests;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import pages.LoginPage;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginPageTest {
    private WebDriver driver;
    private LoginPage loginPage;
    Wait<WebDriver> wait;

    private final By usernameField = By.id("user-name");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("login-button");

    @BeforeEach
    public void setUp() {
        driver = new ChromeDriver(Methods.passwordSecurityFix());
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        loginPage = new LoginPage(driver);
        loginPage.open("https://www.saucedemo.com/");
        wait.until(d -> d.findElement(usernameField).isDisplayed());
    }

    @AfterEach
    public void tearDown() {
        driver.quit();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/login.csv", numLinesToSkip = 1)
    public void testLogin(String username, String password, String expectedOutcome) {
        loginPage = new LoginPage(driver);
        driver.findElement(usernameField).sendKeys(username);
        driver.findElement(passwordField).sendKeys(password);
        driver.findElement(loginButton).click();

        if (expectedOutcome.equals("success")) {
            assertTrue(driver.getCurrentUrl().contains("inventory.html"));
        } else {
            assertFalse(driver.getCurrentUrl().contains("inventory.html"));
        }
    }
}
