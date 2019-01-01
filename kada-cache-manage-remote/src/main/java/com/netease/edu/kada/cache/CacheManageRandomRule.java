package com.netease.edu.kada.cache;

import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.Server;

import java.util.List;

/**
 * @author zhangchanglu
 * @since 2018/12/31 17:39.
 */
public class CacheManageRandomRule extends RandomRule {

    @Override
    public Server choose(ILoadBalancer lb, Object key) {
        return super.choose(lb, key);
    }

}
