package org.elasticsearch.plugin.hanlp.conf;

import com.hankcs.hanlp.HanLP;
import org.junit.Test;

/**
 * elasticsearch-analysis-hanlp
 * elasticsearch-analysis-hanlp
 * Created by hezl on 2018-12-05.
 */
public class ConfigHelperTest {

    @Test
    public void getConfig() {
        System.out.println(HanLP.segment("你和对方但是"));
    }
}