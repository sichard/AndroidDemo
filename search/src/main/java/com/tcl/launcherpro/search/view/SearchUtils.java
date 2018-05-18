package com.tcl.launcherpro.search.view;

import android.content.Context;

import com.tcl.launcherpro.search.common.MatchResult;
import com.tcl.launcherpro.search.utils.StringUtil;

import java.util.ArrayList;

/**
 *<br>类描述：搜索工具类
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date   2016-7-27</b>
 */
public class SearchUtils {

	private static SearchUtils sInstance = null;
	private Context mContex = null;

	/** 所有汉字对应的拼音 */
	private static String[] pystr = new String[] { "a", "ai", "an", "ang",
			"ao", "ba", "bai", "ban", "bang", "bao", "bei", "ben", "beng",
			"bi", "bian", "biao", "bie", "bin", "bing", "bo", "bu", "ca",
			"cai", "can", "cang", "cao", "ce", "ceng", "cha", "chai", "chan",
			"chang", "chao", "che", "chen", "cheng", "chi", "chong", "chou",
			"chu", "chuai", "chuan", "chuang", "chui", "chun", "chuo", "ci",
			"cong", "cou", "cu", "cuan", "cui", "cun", "cuo", "da", "dai",
			"dan", "dang", "dao", "de", "deng", "di", "dian", "diao", "die",
			"ding", "diu", "dong", "dou", "du", "duan", "dui", "dun", "duo",
			"e", "en", "er", "fa", "fan", "fang", "fei", "fen", "feng", "fo",
			"fou", "fu", "ga", "gai", "gan", "gang", "gao", "ge", "gei", "gen",
			"geng", "gong", "gou", "gu", "gua", "guai", "guan", "guang", "gui",
			"gun", "guo", "ha", "hai", "han", "hang", "hao", "he", "hei",
			"hen", "heng", "hong", "hou", "hu", "hua", "huai", "huan", "huang",
			"hui", "hun", "huo", "ji", "jia", "jian", "jiang", "jiao", "jie",
			"jin", "jing", "jiong", "jiu", "ju", "juan", "jue", "jun", "ka",
			"kai", "kan", "kang", "kao", "ke", "ken", "keng", "kong", "kou",
			"ku", "kua", "kuai", "kuan", "kuang", "kui", "kun", "kuo", "la",
			"lai", "lan", "lang", "lao", "le", "lei", "leng", "li", "lia",
			"lian", "liang", "liao", "lie", "lin", "ling", "liu", "long",
			"lou", "lu", "lv", "luan", "lue", "lun", "luo", "ma", "mai", "man",
			"mang", "mao", "me", "mei", "men", "meng", "mi", "mian", "miao",
			"mie", "min", "ming", "miu", "mo", "mou", "mu", "na", "nai", "nan",
			"nang", "nao", "ne", "nei", "nen", "neng", "ni", "nian", "niang",
			"niao", "nie", "nin", "ning", "niu", "nong", "nu", "nv", "nuan",
			"nue", "nuo", "o", "ou", "pa", "pai", "pan", "pang", "pao", "pei",
			"pen", "peng", "pi", "pian", "piao", "pie", "pin", "ping", "po",
			"pu", "qi", "qia", "qian", "qiang", "qiao", "qie", "qin", "qing",
			"qiong", "qiu", "qu", "quan", "que", "qun", "ran", "rang", "rao",
			"re", "ren", "reng", "ri", "rong", "rou", "ru", "ruan", "rui",
			"run", "ruo", "sa", "sai", "san", "sang", "sao", "se", "sen",
			"seng", "sha", "shai", "shan", "shang", "shao", "she", "shen",
			"sheng", "shi", "shou", "shu", "shua", "shuai", "shuan", "shuang",
			"shui", "shun", "shuo", "si", "song", "sou", "su", "suan", "sui",
			"sun", "suo", "ta", "tai", "tan", "tang", "tao", "te", "teng",
			"ti", "tian", "tiao", "tie", "ting", "tong", "tou", "tu", "tuan",
			"tui", "tun", "tuo", "wa", "wai", "wan", "wang", "wei", "wen",
			"weng", "wo", "wu", "xi", "xia", "xian", "xiang", "xiao", "xie",
			"xin", "xing", "xiong", "xiu", "xu", "xuan", "xue", "xun", "ya",
			"yan", "yang", "yao", "ye", "yi", "yin", "ying", "yo", "yong",
			"you", "yu", "yuan", "yue", "yun", "za", "zai", "zan", "zang",
			"zao", "ze", "zei", "zen", "zeng", "zha", "zhai", "zhan", "zhang",
			"zhao", "zhe", "zhen", "zheng", "zhi", "zhong", "zhou", "zhu",
			"zhua", "zhuai", "zhuan", "zhuang", "zhui", "zhun", "zhuo", "zi",
			"zong", "zou", "zu", "zuan", "zui", "zun", "zuo" };

