package org.elasticsearch.hanlp.conf;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.settings.Settings;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * elasticsearch-analysis-hanlp
 * elasticsearch-analysis-hanlp
 * Created by hezl on 2018-11-20.
 */
public class SegmentConfig {
    /**
     * 根据 获取用户的自定义设置
     * @param settings ES基本设置
     * @return 自定义分词配置信息
     */
    public static Map<String, Object> getEnableSetting(Settings settings) {
        Map<String, Object> settingMap = new HashMap<>();

        //region 配置信息
        //拼音
        //module: pinyin
        //mode: full/first/mix/all

        //繁简
        //module: ts
        //mode: t2s/s2t/all

        //NLP
        //module: nlp
        //mode: name/place/org/email/url/date

        //常规
        //module: normal
        //mode: index/search
        //stop: true/false
        //index:true/false

        //自定义
        //module: custom
        //mode: 算法名称
        //stop: true/false
        //index:true/false
        //user_dic: true/false
        //name_reg: true/false
        //place_reg: true/false
        //org_reg: true/false
        //num_reg: true/false

        //数组
        //module: array
        //delimiter: '[,;。？?！!，、；：:~…/\\\\]+'
        //index: 0/1/2/all/1-2/

        //NGram
        //module: ngram
        //mode: char/word
        //minWord: 1
        //maxWord: 5
        //minLength: 2
        //maxLength: 10
        //stop: true/false

        //endregion

        settingMap.put("module", settings.get("module", "normal"));
        settingMap.put("mode", settings.get("mode", "index"));
        settingMap.put("split", settings.getAsBoolean("split", true));
        settingMap.put("stop", settings.getAsBoolean("stop", false));
        settingMap.put("force", settings.getAsBoolean("force", false));

        settingMap.put("delimiter", settings.get("delimiter", " "));
        settingMap.put("index", settings.get("index", "all"));

        settingMap.put("minWord", settings.getAsInt("minWord", 1));
        settingMap.put("maxWord", settings.getAsInt("maxWord", 5));

        settingMap.put("minLength", settings.getAsInt("minLength", 2));
        settingMap.put("maxLength", settings.getAsInt("maxLength", 10));
        return settingMap;
    }

