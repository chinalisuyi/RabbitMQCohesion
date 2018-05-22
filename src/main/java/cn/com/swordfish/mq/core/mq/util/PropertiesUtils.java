package cn.com.swordfish.mq.core.mq.util;

import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.support.PropertiesLoaderUtils;

/**
 * properties文件处理类，默认加载classpath中的config.properties文件
 * 
 * @author wallechen
 *
 */
public class PropertiesUtils {
	private final static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);
	private static Properties propertis;

	static {
		if (propertis == null) {
			propertis = new Properties();
		}
		try {
			propertis = PropertiesLoaderUtils.loadAllProperties("config.properties");
		} catch (IOException e) {
			logger.error("load config.properties error!");
		}
	}

	/**
	 * 获取默认的properties中的值
	 * 
	 * @param key
	 * @return
	 */
	public static String getValue(String key) {
		return getValue(key, propertis);
	}

	/**
	 * 获取某个properties文件中的值
	 * 
	 * @param key
	 * @param pro
	 * @return
	 */
	public static String getValue(String key, Properties pro) {
		if (StringUtils.isEmpty(key)) {
			return "";
		}
		if (pro == null) {
			return "";
		}
		String value = pro.getProperty(key);
		if (StringUtils.isEmpty(value)) {
			return "";
		}
		return value;
	}

	/**
	 * 获取某一个给定的类路径上的文件里面的属性值
	 * 
	 * @param key
	 * @param resourceName
	 * @return
	 */
	public static String getValue(String key, String resourceName) {
		Properties pro = null;
		try {
			pro = PropertiesLoaderUtils.loadAllProperties(resourceName);
		} catch (IOException e) {
			logger.error("load {} error!", resourceName);
		}
		return getValue(key, pro);
	}

}
