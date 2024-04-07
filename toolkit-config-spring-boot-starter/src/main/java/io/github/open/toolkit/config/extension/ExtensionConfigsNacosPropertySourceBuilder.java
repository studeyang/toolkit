package io.github.open.toolkit.config.extension;

import com.alibaba.nacos.api.config.ConfigType;
import com.alibaba.nacos.spring.context.event.config.NacosConfigMetadataEvent;
import com.alibaba.nacos.spring.core.env.AbstractNacosPropertySourceBuilder;
import com.alibaba.nacos.spring.core.env.NacosPropertySource;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import static com.alibaba.nacos.api.common.Constants.DEFAULT_GROUP;
import static com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource.*;
import static com.alibaba.nacos.spring.util.NacosUtils.*;

public class ExtensionConfigsNacosPropertySourceBuilder extends AbstractNacosPropertySourceBuilder<ExtensionConfigsBeanDefinition> {

    public static final String BEAN_NAME = "extensionConfigsNacosPropertySourceBuilder";

    @Override
    protected Map<String, Object>[] resolveRuntimeAttributesArray(ExtensionConfigsBeanDefinition beanDefinition, Properties globalNacosProperties) {

        List<ExtensionConfigsBeanDefinition.Config> configList = beanDefinition.getConfigs();

        Map<String, Object>[] result = new Map[configList.size()];

        for (int i = 0; i < configList.size(); i++) {
            ExtensionConfigsBeanDefinition.Config config = configList.get(i);
            Map<String, Object> runtimeAttributes = new HashMap<String, Object>(4);
            // Nacos Metadata
            runtimeAttributes.put(DATA_ID_ATTRIBUTE_NAME, resolveValue(config.getDataId(), DEFAULT_STRING_ATTRIBUTE_VALUE));
            runtimeAttributes.put(GROUP_ID_ATTRIBUTE_NAME, resolveValue(config.getGroupId(), DEFAULT_GROUP));
            // PropertySource Name
            runtimeAttributes.put(NAME_ATTRIBUTE_NAME, resolveValue(config.getName(), DEFAULT_STRING_ATTRIBUTE_VALUE));
            // auto-refresh
            runtimeAttributes.put(AUTO_REFRESHED_ATTRIBUTE_NAME, resolveValue(config.getAutoRefreshed(), DEFAULT_BOOLEAN_ATTRIBUTE_VALUE));
            // is first order
            runtimeAttributes.put(FIRST_ATTRIBUTE_NAME, resolveValue(config.getFirst(), DEFAULT_BOOLEAN_ATTRIBUTE_VALUE));
            // The relative order before specified
            runtimeAttributes.put(BEFORE_ATTRIBUTE_NAME, resolveValue(config.getBefore(), DEFAULT_STRING_ATTRIBUTE_VALUE));
            // The relative order after specified
            runtimeAttributes.put(AFTER_ATTRIBUTE_NAME, resolveValue(config.getAfter(), "systemEnvironment"));
            // Config type
            String type = resolveValue(config.getType(), ConfigType.UNSET.getType());

            try {
                runtimeAttributes.put(CONFIG_TYPE_ATTRIBUTE_NAME, ConfigType.valueOf(type.toUpperCase()));
                //return new Map[]{runtimeAttributes};
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(
                        "Now the config type just support [properties, json, yaml, xml, text, html]");
            }

            // TODO support nested properties
            runtimeAttributes.put(PROPERTIES_ATTRIBUTE_NAME, new Properties());

            result[i] = runtimeAttributes;
        }

        return result;
    }

    @Override
    protected void initNacosPropertySource(NacosPropertySource nacosPropertySource, ExtensionConfigsBeanDefinition beanDefinition,
                                           Map<String, Object> attributes) {

        // Origin
        nacosPropertySource.setOrigin(beanDefinition.getConfigs());
        // AttributesMetadata
        nacosPropertySource.setAttributesMetadata(attributes);

        // Auto-Refreshed
        initAutoRefreshed(nacosPropertySource, attributes);

        // Order
        initOrder(nacosPropertySource, attributes);

    }

    private void initAutoRefreshed(NacosPropertySource nacosPropertySource, Map<String, Object> attributes) {
        boolean autoRefreshed = getAttribute(attributes, AUTO_REFRESHED_ATTRIBUTE_NAME, DEFAULT_BOOLEAN_ATTRIBUTE_VALUE);
        nacosPropertySource.setAutoRefreshed(autoRefreshed);
    }

    private void initOrder(NacosPropertySource nacosPropertySource, Map<String, Object> attributes) {
        boolean first = getAttribute(attributes, FIRST_ATTRIBUTE_NAME,
                DEFAULT_BOOLEAN_ATTRIBUTE_VALUE);
        String before = getAttribute(attributes, BEFORE_ATTRIBUTE_NAME,
                DEFAULT_STRING_ATTRIBUTE_VALUE);
        String after = getAttribute(attributes, AFTER_ATTRIBUTE_NAME,
                DEFAULT_STRING_ATTRIBUTE_VALUE);
        nacosPropertySource.setFirst(first);
        nacosPropertySource.setBefore(before);
        nacosPropertySource.setAfter(after);
    }

    private <T> T getAttribute(Map<String, Object> attributes, String name, T defaultValue) {
        if (attributes.containsKey(name)) {
            Object valueObj = attributes.get(name);
            if (valueObj instanceof String) {
                return resolveValue((String) valueObj, defaultValue);
            } else {
                return (T) valueObj;
            }
        } else {
            return defaultValue;
        }
    }

    private <T> T resolveValue(String value, T defaultValue) {
        if (null == value) {
            return defaultValue;
        }
        ConversionService conversionService = environment.getConversionService();
        String resolvedValue = environment.resolvePlaceholders(value);
        T attributeValue = StringUtils.hasText(resolvedValue)
                ? (T) conversionService.convert(resolvedValue, defaultValue.getClass())
                : defaultValue;
        return attributeValue;
    }

    @Override
    protected NacosConfigMetadataEvent createMetaEvent(NacosPropertySource nacosPropertySource, ExtensionConfigsBeanDefinition beanDefinition) {
        return new NacosConfigMetadataEvent(beanDefinition.getConfigs());
    }

    @Override
    protected void doInitMetadataEvent(NacosPropertySource nacosPropertySource, ExtensionConfigsBeanDefinition beanDefinition, NacosConfigMetadataEvent metadataEvent) {

    }
}
