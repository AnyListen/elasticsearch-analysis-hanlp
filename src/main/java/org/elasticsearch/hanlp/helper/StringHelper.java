package org.elasticsearch.hanlp.helper;

import com.hankcs.hanlp.seg.common.Term;

import java.util.Iterator;
import java.util.List;

/**
 * bigdata-nlp
 * elasticsearch-plugin-hanlp
 * Created by HEZHILONG on 2018-03-21.
 */
public class StringHelper {
    /**
     * 字符串拼接
     *
     * @param array     待拼接集合
     * @param separator 连接符
     * @return 拼接结果
     */
    public static String join(final Object[] array, final String separator) {
        if (array == null) {
            return null;
        }
        return join(array, separator, 0, array.length);
    }

    /**
     * 字符串拼接
     *
     * @param array      待拼接集合
     * @param separator  连接符
     * @param startIndex 开始Index
     * @param endIndex   结束Index
     * @return 拼接结果
     */
    public static String join(final Object[] array, String separator, final int startIndex, final int endIndex) {
        if (array == null) {
            return null;
        }
        if (separator == null) {
            separator = "";
        }
        final int noOfItems = endIndex - startIndex;
        if (noOfItems <= 0) {
            return "";
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
     * 字符串拼接
     *
     * @param iterator  待拼接迭代器
     * @param separator 连接符
     * @return 拼接结果
     */
    public static String join(final Iterator<?> iterator, final String separator) {
        if (iterator == null) {
            return null;
        }
        if (!iterator.hasNext()) {
            return "";
        }
        final Object first = iterator.next();
        if (!iterator.hasNext()) return first.toString();
        final StringBuilder buf = new StringBuilder(256);
        if (first != null) {
            buf.append(first);
        }
        while (iterator.hasNext()) {
            if (separator != null) {
                buf.append(separator);
            }
            final Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        return buf.toString();
    }

    /**
     * 去掉文本前后制定的字符
     *
     * @param str   待处理文本
     * @param rmStr 要移除的字符
     * @return 处理后的文本
     */
    public static String trim(String str, String rmStr) {
        rmStr = rmStr.replaceAll("\\[", "\\\\[").replaceAll("\\^", "\\\\^");
        String begin = "^[\\s" + rmStr + "]+";
        String end = "[\\s" + rmStr + "]+$";
        return str.replaceAll(begin, "").replaceAll(end, "");
    }

    public static String join(List<Term> termList, String separator) {
        if (termList == null || termList.size() <= 0) {
            return "";
        }
        if (separator == null) {
            separator = ",";
        }
        StringBuilder sb = new StringBuilder();
        for (Term term : termList) {
            sb.append(term.word).append(separator);
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * 获取源字符串对于目标字符串而言的包含程度
     *
     * @param source 源字符串
     * @param target 目标字符串
     * @return 包含百分比
     */
    public static double getIncludePercent(String source, String target) {
        int count = 0;
        for (char c : source.toCharArray()) {
            if (target.contains(c + "")) {
                count++;
            }
        }
        return count / 1.0 / source.length();
    }
}
