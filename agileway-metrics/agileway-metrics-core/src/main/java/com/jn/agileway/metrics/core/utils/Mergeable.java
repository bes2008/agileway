package com.jn.agileway.metrics.core.utils;

public interface Mergeable<T> {

    /**
     * Merge the current value set with that of the supplied object. The supplied object
     * is considered the parent, and values in the callee's value set must override those
     * of the supplied object.
     * @param parent the object to merge with
     * @return the result of the merge operation
     */
    T merge(T parent);

}
