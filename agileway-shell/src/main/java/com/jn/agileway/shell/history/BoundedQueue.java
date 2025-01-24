package com.jn.agileway.shell.history;

import com.jn.langx.util.Preconditions;
import com.jn.langx.util.collection.BoundedCollection;
import com.jn.langx.util.collection.forwarding.ForwardingList;

import java.util.*;

class BoundedQueue<E> extends ForwardingList<E> implements Queue<E>, BoundedCollection<E> {
    private int maxSize;
    private boolean indexable;
    private boolean pollIfFull;
    BoundedQueue(int maxCapacity){
        this(maxCapacity, false);
    }
    BoundedQueue(int maxCapacity, boolean pollIfFull){
        this(maxCapacity, pollIfFull,false);
    }

    BoundedQueue(int maxCapacity, boolean pollIfFull, boolean indexable){
        setDelegate(new LinkedList<E>());
        Preconditions.checkArgument(maxCapacity>=0);
        this.maxSize = maxCapacity;
        this.pollIfFull = pollIfFull;
        this.indexable = indexable;
    }
    @Override
    public boolean isFull() {
        return size()>=maxSize;
    }

    @Override
    public int maxSize() {
        return maxSize;
    }

    @Override
    public boolean add(E e) {
        if(e == null){
            throw new NullPointerException();
        }
        if(isFull()){
            if(pollIfFull){
                remove();
                return super.add(e);
            }else{
                throw new IllegalStateException();
            }
        }
        return super.add(e);
    }

    @Override
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean offer(E e) {
        return add(e);
    }

    @Override
    public E remove() {
        if(isEmpty()){
            throw new NoSuchElementException();
        }
        return super.remove(0);
    }

    @Override
    public E get(int index) {
        if(indexable) {
            return super.get(index);
        }else{
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        if(indexable) {
            return super.subList(fromIndex, toIndex);
        }
        else{
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public E poll() {
        if(isEmpty()){
            return null;
        }
        return super.remove(0);
    }

    @Override
    public E element() {
        if(isEmpty()) {
            return null;
        }
        return getDelegate().get(0);
    }

    @Override
    public E peek() {
        if(isEmpty()) {
            throw new NoSuchElementException();
        }
        return getDelegate().get(0);
    }

}