	/** 所有汉字对应的拼音以及声母 */
	private static String[] PY_STR_ALL = new String[] { "a", "ai", "an", "ang",
			"ao", "b", "ba", "bai", "ban", "bang", "bao", "bei", "ben", "beng",
			"bi", "bian", "biao", "bie", "bin", "bing", "bo", "bu", "c", "ca",
			"cai", "can", "cang", "cao", "ce", "ceng", "ch", "cha", "chai", "chan",
			"chang", "chao", "che", "chen", "cheng", "chi", "chong", "chou",
			"chu", "chuai", "chuan", "chuang", "chui", "chun", "chuo", "ci",
			"cong", "cou", "cu", "cuan", "cui", "cun", "cuo", "d", "da", "dai",
			"dan", "dang", "dao", "de", "deng", "di", "dian", "diao", "die",
			"ding", "diu", "dong", "dou", "du", "duan", "dui", "dun", "duo",
			"e", "en", "er", "f", "fa", "fan", "fang", "fei", "fen", "feng", "fo",
			"fou", "fu", "g", "ga", "gai", "gan", "gang", "gao", "ge", "gei", "gen",
			"geng", "gong", "gou", "gu", "gua", "guai", "guan", "guang", "gui",
			"gun", "guo", "h", "ha", "hai", "han", "hang", "hao", "he", "hei",
			"hen", "heng", "hong", "hou", "hu", "hua", "huai", "huan", "huang",
			"hui", "hun", "huo", "j", "ji", "jia", "jian", "jiang", "jiao", "jie",
			"jin", "jing", "jiong", "jiu", "ju", "juan", "jue", "jun", "k", "ka",
			"kai", "kan", "kang", "kao", "ke", "ken", "keng", "kong", "kou",
			"ku", "kua", "kuai", "kuan", "kuang", "kui", "kun", "kuo", "l", "la",
			"lai", "lan", "lang", "lao", "le", "lei", "leng", "li", "lia",
			"lian", "liang", "liao", "lie", "lin", "ling", "liu", "long",
			"lou", "lu", "lv", "luan", "lue", "lun", "luo", "m", "ma", "mai", "man",
			"mang", "mao", "me", "mei", "men", "meng", "mi", "mian", "miao",
			"mie", "min", "ming", "miu", "mo", "mou", "mu", "n", "na", "nai", "nan",
			"nang", "nao", "ne", "nei", "nen", "neng", "ni", "nian", "niang",
			"niao", "nie", "nin", "ning", "niu", "nong", "nu", "nv", "nuan",
			"nue", "nuo", "o", "ou", "p", "pa", "pai", "pan", "pang", "pao", "pei",
			"pen", "peng", "pi", "pian", "piao", "pie", "pin", "ping", "po",
			"pu", "q", "qi", "qia", "qian", "qiang", "qiao", "qie", "qin", "qing",
			"qiong", "qiu", "qu", "quan", "que", "qun", "r", "ran", "rang", "rao",
			"re", "ren", "reng", "ri", "rong", "rou", "ru", "ruan", "rui",
			"run", "ruo", "s", "sa", "sai", "san", "sang", "sao", "se", "sen",
			"seng", "sh", "sha", "shai", "shan", "shang", "shao", "she", "shen",
			"sheng", "shi", "shou", "shu", "shua", "shuai", "shuan", "shuang",
			"shui", "shun", "shuo", "si", "song", "sou", "su", "suan", "sui",
			"sun", "suo", "t", "ta", "tai", "tan", "tang", "tao", "te", "teng",
			"ti", "tian", "tiao", "tie", "ting", "tong", "tou", "tu", "tuan",
			"tui", "tun", "tuo", "w", "wa", "wai", "wan", "wang", "wei", "wen",
			"weng", "wo", "wu", "x", "xi", "xia", "xian", "xiang", "xiao", "xie",
			"xin", "xing", "xiong", "xiu", "xu", "xuan", "xue", "xun", "y", "ya",
			"yan", "yang", "yao", "ye", "yi", "yin", "ying", "yo", "yong",
			"you", "yu", "yuan", "yue", "yun", "z", "za", "zai", "zan", "zang",
			"zao", "ze", "zei", "zen", "zeng", "zh", "zha", "zhai", "zhan", "zhang",
			"zhao", "zhe", "zhen", "zheng", "zhi", "zhong", "zhou", "zhu",
			"zhua", "zhuai", "zhuan", "zhuang", "zhui", "zhun", "zhuo", "zi",
			"zong", "zou", "zu", "zuan", "zui", "zun", "zuo" };
	public static SearchUtils getInstance(Context context) {
		if (sInstance == null) {
			sInstance = new SearchUtils(context);
		}
		return sInstance;
	}

