package org.elasticsearch.hanlp.seg;

import com.hankcs.hanlp.seg.WordBasedSegment;
import com.hankcs.hanlp.seg.common.Term;

import java.util.ArrayList;
import java.util.List;

/**
 * bigdata-nlp
 * elasticsearch-plugin-hanlp
 * Updated by 何志龙 on 2018-03-20
 */
public class PinyinSegment extends WordBasedSegment
{

    private PinyinMode pinyinMode = PinyinMode.MIX_ALL;

    public PinyinMode getPinyinMode()
    {
        return pinyinMode;
    }

    public void setPinyinMode(PinyinMode pinyinMode)
    {
        this.pinyinMode = pinyinMode;
    }

    public PinyinSegment()
    {
        this.pinyinMode = PinyinMode.MIX_ALL;
    }

    public PinyinSegment(PinyinMode mode)
    {
        this.pinyinMode = mode;
    }

    protected List<Term> segSentence(char[] sentence)
    {
        String text = new String(sentence);
        String[] sentences = SentenceHelper.getSentences(text);
        List<Term> list = new ArrayList<Term>();
        for (String st : sentences)
        {
            List<Term> terms = SegmentHelper.segSentence(st.toCharArray(), this.pinyinMode);
            if (terms != null && terms.size() > 0)
            {
                list.addAll(terms);
            }
        }
        return list;
    }

}
