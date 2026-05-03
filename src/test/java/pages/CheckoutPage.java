package pages;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
// Page Object for the saucedemo.com checkout step one form.
// Locators and actions live here. Assertions stay in CheckoutTest.
public class CheckoutPage {

    private WebDriver driver;
    private final By firstNameField = By.id("first-name");
    private final By lastNameField  = By.id("last-name");
    private final By postalCodeField = By.id("postal-code");
    private final By continueButton  = By.id("continue");
    private final By errorMessage    = By.cssSelector("h3[data-test='error']");
    private final By pageTitle       = By.cssSelector(".title");

    public CheckoutPage(WebDriver driver) {
        this.driver = driver;
    }
    public void enterFirstName(String firstName) {
        driver.findElement(firstNameField).clear();
        driver.findElement(firstNameField).sendKeys(firstName);
    }

    public void enterLastName(String lastName) {
        driver.findElement(lastNameField).clear();
        driver.findElement(lastNameField).sendKeys(lastName);
    }

    public void enterPostalCode(String postalCode) {
        driver.findElement(postalCodeField).clear();
        driver.findElement(postalCodeField).sendKeys(postalCode);
    }

    public void clickContinue() {
        driver.findElement(continueButton).click();
    }

    // returns the error banner text shown when validation fails
    public String getErrorMessage() {
        return driver.findElement(errorMessage).getText();
    }

    // returns the page title(changes to "Checkout: Overview" on success)
    public String getPageTitle() {
        return driver.findElement(pageTitle).getText();
    }
}