	private SearchUtils(Context context) {
		mContex = context;
	}

	/**
	 * 根据查询条件匹配被查询对象是否包含查询条件关键字
	 * 
	 * @param key 搜索关键字
	 * @param target 被搜索的对象
	 * @return
	 */
	public MatchResult match(String key, String target) {

		if (key == null || "".equals(key) || target == null || "".equals(target)) {
			return null;
		}
		MatchResult item = null;
		// 先进行原文匹配，若能匹配到则不进行拼音匹配了
		item = matchSrc(key, target);
		if (item != null && item.mMatchWords > 0) {
			return item;
		}

		// 混合写匹配
		item = matchMixCaseSrc(key, target);
		if (item != null && item.mMatchWords > 0) {
			return item;
		}

		// 英文转中文或中文直接匹配
		ArrayList<String> spells = changeStringToSpellList(target);
		if (spells != null) {
			item = new MatchResult();
			getMatchValue(key, item, spells);
		}
		return item;
	}

	/**
	 * 判定给定target中是否包换关键字(包括英文转中文以及中文直接搜索)
	 * @param key 搜索关键字
	 * @param item
	 * @param target 被搜索的对象
	 */
	private void getMatchValue(String key, MatchResult item, ArrayList<String> target) {
		// 搜索字符是否包含汉字
		if (!StringUtil.isContainsChinese(key)) {
			String keyUpperCase = key.toUpperCase();

			/*****************模糊匹配start*******************/
			// TODO: 2016-11-23 输入“shez” 查看结果
//			{
//				//将key分割成汉字拼音或者声母的list
//				/*思路：让key遍历所有汉字拼音及声母，每遍历一次，取出一个汉字或者声母*/
//				String keyTemp = new String(keyUpperCase);
//				List<String> keyStrings = new ArrayList<>();
//				do {
//					int length = 0;
//					for (int i = 0; i < PY_STR_ALL.length; i++) {
//						final String s = PY_STR_ALL[i].toUpperCase();
//						final int index = keyTemp.indexOf(s);
//						if (index == 0 && length < s.length()) {
//							length = s.length();
//						}
//					}
//					if (length == 0) {
//						return;
//					}
//					keyStrings.add(keyTemp.substring(0, length));
//					if (length == keyTemp.length()) {
//						break;
//					}
//					keyTemp = keyTemp.substring(length, keyTemp.length());
//				} while (!TextUtils.isEmpty(key));
//
//				// 模糊匹配
//				/*将key分成的拼音列表依次遍历target的列表去匹配*/
//				int index = 0, j = 0;
//				boolean isFirstContain = false; //用于记录第一个匹配字符的index
//				for (int i = 0; i < keyStrings.size(); i++) {
//					final String s = keyStrings.get(i);
//
//					boolean isContain = false;
//					for (;j < target.size(); j++) {
//						final String s1 = target.get(j);
//						if (s1.indexOf(s) == 0) {
//							isContain = true;
//							if (!isFirstContain) {
//								isFirstContain = true;
//								index = j;
//							}
//							break;
//						}
//					}
//					if (isContain) {
//						if (i < keyStrings.size() - 1) {
//							continue;
//						}
//						if (i == keyStrings.size() - 1) {
//							item.mMatchPos = index;
//							item.mMatchWords = keyStrings.size();
//							Log.e("csc", "getMatchValue: " + keyStrings + "|" + target);
//							return;
//						}
//					} else {
//						break;
//					}
//				}
//			}
			/*****************模糊匹配end*******************/



			// 如果搜索字符以a开头，
			if (keyUpperCase.startsWith("A")) {
				for (int i = 0; i < target.size(); i++) {
					final String string = target.get(i);
					if (string.startsWith("A") && string.contains(keyUpperCase)) {
						item.mStart = i;
						item.mMatchWords = 1;
					}
				}
			}

			// 首字母匹配
			StringBuilder firstLetter = new StringBuilder();
			// 获取target的首字母
			for (int i = 0; i < target.size(); i++) {
				final String s = target.get(i);
				final String substring = s.substring(0, 1);
				firstLetter.append(substring);
			}
			// 判定target是否包括要搜索的字符
			if (firstLetter.toString().contains(keyUpperCase)) {
				item.mStart = firstLetter.toString().indexOf(keyUpperCase);
				item.mMatchWords = key.length();
			}
		}

	}

