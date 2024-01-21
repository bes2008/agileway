package com.jn.agileway.test.util.os.virtualization;

import java.util.Map;

public class RuntimeContainer {
    /**
     * 容器类型，目前最为流行的是 docker
     */
    private String type;
    private Map<String, String> props;

    public RuntimeContainer(String type){
        this.type = type;
    }

    public RuntimeContainer(String type, Map<String, String> props){
        this.type = type;
        this.props = props;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, String> getProps() {
        return props;
    }

    public void setProps(Map<String, String> props) {
        this.props = props;
    }
}
