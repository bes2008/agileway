package com.jn.agileway.audit.core.model;

import java.io.Serializable;
import java.util.HashMap;

public class Resource extends HashMap<String, Object> implements Serializable {
    public static final long serialVersionUID = 1L;
    public static final String RESOURCE_ID = "resourceId";
    public static final String RESOURCE_ID_DEFAULT_KEY = "id";
    public static final String RESOURCE_TYPE = "resourceType";
    public static final String RESOURCE_TYPE_DEFAULT_KEY = "type";
    public static final String RESOURCE_NAME = "resourceName";
    public static final String RESOURCE_NAME_DEFAULT_KEY = "name";
    public static final String RESOURCE_LOADER = "entityLoader";

    public String getResourceId() {
        Object resourceId = get(RESOURCE_ID);
        if (resourceId != null) {
            return resourceId.toString();
        }
        return null;
    }

    public void setResourceId(String resourceId) {
        this.put(RESOURCE_ID, resourceId);
    }

    public String getResourceType() {
        Object resourceType = get(RESOURCE_TYPE);
        if (resourceType != null) {
            return resourceType.toString();
        }
        return null;
    }

    public void setResourceType(String resourceType) {
        this.put(RESOURCE_TYPE, resourceType);
    }

    public String getResourceName() {
        Object resourceName = get(RESOURCE_NAME);
        if (resourceName != null) {
            return resourceName.toString();
        }
        return null;
    }

    public void setResourceName(String resourceName) {
        this.put(RESOURCE_NAME, resourceName);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
