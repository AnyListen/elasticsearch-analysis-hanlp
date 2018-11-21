package org.elasticsearch.hanlp.seg.mode;

/**
 * elasticsearch-plugin-hanlp
 * Created by 何志龙 on 2017-09-12.
 */
public enum  PinyinMode {
    FIRST_LETTER,       //首字母模式
    FULL_PINYIN,        //全拼模式
    MIX_PINYIN,         //拼音混合模式：全拼+简拼，简拼+全拼
    MIX_ALL             //混合模式:全拼+简拼，简拼+全拼，中文+全拼，中文+简拼
}
