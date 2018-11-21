package org.elasticsearch.hanlp.helper;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.other.CharTable;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.utility.CharacterHelper;
import com.hankcs.hanlp.utility.TextUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * 大段文本或者单个句子预处理以及分词常用的方法集合
 * elasticsearch-plugin-hanlp
 * Created by 何志龙 on 2017-08-07.
 */
public class SentenceHelper {

    //region NGram切分

    /**
     * 将文本按词进行N切分
     *
     * @param text            待切分文本
     * @param maxLength       最大短语长度
     * @param removeStopWords 是否移除停用词
     * @return 返回 List(短语)
     */
    public static List<String> nWordGram(String text, int maxLength, boolean removeStopWords) {
        return nWordGram(text, removeStopWords, 7, 1, maxLength, 2);
    }

    /**
     * 将文本按词进行N切分
     * 例如：智库大平台：智库；智库大；智库大平台；大平台；平台
     *
     * @param text            待切分文本
     * @param removeStopWords 是否移除停用词
     * @param maxWordLength   最大词语个数
     * @param minWordLength   最小词语个数
     * @param maxLength       最大短语长度
     * @param minLength       最小短语长度
     * @return 返回 List(短语)
     */
    public static List<String> nWordGram(String text, boolean removeStopWords, int maxWordLength, int minWordLength, int maxLength, int minLength) {
        Map<String, Integer> map = sentenceToNGramMap(text, removeStopWords, maxWordLength, minWordLength, maxLength, minLength);
        List<String> result = new ArrayList<String>();
        if (map != null) {
            result.addAll(map.keySet());
        }
        return result;
    }

    /**
     * 将句子以词为单位转成短语集合
     *
     * @param text            待切分文本
     * @param maxLength       最大短语长度
     * @param removeStopWords 是否移除停用词
     * @return 返回 List(短语,词频)
     */
    public static Map<String, Integer> sentenceToNGramMap(String text, int maxLength, boolean removeStopWords) {
        return sentenceToNGramMap(text, removeStopWords, 7, 1, maxLength, 2);
    }

    /**
     * 将句子以词为单位转成短语集合
     * 例如：智库大平台：智库；智库大；智库大平台；大平台；平台
     *
     * @param text            待切分文本
     * @param removeStopWords 是否移除停用词
     * @param maxWordLength   最大词语个数
     * @param minWordLength   最小词语个数
     * @param maxLength       最大短语长度
     * @param minLength       最小短语长度
     * @return 返回 List(短语,词频)
     */
    public static Map<String, Integer> sentenceToNGramMap(String text, boolean removeStopWords, int maxWordLength, int minWordLength, int maxLength, int minLength) {
        if (HanLP.Config.Normalization) {
            text = text.toLowerCase();
        }

        //region 对文本进行预处理
        /*
         * 示例:n元语法((n-gram grammar)建立在马尔可夫模型上的一种概率语法
         * 1.对英文组合词、缩写词进行匹配，并预存储---->n元语法((hzl00110 grammar)建立在马尔可夫模型上的一种概率语法；
         * 2.按照非中英文数字进行切分----->n元语法/hzl00110/grammar/建立在马尔可夫模型上的一种概率语法
         * 3.还原为句子集合--->n元语法；hzl00110 grammar 建立在马尔可夫模型上的一种概率语法
         * 4.还原组合词---->n元语法；n-gram grammar；建立在马尔可夫模型上的一种概率语法
         */
        Matcher tempMatcher = RegexHelper.EnMixTextPattern.matcher(text);
        Map<String, String> tempMap = new HashMap<String, String>();
        int index = 0;
        while (tempMatcher.find()) {
            index++;
            String k = "hzl001b" + index;
            text = text.replace(tempMatcher.group(), k);
            tempMap.put(k, tempMatcher.group());
        }
        //按照非中英文字符进行分割
        String[] arr = text.split("[^0-9A-Za-z\\u4e00-\\u9fa5]+");
        List<String> optArr = new ArrayList<String>();
        //
        //将英文句子的特殊符号移除，转为标准英文句子
        for (int i = 0; i < arr.length; i++) {
            if (RegexHelper.ChineseTextPattern.matcher(arr[i]).find()) {
                optArr.add(arr[i]);
                continue;
            }
            StringBuilder tempStr = new StringBuilder();
            while (arr[i].matches("\\w+")) {
                tempStr.append(arr[i]).append(" ");
                i++;
                if (i == arr.length) {
                    i--;
                    break;
                }
            }
            if (!tempStr.toString().equals("")) {
                optArr.add(tempStr.toString().trim());
                if (!arr[i].matches("\\w+")) {
                    i--;
                }
            }
        }
        //将缩写词等进行还原
        for (int i = 0; i < optArr.size(); i++) {
            for (String k : tempMap.keySet()) {
                if (optArr.get(i).contains(k)) {
                    optArr.set(i, optArr.get(i).replace(k, tempMap.get(k)));
                }
            }
        }
        //endregion

        Map<String, Integer> resultMap = new HashMap<String, Integer>();
        for (String s : optArr) {
            List<Term> terms = LTP.segment.seg(s);
            if (removeStopWords) {
                for (int i = 0; i < terms.size(); i++) {
                    if (PredefineDic.allStopWordSet.contains(terms.get(i).word)) {
                        terms.set(i, new Term(";", Nature.w));
                    }
                }
            }
            boolean isChi = RegexHelper.ChineseTextPattern.matcher(s).find();
            //如果非中文
            if (!isChi) {
                terms = new ArrayList<Term>();
                String[] strArr = s.split(" ");
                for (String ss : strArr) {
                    terms.add(new Term(ss, Nature.nx));
                }
            }
            for (int i = 0, len = terms.size(); i < len; i++) {
                String word = terms.get(i).word;
                if (word.equals(";")) {
                    continue;
                }
                if (word.length() >= minLength && minWordLength == 1) {
                    if (!PredefineDic.allStopWordSet.contains(word)) {
                        if (resultMap.containsKey(word)) {
                            resultMap.put(word, resultMap.get(word) + 1);
                        } else {
                            resultMap.put(word, 1);
                        }
                    }
                }
                for (int j = i + 1; j <= i + maxWordLength; j++) {
                    if (minWordLength > (j - i + 1)) {
                        continue;
                    }
                    if (j >= len || terms.get(j).word.equals(";")) {
                        break;
                    }
                    word += ((isChi ? "" : " ") + terms.get(j).word);
                    if (isChi && word.length() < minLength) {
                        continue;
                    }
                    if (isChi && word.length() > maxLength) {
                        break;
                    }
                    if (resultMap.containsKey(word)) {
                        resultMap.put(word, resultMap.get(word) + 1);
                    } else {
                        resultMap.put(word, 1);
                    }
                }
            }
        }
        return resultMap;
    }

