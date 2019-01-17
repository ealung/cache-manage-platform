package com.netease.edu.kada.cache.config;

import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.RandomRule;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * @author zhangchanglu
 * @since 2018/12/31 17:39.
 */
public class CacheManageRandomRule extends RandomRule {
    Random rand;

    public CacheManageRandomRule() {
        rand = new Random();
    }

    @Override
    public Server choose(ILoadBalancer lb, Object key) {
        if (lb == null) {
            return null;
        }
        Server server = null;

        while (server == null) {
            if (Thread.interrupted()) {
                return null;
            }
            List<Server> upList = getCacheClient(lb.getReachableServers(), key.toString());
            List<Server> allList = getCacheClient(lb.getAllServers(), key.toString());

            int serverCount = allList.size();
            if (serverCount == 0) {
                /*
                 * No servers. End regardless of pass, because subsequent passes
                 * only get more restrictive.
                 */
                return null;
            }

            int index = rand.nextInt(serverCount);
            server = upList.get(index);

            if (server == null) {
                /*
                 * The only time this should happen is if the server list were
                 * somehow trimmed. This is a transient condition. Retry after
                 * yielding.
                 */
                Thread.yield();
                continue;
            }

            if (server.isAlive()) {
                return (server);
            }

            // Shouldn't actually happen.. but must be transient or a bug.
            server = null;
            Thread.yield();
        }

        return server;
    }

    public List<Server> getCacheClient(List<Server> list, String cacheName) {
        return list;
        /*return list.stream().filter(server -> {
            String s = ((DiscoveryEnabledServer) server).getInstanceInfo().getMetadata().get("cacheName");
            return cacheName.equals(s);
        }).collect(Collectors.toList());*/
    }
}
