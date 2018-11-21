package org.elasticsearch.hanlp.helper;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.utility.TextUtility;
import org.elasticsearch.hanlp.seg.mode.PinyinMode;
import org.elasticsearch.hanlp.seg.mode.TsMode;

import java.util.ArrayList;
import java.util.List;

public class SegmentHelper {

    private static final String defaultDelimiter = "[，,。:：“”？?！!；;\\s\"]+";

    private static List<Term> segSentence(Segment segment, String text, String splitStr) {
        if (TextUtility.isBlank(splitStr)) {
            return segment.seg(text);
        }
        String[] arr = text.split(splitStr);
        List<Term> result = new ArrayList<Term>();
        for (String s : arr) {
            if (TextUtility.isBlank(s)) {
                continue;
            }
            result.addAll(segment.seg(s));
            result.add(new Term(" ", Nature.wb));
        }
        return result;
    }

    //region 拼音模式
    public static List<Term> segSentence(char[] chars, PinyinMode mode) {
        String text = new String(chars);
        text = text.toLowerCase();
        List<Term> list = new ArrayList<Term>();
        if (text.matches("^[\\w ]+$")) {
            list.add(new Term(text, Nature.nx));
            StringBuilder fStr = new StringBuilder();
            String[] tmpArr = text.split(" ");
            for (String s : tmpArr) {
                fStr.append(s, 0, 1);
            }
            if (mode != PinyinMode.FULL_PINYIN) {
                list.add(new Term(fStr.toString(), Nature.nx));
            }
        } else {
            String[] fullArr = HanLP.convertToPinyinString(text, "#", false).split("#");
            String[] firstArr = HanLP.convertToPinyinFirstCharString(text, "#", true).split("#");
            if (mode == PinyinMode.FULL_PINYIN) {
                list.add(new Term(StringHelper.join(fullArr, "").trim(), Nature.nx));
            } else if (mode == PinyinMode.FIRST_LETTER) {
                list.add(new Term(StringHelper.join(firstArr, "").trim(), Nature.nx));
            } else {
                List<String> orgWords = new ArrayList<String>();
                String tmpEng = "";
                for (int i = 0; i < firstArr.length; i++) {
                    String c = (chars[i] + "").toLowerCase();
                    if (firstArr[i].equals(" ")) {
                        firstArr[i] = fullArr[i];
                        if (c.matches("[a-z]+")) {
                            tmpEng += c;
                        } else if (c.equals(" ")) {
                            if (!TextUtility.isBlank(tmpEng)) {
                                orgWords.add(tmpEng.trim());
                                tmpEng = "";
                            }
                        } else if (c.equals("'") || c.equals(".")) {
                            if (i > 0 && (chars[i - 1] + "").toLowerCase().matches("[a-z]+")) {
                                tmpEng += c;
                            }
                        }
                    } else {
                        if (!TextUtility.isBlank(tmpEng)) {
                            orgWords.add(tmpEng.trim());
                            tmpEng = "";
                        }
                        orgWords.add(c);
                    }
                }
                if (!TextUtility.isBlank(tmpEng)) {
                    orgWords.add(tmpEng.trim());
                }
                String[] wordsArr = orgWords.toArray(new String[0]);
                String[] fullPinyin = new String[wordsArr.length];
                for (int i = 0; i < wordsArr.length; i++) {
                    fullPinyin[i] = HanLP.convertToPinyinString(wordsArr[i], "", false);
                }
                joinPinyin(list, fullArr, firstArr);
                joinPinyin(list, firstArr, fullArr);
                if (mode == PinyinMode.MIX_ALL) {
                    joinPinyin(list, wordsArr, fullPinyin);
                    joinPinyin(list, wordsArr, firstArr);
                }
            }
        }
        return list;
    }

    private static void joinPinyin(List<Term> list, String[] a, String[] arr) {
        String[] b = new String[arr.length];
        System.arraycopy(arr, 0, b, 0, arr.length);
        for (int i = 0; i < a.length; i++) {
            System.arraycopy(a, 0, b, 0, i + 1);
            Term term = new Term(StringHelper.join(b, "").trim(), Nature.nx);
            TermHelper.addTermToList(list, term);
        }
    }

    //endregion

    //region 繁简体模式
    public static List<Term> segSentence(char[] chars, TsMode mode) {
        List<Term> list = new ArrayList<Term>();
        String text = new String(chars);
        if (mode == TsMode.T2S) {
            list.add(new Term(HanLP.convertToSimplifiedChinese(text), Nature.nz));
        } else if (mode == TsMode.S2T) {
            list.add(new Term(HanLP.convertToTraditionalChinese(text), Nature.nz));
        } else {
            String t1 = HanLP.convertToTraditionalChinese(text);
            String t2 = HanLP.convertToSimplifiedChinese(text);
            list.add(new Term(t1, Nature.nz));
            list.add(new Term(t2, Nature.nz));
        }
        return list;
    }
    //endregion
}
