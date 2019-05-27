package org.hz.caricature.view;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hz.caricature.utils.Constant;
import org.hz.caricature.utils.JsoupUtil;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AcgView {
	String path = "F:\\图片区\\我是谁";
	
	public void startTask(String url){
		try {
			chaptersTask(url);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void chaptersTask(String url) throws IOException, InterruptedException{
		Document doc = JsoupUtil.get(url);
		if(doc != null){
			String basePath = url.substring(0, url.lastIndexOf("/"));
			
			//获取章节名称
			String chaptersName = getChaptersName(doc, "ptitle");
			String tempPath = path + Constant.pathSeparator + chaptersName;
			File pathFile = new File(tempPath);
			if(!pathFile.exists()){
				pathFile.mkdirs();
			}
			
			//获取下一章节地址
			String nextUrl = getNextChaptersUrl(doc);
			
			//获取所有图片HTML页面
			List<String> pages = getPages(doc, "dedepagetitles");
			int size = pages.size();
			System.out.println(chaptersName + ", " + size + ", " + tempPath);
			//访问图片页面并下载图片
			//放弃第一张
			for(int i=1;i<size;i++){
				getPage(basePath + Constant.pathSeparator +pages.get(i), tempPath, getName(size, i));
				Thread.sleep(1000);
			}
			//
		}else{
			System.out.println("doc is null");
		}
		
	}
	private String getName(int size, int page){
		String result = "";
		String sizeStr = String.valueOf(size);
		String pageStr = String.valueOf(page);
		if(sizeStr.length() > pageStr.length()){
			int c = sizeStr.length() - pageStr.length();
			String temp = "0";
			for(int i=0;i<c;i++){
				result += temp;
			}
			result += pageStr;
		}else{
			result += page;
		}
		return result;
	}
	
	public String getChaptersName(Document doc, String className){
		String name = null;
		Elements eles = JsoupUtil.getElementByClass(doc, className);
		if(eles != null){
			try{
				name = eles.first().getElementsByTag("strong").first().text();
				if(name != null){
					if(name.indexOf(":") > 0){
						name = name.substring(name.lastIndexOf(":")+1);
					}else if(name.indexOf("：") > 0){
						name = name.substring(name.lastIndexOf("：")+1);
					}
					
				}
			}catch(NullPointerException e){
				e.printStackTrace();
			}
		}
		return name;
	}
	
	public List<String> getPages(Document doc, String id){
		List<String> list = new ArrayList<String>();
		Element ele = JsoupUtil.getElementById(doc, id);
		if(ele != null){
			Elements eles = ele.getElementsByTag("option");
			if(eles != null){
				for(int i=0; i<eles.size();i++){
					String v = eles.get(i).attr("value");
					if(!StringUtil.isBlank(v)){
						list.add(v);
					}
				}
			}
		}
		return list;
	}
	
	public boolean getPage(String url, String path, String fileName) throws IOException{
		System.out.print(url);
		Document doc = JsoupUtil.get(url);
		Element imgDivEle = JsoupUtil.getElementById(doc, "nr234img");
		String urlStr = JsoupUtil.getElementAttr(imgDivEle.getElementsByTag("img").first(), "src");
		System.out.print(",");
		return JsoupUtil.down(urlStr, path, fileName, Constant.type);
	}
	
	public String getNextChaptersUrl(Document doc){
		String result = null;
		Elements eles = doc.getElementsByTag("script").eq(11);
		for(Element e:eles){
			String data = e.data();
			Pattern p=Pattern.compile("(?<=下一篇：下一篇：<a href=')(\\S)+(?=')"); 
			Matcher m=p.matcher(data); 
			while(m.find()) { 
				result = m.group(); 
			}
		}
		return result;
	}
}
