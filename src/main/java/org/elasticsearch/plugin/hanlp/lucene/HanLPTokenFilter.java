package org.elasticsearch.plugin.hanlp.lucene;

import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
/**
 * @author hankcs
 */
public class HanLPTokenFilter extends TokenFilter {
    private CharTermAttribute termAtt = (CharTermAttribute) this.addAttribute(CharTermAttribute.class);
    private String mode;
    private LinkedList<String> tokensCache = new LinkedList<>();

    public HanLPTokenFilter(String mode, TokenStream input) {
        super(input);
        this.mode = mode;
    }

    @Override
    public boolean incrementToken() throws IOException {
        if (hasMoreTokenInCache()) {
            this.termAtt.setEmpty();
            this.termAtt.append(nextTokenLexeme());
            return true;
        }
        if (this.input.incrementToken()) {
            char[] text = this.termAtt.buffer();
            List<Term> terms;
            switch (this.mode) {
                //todo 实现分词

//                case "t2s":
//                    terms = SegmentHelper.segSentence(text, TsMode.T2S);
//                    break;
//                case "s2t":
//                    terms = SegmentHelper.segSentence(text, TsMode.S2T);
//                    break;
//                case "ts":
//                    terms = SegmentHelper.segSentence(text, TsMode.ALL);
//                    break;
//                case "py_first":
//                    terms = SegmentHelper.segSentence(text, PinyinMode.FIRST_LETTER);
//                    break;
//                case "py_full":
//                    terms = SegmentHelper.segSentence(text, PinyinMode.FULL_PINYIN);
//                    break;
//                case "py_mix":
//                    terms = SegmentHelper.segSentence(text, PinyinMode.MIX_PINYIN);
//                    break;
//                case "py_all":
//                    terms = SegmentHelper.segSentence(text, PinyinMode.MIX_ALL);
//                    break;
                default:
                    terms = new ArrayList<>();
                    terms.add(new Term(new String(text), Nature.nz));
                    break;
            }
            Iterator<Term> pinyinIterator = terms.iterator();
            if (pinyinIterator.hasNext()) {
                String pinyinItem = pinyinIterator.next().word;
                while (pinyinIterator.hasNext()) {
                    addTokenToCache(pinyinIterator.next().word);
                }
                this.termAtt.setEmpty();
                this.termAtt.append(pinyinItem);
            }
            return true;
        }
        return false;
    }

    @Override
    public void reset() throws IOException {
        super.reset();
        tokensCache.clear();
    }

    protected boolean hasMoreTokenInCache() {
        return !tokensCache.isEmpty();
    }

    private String nextTokenLexeme() {
        return tokensCache.pollFirst();
    }

    private void addTokenToCache(String token) {
        if (token != null) {
            tokensCache.add(token);
        }
    }
}
