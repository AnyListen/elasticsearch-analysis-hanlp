package org.elasticsearch.hanlp.seg;

import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.WordBasedSegment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.utility.TextUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 根据分隔符进行切分，并返回指定序号的子元素
 * Created by 何志龙 on 2017-10-24.
 */
public class DelimiterSegment extends WordBasedSegment
{
    private List<Integer> indexList = new ArrayList<Integer>();
    private Pattern pattern;
    public static final List<Character> SpecialChars = new ArrayList<Character>()
    {{
        add('\\');
        add('$');
        add('(');
        add(')');
        add('*');
        add('+');
        add('.');
        add('[');
        add(']');
        add('?');
        add('^');
        add('{');
        add('}');
        add('|');
    }};

    /**
     * 新建分隔符切分
     * @param delimiter 分隔符，例如“，。/、”等
     * @param index 指定只获取切分后第n个元素，all代表全部；支持“1,2-5,8”这种模式
     */
    public DelimiterSegment(String delimiter, String index)
    {
        if (TextUtility.isBlank(delimiter))
        {
            delimiter = " ";
        }
        initIndex(index);
        List<Character> characterList = new ArrayList<Character>();
        for (Character c : delimiter.toCharArray())
        {
            if (characterList.contains(c))
            {
                continue;
            }
            characterList.add(c);
        }
        StringBuilder regexStr = new StringBuilder("[");
        for (Character c : characterList)
        {
            if (SpecialChars.contains(c))
            {
                regexStr.append("\\").append(c);
            }
            else
            {
                regexStr.append(c);
            }
        }
        regexStr.append("]+");
        pattern = Pattern.compile(regexStr.toString());
    }

    private void initIndex(String index)
    {
        if (index.equals("all"))
        {
            this.indexList = null;
        }
        else if (index.contains(","))
        {
            String[] split = index.split(",");
            for (String s : split)
            {
                dealSingle(s);
            }
        }
        else
        {
            dealSingle(index);
        }
    }

    private void dealSingle(String s)
    {
        if (s.contains("-"))
        {
            try
            {
                String[] arr = s.split("-");
                if (arr.length == 2)
                {
                    int a1 = Integer.parseInt(arr[0]);
                    int a2 = Integer.parseInt(arr[1]);
                    for (int i = a1; i <= a2; i++)
                    {
                        if (!this.indexList.contains(i))
                        {
                            this.indexList.add(i);
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            try
            {
                this.indexList.add(Integer.parseInt(s));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 新建分隔符切分
     * 默认分隔符为"[,;。？?！!，、；：:“”‘’《》【】（）~〈〉「」『』…/\\[\\]<>\"`*+^]+"
     * 默认返回全部切分结果
     */
    public DelimiterSegment()
    {
        pattern = Pattern.compile("[,;。？?！!，、；：:“”‘’《》【】（）~〈〉「」『』…/\\[\\]<>\"`*+^]+");
        initIndex("all");
    }

    /**
     * 新建分隔符切分
     * @param index 指定只获取切分后第n个元素
     */
    public DelimiterSegment(String index)
    {
        pattern = Pattern.compile("[,;。？?！!，、；：:“”‘’《》【】（）~〈〉「」『』…/\\[\\]<>\"`*+^]+");
        initIndex(index);
    }

    @Override
    protected List<Term> segSentence(char[] chars)
    {
        List<Term> list = new ArrayList<Term>();
        String text = new String(chars);
        if (TextUtility.isBlank(text))
        {
            return list;
        }
        String[] arr = pattern.split(text);
        if (this.indexList == null || this.indexList.size() == 0)
        {
            for (String a : arr)
            {
                if (TextUtility.isBlank(a))
                {
                    continue;
                }
                Term term = new Term(a, Nature.nz);
                int i = text.indexOf(a);
                term.offset = i >= 0 ? i : 0;
                list.add(term);
            }
        }
        else
        {
            for (int i : indexList)
            {
                if (i < arr.length)
                {
                    if (TextUtility.isBlank(arr[i]))
                    {
                        continue;
                    }
                    Term term = new Term(arr[i], Nature.nz);
                    int n = text.indexOf(arr[i]);
                    term.offset = n >= 0 ? n : 0;
                    list.add(term);
                }
            }
        }
        return list;
    }
}
