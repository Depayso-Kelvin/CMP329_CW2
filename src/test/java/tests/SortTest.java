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
public class SortTest {

    private static final Logger logger = Logger.getLogger(SortTest.class.getName());

    private WebDriver driver;
    private InventoryPage inventoryPage;

    @BeforeEach
    void setup() {
        // suppress the Chrome password leak security warning
        driver = new ChromeDriver(Methods.passwordSecurityFix());
        driver.manage().window().maximize();

        // login is a precondition(we need to be on the inventory page to test sorting)
        LoginPage loginPage = new LoginPage(driver);
        loginPage.open("https://www.saucedemo.com/");
        inventoryPage = loginPage.loginAs("standard_user", "secret_sauce");
    }

    @AfterEach
    void teardown() {
        if (driver != null) driver.quit();
    }

    // Tests the sort dropdown on the inventory page.
    // Each CSV row picks a sort option and checks which product appears first.
    // CSV columns: sortOption, expectedFirstProduct
    @ParameterizedTest
    @CsvFileSource(resources = "/sort_data.csv", numLinesToSkip = 1)
    @DisplayName("Verify inventory sort filter changes the product order")
    void testSortFilter(String sortOption, String expectedFirstProduct) {

        logger.log(Level.INFO, "START: Testing sortOption={0}", sortOption);

        try {
            // select the sort option from the dropdown
            inventoryPage.selectSortOption(sortOption);

            // check the first product shown matches what we expect for that sort order
            String actualFirstProduct = inventoryPage.getFirstProductName();
            Assertions.assertEquals(expectedFirstProduct, actualFirstProduct,
                    "After sorting by '" + sortOption + "' the first product should be: " + expectedFirstProduct);

            takeScreenshot("PASS_SortTest_" + sortOption.replace(" ", "_").replace("(", "").replace(")", ""));
            logger.log(Level.INFO, "SUCCESS: sortOption={0}", sortOption);

        } catch (AssertionError e) {
            takeScreenshot("FAIL_SortTest_" + sortOption.replace(" ", "_").replace("(", "").replace(")", ""));
            logger.log(Level.WARNING, "FAILED: sortOption={0}", sortOption);
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
