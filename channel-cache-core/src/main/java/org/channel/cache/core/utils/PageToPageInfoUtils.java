package org.channel.cache.core.utils;

import org.channel.cache.core.dto.PageInfo;
import org.springframework.data.domain.Page;

import java.util.Collection;

/**
 * @author zhangchanglu
 * @since 2019/01/18 13:08.
 */
public class PageToPageInfoUtils {
    public static <T> PageInfo<T> convertTOPageInfo(Collection<T> collection, Page<?> page) {
        PageInfo<T> pageInfo = new PageInfo<>();
        pageInfo.setPageIndex(page.getNumber());
        pageInfo.setPageSize(page.getSize());
        pageInfo.setHasNext(page.hasNext());
        pageInfo.setTotalCount(page.getTotalElements());
        pageInfo.setTotalPages(page.getTotalPages());
        pageInfo.setResult(collection);
        return pageInfo;
    }
}
