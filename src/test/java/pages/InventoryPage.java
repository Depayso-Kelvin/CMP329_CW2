package pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
public class InventoryPage {

    private WebDriver driver;
    private WebDriverWait wait;

    private final By cartBadge = By.cssSelector(".shopping_cart_badge");
    private final By cartLink = By.cssSelector(".shopping_cart_link");
    private final By sortDropdown = By.cssSelector("[data-test='product-sort-container']");
    private final By firstProductName = By.cssSelector(".inventory_item_name");

    public InventoryPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // builds the Add to Cart button locator dynamically from the product name in the CSV
    public void addItemToCart(String productName) {
        String xpath = String.format(
            "//div[text()='%s']/ancestor::div[@class='inventory_item']//button", productName
        );
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
    }

    // builds the Remove button locator dynamically from the product name
    public void removeItemFromCart(String productName) {
        String xpath = String.format(
            "//div[text()='%s']/ancestor::div[@class='inventory_item']//button[text()='Remove']", productName
        );
        wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath))).click();
    }

    // selects a sort option by its visible label e.g. "Name (A to Z)"
    // uses the standard Selenium Select class because this is a native HTML element
    public void selectSortOption(String visibleText) {
        wait.until(ExpectedConditions.elementToBeClickable(sortDropdown));
        Select select = new Select(driver.findElement(sortDropdown));
        select.selectByVisibleText(visibleText);
    }

    // returns the name of the first product currently shown on the inventory page
    public String getFirstProductName() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(firstProductName)).getText();
    }

    public String getCartBadgeCount() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(cartBadge)).getText();
    }

    // returns true if the cart badge has disappeared(cart is empty)
    public boolean isCartBadgeGone() {
        return driver.findElements(cartBadge).isEmpty();
    }

    public CartPage navigateToCart() {
        wait.until(ExpectedConditions.elementToBeClickable(cartLink)).click();
        return new CartPage(driver);
    }
}
