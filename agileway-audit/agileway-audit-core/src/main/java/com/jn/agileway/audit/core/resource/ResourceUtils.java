package com.jn.agileway.audit.core.resource;

import com.jn.agileway.audit.core.annotation.ResourceMapping;
import com.jn.agileway.audit.core.model.Resource;
import com.jn.langx.util.Emptys;

public class ResourceUtils {
    /**
     * 判断是否 有 resourceName
     */
    public static boolean isValid(Resource resource) {
        if (Emptys.isEmpty(resource)) {
            return false;
        }
        return Emptys.isNotEmpty(resource.getResourceName());
    }

    /**
     * 判断是否 有 resourceId ,但没有 resourceName
     */
    public static boolean isOnlyResourceId(Resource resource) {
        if (!isValid(resource)) {
            if (Emptys.isEmpty(resource)) {
                return false;
            }
            return Emptys.isNotEmpty(resource.getResourceId());
        }
        return false;
    }

    public static boolean isDefaultResourceMapping(ResourceMapping mapping){
        if(mapping==null){
            return true;
        }
        if("".equals(mapping.type()) && Resource.RESOURCE_ID_DEFAULT_KEY.equals(mapping.id()) && Resource.RESOURCE_NAME_DEFAULT_KEY.equals(mapping.name())){
            return true;
        }
        return false;
    }
}
