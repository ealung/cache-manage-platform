package com.netease.edu.kada.cache.core.dto;

import lombok.Data;

import java.util.Collection;

/**
 * @author zhangchanglu
 * @since 2019/01/18 13:02.
 */
@Data
public class PageInfo<T> {
    private int pageIndex;
    private int pageSize;
    private boolean hasNext;
    private int totalPages;
    private long totalCount;
    private Collection<T> result;

    public static <T> PageInfo<T> create(Collection<T> result) {
        PageInfo<T> pageInfo = new PageInfo<>();
        pageInfo.setResult(result);
        pageInfo.setTotalPages(1);
        pageInfo.setTotalCount(result.size());
        pageInfo.setHasNext(false);
        pageInfo.setPageSize(result.size());
        pageInfo.setPageIndex(1);
        return pageInfo;
    }
}
