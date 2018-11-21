package org.elasticsearch.hanlp.seg;

import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.WordBasedSegment;
import com.hankcs.hanlp.seg.common.Term;
import net.luculent.bigdata.nlp.helper.SentenceHelper;

import java.util.*;

/**
 * N-gram是指给定的一段文本中N个item的序列，item可以是音节、字母、单词等<br/>
 * NGramSegment是N元切分
 * Created by 何志龙 on 2017-09-16
 * Updated by 何志龙 on 2018-03-20
 */
public class NGramSegment extends WordBasedSegment
{

    /**
     * 是否以词为单个单位
     */
    private boolean wordGram;
    /**
     * 是否移除停用词
     */
    private boolean removeStopWords;
    /**
     * 短语最大长度
     */
    private int maxLength = 10;
    /**
     * 短语最小长度
     */
    private int minLength = 2;
    /**
     * 短语最大词数
     */
    private int maxWordLength = 5;
    /**
     * 短语最小词数
     */
    private int minWordLength = 1;

    public boolean isWordGram()
    {
        return wordGram;
    }

    public void setWordGram(boolean wordGram)
    {
        this.wordGram = wordGram;
    }

    public int getMaxLength()
    {
        return maxLength;
    }

    public void setMaxLength(int maxLength)
    {
        this.maxLength = maxLength;
    }

    public int getMinLength()
    {
        return minLength;
    }

    public void setMinLength(int minLength)
    {
        this.minLength = minLength;
    }

    public int getMaxWordLength()
    {
        return maxWordLength;
    }

    public void setMaxWordLength(int maxWordLength)
    {
        this.maxWordLength = maxWordLength;
    }

    public int getMinWordLength()
    {
        return minWordLength;
    }

    public void setMinWordLength(int minWordLength)
    {
        this.minWordLength = minWordLength;
    }

    public boolean isRemoveStopWords()
    {
        return removeStopWords;
    }

    public void setRemoveStopWords(boolean removeStopWords)
    {
        this.removeStopWords = removeStopWords;
    }

    public NGramSegment()
    {
        this.maxLength = 10;
        this.minLength = 2;
        this.maxWordLength = 5;
        this.minWordLength = 1;
        this.wordGram = true;
        this.removeStopWords = true;
    }

    public NGramSegment(boolean wordGram, boolean removeStopWords)
    {
        this.wordGram = wordGram;
        this.removeStopWords = removeStopWords;
    }

    public NGramSegment(boolean wordGram, boolean removeStopWords, int maxLength, int minLength, int maxWordLength, int minWordLength)
    {
        this.maxLength = maxLength;
        this.minLength = minLength;
        this.maxWordLength = maxWordLength;
        this.minWordLength = minWordLength;
        this.wordGram = wordGram;
        this.removeStopWords = removeStopWords;
    }

    public List<Term> segSentence(char[] chars)
    {
        String text = new String(chars);
        List<String> result = wordGram ?
                SentenceHelper.nWordGram(text, removeStopWords, maxWordLength, minWordLength, maxLength, minLength) :
                SentenceHelper.nGram(text, maxLength, minLength);
        List<Term> termList = new LinkedList<Term>();
        for (String s : result)
        {
            Term term = new Term(s, Nature.nz);
            if (contains(termList, term))
            {
                continue;
            }
            int i = text.indexOf(s);
            term.offset = i >=0 ? i : 0;
            termList.add(term);
        }
        Collections.sort(termList, new Comparator<Term>()
        {
            @Override
            public int compare(Term o1, Term o2)
            {
                int a = o1.offset - o2.offset;
                if (a != 0){
                    return a;
                }
                return o1.length() - o2.length();
            }
        });
        return termList;
    }

    private static boolean contains(List<Term> list, Term term)
    {
        if (term == null)
        {
            return list.contains(null);
        }
        for (Term t : list)
        {
            if (t.word.equals(term.word))
            {
                return true;
            }
        }
        return false;
    }
}
