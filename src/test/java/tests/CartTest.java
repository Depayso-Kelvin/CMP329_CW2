package tests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.CartPage;
import pages.InventoryPage;
import pages.LoginPage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
@ExtendWith(LoggingTestWatcher.class)
public class CartTest {

    private static final Logger logger = Logger.getLogger(CartTest.class.getName());

    private WebDriver driver;
    private InventoryPage inventoryPage;

    @BeforeEach
    void setup() {
        // suppress the Chrome password leak security warning
        driver = new ChromeDriver(Methods.passwordSecurityFix());
        driver.manage().window().maximize();

        // login is a precondition for cart tests, not the thing being tested
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open("https://www.saucedemo.com/");
        inventoryPage = loginPage.loginAs("standard_user", "secret_sauce");
    }

    @AfterEach
    void teardown() {
        if (driver != null) driver.quit();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/products.csv", numLinesToSkip = 1)
    @DisplayName("Verify cart add/remove flow using CSV data")
    void testAddToCartFlow(String productName, String expectedBadgeCount, String expectedOutcome) {

        logger.log(Level.INFO, "START: Testing productName={0}, expectedOutcome={1}",
                new Object[]{productName, expectedOutcome});

        try {
            if (expectedOutcome.equals("success")) {

                // add the product and check the cart badge updates
                inventoryPage.addItemToCart(productName);
                Assertions.assertEquals(expectedBadgeCount, inventoryPage.getCartBadgeCount(),
                        "Cart badge count should be " + expectedBadgeCount + " after adding " + productName);

                // navigate to cart and check the correct item is listed
                CartPage cartPage = inventoryPage.navigateToCart();
                Assertions.assertEquals(productName, cartPage.getFirstItemName(),
                        "Cart should contain: " + productName);

            } else if (expectedOutcome.equals("failure")) {

                // a product that doesn't exist should throw a TimeoutException when we try to click it
                Exception exception = Assertions.assertThrows(org.openqa.selenium.TimeoutException.class, () -> {
                    inventoryPage.addItemToCart(productName);
                });

                Assertions.assertTrue(exception.getMessage().contains("Expected condition failed"),
                        "Should throw a timeout for a product that does not exist on the page");
            }

            takeScreenshot("PASS_CartTest_" + productName.replace(" ", "_"));
            logger.log(Level.INFO, "SUCCESS: productName={0}", productName);

        } catch (AssertionError e) {
            takeScreenshot("FAIL_CartTest_" + productName.replace(" ", "_"));
            logger.log(Level.WARNING, "FAILED: productName={0}", productName);
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