	/**
	 * 不做任何转换，直接原文匹配
	 * 
	 * @param key
	 * @param target
	 * @return
	 */
	private MatchResult matchSrc(String key, String target) {
		if (key == null || "".equals(key) || target == null || "".equals(target)) {
			return null;
		}

		MatchResult item = null;
		int index = target.indexOf(key);
		if (index > -1) {
			item = new MatchResult();
			item.mStart = index;
			item.mMatchWords = key.length();
		}
		return item;
	}

	/**
	 * 进行数据大小写匹配
	 * 
	 * @author huyong
	 * @param key
	 * @param target
	 * @return
	 */
	private MatchResult matchMixCaseSrc(String key, String target) {
		if (key == null || "".equals(key) || target == null || "".equals(target)) {
			return null;
		}
		// 小写匹配
		key = key.toLowerCase();
		target = target.toLowerCase();
		MatchResult item = matchSrc(key, target);
		if (item != null) {
			return item;
		}
		// 大写匹配
		key = key.toUpperCase();
		target = target.toUpperCase();
		item = matchSrc(key, target);
		return item;

	}

	/**
	 * 将一个字符串转换成一个拼音组合列表
	 * 
	 * @param str
	 * @return 转换后的列表，英文字母不做变换
	 */
	private ArrayList<String> changeStringToSpellList(String str) {

		if (str == null || "".equals(str)) {
			return null;
		}

		ArrayList<String> result = new ArrayList<String>();
		String spells = null;
		char[] chars = str.toCharArray();

		for (int i = 0; i < chars.length; i++) {
			spells = StringUtil.convertCharToChineseSpell(chars[i]);

			if (spells != null) {
				result.add(spells);
			}
		}

		return result;
	}

	/**
	 * 是否汉字
	 * 
	 * @param key
	 * @return
	 */
	private boolean isHanzi(char key) {
		boolean isHanzi = false;
		if (key >= 0x4e00 && key <= 0x9fa5) {
			isHanzi = true;
		}
		return isHanzi;
	}
}
