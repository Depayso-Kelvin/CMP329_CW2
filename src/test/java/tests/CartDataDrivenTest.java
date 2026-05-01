package tests;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.LoginPage;
import pages.InventoryPage;
import pages.CartPage;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class CartDataDrivenTest {

    private WebDriver driver;
    private InventoryPage inventoryPage;

    @BeforeEach
    void setup() {
        // handling the chrome password leak security warning from week 9 practical
        Map<String, Object> chromePrefs = new HashMap<>();
        chromePrefs.put("profile.password_manager_leak_detection", false);
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", chromePrefs);

        driver = new ChromeDriver(options);
        driver.manage().window().maximize();

        // login is a precondition, not the test itself
        LoginPage login = new LoginPage(driver);
        login.open("https://www.saucedemo.com/");
        inventoryPage = login.loginAs("standard_user", "secret_sauce");
    }

    @AfterEach
    void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/products.csv", numLinesToSkip = 1)
    @DisplayName("Verify dynamic cart additions via CSV data (Success and Failure)")
    void testAddToCartFlow(String productName, String expectedBadgeCount, String expectedOutcome) {

        if (expectedOutcome.equals("success")) {
            // SUCCESS SCENARIO: Item exists
            inventoryPage.addItemToCart(productName);

            // Assert visual element (cart badge)
            assertEquals(expectedBadgeCount, inventoryPage.getCartBadgeCount(), "cart badge did not update correctly");

            // Verify navigation element works and assert content
            CartPage cartPage = inventoryPage.navigateToCart();
            assertEquals(productName, cartPage.getFirstItemName(), "the wrong item was found in the cart");

        } else if (expectedOutcome.equals("failure")) {
            // FAILURE SCENARIO: Item does not exist, expect a TimeoutException
            Exception exception = assertThrows(org.openqa.selenium.TimeoutException.class, () -> {
                inventoryPage.addItemToCart(productName);
            });

            // Verify the correct type of failure occurred
            assertTrue(exception.getMessage().contains("Expected condition failed"), "Did not throw expected timeout for invalid product");
        }
    }
}