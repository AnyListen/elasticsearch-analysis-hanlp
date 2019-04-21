HanLP Analysis for Elasticsearch
=====

基于 [HanLP](https://github.com/hankcs/HanLP) 的 Elasticsearch 中文分词插件，核心功能：

1. 兼容 ES 5.x-7.x；
2. 内置词典，无需额外配置即可使用；
3. 支持用户自定义词典；
4. 支持远程词典热更新（待开发）；
5. 内置多种分词模式，适合不同场景；
6. 拼音过滤器（待开发）；
7. 简繁体转换过滤器（待开发）。

## 版本
插件版本和 ES 版本一致，直接下载对应版本的插件进行安装即可。

- 插件开发完成时，最新版本已经为 6.5.2 了，所以个人只对典型的版本进行了测试；
- 5.X 在 5.0.0、5.5.0 版本进行了测试；
- 6.X 在 6.0.0、6.3.0、6.4.1、6.5.1 版本进行了测试；
- 7.X 在 7.0.0 版本进行了测试。

## 安装使用
### 下载编译
git clone 对应版本的代码，打开 `pom.xml` 文件，修改 `<elasticsearch.version>6.5.1</elasticsearch.version>` 为需要的 ES 版本；然后使用 `mvn package` 生产打包文件，最终文件在 `target/release` 文件夹下。

打包完成后，使用离线方式安装即可。

### 使用默认词典
- 在线安装：`.\elasticsearch-plugin install https://github.com/AnyListen/elasticsearch-analysis-hanlp/releases/download/vA.B.C/elasticsearch-analysis-hanlp-A.B.C.zip`
- 离线安装：`.\elasticsearch-plugin install file:///FILE_PATH/elasticsearch-analysis-hanlp-A.B.C.zip`

> 离线安装请把 `FILE_PATH` 更改为 zip 文件路径；A、B、C 对应的是 ES 版本号。

### 使用自定义词典
默认词典是精简版的词典，能够满足基本需求，但是无法使用感知机和 CRF 等基于模型的分词器。

HanLP 提供了更加[完整的词典](http://nlp.hankcs.com/download.php?file=data)，请按需下载。

词典下载后，解压到任意目录，然后修改**插件安装目录下**的 `hanlp.properties` 文件，只需修改第一行
```
root=D:/JavaProjects/HanLP/
```
为 `data` 的父目录即可，比如 `data` 目录是 `/Users/hankcs/Documents/data`，那么 `root=/Users/hankcs/Documents/`。

### 使用自定义配置文件
如果你在其他地方使用了 HanLP，希望能够复用 `hanlp.properties` 文件，你只需要修改**插件安装目录下**的 `plugin.properties` 文件，将 `configPath` 配置为已有的 `hanlp.properties` 文件地址即可。

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

### 自定义分词器
插件有较为丰富的选项允许用户自定义分词器，下面是可用的配置项：

| 配置项名称       | 功能   |  默认值  |
| --------   | -----:  | :----:  |
| algorithm   | 可选项有：<br/> viterbi：维特比分词 <br/> |   viterbi     |
| enableIndexMode    | 设为索引模式（细粒度切分） |   false     |
| enableCustomDictionary    | 是否启用用户词典 |   true     |
| customDictionaryPath    | 用户词典路径(绝对路径,多个词典用`;`隔开) |   null     |
| enableCustomDictionaryForcing    | [用户词典高优先级](https://github.com/hankcs/HanLP/wiki/FAQ#%E4%B8%BA%E4%BB%80%E4%B9%88%E4%BF%AE%E6%94%B9%E4%BA%86%E8%AF%8D%E5%85%B8%E8%BF%98%E6%98%AF%E6%B2%A1%E6%9C%89%E6%95%88%E6%9E%9C) |   false     |
| enableStopWord    | 是否启用停用词过滤 |   false     |
| stopWordDictionaryPath    | 停用词词典路径 |   null     |
| enableNumberQuantifierRecognize    | 是否启用数词和数量词识别 |   true     |
| enableNameRecognize    | 开启人名识别 |   true     |
| enableTranslatedNameRecognize    | 是否启用音译人名识别 |   false     |
| enableJapaneseNameRecognize    | 是否启用日本人名识别 |   false     |
| enableOrganizationRecognize    | 开启机构名识别 |   false     |
| enablePlaceRecognize    | 开启地名识别 |   false     |
| enableTraditionalChineseMode    | 开启精准繁体中文分词 |   false     |

**案例展示：**
```
# 创建自定义分词器
PUT my_index
{
  "settings": {
    "analysis": {
      "analyzer": {
        "my_analyzer": {
          "type": "hanlp",
          "algorithm": "viterbi",
          "enableIndexMode": "true",
          "enableCustomDictionary": "true",
          "customDictionaryPath": "",
          "enableCustomDictionaryForcing": "false",
          "enableStopWord": "true",
          "stopWordDictionaryPath": "",
          "enableNumberQuantifierRecognize": "true",
          "enableNameRecognize": "true",
          "enableTranslatedNameRecognize": "true",
          "enableJapaneseNameRecognize": "true",
          "enableOrganizationRecognize": "true",
          "enablePlaceRecognize": "true",
          "enableTraditionalChineseMode": "false"
        }
      }
    }
  }
}

# 测试分词器
POST my_index/_analyze
{
  "analyzer": "my_analyzer",
  "text": "张惠妹在上海市举办演唱会啦"
}
```

## 分词速度（仅供参考）
> 借助 `_analyze` API（**1核1G单线程**），通过改变分词器类型，对 2W 字的文本进行分词，以下为从请求到返回的耗时：

分词器 | 耗时（ms）
--- | ---
`hanlp_smart` | 148
`hanlp_nlp`  | 182
`hanlp_per`  | 286
`hanlp_crf` | 357
