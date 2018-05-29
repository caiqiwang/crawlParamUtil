package com.util.CrawlerUtil;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Test {
	public static void main(String[] args) {
		WebDriver driver = PhantomJsUtil.debugWebPage();
		driver.get("https://login.youzan.com/sso/index?service=kdt");
		driver.manage().window().maximize();
		try {// 睡眠5秒 等待加载
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WebElement userName = driver.findElement(By.name("mobile"));
		WebElement password = driver.findElement(By.name("password"));
		userName.sendKeys("15005732520");
		password.sendKeys("cqw15005732520");
		String imgpath = "E:\\img\\demo.png";
		String code = YunImageIdentify.invoke(imgpath);
		System.out.println(code);
		WebElement imgElement = driver.findElement(By.className("captcha-img"));
		driver.findElement(By.name("captcha_code")).sendKeys(code);
		WebElement login = driver.findElement(
				By.xpath("/html/body/div[2]/div/div/div[1]/div/div[1]/div[2]/form/fieldset/div[5]/button"));
		// 模拟点击
		login.click();
		try {// 睡眠5秒 等待加载
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String windowHandle = driver.getWindowHandle();
		driver.switchTo().window(windowHandle);
		StringBuilder sb = new StringBuilder();
		for (Cookie cookie : driver.manage().getCookies()) {
			sb.append(MessageFormat.format("{0}={1}; ", cookie.getName(), cookie.getValue()));
		}
		System.out.println(sb.toString());
	}
}
