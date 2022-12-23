
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;
import java.util.Set;

@Epic("GismeteoTest")
@Feature("Check gismeteo data")
public class GismeteoTest {

    String YA_URL = "https://ya.ru";

    @Test
    @Story("We try to open ya.ru, search gismeteo site, open it, by clicking link from ya.ru and select correct city weather")
    @Description("Gismeteo site test with correct city data")
    public void checkWheatherInStPete() throws InterruptedException {
        // init vars
        WebDriver driver = new ChromeDriver();
        String baseUrl = "https://ya.ru/";
        String urlForChecking = "gismeteo.ru";
        String expectedTitle = "Яндекс";
        String city = "Санкт-Петербург";
        String actualTitle = "";
        // open ya.ru
        driver.get(baseUrl);
        actualTitle = driver.getTitle();
        Assertions.assertEquals(actualTitle, expectedTitle);
        // find search form and button
        WebElement form = driver.findElement(By.id("text"));
        WebElement searchButton = driver.findElement(By.className("search3__button"));
        // search gismeteo
        form.sendKeys("gismeteo");
        searchButton.submit();
        // get links and find gismeteo.ru, click it
        List<WebElement> links = driver.findElements(By.cssSelector("#search-result > .serp-item a.link > b"));
        for (WebElement elem: links) {
            if (elem.getText().equals(urlForChecking)) {
                elem.click();
                break;
            }
        }

        Set<String> handles = driver.getWindowHandles();
        String currentHandle = driver.getWindowHandle();
        for (String handle : handles) {
            if (!handle.equals(currentHandle)) {
                driver.switchTo().window(handle);
            }
        }

        form = driver.findElement(By.className("input"));
        form.click();
        form.sendKeys(city);
        int size = (driver.findElements(By.className("search-item")).size());
        for (int i = 0; i < size; i++) {
            if (driver.findElements(By.className("search-item")).get(i).getText().equals(city)) {
                driver.findElements(By.className("search-item")).get(i).click();
                break;
            }
        }
        // check correct string on the page
        Assertions.assertTrue(driver.getPageSource().contains("Погода в Санкт-Петербурге сегодня"));

        driver.close();
        driver.switchTo().window(currentHandle);
        driver.close();
    }
}
