package com.sibu.chat.common.utils;

import java.io.File;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;

public class Config {
	private static PropertiesConfiguration config = null;

	static {
		init();
	}

	public static void init() {

		try {
			String home = System.getProperty("home");
			if (home != null) {
				config = new PropertiesConfiguration(home + File.separator + "conf/configure.properties");
				config.setProperty("home", home);
				FileChangedReloadingStrategy fs = new FileChangedReloadingStrategy();
				fs.setRefreshDelay(10);
				fs.setConfiguration(config);
				config.setReloadingStrategy(fs);
			} else {
				config = new PropertiesConfiguration("conf/configure.properties");
				FileChangedReloadingStrategy fs = new FileChangedReloadingStrategy();
				fs.setRefreshDelay(10);
				fs.setConfiguration(config);
				config.setReloadingStrategy(fs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Configuration subset(String prefix) {
		return config.subset(prefix);
	}

	public static boolean isEmpty() {
		return config.isEmpty();
	}

	public static boolean containsKey(String key) {
		return config.containsKey(key);
	}

	public static void addProperty(String key, Object value) {
		config.addProperty(key, value);
	}

	public static void setProperty(String key, Object value) {
		config.setProperty(key, value);
	}

	public static void clearProperty(String key) {
		config.clearProperty(key);
	}

	public static Object getProperty(String key) {
		return config.getProperties(key);
	}

	public static Iterator<String> getKeys(String key) {
		return config.getKeys(key);
	}

	public static Iterator<String> getKeys() {
		return config.getKeys();
	}

	public static Properties getProperties(String key) {
		return config.getProperties(key);
	}

	public static boolean getBoolean(String key) {
		return config.getBoolean(key);
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		return config.getBoolean(key, defaultValue);
	}

	public static Boolean getBoolean(String key, Boolean defaultValue) {
		return config.getBoolean(key, defaultValue);
	}

	public static byte getByte(String key) {
		return config.getByte(key);
	}

	public static byte getByte(String key, byte defaultValue) {
		return config.getByte(key, defaultValue);
	}

	public static Byte getByte(String key, Byte defaultValue) {
		return config.getByte(key, defaultValue);
	}

	public static double getDouble(String key) {
		return config.getDouble(key);
	}

	public static double getDouble(String key, double defaultValue) {
		return config.getDouble(key, defaultValue);
	}

	public static Double getDouble(String key, Double defaultValue) {
		return config.getDouble(key, defaultValue);
	}

	public static float getFloat(String key) {
		return config.getFloat(key);
	}

	public static float getFloat(String key, float defaultValue) {
		return config.getFloat(key, defaultValue);
	}

	public static Float getFloat(String key, Float defaultValue) {
		return config.getFloat(key, defaultValue);
	}

	public static int getInt(String key) {
		return config.getInt(key);
	}

	public static int getInt(String key, int defaultValue) {
		return config.getInt(key, defaultValue);
	}

	public static Integer getInteger(String key, Integer defaultValue) {
		return config.getInteger(key, defaultValue);
	}

	public static long getLong(String key) {
		return config.getLong(key);
	}

	public static long getLong(String key, long defaultValue) {
		return config.getLong(key, defaultValue);
	}

	public static Long getLong(String key, Long defaultValue) {
		return config.getLong(key, defaultValue);
	}

	public static short getShort(String key) {
		return config.getShort(key);
	}

	public static short getShort(String key, short defaultValue) {
		return config.getShort(key, defaultValue);
	}

	public static Short getShort(String key, Short defaultValue) {
		return config.getShort(key, defaultValue);
	}

	public static String getString(String key) {
		return config.getString(key);
	}

	public static String getString(String key, String defaultValue) {
		return config.getString(key, defaultValue);
	}

	public static String[] getStringArray(String key) {
		return config.getStringArray(key);
	}

}
