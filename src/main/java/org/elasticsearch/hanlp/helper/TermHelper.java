package org.elasticsearch.hanlp.helper;

import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.utility.LexiconUtility;

import java.util.List;

/**
 * bigdata-nlp
 * bigdata-nlp
 * Created by Rochy on 2018-03-20.
 */
public class TermHelper {
    /**
     * 判断Term是否存在List
     *
     * @param list 待检测集合
     * @param term 待匹配Term
     * @return true-->存在
     */
    public static boolean termInList(List<Term> list, Term term) {
        if (term == null) {
            return list.contains(null);
        }
        for (Term t : list) {
            if (t.word.equals(term.word)) {
                return true;
            }
        }
        return false;
    }

    public static void addTermToList(List<Term> list, Term term) {
        if (termInList(list, term)) {
            return;
        }
        list.add(term);
    }

    public static boolean wordInDic(String word) {
        return LexiconUtility.getFrequency(word) > 0;
    }

    public static boolean termInDic(Term term) {
        return LexiconUtility.getFrequency(term.word) > 0;
    }

    /**
     * 判断Term是否应该被排除
     *
     * @param term 待检测Term
     * @return true--应该被删除
     */
    public static boolean shouldDeleteTerm(Term term) {
        if (term.nature == null) {
            term.nature = Nature.nz;
        }
        //todo 需要再三核查

//        if (term.nature.startsWith("nr") || term.nature.startsWith("ns") || term.nature.startsWith("nt") || term.nature == Nature.i)
//        {
//            return false;
//        }
        String nature = term.nature.toString();
        char firstChar = nature.charAt(0);
        switch (firstChar) {
            case 'b':                       //区别词
            case 'c':                       //连词
            case 'e':                       //叹词
                //case 'm':                       //数词
            case 'o':                       //拟声词
            case 'p':                       //介词
            case 'q':                       //量词
            case 'r':                       //代词
            case 'u':                       //助词
            case 'w':                       //标点符号
            case 'y':                       //语气词
            case 'z':                       //状态词
                //case 'd':                       //副词
                //case 'f':                       //方位词
            case 't':                       //时间
                return true;
            //                case 'g':                       //学术词汇
            //                case 'h':                       //前缀
            //                case 'i':                       //成语
            //                case 'j':                       //简称略语
            //                case 'k':                       //后缀
            //                case 'l':                       //习用语
            //                case 'n':                       //名词
            //                case 's':                       //处所词
            //                case 'v':                       //动词
            //                case 'x':                       //字符串
            default:
                return PredefineDic.allStopWordSet.contains(term.word);
        }
    }


}
