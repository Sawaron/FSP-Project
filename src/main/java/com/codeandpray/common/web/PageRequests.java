package com.codeandpray.common.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageRequests {
    private PageRequests() {}

    public static Pageable of(int page, int size, Sort sort) {
        return PageRequest.of(page, size, sort);
    }
}