    /**
     * 以字为单位进行N切分
     *
     * @param text 待切分文本
     * @return 返回 List(短语)
     */
    public static List<String> nGram(String text) {
        return nGram(text, 7, 2);
    }

    /**
     * 以字为单位进行N切分
     *
     * @param text      待切分文本
     * @param maxLength 最大短语长度
     * @param minLength 最小短语长度
     * @return 返回 List(短语)
     */
    public static List<String> nGram(String text, int maxLength, int minLength) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < text.length(); i++) {
            for (int j = i + minLength; j <= text.length(); j++) {
                if (j - i > maxLength) {
                    break;
                }
                result.add(text.substring(i, j));
            }
        }
        return result;
    }

    //endregion

    private static final String[] filterRegex = new String[]
            {
                    "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*",
                    "[A-Za-z]{3,5}?://(([a-zA-Z0-9_-])+(\\.)?)*(:\\d+)?(/((\\.)?(\\?)?=?&?[a-zA-Z0-9_-](\\?)?)*)*",
                    "(\\w+\\s+)+\\w+",
                    "\\d+(年份|月份|年|月|日|时|分|秒|年代|世纪|小时|分钟|刻|天|辆)"
            };

    public static String prevDeal(String text) {
        for (String s : filterRegex) {
            text = text.replaceAll(s, ";");
        }
        return text;
    }

    public static List<Term> segment(String text) {
        return HanLP.segment(text);
    }

    public static List<List<Term>> sentencesToTermList(String text) {
        text = text.replace("\r", "。").replace("\n", "。")
                .replace("\0", "。").replace("\u202A", "。")
                .replaceAll("[^A-Za-z0-9\\u4e00-\\u9fa5\\-]+", "。");
        List<List<Term>> resultList = new ArrayList<List<Term>>();
        List<Term> termList = segment(text);
        StringBuilder natureStr = new StringBuilder();
        for (Term term : termList) {
            if (TermHelper.shouldDeleteTerm(term)) {
                natureStr.append("w");
            } else {
                natureStr.append(term.nature.firstChar());
            }
        }
        String[] strings = natureStr.toString().split("(m+q+)|((mq)+)|(w+)|(c+)|(u+)");
        int index = 0;
        for (String s : strings) {
            if (TextUtility.isBlank(s)) {
                continue;
            }
            index = natureStr.indexOf(s, index);
            List<Term> list = new ArrayList<Term>();
            for (int len = index + s.length(); index < len; index++) {
                list.add(termList.get(index));
            }
            resultList.add(list);
        }
        return resultList;
    }

    public static String[] getSentences(String text) {
        String[] result = text.split("[^A-Za-z0-9\\u4e00-\\u9fa5]+");
        for (int i = 0, l = result.length; i < l; i++) {
            result[i] = result[i].trim();
        }
        return result;
    }

    public static List<String> strToStings(String str) {
        char[] chars = str.toCharArray();
        List<String> list = new ArrayList<String>();
        for (int i = 0, len = chars.length; i < len; i++) {
            String tempStr = "";
            if (CharacterHelper.isCJKCharacter(chars[i])) {
                list.add(tempStr + chars[i]);
                continue;
            }
            while (CharacterHelper.isEnglishLetter(chars[i]) || CharacterHelper.isArabicNumber(chars[i])) {
                tempStr += chars[i];
                i++;
                if (i == len) {
                    i--;
                    break;
                }
            }
            if (!tempStr.equals("")) {
                list.add(tempStr.trim());
            }
            if (!tempStr.equals("") && CharacterHelper.isCJKCharacter(chars[i])) {
                i--;
            }
        }
        return list;
    }


    public static String normalize(String input) {
        input = input.replace("①", "一").replace("②", "二")
                .replace("③", "三").replace("④", "四")
                .replace("⑤", "五").replace("⑥", "六")
                .replace("⑦", "七").replace("⑧", "八")
                .replace("⑨", "九").replace("⑩", "十")
                .replace("〇", "零")
                .replace("上周周", "上周");
        return CharTable.convert(input);
    }
}
