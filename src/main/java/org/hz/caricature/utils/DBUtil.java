package org.hz.caricature.utils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DBUtil {

	// SQLServerL
	private final static String DBDRIVER = "com.mysql.jdbc.Driver";
	private final static String DBURL = "jdbc:mysql://127.0.0.1:3309/caricature";
	private final static String USERNAME = "caricature";
	private final static String USERPWD = "caricature";

	static {
		try {
			Class.forName(DBDRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private DBUtil() {

	}

	/**
	 * 获取数据库的连接
	 * 
	 * @return conn
	 */
	public static Connection getConnection() {
		try {
			return DriverManager.getConnection(DBURL, USERNAME, USERPWD);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 释放资源
	 * 
	 * @param conn
	 * @param pstmt
	 * @param rs
	 */
	public static void closeResources(Connection conn, PreparedStatement ps,
			ResultSet rs) {
		if (null != rs) {
			try {
				rs.close();
				rs = null;
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			} finally {
				if (null != ps) {
					try {
						ps.close();
						ps = null;
					} catch (SQLException e) {
						e.printStackTrace();
						throw new RuntimeException(e);
					} finally {
						if (null != conn) {
							try {
								conn.close();
								conn = null;
							} catch (SQLException e) {
								e.printStackTrace();
								throw new RuntimeException(e);
							}
						}
					}
				}
			}
		}
	}

	public static int update(String sql, Object[] params) {
		Connection conn = getConnection();
		int i = 0;
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			if (params != null) {
				for (int j = 0; j < params.length; j++) {
					if (params[j] instanceof String) {
						pstmt.setString(j + 1, params[j].toString());
					} else if (params[j] instanceof Integer) {
						pstmt.setInt(j + 1,
								Integer.valueOf(params[j].toString()));
					} else if (params[j] instanceof Date) {
						pstmt.setDate(j + 1, (Date) params[j]);
					} else {
						pstmt.setString(j + 1, params[j].toString());
					}

				}
			}

			i = pstmt.executeUpdate();
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return i;
	}
	
	public static List<Map<String, Object>> query(String sql, Object[] params) {
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		Connection conn = getConnection();
		PreparedStatement pstmt;
		try {
			pstmt = (PreparedStatement) conn.prepareStatement(sql);
			if (params != null) {
				for (int j = 0; j < params.length; j++) {
					if (params[j] instanceof String) {
						pstmt.setString(j + 1, params[j].toString());
					} else if (params[j] instanceof Integer) {
						pstmt.setInt(j + 1,
								Integer.valueOf(params[j].toString()));
					} else if (params[j] instanceof Date) {
						pstmt.setDate(j + 1, (Date) params[j]);
					} else {
						pstmt.setString(j + 1, params[j].toString());
					}

				}
			}

			ResultSet rs = pstmt.executeQuery();
			int col = rs.getMetaData().getColumnCount();
			ResultSetMetaData md = rs.getMetaData();// 获得结果集结构信息（元数据）
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
	            for (int i = 1; i <= col; i++) {
	            	map.put(md.getColumnName(i).toLowerCase(), rs.getObject(i));
	             }
	            result.add(map);
	        }
			
			pstmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

}
