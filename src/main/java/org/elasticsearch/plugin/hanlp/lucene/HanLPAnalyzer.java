package org.elasticsearch.plugin.hanlp.lucene;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;

import java.util.Set;

public class HanLPAnalyzer extends Analyzer {
    private boolean enablePorterStemming;
    private Set<String> filter;
    private Segment segment;

    /**
     * @param filter               停用词
     * @param enablePorterStemming 是否分析词干（仅限英文）
     */
    public HanLPAnalyzer(Segment segment, Set<String> filter, boolean enablePorterStemming) {
        this.segment = segment;
        this.filter = filter;
        this.enablePorterStemming = enablePorterStemming;
    }

    /**
     * @param enablePorterStemming 是否分析词干.进行单复数,时态的转换
     */
    public HanLPAnalyzer(Segment segment, boolean enablePorterStemming) {
        this.segment = segment;
        this.enablePorterStemming = enablePorterStemming;
    }

    public HanLPAnalyzer(Segment segment, Set<String> filter) {
        this.segment = segment;
        this.filter = filter;
        this.enablePorterStemming = true;
    }

    public HanLPAnalyzer(Segment segment) {
        this.segment = segment;
        this.enablePorterStemming = true;
    }

    public HanLPAnalyzer() {
        super();
        this.segment = HanLP.newSegment().enableOffset(true).enableIndexMode(true).enablePartOfSpeechTagging(true);
    }

    /**
     * 重载Analyzer接口，构造分词组件
     */
    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokenizer = new HanLPTokenizer(this.segment, filter, enablePorterStemming);
        return new TokenStreamComponents(tokenizer);
    }
}

