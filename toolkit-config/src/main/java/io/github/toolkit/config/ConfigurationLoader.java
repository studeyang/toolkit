package io.github.toolkit.config;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import io.github.toolkit.config.annotation.PrepareConfigurations;
import io.github.toolkit.config.utils.NetworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.cloud.config.client.ConfigClientProperties;
import org.springframework.cloud.config.client.ConfigServicePropertySourceLocator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.env.*;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author infra
 */
@Configuration
public class ConfigurationLoader implements ImportSelector, EnvironmentAware, BeanFactoryAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigurationLoader.class);
    private static final String IP_ADDRESS_PROPERTY_NAME = "server.ipAddress";

    private ConfigurableEnvironment environment;
    private BeanFactory beanFactory;

    @Override
    public void setEnvironment(Environment environment) {
        Assert.isInstanceOf(ConfigurableEnvironment.class, environment);
        this.environment = (ConfigurableEnvironment) environment;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        ConfigCenterType configCenterType = resolve();

        if (configCenterType == ConfigCenterType.CloudConfig) {
            prepareConfigFromSpringCloudConfig(importingClassMetadata);

        } else if (configCenterType == ConfigCenterType.Nacos) {

            try {
                prepareConfigFromNacos(importingClassMetadata);
            } catch (NacosException e) {
                LOGGER.error("prepareConfigFromNacos Fail", e);
            }

        } else if (configCenterType == ConfigCenterType.Panda) {
            LOGGER.info("识别当前使用 PANDA 配置中心, 忽略 @PrepareConfigurations 配置附加!");
        } else {
            LOGGER.warn("未识别到配置中心！");
        }

        return new String[]{};
    }

    private void prepareConfigFromNacos(AnnotationMetadata importingClassMetadata) throws NacosException {
        // 初始化 nacos 连接
        String serverAddr = environment.getProperty("nacos.config.server-addr");
        String namespace = environment.getProperty("nacos.config.namespace");
        String username = environment.getProperty("nacos.config.username");
        String password = environment.getProperty("nacos.config.password");
        if (serverAddr == null || namespace == null) {
            LOGGER.warn("未识别到Nacos配置, nacos.config.server-addr={}, nacos.config.namespace={}",
                    serverAddr, namespace);
            return;
        }
        Properties nacosConnectProperties = new Properties();
        if (username != null && !username.isEmpty()) {
            nacosConnectProperties.put("username", username);
        }
        if (password != null && !password.isEmpty()) {
            nacosConnectProperties.put("password", password);
        }
        nacosConnectProperties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        nacosConnectProperties.put(PropertyKeyConst.NAMESPACE, namespace);
        ConfigService configService = NacosFactory.createConfigService(nacosConnectProperties);

        // 加载公共配置
        Map<String, Object> attrs = importingClassMetadata.getAnnotationAttributes(PrepareConfigurations.class.getName());
        String[] names = (String[]) attrs.get("value");
        for (String configName : names) {
            String content = configService.getConfig(configName, "commons", 3000);
            if (content == null || content.isEmpty()) {
                LOGGER.error("{} 读取失败", configName);
                continue;
            }

            YamlPropertiesFactoryBean yamlFactory = new MultiProfilesYamlPropertiesFactoryBean(environment);
            yamlFactory.setResources(new ByteArrayResource(content.getBytes()));
            Properties commonsProperties = yamlFactory.getObject();
            PropertySource<?> propertySource = new MapPropertySource(configName, propertiesToMap(commonsProperties));
            environment.getPropertySources().addLast(propertySource);
            LOGGER.info("{} 附加完成[{}]", configName, "Nacos");
        }
    }

    private ConfigCenterType resolve() {
        try {
            Class.forName("org.springframework.cloud.config.client.ConfigServicePropertySourceLocator");
            return ConfigCenterType.CloudConfig;
        } catch (ClassNotFoundException ignore) {
        }

        try {
            Class.forName("io.github.open.toolkit.config.extension.ExtensionConfigsBeanDefinition");
            return ConfigCenterType.Panda;
        } catch (ClassNotFoundException ignore) {
        }

        try {
            Class.forName("com.alibaba.nacos.api.config.ConfigService");
            return ConfigCenterType.Nacos;
        } catch (ClassNotFoundException ignore) {
        }
        return ConfigCenterType.None;
    }

    private void prepareConfigFromSpringCloudConfig(AnnotationMetadata importingClassMetadata) {
        ConfigServicePropertySourceLocator locator = beanFactory.getBean(ConfigServicePropertySourceLocator.class);

        //先准备一些必要的系统变量
        String ipAddress = System.getProperty(IP_ADDRESS_PROPERTY_NAME);
        if (ipAddress == null || ipAddress.isEmpty()) {
            System.setProperty(IP_ADDRESS_PROPERTY_NAME, NetworkUtils.getServerIPv4());
        }

        String nameKey = ConfigClientProperties.PREFIX + ".name";
        String oldName = System.getProperty(nameKey);

        // 加载公共配置
        Map<String, Object> attrs = importingClassMetadata.getAnnotationAttributes(PrepareConfigurations.class.getName());
        String[] names = (String[]) attrs.get("value");
        for (String configName : names) {
            System.setProperty(nameKey, configName);
            CompositePropertySource result = (CompositePropertySource) locator.locate(this.environment);
            if (null != result) {
                for (PropertySource<?> propertySource : result.getPropertySources()) {
                    environment.getPropertySources().addLast(propertySource);
                }
                LOGGER.info("{} 附加完成[{}]", configName, "Config");
            }
        }

        if (oldName == null || oldName.isEmpty()) {
            System.clearProperty(nameKey);
        } else {
            System.setProperty(nameKey, oldName);
        }
    }

    private Map<String, Object> propertiesToMap(Properties properties) {
        Map<String, Object> result = new HashMap<>(16);
        Enumeration<String> keys = (Enumeration<String>) properties.propertyNames();
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();
            String value = properties.getProperty(key);
            if (value != null) {
                result.put(key, value.trim());
            } else {
                result.put(key, null);
            }
        }
        return result;
    }

    enum ConfigCenterType {
        CloudConfig, Nacos, Panda, None
    }

}
