package com.jn.agileway.audit.core.model;

import com.jn.easyjson.core.util.JSONs;
import com.jn.langx.configuration.Configuration;
import com.jn.langx.util.Objs;
import com.jn.langx.util.Strings;
import com.jn.langx.util.collection.CommonProps;
import com.jn.langx.util.hash.HashCodeBuilder;


/**
 * may be in any style: xml, yaml, database
 */
public class OperationDefinition extends CommonProps implements Configuration {
    public static final long serialVersionUID = 1L;
    private String id; // {required} the id , also the method full name
    private String code; // {required}  the operate code
    private String name; // {required}  the operate name
    private String type; // {optional}  the operate type
    private String module; // {optional}  the module
    private String description;// {optional} the operate description
    private OperationImportance importance;  // {optional} the importance
    private ResourceDefinition resourceDefinition;

    public String getCode() {
        return Strings.isEmpty(code) ? this.id : this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public OperationImportance getImportance() {
        return importance;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }


    public void setImportance(OperationImportance importance) {
        this.importance = importance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public ResourceDefinition getResourceDefinition() {
        if (resourceDefinition == null) {
            return ResourceDefinition.DEFAULT_DEFINITION;
        }
        return resourceDefinition;
    }

    public void setResourceDefinition(ResourceDefinition resourceDefinition) {
        this.resourceDefinition = resourceDefinition;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OperationDefinition that = (OperationDefinition) o;

        if (!Objs.equals(id, that.id)) {
            return false;
        }
        if (!Objs.equals(name, that.name)) {
            return false;
        }
        if (!Objs.equals(code, that.code)) {
            return false;
        }
        if (!Objs.equals(type, that.type)) {
            return false;
        }
        if (!Objs.equals(module, that.module)) {
            return false;
        }
        if (!Objs.equals(importance, that.importance)) {
            return false;
        }
        if (!Objs.equals(description, that.description)) {
            return false;
        }
        if (!Objs.equals(getResourceDefinition(), that.getResourceDefinition())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .with(id)
                .with(name)
                .with(code)
                .with(type)
                .with(importance)
                .with(description)
                .with(getResourceDefinition())
                .build();
    }

    @Override
    public String toString() {
        return JSONs.toJson(this);
    }
}
