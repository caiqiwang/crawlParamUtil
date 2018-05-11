package com.util.CrawlerUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class CrawlParam {
	private final static Random RANDOM = new Random();

	// 访问链接，必须有
	private String urlStr;
	// 间隔时间 单位是毫秒 默认0 控制爬虫访问频率的(单位ms)
	private int interval;
	// 随机时间范围 默认0(单位ms)
	private int intervalRange;
	// 请求方式 默认是get请求
	private String requestMethod = ConstantUtil.REQUEST_GET;
	// post请求参数，get请求时为空，post请求时不能为空
	// private String postParam;
	// cookie值，默认是null
	private String cookie;
	// 编码方式 默认是utf-8
	private String charset = ConstantUtil.DETAIL_CODE;
	// 是否使用代理 默认是false
	private boolean useProxy = false;
	// 是否使用GZip解析获取的数据流，默认是false
	private boolean useGZip = false;
	// 访问网址保存路径 默认null，不为空时最好填写绝对路径
	private String outputPath;
	// 请求头信息（通常不用设置）
	private Map<String, String> requestHeadMap;
	private Map<String, String> postParam;
	// post请求传递的参数类型。默认为map。true为json格式
	private Boolean isJsonPost = false;
	private String jsonData;
	// 代理IP 默认为空
	private String proxyHost;
	// 代理端口 默认为空
	private int proxyPort;

	public String getJsonData() {
		return jsonData;
	}

	public void setJsonData(String jsonData) {
		this.jsonData = jsonData;
	}

	// post 参数数据 存于map中
	public Map<String, String> getpostParam() {
		return postParam;
	}

	public CrawlParam setPostParam(String key, String value) {
		if (this.postParam == null) {
			this.postParam = new HashMap<String, String>();
		}
		this.postParam.put(key, value);
		return this;
	}

	public CrawlParam setPostParam(Map<String, String> infoMap) {

		this.postParam = infoMap;
		return this;
	}

	public CrawlParam setProxyHost(String proxyHost) {
		this.proxyHost = proxyHost;
		return this;
	}

	public String getProxyHost() {
		return proxyHost;
	}

	public Boolean getIsJsonPost() {
		return isJsonPost;
	}

	public CrawlParam setIsJsonPost(Boolean isJsonPost) {
		this.isJsonPost = isJsonPost;
		return this;
	}

	public CrawlParam setProxyPort(int proxyPort) {
		this.proxyPort = proxyPort;
		return this;
	}

	public int getProxyPort() {
		return proxyPort;
	}

	public CrawlParam() {

	}

	public CrawlParam(String urlStr) {
		this.urlStr = urlStr;
	}

	public String getUrlStr() {
		return urlStr;
	}

	public CrawlParam setUrlStr(String urlStr) {
		this.urlStr = urlStr;
		return this;
	}

	public int getInterval() {
		return interval;
	}

	public CrawlParam setInterval(int interval) {
		this.interval = interval;
		return this;
	}

	public int getIntervalRange() {
		return intervalRange;
	}

	public CrawlParam setIntervalRange(int intervalRange) {
		this.intervalRange = RANDOM.nextInt(intervalRange);
		return this;
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public CrawlParam setRequestMethod(String requestMethod) {
		this.requestMethod = requestMethod;
		return this;
	}

	/*public String getPostParam() {
		return postParam;
	}
	
	public CrawlParam setPostParam(String postParam) {
		this.postParam = postParam;
		return this;
	}*/

	public String getCookie() {
		return cookie;
	}

	public CrawlParam setCookie(String cookie) {
		this.cookie = cookie;
		return this;
	}

	public String getCharset() {
		return charset;
	}

	public CrawlParam setCharset(String charset) {
		this.charset = charset;
		return this;
	}

	public boolean isUseProxy() {
		return useProxy;
	}

	public CrawlParam setUseProxy(boolean useProxy) {
		this.useProxy = useProxy;
		return this;
	}

	public boolean isUseGZip() {
		return useGZip;
	}

	public CrawlParam setUseGZip(boolean useGZip) {
		this.useGZip = useGZip;
		return this;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public CrawlParam setOutputPath(String outputPath) {
		this.outputPath = outputPath;
		return this;
	}

	public Map<String, String> getRequestHeadMap() {
		return requestHeadMap;
	}

	public CrawlParam setRequestHeadInfo(String key, String value) {
		if (this.requestHeadMap == null) {
			this.requestHeadMap = new HashMap<String, String>();
		}
		this.requestHeadMap.put(key, value);
		return this;
	}

	public CrawlParam setRequestHeadInfo(Map<String, String> infoMap) {

		this.requestHeadMap = infoMap;
		return this;
	}

}
