package org.elasticsearch.plugin.hanlp.conf;


/**
 * elasticsearch-analysis-hanlp
 * elasticsearch-analysis-hanlp
 * Created by hezl on 2018-12-03.
 */
public class HanLPConfig {
    /**
     * 分词算法，传入算法的中英文名都可以，可选列表：<br>
     * <ul>
     * <li>维特比 (viterbi)：效率和效果的最佳平衡</li>
     * <li>双数组trie树 (dat)：极速词典分词，千万字符每秒</li>
     * <li>条件随机场 (crf)：分词、词性标注与命名实体识别精度都较高，适合要求较高的NLP任务</li>
     * <li>感知机 (perceptron)：分词、词性标注与命名实体识别，支持在线学习</li>
     * <li>N最短路 (nshort)：命名实体识别稍微好一些，牺牲了速度</li>
     * </ul>
     */
    private String algorithm;
    /**
     * 设为索引模式（最细粒度切分）
     */
    private boolean enableIndexMode;
    /**
     * 是否启用用户词典
     */
    private boolean enableCustomDictionary;
    /**
     * 用户词典路径（绝对路径，多个词典用 ; 隔开）
     */
    private String customDictionaryPath;
    /**
     * 用户词典高优先级
     */
    private boolean enableCustomDictionaryForcing;
    /**
     * 停用词词典路径
     */
    private boolean enableStopWord;
    /**
     * 停用词词典路径
     */
    private String stopWordDictionaryPath;
    /**
     * 是否启用数词和数量词识别
     */
    private boolean enableNumberQuantifierRecognize;
    /**
     * 开启人名识别
     */
    private boolean enableNameRecognize;
    /**
     * 是否启用音译人名识别
     */
    private boolean enableTranslatedNameRecognize;
    /**
     * 是否启用日本人名识别
     */
    private boolean enableJapaneseNameRecognize;
    /**
     * 开启机构名识别
     */
    private boolean enableOrganizationRecognize;
    /**
     * 开启地名识别
     */
    private boolean enablePlaceRecognize;
    /**
     * 开启精准繁体中文分词
     */
    private boolean enableTraditionalChineseMode;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public boolean isEnableIndexMode() {
        return enableIndexMode;
    }

    public void setEnableIndexMode(boolean enableIndexMode) {
        this.enableIndexMode = enableIndexMode;
    }

    public boolean isEnableCustomDictionary() {
        return enableCustomDictionary;
    }

    public void setEnableCustomDictionary(boolean enableCustomDictionary) {
        this.enableCustomDictionary = enableCustomDictionary;
    }

    public String getCustomDictionaryPath() {
        return customDictionaryPath;
    }

    public void setCustomDictionaryPath(String customDictionaryPath) {
        this.customDictionaryPath = customDictionaryPath;
    }

    public boolean isEnableCustomDictionaryForcing() {
        return enableCustomDictionaryForcing;
    }

    public void setEnableCustomDictionaryForcing(boolean enableCustomDictionaryForcing) {
        this.enableCustomDictionaryForcing = enableCustomDictionaryForcing;
    }

    public String getStopWordDictionaryPath() {
        return stopWordDictionaryPath;
    }

    public void setStopWordDictionaryPath(String stopWordDictionaryPath) {
        this.stopWordDictionaryPath = stopWordDictionaryPath;
    }

    public boolean isEnableNumberQuantifierRecognize() {
        return enableNumberQuantifierRecognize;
    }

    public void setEnableNumberQuantifierRecognize(boolean enableNumberQuantifierRecognize) {
        this.enableNumberQuantifierRecognize = enableNumberQuantifierRecognize;
    }

    public boolean isEnableNameRecognize() {
        return enableNameRecognize;
    }

    public void setEnableNameRecognize(boolean enableNameRecognize) {
        this.enableNameRecognize = enableNameRecognize;
    }

    public boolean isEnableTranslatedNameRecognize() {
        return enableTranslatedNameRecognize;
    }

    public void setEnableTranslatedNameRecognize(boolean enableTranslatedNameRecognize) {
        this.enableTranslatedNameRecognize = enableTranslatedNameRecognize;
    }

    public boolean isEnableJapaneseNameRecognize() {
        return enableJapaneseNameRecognize;
    }

    public void setEnableJapaneseNameRecognize(boolean enableJapaneseNameRecognize) {
        this.enableJapaneseNameRecognize = enableJapaneseNameRecognize;
    }

    public boolean isEnableOrganizationRecognize() {
        return enableOrganizationRecognize;
    }

    public void setEnableOrganizationRecognize(boolean enableOrganizationRecognize) {
        this.enableOrganizationRecognize = enableOrganizationRecognize;
    }

    public boolean isEnablePlaceRecognize() {
        return enablePlaceRecognize;
    }

    public void setEnablePlaceRecognize(boolean enablePlaceRecognize) {
        this.enablePlaceRecognize = enablePlaceRecognize;
    }

    public boolean isEnableTraditionalChineseMode() {
        return enableTraditionalChineseMode;
    }

    public void setEnableTraditionalChineseMode(boolean enableTraditionalChineseMode) {
        this.enableTraditionalChineseMode = enableTraditionalChineseMode;
    }

    public boolean isEnableStopWord() {
        return enableStopWord;
    }

    public void setEnableStopWord(boolean enableStopWord) {
        this.enableStopWord = enableStopWord;
    }
}
