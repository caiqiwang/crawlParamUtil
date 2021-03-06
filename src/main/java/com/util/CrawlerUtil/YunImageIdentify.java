package com.util.CrawlerUtil;

import java.io.UnsupportedEncodingException;

import com.sun.jna.Library;
import com.sun.jna.Native;

public class YunImageIdentify {
	private static String DLLPATH = PhantomJsUtil.getResourcePath("lib/yundamaAPI-x64.dll");
	private static int uid = 0;

	public interface YDM extends Library {
		YDM INSTANCE = (YDM) Native.loadLibrary(DLLPATH, YDM.class);

		public void YDM_SetBaseAPI(String lpBaseAPI);

		public void YDM_SetAppInfo(int nAppId, String lpAppKey);

		public int YDM_Login(String lpUserName, String lpPassWord);

		public int YDM_DecodeByPath(String lpFilePath, int nCodeType, byte[] pCodeResult);

		public int YDM_UploadByPath(String lpFilePath, int nCodeType);

		public int YDM_EasyDecodeByPath(String lpUserName, String lpPassWord, int nAppId, String lpAppKey,
				String lpFilePath, int nCodeType, int nTimeOut, byte[] pCodeResult);

		public int YDM_DecodeByBytes(byte[] lpBuffer, int nNumberOfBytesToRead, int nCodeType, byte[] pCodeResult);

		public int YDM_UploadByBytes(byte[] lpBuffer, int nNumberOfBytesToRead, int nCodeType);

		public int YDM_EasyDecodeByBytes(String lpUserName, String lpPassWord, int nAppId, String lpAppKey,
				byte[] lpBuffer, int nNumberOfBytesToRead, int nCodeType, int nTimeOut, byte[] pCodeResult);

		public int YDM_GetResult(int nCaptchaId, byte[] pCodeResult);

		public int YDM_Report(int nCaptchaId, boolean bCorrect);

		public int YDM_EasyReport(String lpUserName, String lpPassWord, int nAppId, String lpAppKey, int nCaptchaId,
				boolean bCorrect);

		public int YDM_GetBalance(String lpUserName, String lpPassWord);

		public int YDM_EasyGetBalance(String lpUserName, String lpPassWord, int nAppId, String lpAppKey);

		public int YDM_SetTimeOut(int nTimeOut);

		public int YDM_Reg(String lpUserName, String lpPassWord, String lpEmail, String lpMobile, String lpQQUin);

		public int YDM_EasyReg(int nAppId, String lpAppKey, String lpUserName, String lpPassWord, String lpEmail,
				String lpMobile, String lpQQUin);

		public int YDM_Pay(String lpUserName, String lpPassWord, String lpCard);

		public int YDM_EasyPay(String lpUserName, String lpPassWord, long nAppId, String lpAppKey, String lpCard);
	}

	public static String invoke(String imagePath) {

		String username = "18257344546";
		String password = "1042030201";

		int appid = 1;
		String appkey = "22cc5376925e9387a23cf797cb9ba745";

		// 图片路径
		// String imagepath = CrawlerUtil.getResourcePath("img/captcha.png");

		// 例：1004表示4位字母数字，不同类型收费不同。请准确填写，否则影响识别率。在此查询所有类型
		// http://www.yundama.com/price.html
		int codetype = 1004;

		// 只需要在初始的时候登陆一次
		if (uid == 0) {
			YDM.INSTANCE.YDM_SetAppInfo(appid, appkey); // 设置软件ID和密钥
			uid = YDM.INSTANCE.YDM_Login(username, password); // 登陆到云打码
			if (uid > 0) {
				System.out.println("登陆成功,正在提交识别...");
			}
		}
		if (uid > 0) {
			byte[] byteResult = new byte[30];
			int cid = YDM.INSTANCE.YDM_DecodeByPath(imagePath, codetype, byteResult);
			// 返回其他错误代码请查询 http://www.yundama.com/apidoc/YDM_ErrorCode.html
			System.out.println("识别返回代码:" + cid);
			try {
				String strResult = new String(byteResult, "UTF-8").trim();
				return strResult;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("登录失败，请核查！");
			uid = 0;
		}
		return "";
	}

	// 解析11位电话号码
	public static String invokePhone(String imagePath) {

		String username = "18257344546";
		String password = "1042030201";

		int appid = 1;
		String appkey = "22cc5376925e9387a23cf797cb9ba745";

		// 图片路径
		// String imagepath = CrawlerUtil.getResourcePath("img/captcha.png");

		// 例：4011表示11位数字，不同类型收费不同。请准确填写，否则影响识别率。在此查询所有类型
		// http://www.yundama.com/price.html
		int codetype = 3012;

		// 只需要在初始的时候登陆一次
		if (uid == 0) {
			YDM.INSTANCE.YDM_SetAppInfo(appid, appkey); // 设置软件ID和密钥
			uid = YDM.INSTANCE.YDM_Login(username, password); // 登陆到云打码
			if (uid > 0) {
				System.out.println("登陆成功,正在提交识别...");
			}
		}
		if (uid > 0) {
			byte[] byteResult = new byte[30];
			int cid = YDM.INSTANCE.YDM_DecodeByPath(imagePath, codetype, byteResult);
			// 返回其他错误代码请查询 http://www.yundama.com/apidoc/YDM_ErrorCode.html
			System.out.println("识别返回代码:" + cid);
			try {
				String strResult = new String(byteResult, "UTF-8").trim();
				return strResult;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("登录失败，请核查！");
			uid = 0;
		}
		return "";
	}

	/**
	 * @Description
	 * @param
	 * @return
	 * @param imagePath
	 * @param codetype
	 *            验证码的数量
	 * @return
	 */
	public static String invoke(String imagePath, int codetype) {

		String username = "18257344546";
		String password = "1042030201";

		int appid = 1;
		String appkey = "22cc5376925e9387a23cf797cb9ba745";

		// 图片路径
		// String imagepath = CrawlerUtil.getResourcePath("img/captcha.png");

		// 例：1004表示4位字母数字，不同类型收费不同。请准确填写，否则影响识别率。在此查询所有类型
		// http://www.yundama.com/price.html
		// int codetype = 1004;

		// 只需要在初始的时候登陆一次
		if (uid == 0) {
			YDM.INSTANCE.YDM_SetAppInfo(appid, appkey); // 设置软件ID和密钥
			uid = YDM.INSTANCE.YDM_Login(username, password); // 登陆到云打码
			if (uid > 0) {
				System.out.println("登陆成功,正在提交识别...");
			}
		}
		if (uid > 0) {
			byte[] byteResult = new byte[30];
			int cid = YDM.INSTANCE.YDM_DecodeByPath(imagePath, codetype, byteResult);
			// 返回其他错误代码请查询 http://www.yundama.com/apidoc/YDM_ErrorCode.html
			System.out.println("识别返回代码:" + cid);
			try {
				String strResult = new String(byteResult, "UTF-8").trim();
				return strResult;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("登录失败，请核查！");
			uid = 0;
		}
		return "";
	}

	public static void main(String[] args) {
		String path = "E:\\img\\JSJobTel.png";
		String code = invoke(path, 1012);
		System.out.println(code);
	}
}
