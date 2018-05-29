package com.util.CrawlerUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClient {// 该工具类引用apache.http包
	private static Logger logger = LoggerFactory.getLogger(HttpClient.class);

	public static void main(String[] args) {
		String url = "http://www.hzrc.com/wc/a/a/wcaa_cont.html";
		ClientCrawlParam crawlParam = new ClientCrawlParam();
		crawlParam.setUrlStr(url);
		crawlParam.setCookie(
				"longinUser=wc; CNZZDATA2145298=cnzz_eid%3D328941854-1527136281-%26ntime%3D1527136281; JSESSIONID=2FuQr6B5a206eCQWspzOeln4Ohk0RIF-qMYd1bw_fQAZ42WxmrFV!-1019999575; UM_distinctid=16390aee6ff2-0d43750df-36675459-13c680-16390aee70033; ");
		String str = doGet(crawlParam);
		System.out.println(str.indexOf("简历"));
		// String str = doGet(crawlParam);
	}

	public static Document getDoPostDocument(ClientCrawlParam crawlParam) {
		String docuemntStr = doPost(crawlParam);
		if (docuemntStr == null) {
			return null;
		}
		// 转换为 document 对象
		return Jsoup.parse(docuemntStr);
	}

	public static Document getDoGetDocument(ClientCrawlParam crawlParam) {
		String docuemntStr = doGet(crawlParam);
		if (docuemntStr == null) {
			return null;
		}
		// 转换为 document 对象
		return Jsoup.parse(docuemntStr);
	}

	public static Document getDoGetDocument(ClientCrawlParam crawlParam, int index) {
		String docuemntStr = doGet(crawlParam);
		while (index > 0 && docuemntStr == null) {
			index--;
			docuemntStr = doGet(crawlParam);
		}
		if (docuemntStr == null) {
			return null;
		}
		// 转换为 document 对象
		return Jsoup.parse(docuemntStr);
	}

	/**
	 * @author cqw
	 * @Introduce post请求， 发送正常的post参数。
	 * @Param
	 * @Return
	 * @Time 2018年5月15日
	 */
	public static String doPost(ClientCrawlParam crawlParam) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(crawlParam.getUrlStr());
			// 判断是否使用代理
			if (crawlParam.isUseProxy()) {
				HttpHost proxy = new HttpHost(crawlParam.getProxyHost(), crawlParam.getProxyPort());
				RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();
				httpPost.setConfig(requestConfig);
			}
			// 设置请求头
			httpPost.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
			if (crawlParam.getRequestHeadMap() != null) {
				for (Entry<String, String> entry : crawlParam.getRequestHeadMap().entrySet()) {
					httpPost.setHeader(entry.getKey(), entry.getValue());
				}
			}
			// 设置参数
			List<NameValuePair> postParam = new ArrayList<NameValuePair>();
			for (Iterator iter = crawlParam.getpostParam().keySet().iterator(); iter.hasNext();) {
				String name = (String) iter.next();
				String value = String.valueOf(crawlParam.getpostParam().get(name));
				postParam.add(new BasicNameValuePair(name, value));
			}
			httpPost.setEntity(new UrlEncodedFormEntity(postParam, ConstantUtil.DETAIL_CODE));
			HttpResponse response = httpClient.execute(httpPost);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) { // 请求成功
				BufferedReader br = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent(), crawlParam.getCharset()));
				StringBuffer sb = new StringBuffer("");
				String line = "";
				String NL = System.getProperty("line.separator");
				while ((line = br.readLine()) != null) {
					sb.append(line + NL);
				}
				br.close();
				httpClient.close();
				return sb.toString();
			} else { //
				logger.info("状态码：" + code + "   " + crawlParam.getUrlStr());
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
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
		// 判断是否需要代理
		if (crawlParam.isUseProxy()) {
			HttpHost proxy = new HttpHost(crawlParam.getProxyHost(), crawlParam.getProxyPort());
			RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).setConnectTimeout(10000)
					.setSocketTimeout(10000).setConnectionRequestTimeout(3000).build();
			httpPost.setConfig(requestConfig);
		}
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
			httpclient.close();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return response;
	}

	/**
	 * @author cqw
	 * @Introduce get 请求
	 * @Param
	 * @Return
	 * @Time 2018年5月15日
	 */
	public static String doGet(ClientCrawlParam crawlParam) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(crawlParam.getUrlStr());
		try {
			if (crawlParam.isUseProxy()) {
				HttpHost proxy = new HttpHost(crawlParam.getProxyHost(), crawlParam.getProxyPort());
				RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).setConnectTimeout(10000)
						.setSocketTimeout(10000).setConnectionRequestTimeout(3000).build();
				httpGet.setConfig(requestConfig);
			}

			httpGet.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
			if (crawlParam.getRequestHeadMap() != null) {
				for (Entry<String, String> entry : crawlParam.getRequestHeadMap().entrySet()) {
					httpGet.setHeader(entry.getKey(), entry.getValue());
				}
			}
			HttpResponse response = httpClient.execute(httpGet);
			int code = response.getStatusLine().getStatusCode();
			if (code == 200) { // 请求成功
				BufferedReader br = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent(), crawlParam.getCharset()));
				StringBuffer sb = new StringBuffer("");
				String line = "";
				String NL = System.getProperty("line.separator");
				while ((line = br.readLine()) != null) {
					sb.append(line + NL);
				}
				br.close();
				httpClient.close();
				return sb.toString();
			} else { //
				logger.info("状态码：" + code + "   " + crawlParam.getUrlStr());
				return null;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @introduce:根据网页链接下载文件到本地(也可以是图片和视频等 )(get请求)
	 * @param urlStr
	 * @param cookie
	 * @param outputPath
	 * @return String (文件保存位置绝对路径)
	 */
	public static String downloadFile(ClientCrawlParam crawlParam) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(crawlParam.getUrlStr());
		try {
			if (crawlParam.getOutputPath() == null) {
				return "文件名不能为空！！！";
			}
			File file = new File(crawlParam.getOutputPath());
			OutputStream os = new FileOutputStream(file);
			httpGet.setHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
			if (crawlParam.getRequestHeadMap() != null) {
				for (Entry<String, String> entry : crawlParam.getRequestHeadMap().entrySet()) {
					httpGet.setHeader(entry.getKey(), entry.getValue());
				}
			}
			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity entity = response.getEntity();
			int statuscode = response.getStatusLine().getStatusCode();
			if (statuscode != HttpStatus.SC_OK) { // 请求成功
				logger.info("=====get document failure ,error code is " + statuscode + " , request url is "
						+ crawlParam.getUrlStr());
				// os.close();
				return null;
			}
			InputStream inputStream = entity.getContent();
			// FileOutputStream fout = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();
			inputStream.close();

			httpGet.releaseConnection();
			return "下载成功 地址为+" + file.getAbsolutePath();
		} catch (Exception e) {
			return "下载失败";
		}
	}

	/**
	 * @Description 模拟登录，需要提供账号 密码 等信息存放到post参数中
	 * @param crawlParam参数为json类型为post
	 *            isJsonPost改为true
	 * @return
	 * @return
	 */
	public static String simulationOn(ClientCrawlParam crawlParam) {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			if (crawlParam.getIsJsonPost()) { // json类型传参
				System.out.println("dddd");
			} else {
				HttpPost httpPost = new HttpPost(crawlParam.getUrlStr());
				// 设置请求头
				httpPost.setHeader("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:59.0) Gecko/20100101 Firefox/59.0");
				if (crawlParam.getRequestHeadMap() != null) {
					for (Entry<String, String> entry : crawlParam.getRequestHeadMap().entrySet()) {
						httpPost.setHeader(entry.getKey(), entry.getValue());
					}
				}
				// 设置参数
				List<NameValuePair> postParam = new ArrayList<NameValuePair>();
				for (Iterator iter = crawlParam.getpostParam().keySet().iterator(); iter.hasNext();) {
					String name = (String) iter.next();
					String value = String.valueOf(crawlParam.getpostParam().get(name));
					postParam.add(new BasicNameValuePair(name, value));
				}
				httpPost.setEntity(new UrlEncodedFormEntity(postParam, ConstantUtil.DETAIL_CODE));
				HttpResponse response = httpClient.execute(httpPost);
				int statuscode = response.getStatusLine().getStatusCode();
				if ((statuscode == HttpStatus.SC_MOVED_TEMPORARILY)// 判断是否重定向或者登录成功
						|| (statuscode == HttpStatus.SC_MOVED_PERMANENTLY) || (statuscode == HttpStatus.SC_SEE_OTHER)
						|| (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT) || statuscode == HttpStatus.SC_OK) {
					Header[] headers = response.getAllHeaders();
					for (Header header : headers) {
						System.out.println(header.getName() + ": " + header.getValue());
					}
					Header[] heade = response.getHeaders("cookier");

				}
				// 如果模拟登录成功
				/* if(httpResponse.getStatusLine().getStatusCode() == 200) {2
				    Header[] headers = httpResponse.getAllHeaders();
				    for (Header header : headers) {
				        out.println(header.getName() + ": " + header.getValue());
				    }
				}*/

			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
}
