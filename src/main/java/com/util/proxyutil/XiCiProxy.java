package com.util.proxyutil;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.util.CrawlerUtil.ConstantUtil;
import com.util.CrawlerUtil.ClientCrawlParam;
import com.util.CrawlerUtil.FileUtil;
import com.util.CrawlerUtil.HttpClientFactory;

import redis.clients.jedis.Jedis;

//爬取西刺代理上的Ip  存入redis 的key为proxy中
public class XiCiProxy {
	private static Logger logger = LoggerFactory.getLogger(HttpClientFactory.class);

	public static void main(String[] args) {
		/*String path = "C:\\Users\\cqw\\workspace\\CrawlerUtil\\src\\main\\resources\\proxyip\\xiciProxy.txt";
		// getProxy();
		
		System.exit(0);*/
		Jedis jedis = new Jedis("127.0.0.1", 6379);
		jedis.auth("foobared");
		jedis.sadd("MFWdetail", "这点就是龙卷风");
		System.out.println(jedis.scard("MFWdetail").toString());
	}

	// 内容写到文件中
	/**
	 * @author cqw
	 * @Introduce 传入文件保存路径，自动爬取内容 写入txt文本中
	 * @Param
	 * @Return
	 * @Time 2018年4月25日
	 */
	public static void getProxy(String path) {
		FileUtil.delFile(path);
		// 西刺首页
		String indexUrl = "http://www.xicidaili.com/nn";
		List<String> list = new ArrayList<>();
		ClientCrawlParam crawlParam = new ClientCrawlParam();
		String IPInfo = "";
		for (int i = 0; i < 30; i++) {// 站定爬取1200页ip
			if (i == 0) {
				crawlParam.setUrlStr(indexUrl);
				Document document = HttpClientFactory.getDocuemnt(crawlParam);
				if (document == null) {
					logger.info("首页进入失败，请重试");
					return;
				}
				Elements elements = document.select("table[id=ip_list]");
				elements = document.select("tbody");
				if (elements.size() == 0) {
					logger.info("西刺----进入div失败，请检查网络");
					return;
				}
				elements = elements.select("tr");
				for (int j = 1; j < elements.size(); j++) {
					Element info = elements.get(j);
					Elements ipInfo = info.select("td");
					IPInfo = ipInfo.get(1).text() + ":" + ipInfo.get(2).text();
					list.add(IPInfo);
				}
			} else {
				String nextUrl = indexUrl + "/" + i + 1;
				crawlParam.setUrlStr(nextUrl);
				Document document = HttpClientFactory.getDocuemnt(crawlParam);
				if (document == null) {
					logger.info("首页进入失败，请重试");
					return;
				}
				Elements elements = document.select("table[id=ip_list]");
				elements = document.select("tbody");
				if (elements.size() == 0) {
					logger.info("西刺----进入div失败，请检查网络");
					return;
				}
				elements = elements.select("tr");
				for (int j = 1; j < elements.size(); j++) {
					Element info = elements.get(j);
					Elements ipInfo = info.select("td");
					IPInfo = ipInfo.get(1).text() + ":" + ipInfo.get(2).text();
					list.add(IPInfo);
				}
			}
		}
		FileUtil.writer(list, path);
	}

	/**
	 * @author cqw
	 * @Introduce 获取代理 数据存储到redis
	 * @Param
	 * @Return
	 * @Time 2018年4月27日
	 */
	public static void getProxy(Jedis jedis) {
		/* = new Jedis("127.0.0.1", 6379);
		jedis.auth("foobared");*/
		// 每次获取ip前 先删除原先的ip
		if (jedis.exists(ConstantUtil.PROXY_POOL_NAME)) {
			jedis.del(ConstantUtil.PROXY_POOL_NAME);
			System.out.println("redis中key 为" + ConstantUtil.PROXY_POOL_NAME + "数据已经清空");
		}
		String indexUrl = "http://www.xicidaili.com/nn";
		ClientCrawlParam crawlParam = new ClientCrawlParam();
		String IPInfo = "";
		for (int i = 0; i < 30; i++) {// 站定爬取1200页ip
			if (i == 0) {
				crawlParam.setUrlStr(indexUrl);
				Document document = HttpClientFactory.getDocuemnt(crawlParam);
				if (document == null) {
					logger.info("首页进入失败，请重试");
					return;
				}
				Elements elements = document.select("table[id=ip_list]");
				elements = document.select("tbody");
				if (elements.size() == 0) {
					logger.info("西刺----进入div失败，请检查网络");
					return;
				}
				elements = elements.select("tr");
				for (int j = 1; j < elements.size(); j++) {
					Element info = elements.get(j);
					Elements ipInfo = info.select("td");
					IPInfo = ipInfo.get(1).text() + ":" + ipInfo.get(2).text();
					jedis.sadd(ConstantUtil.PROXY_POOL_NAME, IPInfo);
				}
			} else {
				String nextUrl = indexUrl + "/" + i + 1;
				crawlParam.setUrlStr(nextUrl);
				Document document = HttpClientFactory.getDocuemnt(crawlParam);
				if (document == null) {
					logger.info("首页进入失败，请重试");
					return;
				}
				Elements elements = document.select("table[id=ip_list]");
				elements = document.select("tbody");
				if (elements.size() == 0) {
					logger.info("西刺----进入div失败，请检查网络");
					return;
				}
				elements = elements.select("tr");
				for (int j = 1; j < elements.size(); j++) {
					Element info = elements.get(j);
					Elements ipInfo = info.select("td");
					IPInfo = ipInfo.get(1).text() + ":" + ipInfo.get(2).text();
					jedis.sadd(ConstantUtil.PROXY_POOL_NAME, IPInfo);
				}
			}
		}
	}
}
