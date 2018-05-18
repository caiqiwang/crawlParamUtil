package com.util.CrawlerUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.httpclient.HttpStatus;
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
		String url = "http://jinhua.ganji.com/zpdianhuaxiaoshou/";
		CrawlParam crawlParam = new CrawlParam(url);
		String str = doGet(crawlParam);
		System.out.println(str);
	}

	public static Document getDoPostDocument(CrawlParam crawlParam) {
		String docuemntStr = doPost(crawlParam);
		if (docuemntStr == null) {
			return null;
		}
		// 转换为 document 对象
		return Jsoup.parse(docuemntStr);
	}

	public static Document getDoGetDocument(CrawlParam crawlParam) {
		String docuemntStr = doGet(crawlParam);
		if (docuemntStr == null) {
			return null;
		}
		// 转换为 document 对象
		return Jsoup.parse(docuemntStr);
	}

	public static Document getDoGetDocument(CrawlParam crawlParam, int index) {
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
	public static String doPost(CrawlParam crawlParam) {
		// 创建Httpclient对象
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			HttpPost httpPost = new HttpPost(crawlParam.getUrlStr());
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
	public static String postJsonData(CrawlParam crawlParam) {
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
	public static String doGet(CrawlParam crawlParam) {
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
	/*public static String downloadFile(CrawlParam crawlParam) {
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
			int statuscode = response.getStatusLine().getStatusCode();
			if (statuscode != HttpStatus.SC_OK) { // 请求成功
				logger.info("=====get document failure ,error code is " + statuscode + " , request url is "
						+ crawlParam.getUrlStr());
				os.close();
				return null;
			}
			
		} catch (Exception e) {
			return "下载失败";
		}
	}*/
}
