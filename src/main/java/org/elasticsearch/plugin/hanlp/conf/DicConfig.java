package org.elasticsearch.plugin.hanlp.conf;


import com.hankcs.hanlp.utility.Predefine;
import com.hankcs.hanlp.utility.TextUtility;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.logging.Loggers;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Properties;

/**
 * elasticsearch-analysis-hanlp
 * elasticsearch-analysis-hanlp
 * Created by hezl on 2018-11-20.
 */
public class DicConfig {
    private static final Logger logger = Loggers.getLogger(DicConfig.class, "DicConfig");
    private static Environment env;
    private static Settings settings;
    private static String configPath;
    private static String remoteDicUrl;
    private static boolean isInit;

    /**
     * 根据配置文件
     * 初始化词典以及远程更新配置
     */
    public static synchronized void initConfig(Environment env, Settings settings) {
        if (isInit) {
            return;
        }
        DicConfig.env = env;
        DicConfig.settings = settings;
        File configFile = getConfigFilePath().toFile();
        if (!configFile.exists()) {
            return;
        }
        Properties properties = new Properties();
        try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(configFile))) {
            properties.load(inputStreamReader);
            configPath = properties.getProperty("configPath", null);
            remoteDicUrl = properties.getProperty("remoteDicUrl", "");
            if (TextUtility.isBlank(configPath)) {
                if (getDefDicConfigPath().toFile().exists()) {
                    configPath = getDefDicConfigPath().toAbsolutePath().toString();
                    Properties cfProp = new Properties();
                    FileInputStream inputStream = new FileInputStream(configPath);
                    cfProp.load(inputStream);
                    if (!cfProp.containsKey("root")){
                        configPath = null;
                    }
                    inputStream.close();
                    cfProp.clear();
                }
            }
            if (TextUtility.isBlank(configPath)) {
                configPath = null;
            }
            Predefine.HANLP_PROPERTIES_PATH = configPath;
            logger.info("HanLP Properties Path: " + Predefine.HANLP_PROPERTIES_PATH);
            //todo 远程更新
        } catch (Exception ex) {
            logger.error(ex);
        } finally {
            properties.clear();
        }
        isInit = true;
    }

    private static Path getPluginPath() {
        return env.pluginsFile().resolve("analysis-hanlp");
    }

    private static Path getDefDicConfigPath() {
        return env.pluginsFile().resolve("analysis-hanlp/hanlp.properties").toAbsolutePath();
    }

    private static Path getConfigFilePath() {
        return env.pluginsFile().resolve("analysis-hanlp/plugin.properties").toAbsolutePath();
    }
}
