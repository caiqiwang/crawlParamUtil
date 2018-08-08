package com.util.CrawlerUtil;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.small.crawler.util.document.CrawlParam;
import com.small.crawler.util.document.HttpURLConnectionFactory;
import com.util.proxyutil.XiCiProxy;

import redis.clients.jedis.Jedis;

//代理IP库。
public class ProxyUtil {
	private static Logger logger = LoggerFactory.getLogger(ProxyUtil.class);
	// IP地址代理库Map
	private static Map<String, Integer> IPProxyRepository = new HashMap<>();
	// keysArray是为了方便生成随机的代理对象
	private static String[] keysArray = null;

	/**
	 * 使用静态代码块将IP代理库加载进set中 每次调用getRandomProxy 获取随机代理IP都会先加载IP池
	 */
	/*static {
		InputStream in = HttpUtil.class.getClassLoader().getResourceAsStream("IPProxyRepository.txt"); // 加载包含代理IP的文本
		// 构建缓冲流对象
		InputStreamReader isr = new InputStreamReader(in);
		BufferedReader bfr = new BufferedReader(isr);
		File file = new File("C:\\Users\\cqw\\workspace\\CrawlerUtil\\src\\main\\resources\\proxyip\\xiciProxy.txt");
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
			String line = null;
			// 循环读每一行，添加进map中
			while ((line = br.readLine()) != null) {
				String[] split = line.split(":"); // 以:作为分隔符，即文本中的数据格式应为192.168.1.1:4893
				String host = split[0];
				int port = Integer.valueOf(split[1]);
				IPProxyRepository.put(host, port);
			}
			Set<String> keys = IPProxyRepository.keySet();
			keysArray = keys.toArray(new String[keys.size()]); // keysArray是为了方便生成随机的代理对象
		} catch (IOException e) {
			e.printStackTrace();
		}
	
	}*/

	/**
	 * 随机返回一个未验证的代理对象
	 *
	 * @return
	 */
	/*public static HostConfiguration getRandomProxy() {
		// 随机获取host:port，并构建代理对象
		Random random = new Random();
		String host = keysArray[random.nextInt(keysArray.length)];
		int port = IPProxyRepository.get(host);
		HostConfiguration proxy = new HostConfiguration(); // 设置http代理
		proxy.setProxy(host, port);
		return proxy;
	}*/

	/**
	 * @author cqw
	 * @Introduce 验证西刺代理池中代理是否可用，并将可用代理存入 nameToProxy中
	 * @Param nameToProxy可用代理池的key值 不可重复 (暂存1000)
	 * @Param info 网站正常打开存在的内容 判断代理可用
	 * @Param proxyObject 代理对象 选择获取那个网站的代理（现只有xici）
	 * @Return
	 * @Time 2018年4月27日
	 */
	public static void getProxyPool(CrawlParam crawlParam, Jedis jedis, String nameToProxy, String info,
			String proxyObject) {
		// 先判断xici代理词是否存在
		if ("xici".equals(proxyObject)) {
			if (!jedis.exists(ConstantUtil.PROXY_POOL_NAME)) {
				XiCiProxy.getProxy(jedis);
			}
		}
		if (!jedis.exists(ConstantUtil.PROXY_POOL_NAME)) {
			logger.info("xici代理池创建失败，请检查原因");
		}
		// 从redis的所有代理的代理库中随机获取一个ip
		String IpInfo = jedis.srandmember(ConstantUtil.PROXY_POOL_NAME);
		// 如果为空 重新抓取代理
		if (IpInfo == null) {
			XiCiProxy.getProxy(jedis);
		}
		String[] split = IpInfo.split(":"); // 以:作为分隔符，数据格式应为192.168.1.1:4893
		String host = split[0];
		String port = split[1];
		crawlParam.setUseProxy(true);
		// 重复取3次 确保ip不能使用
		crawlParam.setTryCount(3);
		String document = HttpURLConnectionFactory.getDocumentStr(crawlParam, host, port);
		// 3次后还为空 则删除redis中的ip信息
		if (document == null || !document.contains(info)) {
			jedis.srem(ConstantUtil.PROXY_POOL_NAME, IpInfo);
			logger.info("成功删除 不可用IP：" + IpInfo);
			getProxyPool(crawlParam, jedis, nameToProxy, info, proxyObject);
		} else {
			// 可用放入新的代理池，并删除老代理池中的数据
			jedis.sadd(nameToProxy, IpInfo);
			jedis.srem(ConstantUtil.PROXY_POOL_NAME, IpInfo);
			// 如果可用代理大于1000则不继续存放
			if (jedis.scard(nameToProxy) < 1000) {
				getProxyPool(crawlParam, jedis, nameToProxy, info, proxyObject);
			}
			return;
		}
	}

	/**
	 * @author cqw
	 * @Introduce 代理池使用完后进行删除
	 * @Param isTrue 是否删除西刺代理池
	 * @Param nameToProxy自己创建代理池的名称
	 * @Return
	 * @Time 2018年4月27日
	 */
	public static void delProxy(Jedis jedis, String nameToProxy, Boolean isTrue) {
		if (jedis.exists(nameToProxy)) {
			jedis.del(nameToProxy);
		}
		if (isTrue) {
			delXiCiProxy(jedis);
		}
		logger.info(nameToProxy + "代理池已经删除");
	}

	public static void delXiCiProxy(Jedis jedis) {
		if (jedis.exists(ConstantUtil.PROXY_POOL_NAME)) {
			jedis.del(ConstantUtil.PROXY_POOL_NAME);
		}
		logger.info("西刺代理池已经删除");
	}
}
