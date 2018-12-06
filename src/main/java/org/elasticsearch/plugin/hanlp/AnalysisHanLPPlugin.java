package org.elasticsearch.plugin.hanlp;


import org.apache.lucene.analysis.Analyzer;
import org.elasticsearch.index.analysis.AnalyzerProvider;
import org.elasticsearch.index.analysis.TokenizerFactory;
import org.elasticsearch.indices.analysis.AnalysisModule;
import org.elasticsearch.plugin.hanlp.analysis.HanLPAnalyzerProvider;
import org.elasticsearch.plugin.hanlp.analysis.HanLPTokenizerFactory;
import org.elasticsearch.plugins.AnalysisPlugin;
import org.elasticsearch.plugins.Plugin;

import java.util.HashMap;
import java.util.Map;

/**
 * elasticsearch-analysis-hanlp
 * elasticsearch-analysis-hanlp
 * Created by hezl on 2018-11-20.
 */
public class AnalysisHanLPPlugin extends Plugin implements AnalysisPlugin {
    public static String PLUGIN_NAME = "analysis-hanlp";

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> getTokenizers() {
        Map<String, AnalysisModule.AnalysisProvider<TokenizerFactory>> extra = new HashMap<>();
        extra.put("hanlp_index", HanLPTokenizerFactory::getIndexTokenizerFactory);
        extra.put("hanlp_smart", HanLPTokenizerFactory::getSmartTokenizerFactory);
        extra.put("hanlp_nlp", HanLPTokenizerFactory::getNLPTokenizerFactory);
        extra.put("hanlp_per", HanLPTokenizerFactory::getPerceptronTokenizerFactory);
        extra.put("hanlp_crf", HanLPTokenizerFactory::getCRFTokenizerFactory);
        extra.put("hanlp", HanLPTokenizerFactory::new);
        return extra;
    }

    @Override
    public Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> getAnalyzers() {
        Map<String, AnalysisModule.AnalysisProvider<AnalyzerProvider<? extends Analyzer>>> extra = new HashMap<>();
        extra.put("hanlp_index", HanLPAnalyzerProvider::getIndexAnalyzerProvider);
        extra.put("hanlp_smart", HanLPAnalyzerProvider::getSmartAnalyzerProvider);
        extra.put("hanlp_nlp", HanLPAnalyzerProvider::getNLPAnalyzerProvider);
        extra.put("hanlp_per", HanLPAnalyzerProvider::getPerceptronAnalyzerProvider);
        extra.put("hanlp_crf", HanLPAnalyzerProvider::getCRFAnalyzerProvider);
        extra.put("hanlp", HanLPAnalyzerProvider::new);
        return extra;
    }

//    @Override
//    public Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> getTokenFilters() {
//        Map<String, AnalysisModule.AnalysisProvider<TokenFilterFactory>> extra = new HashMap<>();
//
//        extra.put("py_all", HanLPTokenFilterFactory::getPinyinFilterFactory);
//        extra.put("py_mix", HanLPTokenFilterFactory::getPinyinMixFilterFactory);
//        extra.put("py_first", HanLPTokenFilterFactory::getPinyinFirstFilterFactory);
//        extra.put("py_full", HanLPTokenFilterFactory::getPinyinFullFilterFactory);
//
//        extra.put("ts", HanLPTokenFilterFactory::getTSFilterFactory);
//        extra.put("t2s", HanLPTokenFilterFactory::getT2SFilterFactory);
//        extra.put("s2t", HanLPTokenFilterFactory::getS2TFilterFactory);
//
//        return extra;
//    }
}
