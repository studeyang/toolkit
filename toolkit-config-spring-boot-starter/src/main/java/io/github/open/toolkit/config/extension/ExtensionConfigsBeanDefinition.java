package io.github.open.toolkit.config.extension;

import org.springframework.beans.factory.support.GenericBeanDefinition;

import java.util.List;

public class ExtensionConfigsBeanDefinition extends GenericBeanDefinition {

    public static final String BEAN_DEFINITION_NAME = "extensionConfigsBeanDefinition";

    private List<Config> configList;

    public ExtensionConfigsBeanDefinition(List<Config> configList) {
        this.configList = configList;
        setBeanClass(getClass());
    }

    public List<Config> getConfigs() {
        return this.configList;
    }

    public static class Config {

        private String name;
        private String type;
        private String dataId;
        private String groupId;
        private String autoRefreshed;
        private String first;
        private String before;
        private String after;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDataId() {
            return dataId;
        }

        public void setDataId(String dataId) {
            this.dataId = dataId;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getAutoRefreshed() {
            return autoRefreshed;
        }

        public void setAutoRefreshed(String autoRefreshed) {
            this.autoRefreshed = autoRefreshed;
        }

        public String getFirst() {
            return first;
        }

        public void setFirst(String first) {
            this.first = first;
        }

        public String getBefore() {
            return before;
        }

        public void setBefore(String before) {
            this.before = before;
        }

        public String getAfter() {
            return after;
        }

        public void setAfter(String after) {
            this.after = after;
        }
    }
}