    /**
     * 根据用户设置返回相应的分词器
     * @param settingMap 用户自定义设置
     * @return 分词器
     */
    public static Segment getSegment(Map<String,Object> settingMap) {
        String module = settingMap.containsKey("module") ? settingMap.get("module").toString() : "normal";
        String mode = settingMap.containsKey("mode") ? settingMap.get("mode").toString() : "index";

        boolean split = settingMap.containsKey("split") && (boolean)settingMap.get("split");
        boolean force = settingMap.containsKey("force") && (boolean) settingMap.get("force");
        boolean stop = settingMap.containsKey("stop") && (boolean) settingMap.get("stop");

        Segment segment;
        switch (module) {
//            case "pinyin":
//            case "py":
//                PinyinMode pinyinMode;
//                switch (mode) {
//                    case "full":
//                        pinyinMode = PinyinMode.FULL_PINYIN;
//                        break;
//                    case "first":
//                        pinyinMode = PinyinMode.FIRST_LETTER;
//                        break;
//                    case "mix":
//                        pinyinMode = PinyinMode.MIX_PINYIN;
//                        break;
//                    default:
//                        pinyinMode = PinyinMode.MIX_ALL;
//                        break;
//                }
//                segment = new PinyinSegment(pinyinMode);
//                break;
//            case "ts":
//                TsMode tsMode;
//                switch (mode) {
//                    case "t2s":
//                        tsMode = TsMode.T2S;
//                        break;
//                    case "s2t":
//                        tsMode = TsMode.S2T;
//                        break;
//                    default:
//                        tsMode = TsMode.ALL;
//                        break;
//                }
//                segment =  new TsSegment(tsMode);
//                break;
//            case "nlp":
//                NLPMode nlpMode;
//                switch (mode) {
//                    case "name":
//                        nlpMode = NLPMode.NAME;
//                        break;
//                    case "place":
//                        nlpMode = NLPMode.PLACE;
//                        break;
//                    case "org":
//                        nlpMode = NLPMode.ORG;
//                        break;
//                    case "email":
//                        nlpMode = NLPMode.EMAIL;
//                        break;
//                    case "url":
//                        nlpMode = NLPMode.URL;
//                        break;
//                    case "date":
//                        nlpMode = NLPMode.DATE;
//                        break;
//                    default:
//                        nlpMode = NLPMode.ALL_NAME;
//                        break;
//                }
//                segment =  new NLPSegment(nlpMode);
//                break;
            case "array":
                String sp = settingMap.get("delimiter").toString();
                String index = settingMap.get("index").toString();
                segment =  new DelimiterSegment(sp, index);
                break;
            case "ngram":
                try {
                    int minLength = (int) settingMap.get("minLength");
                    int maxLength = (int) settingMap.get("maxLength");
                    int minWord = (int) settingMap.get("minWord");
                    int maxWord = (int) settingMap.get("maxWord");
                    segment =  new NGramSegment(mode.equals("word"), stop, maxLength, minLength, maxWord, minWord);
                } catch (Exception e) {
                    e.printStackTrace();
                    segment =  new NGramSegment(true, true);
                }
                break;
            case "custom":
                segment =  HanLP.newSegment(mode).enableOffset(true)
                        .enablePartOfSpeechTagging(true)
                        .enableNameRecognize(true)
                        .enablePlaceRecognize(false)
                        .enableCustomDictionary(true)
                        .enableOrganizationRecognize(false)
                        .enableNumberQuantifierRecognize(true);
                break;
            default:
                IndexSegment indexSegment = new IndexSegment(mode.equals("search"));
                indexSegment.enableOffset(true)
                        .enablePartOfSpeechTagging(true)
                        .enableNameRecognize(true)
                        .enablePlaceRecognize(false)
                        .enableCustomDictionary(true)
                        .enableOrganizationRecognize(false)
                        .enableNumberQuantifierRecognize(true);
                segment =  indexSegment;
                break;
        }
        return segment.enableIndexMode(split).enableCustomDictionaryForcing(force);
    }

    /**
     * 根据Setting返回相应的分词器
     * @param settings ES原始设置
     * @return 分词器
     */
    public static Segment getSegmentBySetting(Settings settings){
        Map<String, Object> userSetting = getEnableSetting(settings);
        return getSegment(userSetting);
    }


    /**
     * 根据用户设置或者ES索引设置获取分词器和停用词
     * @param settings ES索引设置
     * @param userSetting 用户设置
     * @return 分词器和停用词集合
     */
    public static Tuple<Segment, Set<String>> getSegmentAndFilter(Settings settings, Map<String,Object> userSetting){
        Segment segment;
        boolean stop;
        if (userSetting != null && userSetting.size() > 0) {
            segment = ConfigHelper.getSegment(userSetting);
            stop = userSetting.containsKey("stop") && (boolean) userSetting.get("stop");
        } else {
            Map<String, Object> enableSetting = ConfigHelper.getEnableSetting(settings);
            segment = ConfigHelper.getSegment(enableSetting);
            stop = (boolean) enableSetting.get("stop");
        }
        Set<String> filter = stop ? DicHelper.getStopWordSet() : null;
        return Tuple.tuple(segment, filter);
    }

    public static Map<String, Object> getIndexSetting(){
        Map<String, Object> userSetting = new HashMap<>();
        userSetting.put("module", "normal");
        userSetting.put("mode", "index");
        userSetting.put("stop", false);
        userSetting.put("split", true);
        return userSetting;
    }

    public static Map<String, Object> getSearchSetting(){
        Map<String, Object> setting = getIndexSetting();
        setting.put("mode", "search");
        return setting;
    }

