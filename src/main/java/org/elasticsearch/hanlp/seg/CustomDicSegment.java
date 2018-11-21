package org.elasticsearch.hanlp.seg;

import com.hankcs.hanlp.collection.AhoCorasick.AhoCorasickDoubleArrayTrie;
import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.dictionary.CoreDictionary;
import com.hankcs.hanlp.seg.DictionaryBasedSegment;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.utility.TextUtility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static com.hankcs.hanlp.utility.Predefine.logger;

/**
 * 完全使用用户自定义词典进行分词
 * bigdata-nlp
 * net.luculent.bigdata.nlp.segment
 * Created by HEZHILONG on 2018-04-04.
 */
public class CustomDicSegment extends DictionaryBasedSegment
{
    AhoCorasickDoubleArrayTrie<CoreDictionary.Attribute> trie;

    @SuppressWarnings("StringConcatenationInLoop")
    @Override
    protected List<Term> segSentence(char[] sentence)
    {
        if (trie == null)
        {
            logger.warning("还未加载任何词典");
            return Collections.emptyList();
        }
        final int[] wordNet = new int[sentence.length];
        Arrays.fill(wordNet, 1);
        final Nature[] natureArray = new Nature[sentence.length];
        trie.parseText(sentence, new AhoCorasickDoubleArrayTrie.IHit<CoreDictionary.Attribute>()
        {
            @Override
            public void hit(int begin, int end, CoreDictionary.Attribute value)
            {
                int length = end - begin;
                if (length > wordNet[begin])
                {
                    wordNet[begin] = length;
                    natureArray[begin] = value.nature[0];
                }
            }
        });
        LinkedList<Term> termList = new LinkedList<Term>();
        for (int i = 0; i < wordNet.length; )
        {
            String word = new String(sentence, i, wordNet[i]);
            CoreDictionary.Attribute attribute = trie.get(word);
            Nature nature = (natureArray[i] == null || natureArray[i] == Nature.nz) ? (attribute == null ? null : attribute.nature[0]) : natureArray[i];
            int orgIndex = i;
//            if (nature == null)
//            {
//                while (true)
//                {
//                    i++;
//                    if (i >= wordNet.length)
//                    {
//                        break;
//                    }
//                    String wd = new String(sentence, i, wordNet[i]);
//                    attribute = trie.get(wd);
//                    if (attribute != null)
//                    {
//                        break;
//                    }
//                    nature = null;
//                    word += wd;
//                }
//            }
//            if (i - orgIndex > 1)
//            {
//                Term tm = new Term(word, Nature.w);
//                tm.offset = orgIndex;
//                termList.add(tm);
//                continue;
//            }

            if (nature != null && nature.startsWith("m"))
            {
                while (true)
                {
                    i++;
                    if (i >= wordNet.length)
                    {
                        break;
                    }
                    String wd = new String(sentence, i, wordNet[i]);
                    attribute = trie.get(wd);
                    nature = attribute == null ? null : attribute.nature[0];
                    if (nature == null || !nature.startsWith("m"))
                    {
                        nature = Nature.m;
                        break;
                    }
                    word += wd;
                }
            }
            if (i - orgIndex > 1)
            {
                nature = Nature.m;
            }
            Term term = new Term(word, nature);
            term.offset = orgIndex;
            termList.add(term);
            i = orgIndex + word.length();
        }
        return termList;
    }

    public CustomDicSegment()
    {
        super();
        config.useCustomDictionary = false;
        config.speechTagging = false;
    }

    public CustomDicSegment(TreeMap<String, CoreDictionary.Attribute> dictionary)
    {
        this();
        trie = new AhoCorasickDoubleArrayTrie<CoreDictionary.Attribute>();
        trie.build(dictionary);
        setTrie(trie);
    }

    @Override
    public Segment enableCustomDictionary(boolean enable)
    {
        throw new UnsupportedOperationException("AhoCorasickDoubleArrayTrieSegment暂时不支持用户词典。");
    }

    public AhoCorasickDoubleArrayTrie<CoreDictionary.Attribute> getTrie()
    {
        return trie;
    }

    public void setTrie(AhoCorasickDoubleArrayTrie<CoreDictionary.Attribute> trie)
    {
        this.trie = trie;
    }

    public CustomDicSegment loadDictionary(String... pathArray)
    {
        trie = new AhoCorasickDoubleArrayTrie<CoreDictionary.Attribute>();
        TreeMap<String, CoreDictionary.Attribute> map;
        try
        {
            map = IOUtil.loadDictionary(pathArray);
        }
        catch (IOException e)
        {
            logger.warning("加载词典失败\n" + TextUtility.exceptionToString(e));
            return this;
        }
        if (map != null && !map.isEmpty())
        {
            trie.build(map);
        }

        return this;
    }

    public CustomDicSegment loadDicFromResource(String... pathArray)
    {
        trie = getTrieFromResource(pathArray);
        return this;
    }

    public static AhoCorasickDoubleArrayTrie<CoreDictionary.Attribute> getTrieFromResource(String... pathArray)
    {
        AhoCorasickDoubleArrayTrie<CoreDictionary.Attribute> arrayTrie = new AhoCorasickDoubleArrayTrie<CoreDictionary.Attribute>();
        TreeMap<String, CoreDictionary.Attribute> map = new TreeMap<String, CoreDictionary.Attribute>();
        try
        {
            for (String path:pathArray)
            {
                InputStream resourceStream = IOUtil.getResourceAsStream(path);
                BufferedReader br = new BufferedReader(new InputStreamReader(resourceStream, "UTF-8"));
                IOUtil.loadDictionary(br, map, false, Nature.nx);
            }
        }
        catch (IOException e)
        {
            logger.warning("加载词典失败\n" + TextUtility.exceptionToString(e));
            return arrayTrie;
        }
        if (!map.isEmpty())
        {
            arrayTrie.build(map);
        }
        return arrayTrie;
    }
}
