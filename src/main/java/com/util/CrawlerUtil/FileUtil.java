package com.util.CrawlerUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
	public static void main(String[] args) {
		String path = "E:\\excel\\FileUtil.txt";
		List<String> list = new ArrayList<String>();
		list = reader(path);
		for (String str : list) {
			System.out.println(str);
		}
		// System.exit(true);
	}

	public static void writer(List<String> list, String path) {
		writer(list, path, "UTF-8");
	}

	/**
	 * @author cqw
	 * @Introduce 通过传递 信息集合 和保存路径 写到文件中 编码
	 * @Param code 编码格式 path 路径 list 写入信息集合
	 * @Time 2018年4月25日
	 */
	public static void writer(List<String> list, String path, String code) {
		File file = new File(path);
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), code));
			for (String str : list) {
				bw.write(str);
				bw.newLine();
			}
			bw.flush();
			bw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @author cqw
	 * @Introduce 通过文件的绝对地址 读取文件 存到list中
	 * @Param
	 * @Return
	 * @Time 2018年4月25日
	 */
	public static List<String> reader(String path, String charset) {
		List<String> list = new ArrayList<String>();
		File file = new File(path);
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));
			String line = "";
			while ((line = br.readLine()) != null) {
				list.add(line);
			}
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;
	}

	public static List<String> reader(String path) {
		return reader(path, "UTF-8");
	}

	public static void mkdirs(String absolutPath) {
		File file = new File(absolutPath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	public static void delFile(String absolutPath) {
		File file = new File(absolutPath);
		if (file.exists()) {
			file.delete();
		}
	}
}
