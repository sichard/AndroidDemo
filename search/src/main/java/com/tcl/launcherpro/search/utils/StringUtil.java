package com.tcl.launcherpro.search.utils;

import com.github.promeg.pinyinhelper.Pinyin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *<br>类描述：字符串工具类方法
 *<br>详细描述：提供桌面字符以及字符串处理工具方法
 *<br><b>Author sichard</b>
 *<br><b>Date   2016-7-27</b>
 */
public class StringUtil {
	/** 中日韩unicode字符开始编码 */
	public static final char  CJK_START_CODE =  0x4e00;
	/** 中日韩unicode字符结束编码 */
	public static final char  CJK_END_CODE =  0x9fa5;
	/** 空白字符串 */
	public static final String EMPTY = "";
	

	/** <br>功能简述:获取中文字符串的拼音表示，其中的英文字符原文输出
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param text 包含中英文的字符串
	 * @return
	 */
	public static final String convertStringToChineseSell(String text) {
		if (text == null) {
			return text;
		}
		char[] chars = text.toCharArray();

		StringBuilder sb = new StringBuilder();
		for (char c : chars) {
			sb.append(convertCharToChineseSpell(c));
		}
		return sb.toString();
	}
	
	/** <br>功能简述:把一个中文字符转换成一个拼音字符串
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param key
	 * @return
	 */
	public static String convertCharToChineseSpell(char key) {
		return Pinyin.toPinyin(key);
	}
	
	/**
	 * 检测给定字符串中是否包含中文字符
	 * @param text
	 * @return
	 */
	public static boolean isContainsChinese(String text) {
		if (null == text) {
			return false;
		}

		char[] chars = text.toCharArray();
		for (char c : chars) {
			if (isChinese(c)) {
				return true;
			}
		}
		return false;
	}
	
	/** <br>功能简述:判断字符是否中文字符
	 * <br>功能详细描述:实际上若key是韩文或者日文也返回true
	 * <br>注意:
	 * @param key
	 * @return
	 */
	public static boolean isChinese(char key) {
		return (key >= CJK_START_CODE && key <= CJK_END_CODE) ? true : false;
	}
	
	/***
     * <br>功能简述: 数组合并成字符串
     * <br>功能详细描述:将指定下标的数组合并成字符串，数组的元素在合并时执行object.toString()方法
     * <br>注意: 下标为左闭右开, 即:[startIndex, endIndex)
     * @param array 数组
     * @param separator 分隔符
     * @param startIndex 开始下标
     * @param endIndex 结束下标
     * @return
     */
	public static String join(final Object[] array, final String separator, final int startIndex, final int endIndex) {
		if (array == null) {
			return null;
		}
		final int noOfItems = endIndex - startIndex;
		if (noOfItems <= 0) {
			return EMPTY;
		}
		final StringBuilder buf = new StringBuilder(noOfItems * 16);
		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}
		return buf.toString();
	}

	/**
	 * 提取字符中的数字
	 * @param string
	 * @return
	 */
	public static String extractNum(String string) {
		if(string == null || ("").equals(string)) {
			return "";
		}
		String regEx = "[^0-9]";
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(string);
		return matcher.replaceAll("").trim();
	}
}
