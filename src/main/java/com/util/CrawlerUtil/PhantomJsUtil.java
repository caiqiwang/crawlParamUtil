package com.util.CrawlerUtil;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Point;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

public class PhantomJsUtil {
	public static void main(String[] args) {
		// screenShot("http://www.qixin.com/", "E:\\img\\qxbs.png");
		// 模拟登录有赞
		// 创建无界浏览器对象
		// PhantomJSDriver driver = getHeadlessDriver();
		// 创建测试的火狐浏览器
		WebDriver driver = debugWebPage();
		driver.get("https://login.youzan.com/sso/index?service=kdt&from_source=baidu_pz_shouye_0");
		driver.manage().window().maximize();
		// driver.navigate().to("http://www.hzrc.com/ww/b/a/wwba_login.html");
		try {// 睡眠5秒 等待加载
			TimeUnit.SECONDS.sleep(3);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//
		WebElement userName = driver.findElement(By.name("mobile"));
		WebElement password = driver.findElement(By.name("password"));
		userName.sendKeys("15005732520");
		password.sendKeys("cqw15005732520");
		WebElement imgElement = driver.findElement(By.className("captcha-img"));
		String imgpath = debugPartOfScreenshot(driver, imgElement);
		System.out.println(imgpath);
		String code = YunImageIdentify.invoke(imgpath);
		System.out.println(code);
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
		// 获取新页面窗口句柄并跳转，模拟登陆完成 切换页面必加
		String windowHandle = driver.getWindowHandle();
		driver.switchTo().window(windowHandle);
		// 获取cookier
		StringBuilder sb = new StringBuilder();
		for (Cookie cookie : driver.manage().getCookies()) {
			sb.append(MessageFormat.format("{0}={1}; ", cookie.getName(), cookie.getValue()));
		}
		driver.close();
		// driver.quit();
		System.out.println(sb.toString());
	}

	/**
	 * @introduce: 获取火狐的驱动器，返回的驱动通过driver.get(url)形式来调用，driver使用结束后需要调用.close()来关闭
	 * @return WebDriver
	 */
	public static WebDriver debugWebPage() {
		String path = getResourcePath("phantomjs/geckodriver.exe");
		System.out.println(path);
		System.setProperty("webdriver.gecko.driver", path);
		return new FirefoxDriver();
	}

	/**
	 * @Description 获取resources下的资源路径
	 * @param
	 * @return
	 * @param fileName
	 * @return
	 */
	public static String getResourcePath(String fileName) {
		URL url = PhantomJsUtil.class.getClassLoader().getResource(fileName);
		if (url != null) {
			return url.getPath().substring(1);
		}
		throw new NullPointerException("filePath 位置不存在文件");
	}

	/**
	 * @introduce: 获取无界面的驱动器，返回的驱动通过driver.get(url)形式来调用，driver使用结束后需要调用.quit()来关闭
	 * @return WebDriver
	 */
	public static PhantomJSDriver getHeadlessDriver() {
		String path = getResourcePath("phantomjs/phantomjs.exe");
		DesiredCapabilities dcaps = new DesiredCapabilities();
		// ssl证书支持
		dcaps.setCapability("acceptSslCerts", true);
		// 截屏支持
		dcaps.setCapability("takesScreenshot", true);
		// css搜索支持
		dcaps.setCapability("cssSelectorsEnabled", true);
		// js支持
		dcaps.setJavascriptEnabled(true);
		dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, path);
		dcaps.setCapability(PhantomJSDriverService.PHANTOMJS_PAGE_CUSTOMHEADERS_PREFIX + "Accept-Language", "en-US");
		/*dcaps.setCapability("phantomjs.page.settings.userAgent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");
		dcaps.setCapability("phantomjs.page.customHeaders.User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36");*/
		ArrayList<String> cliArgsCap = new ArrayList<String>();
		cliArgsCap.add("--web-security=false");
		cliArgsCap.add("--ssl-protocol=any");
		cliArgsCap.add("--ignore-ssl-errors=true");
		((DesiredCapabilities) dcaps).setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
		return new PhantomJSDriver(dcaps);
	}

	/**
	 * @introduce: 用实际的火狐浏览器来调试，根据指定的webElement进行截图
	 * @param driver
	 * @param imageElement
	 * @return 保存截取的图片内容存于本地 File screen = ((TakesScreenshot)
	 *         driver).getScreenshotAs(OutputType.FILE); 直接全屏截图
	 */
	public static String debugPartOfScreenshot(WebDriver driver, WebElement imageElement) {
		// 截图整个页面
		File screen = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			BufferedImage img = ImageIO.read(screen);
			// 获得元素的高度和宽度
			int width = imageElement.getSize().getWidth();
			int height = imageElement.getSize().getHeight();
			// 创建一个矩形使用上面的高度，和宽度
			Rectangle rect = new Rectangle(width, height);
			Point p = imageElement.getLocation();
			BufferedImage dest = img.getSubimage(p.getX(), p.getY(), rect.width, rect.height);
			// 存为png格式
			ImageIO.write(dest, "png", screen);
			return screen.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @introduce: 用无界面的驱动器，根据指定的webElement进行截图
	 * @param driver
	 * @param imageElement
	 * @return String File screen = driver.getScreenshotAs(OutputType.FILE); 直接全屏截图
	 */
	public static String partOfScreenshot(PhantomJSDriver driver, WebElement imageElement) {
		// 截图整个页面
		File screen = driver.getScreenshotAs(OutputType.FILE);
		try {
			BufferedImage img = ImageIO.read(screen);
			// 获得元素的高度和宽度
			int width = imageElement.getSize().getWidth();
			int height = imageElement.getSize().getHeight();
			// 创建一个矩形使用上面的高度，和宽度
			Rectangle rect = new Rectangle(width, height);
			Point p = imageElement.getLocation();
			BufferedImage dest = img.getSubimage(p.getX(), p.getY(), rect.width, rect.height);
			// 存为png格式
			ImageIO.write(dest, "png", screen);
			return screen.getAbsolutePath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @Description 根据url进行全屏截图
	 * @param url：截图网站
	 *            imgpath:截图保存地址
	 * @return
	 * @param url
	 * @param imgPath
	 */
	public static void screenShot(String url, String imgPath) {
		String BLANK = "  ";
		Process process;
		try {
			process = Runtime.getRuntime().exec("D:\\phantomjs\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe" + BLANK // 你的phantomjs.exe路径
					+ getResourcePath("phantomJsJavaScript/screenshot.js") + BLANK // 就是段javascript脚本的存放路径
					+ url + BLANK // 你的目标url地址
					+ imgPath);// 你的图片输出路径
			InputStream inputStream = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String tmp = "";
			while ((tmp = reader.readLine()) != null) {
				if (reader != null) {
					reader.close();
				}
				if (process != null) {
					process.destroy();
					process = null;
				}
				System.out.println("渲染成功..." + imgPath);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
