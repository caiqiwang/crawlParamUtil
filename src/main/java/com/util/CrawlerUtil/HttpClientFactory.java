package com.util.CrawlerUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.Cookie;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientFactory {
	private static Logger logger = LoggerFactory.getLogger(HttpClientFactory.class);

	public static void main(String[] args) {

		String url = "http://www.job98.com/templates/_Common/Membership/Utils.ashx?action=login&tbxPersonalName=1532129385@qq.com&tbxPersonalPassword=cqw1532129385&_=1526970864003&tName=PersonalLogin";
		ClientCrawlParam crawlParam = new ClientCrawlParam();
		crawlParam.setUrlStr(url);
		// crawlParam.setPostParam("userName", "15005732520");
		// crawlParam.setPostParam("goingToURL", "");
		// crawlParam.setPostParam("password", "cqw15005732520");
		// crawlParam.setRequestMethod(ConstantUtil.REQUEST_POST);
		String cookier = simulationOn(crawlParam);

		// 测试cookier
		test(cookier);
	}

	public static void test(String cookier) {
		String url = "http://www.job98.com/PersonalResumeList.aspx";
		ClientCrawlParam crawlParam = new ClientCrawlParam();
		crawlParam.setUrlStr(url);
		crawlParam.setCookie(cookier);
		Document document = getDocuemnt(crawlParam);
		Elements elements = document.select("div[class=ResumeList]");
	}

	/**
	 * @author post请求 参数为json格式 只有提交json格式数据才使用
	 * @Param
	 * @Return 返回字符串
	 * @Time 2018年5月9日
	 */
	public static String postJsonData(ClientCrawlParam crawlParam) {
		String response = "";
		CloseableHttpClient httpclient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(crawlParam.getUrlStr());
		// 添加请求头信息
		httpPost.setHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
		if (crawlParam.getRequestHeadMap() != null) {
			for (Entry<String, String> entry : crawlParam.getRequestHeadMap().entrySet()) {
				httpPost.setHeader(entry.getKey(), entry.getValue());
			}
		}
		// 添加cookie
		if (crawlParam.getCookie() != null) {
			httpPost.setHeader("Cookie", crawlParam.getCookie());
		}
		StringEntity se = new StringEntity(crawlParam.getJsonData(), crawlParam.getCharset());
		se.setContentEncoding("UTF-8");
		se.setContentType("application/json");
		httpPost.setEntity(se);
		try {
			HttpResponse res = httpclient.execute(httpPost);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = res.getEntity();
				response = EntityUtils.toString(res.getEntity());// 返回json格式：

			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;
	}

	/**
	 * @author cqw
	 * @Introduce 通过crawlParam对象设置的参数，获取页面信息（暂无代理）
	 * @Param crawlParam对象
	 * @Return url页面内容
	 * @Time 2018年4月20日
	 */
	public static String getDocuemntStr(ClientCrawlParam crawlParam) {
		HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());// 保证线程安全
		HttpMethod httpMethod = getHttpMethod(crawlParam);
		try {
			// 判断是否使用代理
			if (crawlParam.isUseProxy()) {
				if (crawlParam.getProxyHost() != null) {
					// 当有host传入是 使用传入的ip
					HostConfiguration proxy = new HostConfiguration();
					proxy.setHost(crawlParam.getProxyHost(), crawlParam.getProxyPort());
					client.setHostConfiguration(proxy);
				} else {
					logger.info("请传入代理所需要的host 和port 参数");

				}
			}
			// 设置连接超时时间
			client.getHttpConnectionManager().getParams().setConnectionTimeout(50000);
			// 设置爬虫间隔频率
			Thread.sleep(crawlParam.getInterval() + crawlParam.getIntervalRange());
			// 执行请求 相当于打开网页
			client.executeMethod(httpMethod);
			// 获得请求状态吗
			int statuscode = httpMethod.getStatusCode();
			// 判断是否连接成功
			if (statuscode != HttpStatus.SC_OK) {
				logger.info("=====get document failure ,error code is " + statuscode + " , request url is "
						+ crawlParam.getUrlStr());
				return null;
			} else if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY)// 判断是否重定向
					|| (statuscode == HttpStatus.SC_MOVED_PERMANENTLY) || (statuscode == HttpStatus.SC_SEE_OTHER)
					|| (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
				Header header = httpMethod.getResponseHeader("location");
				// Header
				// cookieHeader[]=httpMethod.getResponseHeaders("Cookie");
				// 读取新的url地址
				if (header != null) {
					String newurl = header.getValue();

					if ((newurl == null) || (newurl.equals("")))
						// newurl = "/";
						httpMethod = new GetMethod(newurl);
					client.executeMethod(httpMethod);
					// 打印重定向后 结果状态码
					logger.info("Redirect:" + httpMethod.getStatusLine().toString());
				} else {
					logger.info("Invalid redirect");
					return null;
				}
			}
			InputStream inputStream = null;
			if (crawlParam.isUseGZip()) {
				inputStream = new GZIPInputStream(httpMethod.getResponseBodyAsStream());
			} else {
				inputStream = httpMethod.getResponseBodyAsStream();
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, crawlParam.getCharset()));
			String readLine = null;
			StringBuffer response = new StringBuffer();
			while ((readLine = br.readLine()) != null) {
				response.append(readLine);
			}
			br.close();
			httpMethod.releaseConnection();
			return response.toString();
		} catch (Exception e) {
			logger.error("===get document error,request url is  " + crawlParam.getUrlStr(), e);
			e.printStackTrace();
			return null;
		}

	}

	public static Document getDocuemnt(ClientCrawlParam crawlParam) {

		String docuemntStr = getDocuemntStr(crawlParam);
		if (docuemntStr == null) {
			return null;
		}
		// 转换为 document 对象
		return Jsoup.parse(docuemntStr);
	}

	/**
	 * @introduce:根据网页链接下载文件到本地(也可以是图片和视频等 )
	 * @param urlStr
	 * @param cookie
	 * @param outputPath
	 * @return String (文件保存位置绝对路径)
	 */
	public static String downloadFile(ClientCrawlParam crawlParam) {
		HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
		HttpMethod httpMethod = getHttpMethod(crawlParam);
		try {
			if (crawlParam.getOutputPath() == null) {
				return "文件名不能为空！！！";
			}
			File file = new File(crawlParam.getOutputPath());
			OutputStream os = new FileOutputStream(file);
			client.executeMethod(httpMethod);
			client.getParams().setSoTimeout(50000);
			int statuscode = httpMethod.getStatusCode();
			if (statuscode != HttpStatus.SC_OK) {
				logger.info("=====get document failure ,error code is " + statuscode + " , request url is "
						+ crawlParam.getUrlStr());
				os.close();
				return null;
			}
			InputStream inputStream = null;
			if (crawlParam.isUseGZip()) {
				inputStream = new GZIPInputStream(httpMethod.getResponseBodyAsStream());
			} else {
				inputStream = httpMethod.getResponseBodyAsStream();
			}
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.flush();
			os.close();
			httpMethod.releaseConnection();

			return file.getAbsolutePath();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "下载失败";
		}
	}

	/**
	 * @Description 模拟页面请求 不使用代理 获取账号信息的cookier
	 * @param post请求
	 *            map形式 需要传递 post请求参数和请求方式
	 * @return cookier 这里获取的cookier 只有包含登录信息，有些页面需要添加完整的cookier才能使用，需要重新拼接。
	 */
	public static String simulationOn(ClientCrawlParam crawlParam) {
		HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());// 保证线程安全
		HttpMethod httpMethod = getHttpMethod(crawlParam);
		try {
			// 设置连接超时时间
			client.getHttpConnectionManager().getParams().setConnectionTimeout(50000);
			// 设置爬虫间隔频率
			Thread.sleep(crawlParam.getInterval() + crawlParam.getIntervalRange());
			// 执行请求 相当于打开网页
			client.executeMethod(httpMethod);
			// 获得请求状态吗
			int statuscode = httpMethod.getStatusCode();
			// 判断是否连接成功
			if (statuscode == HttpStatus.SC_OK) {
				Cookie[] cookies = client.getState().getCookies();
				StringBuffer tmpcookies = new StringBuffer();
				for (Cookie c : cookies) {
					tmpcookies.append(c.toString() + ";");
					System.out.println("cookies = " + c.toString());
				}
				return tmpcookies.toString();
			} else if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY)// 判断是否重定向
					|| (statuscode == HttpStatus.SC_MOVED_PERMANENTLY) || (statuscode == HttpStatus.SC_SEE_OTHER)
					|| (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT)) {
				Header header = httpMethod.getResponseHeader("location");
				// 读取新的url地址
				if (header != null) {
					String newurl = header.getValue();
					System.out.println(newurl);
					if ((newurl != null) || (!newurl.equals(""))) {
						crawlParam.setRequestMethod(ConstantUtil.REQUEST_GET);
						crawlParam.setUrlStr(newurl);
						httpMethod = getHttpMethod(crawlParam);
						client.executeMethod(httpMethod);
						statuscode = httpMethod.getStatusCode();
						if (statuscode == HttpStatus.SC_OK) {
							Cookie[] cookies = client.getState().getCookies();
							StringBuffer tmpcookies = new StringBuffer();
							for (Cookie c : cookies) {
								tmpcookies.append(c.toString() + ";");
								System.out.println("cookies = " + c.toString());
							}
							return tmpcookies.toString();
						}
					}

				} else {
					logger.info("Invalid redirect");
					return null;
				}
			}
		} catch (Exception e) {
			logger.error("===get document error,request url is  " + crawlParam.getUrlStr(), e);
			return null;
		}
		return null;
	}

	/**
	 * @author cqw
	 * @Introduce 判断get或者post请求，并设置好请求头参数和cookie
	 * @Param CrawlParam 参数
	 * @Return 已经设置好的HttpMethod
	 * @Time 2018年4月23日
	 */
	public static HttpMethod getHttpMethod(ClientCrawlParam crawlParam) {
		HttpMethod httpMethod = null;
		// post 请求
		if (crawlParam.getRequestMethod().equals(ConstantUtil.REQUEST_POST)) {
			httpMethod = new PostMethod(crawlParam.getUrlStr());
			if (crawlParam.getIsJsonPost()) {
				// 传递json数据 crawlParam.getJsonData()
				logger.info("提交json数据的post请求，调用 postJsonData");
			} else {
				Map<String, String> params = crawlParam.getpostParam();
				if (params.size() == 0) {
					logger.info("Post 请求  需要传入参数，请确认");
					return null;
				}
				Iterator paramKeys = params.keySet().iterator();
				NameValuePair[] form = new NameValuePair[params.size()];
				int formIndex = 0;
				while (paramKeys.hasNext()) {
					String key = (String) paramKeys.next();
					Object value = params.get(key);
					if (value != null && value instanceof String && !value.equals("")) {
						form[formIndex] = new NameValuePair(key, (String) value);
						formIndex++;
					} else if (value != null && value instanceof String[] && ((String[]) value).length > 0) {
						NameValuePair[] tempForm = new NameValuePair[form.length + ((String[]) value).length - 1];
						for (int i = 0; i < formIndex; i++) {
							tempForm[i] = form[i];
						}
						form = tempForm;
						for (String v : (String[]) value) {
							form[formIndex] = new NameValuePair(key, (String) v);
							formIndex++;
						}
					} else { // 当post 参数value为""
						form[formIndex] = new NameValuePair(key, (String) value);
						formIndex++;
					}
				}
				((PostMethod) httpMethod).setRequestBody(form);
			}
		} else { // get 请求
			httpMethod = new GetMethod(crawlParam.getUrlStr());
		}
		// 添加请求头信息
		httpMethod.setRequestHeader("User-Agent",
				"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
		if (crawlParam.getRequestHeadMap() != null) {
			for (Entry<String, String> entry : crawlParam.getRequestHeadMap().entrySet()) {
				httpMethod.setRequestHeader(entry.getKey(), entry.getValue());
			}
		}
		// 添加cookie
		if (crawlParam.getCookie() != null) {
			httpMethod.addRequestHeader("Cookie", crawlParam.getCookie());
		}
		return httpMethod;
	}
}
