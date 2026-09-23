package org.openqa.selenium.chrome;

public class ChromeDriver {
	    public static void main(String[] args) {
	        // Set path to ChromeDriver (if not added to PATH)
	        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver\\chromedriver.exe");

	        // Create a new Chrome browser instance
	        WebDriver driver = new ChromeDriver();

	        // Open a website
	        driver.get("https://www.google.com");

	        // Print the page title
	        System.out.println("Page Title: " + driver.getTitle());

	        // Close the browser
	        driver.quit();
	}


}
