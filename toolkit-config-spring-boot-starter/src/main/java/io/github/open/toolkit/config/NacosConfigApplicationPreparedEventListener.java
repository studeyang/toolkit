package io.github.open.toolkit.config;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySources;
import com.alibaba.spring.util.PropertyValuesUtils;
import com.google.common.collect.Lists;
import io.github.open.toolkit.config.extension.ExtensionConfigsBeanDefinition;
import io.github.open.toolkit.config.extension.ExtensionConfigsNacosPropertySourceBuilder;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.alibaba.nacos.spring.util.NacosBeanUtils.registerInfrastructureBeanIfAbsent;

public class NacosConfigApplicationPreparedEventListener implements ApplicationListener<ApplicationPreparedEvent> {

    private static final String CASS_NACOS_PREFIX = "cass.nacos";
    private static final String CASS_NACOS_EXTENSION_CONFIG_TIPS = "extensionConfigs";

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {

        ConfigurableApplicationContext context = event.getApplicationContext();

        if (context.isActive()) {
            return;
        }

        if (AnnotationConfigApplicationContext.class.equals(context.getClass())) {
            return;
        }

        ConfigurableEnvironment environment = context.getEnvironment();

        // YamlConfigParseSupport 需要知道当前环境的 Profiles
        MultiProfilesYamlConfigParseSupport.initSpringProfiles(
                environment.getActiveProfiles(),
                environment.getDefaultProfiles());

        //通过配置文件生成 NacosPropertySource
        List<ExtensionConfigsBeanDefinition.Config> extensionConfigs = getExtensionConfigs(environment, CASS_NACOS_PREFIX);
        if (null != extensionConfigs && !extensionConfigs.isEmpty()) {

            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context.getBeanFactory();

            registry.registerBeanDefinition(ExtensionConfigsBeanDefinition.BEAN_DEFINITION_NAME,
                    new ExtensionConfigsBeanDefinition(extensionConfigs));

            registerInfrastructureBeanIfAbsent(registry,
                    ExtensionConfigsNacosPropertySourceBuilder.BEAN_NAME,
                    ExtensionConfigsNacosPropertySourceBuilder.class);
        }

        //通过注解扫描生成 NacosPropertySource
        this.doScanNacosPropertySourceAnnotation(event, context);

        context.addBeanFactoryPostProcessor(new NacosConfigBeanDefinitionRegistryPostProcessor(context));

    }

    private int doScanNacosPropertySourceAnnotation(SpringApplicationEvent event, ApplicationContext context) {

        Set<String> basePackages = new LinkedHashSet<String>();
        Class<?> mainClass = event.getSpringApplication().getMainApplicationClass();
        AnnotationMetadata mainClassMetadata = new StandardAnnotationMetadata(mainClass, true);
        Map<String, Object> attributesMap =  mainClassMetadata.getAnnotationAttributes(ComponentScan.class.getName(), false);
        if (null != attributesMap) {
            AnnotationAttributes attributes = AnnotationAttributes.fromMap(attributesMap);

            String[] basePackagesArray = attributes.getStringArray("basePackages");
            for (String pkg : basePackagesArray) {
                String[] tokenized = StringUtils.tokenizeToStringArray(context.getEnvironment().resolvePlaceholders(pkg),
                        ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
                basePackages.addAll(Arrays.asList(tokenized));
            }
            for (Class<?> clazz : attributes.getClassArray("basePackageClasses")) {
                basePackages.add(ClassUtils.getPackageName(clazz));
            }
        }

        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(mainClass.getName()));
        }

        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) context);
        scanner.setIncludeAnnotationConfig(false);
        scanner.resetFilters(false);
        scanner.addExcludeFilter((metadataReader, metadataReaderFactory) -> {
            AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();
            return !metadata.hasAnnotation(Configuration.class.getName());
        });
        scanner.addIncludeFilter(new AnnotationTypeFilter(NacosPropertySource.class));
        scanner.addIncludeFilter(new AnnotationTypeFilter(NacosPropertySources.class));

        int scanResult = 0;
        for (String basePackage:basePackages) {
            scanResult += scanner.scan(basePackage);
        }
        return scanResult;
    }

    private List<ExtensionConfigsBeanDefinition.Config> getExtensionConfigs(ConfigurableEnvironment environment, String prefix) {
        PropertyValues values = PropertyValuesUtils.getSubPropertyValues(environment, prefix);
        if (values.isEmpty()) {
            return Collections.emptyList();
        }

        int i = 0;
        List<ExtensionConfigsBeanDefinition.Config> configs = Lists.newArrayList();
        ExtensionConfigsBeanDefinition.Config config = new ExtensionConfigsBeanDefinition.Config();
        for (PropertyValue pv : values.getPropertyValues()) {

            if (!pv.getName().startsWith(CASS_NACOS_EXTENSION_CONFIG_TIPS + "[" + i + "]")) {
                configs.add(config);
                config = new ExtensionConfigsBeanDefinition.Config();
                i++;
            }

            if (pv.getName().endsWith("." + NacosPropertySource.NAME_ATTRIBUTE_NAME)) {
                config.setName(pv.getValue().toString());
            }
            if (pv.getName().endsWith("." + NacosPropertySource.CONFIG_TYPE_ATTRIBUTE_NAME)) {
                config.setType(pv.getValue().toString());
            }
            if (pv.getName().endsWith("." + NacosPropertySource.DATA_ID_ATTRIBUTE_NAME)) {
                config.setDataId(pv.getValue().toString());
            }
            if (pv.getName().endsWith("." + NacosPropertySource.GROUP_ID_ATTRIBUTE_NAME)) {
                config.setGroupId(pv.getValue().toString());
            }
            if (pv.getName().endsWith("." + NacosPropertySource.AUTO_REFRESHED_ATTRIBUTE_NAME)) {
                config.setAutoRefreshed(pv.getValue().toString());
            }
            if (pv.getName().endsWith("." + NacosPropertySource.FIRST_ATTRIBUTE_NAME)) {
                config.setFirst(pv.getValue().toString());
            }
            if (pv.getName().endsWith("." + NacosPropertySource.BEFORE_ATTRIBUTE_NAME)) {
                config.setBefore(pv.getValue().toString());
            }
            if (pv.getName().endsWith("." + NacosPropertySource.AFTER_ATTRIBUTE_NAME)) {
                config.setAfter(pv.getValue().toString());
            }
        }
        configs.add(config);

        return configs.stream().filter(c -> {
            //忽略掉已经从本地文件中导入的配置
            String key = c.getGroupId() + "." + c.getDataId();
            for (PropertySource<?> propertySource : environment.getPropertySources()) {
                if (propertySource.getName().contains(key)) {
                    return false;
                }
            }
            return true;
        }).collect(Collectors.toList());
    }
}
