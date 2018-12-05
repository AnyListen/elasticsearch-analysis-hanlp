package org.elasticsearch.plugin.hanlp.conf;


import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.io.IOUtil;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.Viterbi.ViterbiSegment;
import com.hankcs.hanlp.utility.TextUtility;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * elasticsearch-analysis-hanlp
 * elasticsearch-analysis-hanlp
 * Created by hezl on 2018-12-03.
 */
public class ConfigHelper {
    private static final Logger logger = Loggers.getLogger(ConfigHelper.class, "*");

    public static final HanLPConfig INDEX_CONFIG = new HanLPConfig(){{
        setAlgorithm("viterbi");
        setEnableIndexMode(true);
        //CustomDic
        setCustomDictionaryPath("");
        setEnableCustomDictionary(true);
        setEnableCustomDictionaryForcing(false);
        //StopWord
        setEnableStopWord(false);
        setStopWordDictionaryPath("");
        //NLP
        setEnableNameRecognize(true);
        setEnableJapaneseNameRecognize(false);
        setEnableTranslatedNameRecognize(false);
        setEnableNumberQuantifierRecognize(true);
        setEnableOrganizationRecognize(false);
        setEnablePlaceRecognize(false);
        setEnableTraditionalChineseMode(false);
    }};

    public static final HanLPConfig SMART_CONFIG = new HanLPConfig(){{
        setAlgorithm("viterbi");
        setEnableIndexMode(false);
        //CustomDic
        setCustomDictionaryPath("");
        setEnableCustomDictionary(true);
        setEnableCustomDictionaryForcing(false);
        //StopWord
        setEnableStopWord(false);
        setStopWordDictionaryPath("");
        //NLP
        setEnableNameRecognize(true);
        setEnableJapaneseNameRecognize(false);
        setEnableTranslatedNameRecognize(false);
        setEnableNumberQuantifierRecognize(true);
        setEnableOrganizationRecognize(false);
        setEnablePlaceRecognize(false);
        setEnableTraditionalChineseMode(false);
    }};

    public static final HanLPConfig NLP_CONFIG = new HanLPConfig(){{
        setAlgorithm("viterbi");
        setEnableIndexMode(false);
        //CustomDic
        setCustomDictionaryPath("");
        setEnableCustomDictionary(true);
        setEnableCustomDictionaryForcing(false);
        //StopWord
        setEnableStopWord(false);
        setStopWordDictionaryPath("");
        //NLP
        setEnableNameRecognize(true);
        setEnableJapaneseNameRecognize(true);
        setEnableTranslatedNameRecognize(true);
        setEnableNumberQuantifierRecognize(true);
        setEnableOrganizationRecognize(true);
        setEnablePlaceRecognize(true);
        setEnableTraditionalChineseMode(false);
    }};

    public static Segment getSegment(HanLPConfig config){
        Segment segment;
        if (config.getAlgorithm().equals("extend")){
            segment = new ViterbiSegment();
        }
        else{
            segment = HanLP.newSegment(config.getAlgorithm());
        }
        segment.enableIndexMode(config.isEnableIndexMode())
                .enableCustomDictionary(config.isEnableCustomDictionary())
                .enableCustomDictionaryForcing(config.isEnableCustomDictionaryForcing())
                .enableAllNamedEntityRecognize(config.isEnableNameRecognize())
                .enableJapaneseNameRecognize(config.isEnableJapaneseNameRecognize())
                .enableTranslatedNameRecognize(config.isEnableTranslatedNameRecognize())
                .enableNumberQuantifierRecognize(config.isEnableNumberQuantifierRecognize())
                .enableOrganizationRecognize(config.isEnableOrganizationRecognize())
                .enablePlaceRecognize(config.isEnablePlaceRecognize())
                .enableTranslatedNameRecognize(config.isEnableTraditionalChineseMode())
                .enableOffset(true).enablePartOfSpeechTagging(true);
        return segment;
    }

    public static Set<String> getStopWords(HanLPConfig config){
        if (!config.isEnableStopWord()){
            return null;
        }
        String filePath = config.getStopWordDictionaryPath();
        if (TextUtility.isBlank(filePath)){
            filePath = HanLP.Config.CoreStopWordDictionaryPath;
        }
        try{
            byte[] bytes;
            if (IOUtil.isResource(filePath)){
                bytes = IOUtil.readBytesFromResource(filePath);
            }
            else{
                bytes = IOUtil.readBytes(filePath);
            }
            Set<String> resultSet = new HashSet<>();
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            InputStreamReader reader = new InputStreamReader(byteArrayInputStream);
            BufferedReader br = new BufferedReader(reader);
            String str;
            while((str = br.readLine()) != null) {
                resultSet.add(str);
            }
            br.close();
            reader.close();
            byteArrayInputStream.close();
            return resultSet;
        }
        catch (Exception ex){
            logger.error(ex);
        }
        return null;
    }

    public static HanLPConfig getConfig(Settings settings){
        HanLPConfig config = new HanLPConfig();
        config.setAlgorithm(settings.get("algorithm", "viterbi"));
        config.setEnableIndexMode(settings.getAsBoolean("enableIndexMode", false));
        //CustomDic
        config.setCustomDictionaryPath(settings.get("customDictionaryPath", ""));
        config.setEnableCustomDictionary(settings.getAsBoolean("enableCustomDictionary", true));
        config.setEnableCustomDictionaryForcing(settings.getAsBoolean("enableCustomDictionaryForcing", false));
        //StopWord
        config.setEnableStopWord(settings.getAsBoolean("enableStopWord", false));
        config.setStopWordDictionaryPath(settings.get("stopWordDictionaryPath", ""));
        //NLP
        config.setEnableNameRecognize(settings.getAsBoolean("enableNameRecognize", true));
        config.setEnableJapaneseNameRecognize(settings.getAsBoolean("enableJapaneseNameRecognize", false));
        config.setEnableTranslatedNameRecognize(settings.getAsBoolean("enableTranslatedNameRecognize", false));
        config.setEnableNumberQuantifierRecognize(settings.getAsBoolean("enableNumberQuantifierRecognize", true));
        config.setEnableOrganizationRecognize(settings.getAsBoolean("enableOrganizationRecognize", false));
        config.setEnablePlaceRecognize(settings.getAsBoolean("enablePlaceRecognize", false));
        config.setEnableTraditionalChineseMode(settings.getAsBoolean("enableTraditionalChineseMode", false));

        return config;
    }

    public static Tuple<Segment, Set<String>> getSegmentAndFilter(Settings settings) {
        HanLPConfig config = getConfig(settings);
        return Tuple.tuple(getSegment(config), getStopWords(config));
    }
}

