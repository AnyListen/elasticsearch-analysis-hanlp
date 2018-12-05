package org.elasticsearch.plugin.hanlp.analysis;


import com.hankcs.hanlp.seg.Segment;
import org.apache.lucene.analysis.Tokenizer;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractTokenizerFactory;
import org.elasticsearch.plugin.hanlp.conf.ConfigHelper;
import org.elasticsearch.plugin.hanlp.conf.DicConfig;
import org.elasticsearch.plugin.hanlp.conf.HanLPConfig;
import org.elasticsearch.plugin.hanlp.lucene.HanLPTokenizer;

import java.util.Set;

/**
 * es-analysis-hanlp
 * net.luculent.bigdata.es.plugin.hanlp.analysis
 * Created by HEZHILONG on 2018-08-23.
 */
public class HanLPTokenizerFactory extends AbstractTokenizerFactory {
    private Tuple<Segment, Set<String>> tuple;

    public HanLPTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);
        DicConfig.initConfig(env, settings);
        tuple = ConfigHelper.getSegmentAndFilter(settings);
    }

    public HanLPTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings, HanLPConfig config) {
        super(indexSettings, name, settings);
        DicConfig.initConfig(env, settings);
        tuple = Tuple.tuple(ConfigHelper.getSegment(config), ConfigHelper.getStopWords(config));
    }

    public static HanLPTokenizerFactory getIndexTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new HanLPTokenizerFactory(indexSettings, env, name, settings, ConfigHelper.INDEX_CONFIG);
    }

    public static HanLPTokenizerFactory getNLPTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new HanLPTokenizerFactory(indexSettings, env, name, settings, ConfigHelper.NLP_CONFIG);
    }

    public static HanLPTokenizerFactory getSmartTokenizerFactory(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new HanLPTokenizerFactory(indexSettings, env, name, settings, ConfigHelper.SMART_CONFIG);
    }

    @Override
    public Tokenizer create() {
        return new HanLPTokenizer(tuple.v1(), tuple.v2(), true);
    }
}
