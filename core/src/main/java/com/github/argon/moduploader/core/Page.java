package com.github.argon.moduploader.core;


import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class Page<E> extends PageImpl<E> {
    private Page(List<E> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }

    private Page(List<E> content) {
        super(content);
    }

    public static <E> Page<E> of(List<E> content) {
        return new Page<>(content);
    }

    public static <E> Page<E> of(List<E> content, Pageable pageable, long total) {
        return new Page<>(content, pageable, total);
    }

    public static <E> Page<E> of(List<E> elements, Pageable pageable) {
        int total = elements.size();
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), total);

        return new Page<>(elements.subList(start, end), pageable, total);
    }
}
