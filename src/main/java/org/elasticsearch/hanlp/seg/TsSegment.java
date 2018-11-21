package org.elasticsearch.hanlp.seg;

import com.hankcs.hanlp.seg.WordBasedSegment;
import com.hankcs.hanlp.seg.common.Term;
import org.elasticsearch.hanlp.seg.mode.TsMode;

import java.util.List;

/**
 * elasticsearch-plugin-hanlp
 * Created by 何志龙 on 2017-09-11
 * Updated by 何志龙 on 2018-03-20
 */
public class TsSegment extends WordBasedSegment
{

    private TsMode tsMode;

    public TsMode getTsMode()
    {
        return tsMode;
    }

    public void setTsMode(TsMode tsMode)
    {
        this.tsMode = tsMode;
    }

    public TsSegment()
    {
        this.tsMode = TsMode.T2S;
    }

    public TsSegment(TsMode tsMode)
    {
        this.tsMode = tsMode;
    }

    protected List<Term> segSentence(char[] chars)
    {
        return SegmentHelper.segSentence(chars, this.tsMode);
    }
}
