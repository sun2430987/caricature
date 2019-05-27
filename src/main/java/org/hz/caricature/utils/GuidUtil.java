package org.hz.caricature.utils;

import java.util.UUID;

public class GuidUtil {
	
	/**
	 * 获取UUID
	 * @return
	 */
	public static String getUUID(){
		String uuid = UUID.randomUUID().toString(); 
		//去掉“-”符号 
		return uuid.replaceAll("-", "");
	}
	
}
