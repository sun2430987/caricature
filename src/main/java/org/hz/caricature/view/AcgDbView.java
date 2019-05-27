package org.hz.caricature.view;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hz.caricature.utils.Constant;
import org.hz.caricature.utils.DBUtil;
import org.hz.caricature.utils.GuidUtil;
import org.hz.caricature.utils.JsoupUtil;
import org.jsoup.internal.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class AcgDbView {
	private String tableName = "t_caricatures";
	private String tableNameExt = "t_caricatures_ext";
	private String baseURL = "http://m.mwspyxgs.com";
	
	public void startTask(String url){
		try {
			chaptersTask(url);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public void startTask(){
		try {
			String sql = "SELECT id,NAME,url,num,e.s "
					+"FROM t_caricatures c,(SELECT a.p_id,COUNT(1) s FROM t_caricatures_ext a GROUP BY a.p_id) e "
					+"WHERE c.id=e.p_id "
					+"ORDER BY create_time "
					+"DESC LIMIT 1";
			List<Map<String, Object>> list = DBUtil.query(sql, null);
			if(list != null){
				Map<String, Object> map = list.get(0);
				String id = map.get("id").toString();
				String url = map.get("url").toString();
				int s = Integer.valueOf(map.get("s").toString());
				chaptersTask(url, id, s+1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void chaptersTask(String url) throws IOException, InterruptedException{
		Document doc = JsoupUtil.get(url);
		if(doc != null){
			String basePath = url.substring(0, url.lastIndexOf("/"));
			
			//获取章节名称
			String chaptersName = getChaptersName(doc, "ptitle");
			
			//获取下一章节地址
			String nextUrl = getNextChaptersUrl(doc);
			
			//获取所有图片HTML页面
			List<String> pages = getPages(doc, "dedepagetitles");
			int size = pages.size();
			String id = GuidUtil.getUUID();
			DBUtil.update("insert t_caricatures(id,name,url,num,create_time,state) values(?,?,?,?,now(),'0')", new Object[]{id, chaptersName, url, size});
			System.out.println("save "+chaptersName);
			//访问图片页面并下载图片
			//放弃第一张
			for(int i=1;i<size;i++){
				try{
					getPage(basePath + Constant.pathSeparator +pages.get(i), getName(size, i), id);
				}catch(SocketTimeoutException e){
					Thread.sleep(1000*10);
					getPage(basePath + Constant.pathSeparator +pages.get(i), getName(size, i), id);
				}
				Thread.sleep(1000*3);
			}
			System.out.println(nextUrl);
			//
			chaptersTask(baseURL+nextUrl);
		}else{
			System.out.println("doc is null");
		}
		
	}
	
	public void chaptersTask(String url, String id, int s) throws IOException, InterruptedException{
		Document doc = JsoupUtil.get(url);
		if(doc != null){
			String basePath = url.substring(0, url.lastIndexOf("/"));
			
			//获取章节名称
			String chaptersName = getChaptersName(doc, "ptitle");
			
			//获取下一章节地址
			String nextUrl = getNextChaptersUrl(doc);
			
			//获取所有图片HTML页面
			List<String> pages = getPages(doc, "dedepagetitles");
			int size = pages.size();
			System.out.println("save "+chaptersName);
			//访问图片页面并下载图片
			//放弃第一张
			for(int i=s;i<size;i++){
				try{
					getPage(basePath + Constant.pathSeparator +pages.get(i), getName(size, i), id);
				}catch(SocketTimeoutException e){
					Thread.sleep(1000*10);
					getPage(basePath + Constant.pathSeparator +pages.get(i), getName(size, i), id);
				}
				Thread.sleep(1000*3);
			}
			System.out.println(nextUrl);
			//
			chaptersTask(baseURL+nextUrl);
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
	
	public boolean getPage(String url, String fileName, String id) throws IOException{
		Document doc = JsoupUtil.get(url);
		Element imgDivEle = JsoupUtil.getElementById(doc, "nr234img");
		String urlStr = JsoupUtil.getElementAttr(imgDivEle.getElementsByTag("img").first(), "src");
		int i = DBUtil.update("insert t_caricatures_ext(id,p_id,img_name,img_url,create_time,state) values(?,?,?,?,now(),'0')", new Object[]{GuidUtil.getUUID(), id, fileName, urlStr});
		System.out.println("save img_url: "+url);
		return i>0?true:false;
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
