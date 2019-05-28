package com.example.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * 文件表单上传
 */
/**
 * Created by kaiyi.cky on 2015/8/16.
 */
/**
 * 文件表单上传
 */
public abstract class InternetFileUtils {

	// 分割符
	private static final String BOUNDARY = "----WebKitFormBoundaryT1HoybnYeFOGFlBR";
	public static final String ROOTURL = "http://39.106.196.211:8080/SportsServer/";

	/**
	 * HttpUrlConnection 实现文件上传
	 * 
	 * @param params
	 *            普通参数
	 * @param fileFormName
	 *            文件在表单中的键
	 * @param uploadFile
	 *            上传的文件
	 * @param newFileName
	 *            文件在表单中的值（服务端获取到的文件名）
	 * @param urlStr
	 *            url
	 * @throws Exception
	 */
	@SuppressWarnings("serial")
	protected static byte[] getUrlBytes(Map<String, String> params, String fileFormName, File uploadFile,
			String newFileName, String urlStr, byte[] filebytes) throws Exception {

		StringBuilder sb = new StringBuilder();
		HttpURLConnection conn = null;
		/**
		 * 普通的表单数据
		 */
		if (params != null) {
			for (String key : params.keySet()) {
				sb.append("--" + BOUNDARY + "\r\n");
				sb.append("Content-Disposition: form-data; name=\"" + key + "\"" + "\r\n");
				sb.append("\r\n");
				sb.append(params.get(key) + "\r\n");
			}
		}

		/**
		 * 上传文件的头
		 */
		sb.append("--" + BOUNDARY + "\r\n");
		sb.append("Content-Disposition: form-data; name=\"" + fileFormName + "\"; filename=\"" + newFileName + "\""
				+ "\r\n");
		// sb.append("Content-Type: image/jpeg" + "\r\n");//
		// 如果服务器端有文件类型的校验，必须明确指定ContentType
		sb.append("\r\n");

		byte[] headerInfo = sb.toString().getBytes("UTF-8");
		byte[] endInfo = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("UTF-8");

		URL url = new URL(urlStr);
		conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		// 设置传输内容的格式，以及长度
		conn.setRequestProperty("Content-Type", "multipart/form-data; charset=UTF-8; boundary=" + BOUNDARY);
		conn.setDoOutput(true);
		InputStream in = null;
		if (uploadFile != null && filebytes != null) {
			throw new Exception() {
				@Override
				public void printStackTrace() {
					// TODO Auto-generated method stub
					System.err.println("uploadFile and ilebytes can not exists at the same time");
				}
			};
		}else if(uploadFile!=null||filebytes!=null){
			if (uploadFile != null) {
				conn.setRequestProperty("Content-Length",
						String.valueOf(headerInfo.length + uploadFile.length() + endInfo.length));
				in = new FileInputStream(uploadFile);

			} else if (filebytes != null) {
				conn.setRequestProperty("Content-Length",
						String.valueOf(headerInfo.length + filebytes.length + endInfo.length));
				in = new ByteArrayInputStream(filebytes);
			}
			OutputStream outs = conn.getOutputStream();
			// 写入头部 （包含了普通的参数，以及文件的标示等）
			outs.write(headerInfo);
			// 写入文件
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) != -1) {
				outs.write(buf, 0, len);
			}
			// 写入尾部
			outs.write(endInfo);
			in.close();
			outs.close();
		}else{
			conn.setRequestProperty("Content-Length",
					String.valueOf(headerInfo.length +  endInfo.length));
			OutputStream outs = conn.getOutputStream();
			// 写入头部 （包含了普通的参数，以及文件的标示等）
			outs.write(headerInfo);
			outs.write(endInfo);
			outs.close();
		}

		try {
			ByteArrayOutputStream baouts = new ByteArrayOutputStream();
			InputStream ins = conn.getInputStream();
			System.out.println(conn.getResponseCode());
			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new IOException(conn.getResponseMessage() + ": with " + urlStr);
			}

			int bytesRead = 0;
			byte[] buffer = new byte[1024];
			while ((bytesRead = ins.read(buffer)) > 0) {
				baouts.write(buffer, 0, bytesRead);
			}
			baouts.close();
			return baouts.toByteArray();
		} finally {
			// TODO: handle finally clause
			conn.disconnect();
		}
	}

	protected static String getUrlString(Map<String, String> params, String fileFormName, File uploadFile,
			String newFileName, String urlStr, byte[] filebytes) throws Exception {
		return new String(getUrlBytes(params, fileFormName, uploadFile, newFileName, urlStr, filebytes));
	}

	abstract public Object fetchItems(Object... o);
}
// byte转InputStream http://javapub.iteye.com/blog/665696
// https://blog.csdn.net/lisdye2/article/details/52222645