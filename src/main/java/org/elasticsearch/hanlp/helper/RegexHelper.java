package org.elasticsearch.hanlp.helper;

import java.util.regex.Pattern;

/**
 * bigdata-nlp
 * bigdata-nlp
 * Created by Rochy on 2018-03-20.
 */
public class RegexHelper
{
    /**
     * 匹配中文
     */
    public static Pattern ChineseTextPattern = Pattern.compile("[\\u4e00-\\u9fa5]+");

    /**
     * 匹配中文、英文、数字组合的文本
     */
    public static Pattern NormalTextPattern = Pattern.compile("[A-Za-z0-9\\u4e00-\\u9fa5]+");

    /**
     * 匹配英文缩写词，英文组合词
     */
    public static Pattern EnMixTextPattern = Pattern.compile("([a-z]+['-.]+)+[a-z]*");
    /**
     * 匹配邮箱地址
     */
    public static final Pattern EmailPattern = Pattern.compile("(\\w+(?:[-+.]\\w+)*)@(\\w+(?:[-.]\\w+)*\\.\\w+(?:[-.]\\w+)*)");
    /**
     * 匹配网址
     */
    public static final Pattern UrlPattern = Pattern.compile("[a-zA-Z]{2,6}?://((?:[a-zA-Z0-9_\\-\\u4e00-\\u9fa5]+(?:\\.)?)*)(:\\d+)?(/(?:(?:\\.)?(?:\\?)?=?&?[%a-zA-Z0-9_\\-\\u4e00-\\u9fa5](?:\\?)?)*)*");
    /**
     * 匹配日期
     */
    public static final Pattern DatePattern = Pattern.compile("(?:([1-2一二][0-9零一二三四五六七八九][0-9零一二三四五六七八九][0-9零一二三四五六七八九])[-.年/])?([0-1零一]?[0-9零一二三四五六七八九])[-.月/]([0-3零一二三]?[0-9零一二三四五六七八九])[日号天]?\\s*(?:([0-2零一二][0-9零一二三四五六七八九])[:时：]([0-6零一二三四五六][0-9零一二三四五六七八九])[:分：]([0-6零一二三四五六][0-9零一二三四五六七八九])[秒]?)?");

}
