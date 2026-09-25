package com.codeandpray.common.web;

import com.codeandpray.common.exception.BusinessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageRequests {
    private PageRequests() {
    }

    public static Pageable of(int page, int size, Sort sort) {
        if (page < 0 || page > 10000 || size < 1 || size > 100) {
            throw BusinessException.badRequest("Допустимы page от 0 до 10000 и size от 1 до 100");
        }
        return PageRequest.of(page, size, sort);
    }
}