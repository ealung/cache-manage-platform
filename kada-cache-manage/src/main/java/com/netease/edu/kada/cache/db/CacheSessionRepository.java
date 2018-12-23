package com.netease.edu.kada.cache.db;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zhangchanglu
 * @since 2018/11/08 22:02.
 */
@Repository
public interface CacheSessionRepository extends CrudRepository<CacheSessionEntity, Long> {
    CacheSessionEntity findByUserNameAndSessionId(String userName,String sessionId);

    int deleteByUserNameAndSessionId(String userName,String sessionId);
}
