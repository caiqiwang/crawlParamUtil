package com.util.CrawlerUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RegexUtil {// 正则工具类
	private static Logger logger = LoggerFactory.getLogger(HttpClientFactory.class);

	public static void main(String[] args) {
		String str = "咨询电话：02160900826  02160907593";
		System.out.println(matchFirstNumber(str));
		System.out.println(matchNumber(str));
	}

	/**
	 * @author cqw
	 * @Introduce 配置中文
	 * @Param allMatch 是否匹配全部中文
	 * @Param splitSymbol 这个为匹配的中文中间添加的分隔符
	 * @Return
	 * @Time 2018年4月14日
	 */
	public static String matchChinese(String info, boolean allMatch, String splitSymbol) {
		Pattern pattern = Pattern.compile("[\u4E00-\u9FA5]+");
		Matcher match = pattern.matcher(info);
		if (!allMatch) {
			if (match.find()) {
				return match.group();
			}
		} else if (allMatch) {
			StringBuilder sb = new StringBuilder();
			while (match.find()) {
				sb.append(match.group()).append(splitSymbol);
			}
			if (sb.length() > 0) {
				sb.setLength(sb.length() - splitSymbol.length());
				return sb.toString();
			}
		}
		return null;
	}

	/**
	 * @author cqw
	 * @Introduce匹配2个字符中间的内容
	 * @Param text ,first last boolean 是否是最后一个值
	 * @Return
	 * @Time 2018年4月12日
	 */
	public static String matchBetweenSymbol(String text, String first, String last, Boolean matchLast) {
		Pattern pattern = Pattern.compile(first + "([^" + last + "]+)");
		if (matchLast) {
			pattern = Pattern.compile(first + "(.*)" + last);
		}
		Matcher match = pattern.matcher(text);
		if (match.find()) {
			return match.group(1);
		}
		return null;
	}

	/**
	 * @author cqw
	 * @Introduce 匹配字符串中的数字（只有1组数字时使用）
	 * @Param
	 * @Return
	 * @Time 2018年5月8日
	 */

	public static String matchNumber(String text) {
		if (text == null) {
			logger.error("文本内容为空 请检查");
			return null;
		}
		Pattern pattern = Pattern.compile("(\\d+)");
		Matcher match = pattern.matcher(text);
		String number = "";
		while (match.find()) {
			number = match.group(1);
		}
		return number;
	}

	/**
	 * @Description 匹配 字符后面的数字 包含小数
	 * @param charactor
	 *            特定字符
	 * @return
	 * @param text
	 * @return
	 */
	public static String matchNumber(String text, String charactor) {
		Pattern pattern = Pattern.compile(charactor + "([-\\d.]+)");
		Matcher match = pattern.matcher(text);
		if (match.find()) {
			return match.group(1);
		}
		return null;
	}

	/**
	 * @Description 匹配字符中的第一组数字
	 * @param
	 * @return
	 * @param text
	 * @return
	 */
	public static String matchFirstNumber(String text) {
		Pattern pattern = Pattern.compile("([0-9]\\d*\\.?\\d*)");
		Matcher match = pattern.matcher(text);
		if (match.find()) {
			return match.group(1);
		}
		return null;
	}

	/**
	 * @Description 取出字符串中的所有数字
	 * @param
	 * @return 所有数字组成的字符串
	 * @param text
	 * @return
	 */
	public static String matchAllNumber(String text) {
		Pattern pattern = Pattern.compile("(\\d+)");
		Matcher match = pattern.matcher(text);
		String senddateint = "";
		while (match.find()) {
			senddateint = senddateint + match.group(1);
		}
		return senddateint;
	}
}
