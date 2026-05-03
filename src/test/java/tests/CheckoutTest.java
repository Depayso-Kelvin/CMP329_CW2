package tests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.CheckoutPage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@ExtendWith(LoggingTestWatcher.class)
public class CheckoutTest {

    private static final Logger logger = Logger.getLogger(CheckoutTest.class.getName());

    private WebDriver driver;
    private CheckoutPage checkoutPage;

    @BeforeEach
    void setup() {
        // suppress the Chrome password leak security warning
        driver = new ChromeDriver(Methods.passwordSecurityFix());
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        driver.manage().window().maximize();

        // login, add an item and navigate to checkout
        driver.get("https://www.saucedemo.com/");
        driver.findElement(By.id("user-name")).sendKeys("standard_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        driver.findElement(By.id("add-to-cart-sauce-labs-backpack")).click();
        driver.findElement(By.cssSelector(".shopping_cart_link")).click();
        driver.findElement(By.id("checkout")).click();

        checkoutPage = new CheckoutPage(driver);
    }

    @AfterEach
    void teardown() {
        if (driver != null) driver.quit();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/ui.csv", numLinesToSkip = 1)
    @DisplayName("Test checkout form validation")
    void testCheckoutFormUI(String firstName, String lastName, String postalCode,
                            String expectedOutcome, String expectedMessage) {

        logger.log(Level.INFO, "START: Testing firstName={0}, expectedOutcome={1}",
                new Object[]{firstName, expectedOutcome});

        // treat null as empty string in case the CSV field is blank
        if (firstName == null)   firstName = "";
        if (lastName == null)    lastName = "";
        if (postalCode == null)  postalCode = "";

        try {
            checkoutPage.enterFirstName(firstName);
            checkoutPage.enterLastName(lastName);
            checkoutPage.enterPostalCode(postalCode);
            checkoutPage.clickContinue();

            if (expectedOutcome.equals("success")) {
                // on success the page title changes to "Checkout: Overview"
                Assertions.assertEquals(expectedMessage, checkoutPage.getPageTitle(),
                        "Page title should be: " + expectedMessage);

            } else if (expectedOutcome.equals("failure")) {
                // on failure an error banner appears with the validation message
                Assertions.assertEquals(expectedMessage, checkoutPage.getErrorMessage(),
                        "Error message should be: " + expectedMessage);
            }

            takeScreenshot("PASS_CheckoutTest_" + firstName);
            logger.log(Level.INFO, "SUCCESS: firstName={0}", firstName);

        } catch (AssertionError e) {
            takeScreenshot("FAIL_CheckoutTest_" + firstName);
            logger.log(Level.WARNING, "FAILED: firstName={0}", firstName);
            throw e;
        }
    }

    // saves a screenshot to the screenshots folder(named PASS or FAIL so results are easy to review)
    private void takeScreenshot(String testName) {
        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File src = ts.getScreenshotAs(OutputType.FILE);
            String dest = "src/test/resources/screenshots/" + testName + "_"
                    + LocalDateTime.now().toString().replace(':', '_') + ".png";
            Files.copy(src.toPath(), Paths.get(dest));
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Screenshot failed", e);
        }
    }
}
