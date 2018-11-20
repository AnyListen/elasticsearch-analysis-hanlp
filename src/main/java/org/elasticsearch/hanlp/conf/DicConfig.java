package org.elasticsearch.hanlp.conf;

import com.hankcs.hanlp.utility.Predefine;
import com.hankcs.hanlp.utility.TextUtility;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.io.PathUtils;
import org.elasticsearch.common.logging.ESLoggerFactory;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.plugin.AnalysisHanLPPlugin;

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
    private final Logger logger = ESLoggerFactory.getLogger(DicConfig.class);
    private Environment env;
    private Settings settings;
    private String configPath;
    private String remoteDicUrl;

    @Inject
    public DicConfig(Environment env, Settings settings){
        this.env = env;
        this.settings = settings;
        initConfig();
    }

    /**
     * 根据配置文件
     * 初始化词典以及远程更新配置
     */
    private void initConfig() {
        File configFile = getConfigFilePath().toFile();
        if (!configFile.exists()){
            return;
        }
        Properties properties = new Properties();
        try(InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(configFile))){
            properties.load(inputStreamReader);
            this.configPath = properties.getProperty("configPath", null);
            this.remoteDicUrl = properties.getProperty("remoteDicUrl", "");
            if (TextUtility.isBlank(this.configPath)){
                if (getDefDicConfigPath().toFile().exists()){
                    this.configPath = getPluginPath().toString();
                }
            }
            if (TextUtility.isBlank(this.configPath)){
                this.configPath = null;
            }
            Predefine.HANLP_PROPERTIES_PATH = this.configPath;
            //todo 远程更新
        }
        catch (Exception ex){
            logger.error(ex);
        }
        finally {
            properties.clear();
        }
    }

    private Path getPluginPath(){
        return PathUtils.get(new File(AnalysisHanLPPlugin.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent()).toAbsolutePath();
    }

    private Path getDefDicConfigPath() {
        return PathUtils.get(getPluginPath().toString(), "hanlp.properties").toAbsolutePath();
    }

    private Path getConfigFilePath() {
        return PathUtils.get(getPluginPath().toString(), "plugin.properties").toAbsolutePath();
    }

    public Environment getEnv() {
        return env;
    }

    public void setEnv(Environment env) {
        this.env = env;
    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public String getConfigPath() {
        return configPath;
    }

    public void setConfigPath(String configPath) {
        this.configPath = configPath;
    }

    public String getRemoteDicUrl() {
        return remoteDicUrl;
    }

    public void setRemoteDicUrl(String remoteDicUrl) {
        this.remoteDicUrl = remoteDicUrl;
    }
}
