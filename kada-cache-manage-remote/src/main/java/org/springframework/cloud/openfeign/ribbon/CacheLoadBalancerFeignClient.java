package org.springframework.cloud.openfeign.ribbon;

import com.google.common.base.Splitter;
import com.netflix.client.ClientException;
import com.netflix.client.config.IClientConfig;
import feign.Client;
import feign.Request;
import feign.Response;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;

import java.io.IOException;
import java.net.URI;
import java.util.*;

/**
 * @author zhangchanglu
 * @since 2019/01/16 20:50.
 */
public class CacheLoadBalancerFeignClient extends LoadBalancerFeignClient {
    private final Client delegate;
    private CachingManagerSpringLoadBalancerFactory lbClientFactory;
    private final String CACHE_APP_NAME = "cacheAppName";

    public CacheLoadBalancerFeignClient(Client delegate, CachingManagerSpringLoadBalancerFactory lbClientFactory, SpringClientFactory clientFactory) {
        super(delegate, lbClientFactory, clientFactory);
        this.delegate = delegate;
        this.lbClientFactory = lbClientFactory;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        try {
            URI asUri = URI.create(request.url());
            String clientName = asUri.getHost();
            URI uriWithoutHost = cleanUrl(request.url(), clientName);
            String cacheAppName = getCacheAppName(uriWithoutHost);
            CacheManageRibbonRequest ribbonRequest = new CacheManageRibbonRequest(
                    this.delegate, request, uriWithoutHost, cacheAppName);
            IClientConfig requestConfig = getClientConfig(options, clientName);
            CacheFeignLoadBalancer cacheFeignLoadBalancer = this.lbClientFactory.create(clientName);
            return cacheFeignLoadBalancer.executeWithLoadBalancer(ribbonRequest,
                    requestConfig).toResponse();
        } catch (ClientException e) {
            IOException io = findIOException(e);
            if (io != null) {
                throw io;
            }
            throw new RuntimeException(e);
        }
    }

    protected static class CacheManageRibbonRequest extends CacheFeignLoadBalancer.RibbonRequest implements Cloneable {
        CacheManageRibbonRequest(Client client, Request request, URI uri, Object loadBalancerKey) {
            super(client, request, uri);
            this.setLoadBalancerKey(loadBalancerKey);
        }
    }

    /**
     * get cache app name for uri
     *
     * @param asUri uri
     * @return cache app name
     */
    private String getCacheAppName(URI asUri) {
        String query = asUri.getQuery();
        Map<String, String> split = Splitter.on("&").withKeyValueSeparator("=").split(query);
        return split.get(CACHE_APP_NAME);
    }
}
