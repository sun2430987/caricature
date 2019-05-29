package org.hz.caricature.view;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.hz.caricature.utils.Constant;
import org.hz.caricature.utils.DBUtil;
import org.hz.caricature.utils.JsoupUtil;
import org.jsoup.internal.StringUtil;

public class AcgDownFormDbView {
	private Logger log = LogManager.getLogger(AcgDownFormDbView.class);
	
	String path = "F:\\图片区\\我是谁";
	
	public void startTask() {
		try{
			String sql = "SELECT id,NAME,url "
					+"FROM t_caricatures c "
					+"WHERE c.state=0 "
					+"ORDER BY create_time";
			log.info(sql);
			List<Map<String, Object>> list = DBUtil.query(sql, null);
			for(Map<String, Object> map:list){
				downChapter(map.get("id").toString(), map.get("name").toString());
				Thread.sleep(1000*10);
			}
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}
	
	public void downChapter(String p_id,String name) throws InterruptedException{
		if(!StringUtil.isBlank(name)){
			log.info(name);
			String temp = path+Constant.pathSeparator+name;
			File file = new File(temp);
			if(!file.exists()){
				file.mkdirs();
			}
			String sql = "SELECT id,img_name,img_url FROM t_caricatures_ext WHERE state='0' AND p_id=? ORDER BY create_time";
			List<Map<String, Object>> list = DBUtil.query(sql, new Object[]{p_id});
			for(Map<String, Object> map:list){
				downImg(getString(map,"img_url"), temp, getString(map,"img_name"), getString(map,"id"));
				Thread.sleep(1000*3);
			}
			
			sql = "update t_caricatures set state='2' WHERE state='0' and id=?";
			DBUtil.update(sql, new Object[]{p_id});
		}else{
			log.info("name is null");
		}
		
	}
	
	
	public boolean downImg(String url, String path, String fileName, String id){
		log.info("save "+fileName);
		boolean b = false;
		try{
			b = JsoupUtil.down(url, path, fileName, Constant.type);
		}catch(Exception e){
			e.printStackTrace();
		}
		String sql = "update t_caricatures_ext set state=? where id=?";
		int i = DBUtil.update(sql, new Object[]{b?"1":"-1", id});
		return i>0?true:false;
	}
	
	private String getString(Map<String, Object> map, String key){
		String v = null;
		if(map != null){
			v = map.get(key)==null?null:map.get(key).toString();
		}
		return v;
	}
	
}
