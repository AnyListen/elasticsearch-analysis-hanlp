package org.elasticsearch.plugin.hanlp.analysis;


import com.hankcs.hanlp.seg.Segment;
import org.elasticsearch.common.collect.Tuple;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;
import org.elasticsearch.index.analysis.AbstractIndexAnalyzerProvider;
import org.elasticsearch.plugin.hanlp.conf.ConfigHelper;
import org.elasticsearch.plugin.hanlp.conf.DicConfig;
import org.elasticsearch.plugin.hanlp.conf.HanLPConfig;
import org.elasticsearch.plugin.hanlp.lucene.HanLPAnalyzer;

import java.util.Set;

/**
 * es-analysis-hanlp
 * net.luculent.bigdata.es.plugin.hanlp.analysis
 * Created by HEZHILONG on 2018-08-23.
 */
public class HanLPAnalyzerProvider extends AbstractIndexAnalyzerProvider<HanLPAnalyzer> {
    private final HanLPAnalyzer analyzer;

    public HanLPAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        super(indexSettings, name, settings);
        DicConfig.initConfig(env, settings);
        Tuple<Segment, Set<String>> tuple = ConfigHelper.getSegmentAndFilter(settings);
        analyzer = new HanLPAnalyzer(tuple.v1(), tuple.v2());
    }

    public HanLPAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings, HanLPConfig config) {
        super(indexSettings, name, settings);
        DicConfig.initConfig(env, settings);
        analyzer = new HanLPAnalyzer(ConfigHelper.getSegment(config), ConfigHelper.getStopWords(config));
    }

    public static HanLPAnalyzerProvider getIndexAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, ConfigHelper.INDEX_CONFIG);
    }

    public static HanLPAnalyzerProvider getSmartAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, ConfigHelper.SMART_CONFIG);
    }

    public static HanLPAnalyzerProvider getNLPAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, ConfigHelper.NLP_CONFIG);
    }

    public static HanLPAnalyzerProvider getPerceptronAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, ConfigHelper.PERCEPTRON_CONFIG);
    }

    public static HanLPAnalyzerProvider getCRFAnalyzerProvider(IndexSettings indexSettings, Environment env, String name, Settings settings) {
        return new HanLPAnalyzerProvider(indexSettings, env, name, settings, ConfigHelper.CRF_CONFIG);
    }

    @Override
    public HanLPAnalyzer get() {
        return this.analyzer;
    }
}
