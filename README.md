HanLP Analysis for Elasticsearch
=====

基于 [HanLP](https://github.com/hankcs/HanLP) 的 Elasticsearch 中文分词插件，核心功能：
1. 内置词典，无需额外配置即可使用；
2. 支持用户自定义词典（待新版 HanLP 适配）；
3. 支持远程词典热更新（待开发）；
4. 内置多种分词模式，适合不同场景（待开发）。

## 内置分词器
### 分析器(Analysis)
- hanlp_index：细粒度切分
- hanlp_smart：常规切分
- hanlp_nlp：命名实体识别
- hanlp_per：感知机分词
- hanlp_crf：CRF分词
- hanlp：自定义

### 分词器(Tokenizer)
- hanlp_index：细粒度切分
- hanlp_smart：常规切分
- hanlp_nlp：命名实体识别
- hanlp_per：感知机分词
- hanlp_crf：CRF分词
- hanlp：自定义
