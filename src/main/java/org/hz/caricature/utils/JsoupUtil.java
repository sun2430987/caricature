package org.hz.caricature.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class JsoupUtil {

	public static Document get(String url) throws IOException {
		Document doc = null;
		if (!StringUtil.isBlank(url)) {
			doc = Jsoup.connect(url).timeout(1000*10).get();
		} else {
			System.out.println("请求地址错误: url is null");
		}
		return doc;
	}

	public static Element getElementById(String url, String id) {
		Element ele = null;
		try {
			Document doc = get(url);
			ele = getElementById(doc, id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ele;
	}

	public static Element getElementById(Document doc, String id) {
		Element ele = null;
		if (doc != null) {
			if (!StringUtil.isBlank(id)) {
				ele = doc.getElementById(id);
			} else {
				System.out.println("获取文档结点错误: id为null");
			}

		} else {
			System.out.println("请求地址错误: 获取文档为null");
		}
		return ele;
	}

	public static Elements getElementByClass(String url, String className) {
		Elements eles = null;
		try {
			Document doc = get(url);
			eles = getElementByClass(doc, className);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return eles;
	}

	public static Elements getElementByClass(Document doc, String className) {
		Elements eles = null;
		if (doc != null) {
			if (!StringUtil.isBlank(className)) {
				eles = doc.getElementsByClass(className);
			} else {
				System.out.println("获取文档结点错误: className为null");
			}

		} else {
			System.out.println("请求地址错误: 获取文档为null");
		}
		return eles;
	}

	public static Elements getElementByTag(String url, String tagName) {
		Elements eles = null;
		try {
			Document doc = get(url);
			eles = getElementByTag(doc, tagName);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return eles;
	}

	public static Elements getElementByTag(Document doc, String tagName) {
		Elements eles = null;
		if (doc != null) {
			if (!StringUtil.isBlank(tagName)) {
				eles = doc.getElementsByTag(tagName);
			} else {
				System.out.println("获取文档结点错误: className为null");
			}

		} else {
			System.out.println("请求地址错误: 获取文档为null");
		}
		return eles;
	}

	public static Elements getElementByTag(String url, String key, String value) {
		Elements eles = null;
		try {
			Document doc = get(url);
			eles = getElementByTag(doc, key, value);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return eles;
	}

	public static Elements getElementByTag(Document doc, String key,
			String value) {
		Elements eles = null;
		if (doc != null) {
			if (!StringUtil.isBlank(key) && !StringUtil.isBlank(value)) {
				eles = doc.getElementsByAttributeValue(key, value);
			} else {
				System.out.println("获取文档结点错误: key或value为null");
			}

		} else {
			System.out.println("请求地址错误: 获取文档为null");
		}
		return eles;
	}

	public static String getElementAttr(Element ele, String key) {
		String value = null;
		if (ele != null) {
			value = ele.attr(key);
		}
		return value;
	}

	public static boolean down(String url, String path, String fileName, String type) {
		Response res = null;
		FileOutputStream out = null;
		boolean result = false;
		try {
			res = Jsoup.connect(url).ignoreContentType(true).execute();

			// output here
			out = new FileOutputStream(new File(path + Constant.pathSeparator + fileName + type));
			out.write(res.bodyAsBytes());
			result = true;
			System.out.println(" "+result);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return result;
	}
	
}
