package com.github.argon.moduploader.core;

import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.ListUtils;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;

public class Chunks<E> implements Iterable<List<E>> {
    private final List<List<E>> pages;
    private final int size;
    private final Duration delay;

    public Chunks(List<E> elements, int chunkSize) {
        this(elements, chunkSize, null);
    }

    public Chunks(List<E> elements, int chunkSize, @Nullable Duration delay) {
        this.delay = delay;
        this.pages = ListUtils.partition(elements, chunkSize);
        this.size = pages.size();
    }

    @Override
    @NonNull
    public Iterator<List<E>> iterator() {
        return new PageIterator();
    }

    @RequiredArgsConstructor
    private class PageIterator implements Iterator<List<E>> {
        int cursor;

        @Override
        public boolean hasNext() {
            return cursor!= size;
        }

        @Override
        public List<E> next() {
            // don't delay on the first element
            if (cursor > 0 && delay != null) {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            return pages.get(cursor);
        }
    }
}
