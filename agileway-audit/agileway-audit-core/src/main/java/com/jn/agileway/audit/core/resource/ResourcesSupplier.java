package com.jn.agileway.audit.core.resource;

import com.jn.agileway.audit.core.model.Resource;
import com.jn.langx.util.collection.Collects;
import com.jn.langx.util.function.Consumer2;
import com.jn.langx.util.valuegetter.ValueGetter;

import java.util.ArrayList;
import java.util.List;

public class ResourcesSupplier<T> implements ValueGetter<List<T>, List<Resource>> {
    private List<ResourceSupplier<T>> resourceSuppliers = Collects.emptyArrayList();

    public ResourcesSupplier() {
    }

    public ResourcesSupplier(List<ResourceSupplier<T>> resourceSuppliers) {
        Collects.addAll(this.resourceSuppliers, resourceSuppliers);
    }

    public void addResourceSupplier(ResourceSupplier<T> supplier) {
        resourceSuppliers.add(supplier);
    }

    @Override
    public List<Resource> get(final List<T> args) {
        final List<Resource> resources = new ArrayList<Resource>();
        Collects.forEach(resourceSuppliers, new Consumer2<Integer, ResourceSupplier<T>>() {
            @Override
            public void accept(Integer index, ResourceSupplier<T> resourceSupplier) {
                T arg = args.get(index);
                resources.add(resourceSupplier.get(arg));
            }
        });
        return resources;
    }
}
