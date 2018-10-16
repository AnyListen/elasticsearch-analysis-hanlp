# HanLP Analysis for Elasticsearch
基于 [HanLP](https://github.com/hankcs/HanLP) 的 Elasticsearch 中文分词插件，核心功能：
1. 内置词典，无需额外配置即可使用；
2. 支持用户自定义词典；
3. 支持远程词典热更新；
4. 内置多种分词模式，适合不同场景。

## 内置分词器
### 分析器(Analysis)
- hanlp_index：
- hanlp_search：
- hanlp_smart：
- hanlp_pinyin：
- hanlp_pinyin_mix：
- hanlp_pinyin_first：
- hanlp_pinyin_full：
- hanlp_array：
- hanlp_first：
- hanlp_ngram：
- hanlp_ngram_char：
- hanlp_url：
- hanlp_email：
- hanlp：

### 分词器(Tokenizer)
- hanlp_index：
- hanlp_search：
- hanlp_smart：
- hanlp_pinyin：
- hanlp_array：
- hanlp_ngram：
- hanlp_url：
- hanlp_email：
- hanlp：

### 过滤器(TokenFilter)
- py_all：
- py_mix：
- py_first：
- py_full：
- ts：
- t2s：
- s2t：
