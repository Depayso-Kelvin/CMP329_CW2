package pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class CartPage {
    private WebDriver driver;
    private WebDriverWait wait;

    private final By inventoryItemName = By.cssSelector(".inventory_item_name");

    public CartPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public String getFirstItemName() {
        // asserts observable state on the ui
        return wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryItemName)).getText();
    }
}