    public static Map<String, Object> getSmartSetting(){
        Map<String, Object> setting = getIndexSetting();
        setting.put("mode", "smart");
        setting.put("split", false);
        return setting;
    }

    public static Map<String, Object> getPinyinAllSetting(){
        Map<String, Object> userSetting = new HashMap<>();
        userSetting.put("module", "pinyin");
        userSetting.put("mode", "all");
        return userSetting;
    }

    public static Map<String, Object> getPinyinFirstSetting(){
        Map<String, Object> setting = getPinyinAllSetting();
        setting.put("mode", "first");
        return setting;
    }

    public static Map<String, Object> getPinyinFullSetting(){
        Map<String, Object> setting = getPinyinAllSetting();
        setting.put("mode", "full");
        return setting;
    }

    public static Map<String, Object> getPinyinMixSetting(){
        Map<String, Object> setting = getPinyinAllSetting();
        setting.put("mode", "mix");
        return setting;
    }

    public static Map<String, Object> getTsAllSetting(){
        Map<String, Object> userSetting = new HashMap<>();
        userSetting.put("module", "ts");
        userSetting.put("mode", "all");
        return userSetting;
    }

    public static Map<String, Object> getT2sAllSetting(){
        Map<String, Object> userSetting = getTsAllSetting();
        userSetting.put("mode", "t2s");
        return userSetting;
    }

    public static Map<String, Object> getS2tAllSetting(){
        Map<String, Object> userSetting = getTsAllSetting();
        userSetting.put("mode", "s2t");
        return userSetting;
    }

    public static Map<String, Object> getNameSetting(){
        Map<String, Object> userSetting = new HashMap<>();
        userSetting.put("module", "nlp");
        userSetting.put("mode", "name");
        return userSetting;
    }

    public static Map<String, Object> getPlaceSetting(){
        Map<String, Object> userSetting = getNameSetting();
        userSetting.put("mode", "place");
        return userSetting;
    }

    public static Map<String, Object> getOrgSetting(){
        Map<String, Object> userSetting = getNameSetting();
        userSetting.put("mode", "org");
        return userSetting;
    }

    public static Map<String, Object> getEmailSetting(){
        Map<String, Object> userSetting = getNameSetting();
        userSetting.put("mode", "email");
        return userSetting;
    }

    public static Map<String, Object> getUrlSetting(){
        Map<String, Object> userSetting = getNameSetting();
        userSetting.put("mode", "url");
        return userSetting;
    }

    public static Map<String, Object> getDateSetting(){
        Map<String, Object> userSetting = getNameSetting();
        userSetting.put("mode", "date");
        return userSetting;
    }

    public static Map<String, Object> getArrayAllSetting(){
        Map<String, Object> userSetting = new HashMap<>();
        userSetting.put("module", "array");
        userSetting.put("delimiter", "[,;。？?！!，、；：:~…/\\\\]+");
        userSetting.put("index", "all");
        return userSetting;
    }

    public static Map<String, Object> getArrayFirstSetting(){
        Map<String, Object> userSetting = getArrayAllSetting();
        userSetting.put("index", "0");
        return userSetting;
    }

    public static Map<String, Object> getWordNGramSetting(){
        Map<String, Object> userSetting = new HashMap<>();
        userSetting.put("module", "ngram");
        userSetting.put("mode", "word");
        userSetting.put("stop", false);
        userSetting.put("minWord", 1);
        userSetting.put("maxWord", 4);
        userSetting.put("minLength", 2);
        userSetting.put("maxLength", 7);
        return userSetting;
    }

    public static Map<String, Object> getCharNGramSetting(){
        Map<String, Object> userSetting = new HashMap<>();
        userSetting.put("module", "ngram");
        userSetting.put("mode", "char");
        userSetting.put("stop", false);
        userSetting.put("minWord", 1);
        userSetting.put("maxWord", 4);
        userSetting.put("minLength", 2);
        userSetting.put("maxLength", 7);
        return userSetting;
    }
}
