package tests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
public class RemoveFromCartTest {

    private static final Logger logger = Logger.getLogger(RemoveFromCartTest.class.getName());

    private WebDriver driver;
    private InventoryPage inventoryPage;

    @BeforeEach
    void setup() {
        // suppress the Chrome password leak security warning
        driver = new ChromeDriver(Methods.passwordSecurityFix());
        driver.manage().window().maximize();

        // login is a precondition for all remove tests
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open("https://www.saucedemo.com/");
        inventoryPage = loginPage.loginAs("standard_user", "secret_sauce");
    }

    @AfterEach
    void teardown() {
        if (driver != null) driver.quit();
    }

    // Tests that adding then removing an item from the cart updates the UI correctly.
    // CSV columns: productName, expectedBadgeAfterAdd
    @ParameterizedTest
    @CsvFileSource(resources = "/remove_data.csv", numLinesToSkip = 1)
    @DisplayName("Verify removing an item from the cart clears the cart badge")
    void testRemoveFromCart(String productName, String expectedBadgeAfterAdd) {

        logger.log(Level.INFO, "START: Testing remove for productName={0}", productName);

        try {
            // add the item first so there is something to remove
            inventoryPage.addItemToCart(productName);

            // check the badge updated after adding
            Assertions.assertEquals(expectedBadgeAfterAdd, inventoryPage.getCartBadgeCount(),
                    "Cart badge should be " + expectedBadgeAfterAdd + " after adding " + productName);

            // now remove the same item
            inventoryPage.removeItemFromCart(productName);

            // the cart badge should disappear completely when the cart is empty
            Assertions.assertTrue(inventoryPage.isCartBadgeGone(),
                    "Cart badge should disappear after removing " + productName);

            takeScreenshot("PASS_RemoveTest_" + productName.replace(" ", "_"));
            logger.log(Level.INFO, "SUCCESS: productName={0}", productName);

        } catch (AssertionError e) {
            takeScreenshot("FAIL_RemoveTest_" + productName.replace(" ", "_"));
            logger.log(Level.WARNING, "FAILED: productName={0}", productName);
            throw e;
        }
    }

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
