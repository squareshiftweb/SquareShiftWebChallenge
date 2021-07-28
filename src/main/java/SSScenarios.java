import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SSScenarios {

    WebDriver driver;
    HttpURLConnection huc = null;
    int respCode;

@BeforeMethod
public void initialize(){
    System.setProperty("webdriver.chrome.driver","/Users/saratchandratellakula/IdeaProjects/SquareShiftWebChallenge/src/main/resources/chromedriver");
    driver = new ChromeDriver();
    driver.manage().window().maximize();
}

@Test
public void scenarioOne() throws InterruptedException {
    driver.get("https://www.channelnewsasia.com/");
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

    validateNewsHeadline();

    validateSecondNewsItem();

}

@Test
public void scenarioTwo() throws InterruptedException {

    driver.get("https://www.channelnewsasia.com/news/international");
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    Thread.sleep(3000);
    Select switcher = new Select(driver.findElement(By.id("home-switcher")));
    switcher.selectByValue("SG");
    Thread.sleep(3000);

    validateNewsHeadline();

    validateSecondNewsItem();

}

@Test
public void scenarioThree() throws InterruptedException {

    driver.get("https://www.channelnewsasia.com/news/international");
    driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
    Thread.sleep(3000);

    driver.findElement(By.xpath("/html/body/header/div[2]/div/div/div[2]/button/em[2]")).click();
    driver.findElement(By.linkText("Weather")).click();
    Thread.sleep(3000);

    JavascriptExecutor js = (JavascriptExecutor) driver;
    js.executeScript("window.scrollTo(0, 1000)");

    List col = driver.findElements(By.xpath("//*[@id=\"map02\"]/div/div[1]/div/table/tbody/tr"));
    System.out.println("No of cols are : " +col.size());
    int i = 1;
    while(i<=col.size()){
        String cityName = driver.findElement(By.xpath("//*[@id=\"map02\"]/div/div[1]/div/table/tbody/tr["+i+"]/td[1]/span")).getText();
        if(cityName.equals("SINGAPORE")){
            String weatherCondition = driver.findElement(By.xpath("//*[@id=\"map02\"]/div/div[1]/div/table/tbody/tr["+i+"]/td[4]/span")).getText();
            System.out.println("The Weather condition of Singapore city is "+weatherCondition);
        }
        i++;
    }

}

@AfterMethod
public void teardown(){
//    driver.close();
//    driver.quit();
}

public void validateURL(String url){

    try {
        respCode = 200;
        huc = (HttpURLConnection)(new URL(url).openConnection());
        huc.setRequestMethod("HEAD");
        huc.connect();

        respCode = huc.getResponseCode();

    } catch (MalformedURLException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        if(respCode >= 400){
            Assert.assertFalse(false,url+" is a broken link");
        }
        else{
            System.out.println(url+" is a valid link");
        }
    }

}

public void validateNewsHeadline(){

        WebElement teaserTtile = driver.findElement(By.className("teaser__title"));
        String newsURL = teaserTtile.getAttribute("href");
        String expectedTitle = teaserTtile.getText();
        validateURL(newsURL);
        WebDriverWait wait=new WebDriverWait(driver, 10);

        teaserTtile.click();
        WebElement headline = driver.findElement(By.className("article__title"));
        wait.until(ExpectedConditions.visibilityOf(headline));
        String actualTitle = headline.getText();
        Assert.assertEquals(expectedTitle, actualTitle);
        System.out.println("We landed on the expected news item");
}

public void validateSecondNewsItem() throws InterruptedException {

    JavascriptExecutor js = (JavascriptExecutor) driver;
    int y_index = 2000;
    boolean linkClicked = false;
    String secondNewsItemHeadline="";
    String pageTitle=null;
    while(y_index<10000){
        js.executeScript("window.scrollTo(0, "+y_index+")");
        Thread.sleep(3000);
        y_index = y_index+250;
        try{
            if(driver.findElement(By.xpath("//*[@id=\"main\"]/div[3]/div[3]/div[2]/div[1]/div[2]/div/article/header/h1")).isDisplayed()){
                secondNewsItemHeadline = driver.findElement(By.xpath("//*[@id=\"main\"]/div[3]/div[3]/div[2]/div[1]/div[2]/div/article/header/h1")).getText();
                js.executeScript("window.scrollTo(0, "+y_index+")");
                driver.findElement(By.xpath("//*[@id=\"main\"]/div[3]/div[3]/div[2]/div[1]/div[3]/button")).click();
                pageTitle = driver.getTitle();
                System.out.println("Found the element and clicked on it.");
                linkClicked = true;
                break;
            }
        }catch(Exception e){
            System.out.println("The element is still not visible");
        }

    }

    Assert.assertEquals(secondNewsItemHeadline, pageTitle);
    System.out.println("The page title and second news item header are the same.");
}


}
