package xyz.supermoonie.server;

import io.netty.channel.EventLoopGroup;

/**
 * @author supermoonie
 * @date 2019/1/24
 */
public class HttpProxyServerConfig {

    private EventLoopGroup proxyLoopGroup;

    public EventLoopGroup getProxyLoopGroup() {
        return proxyLoopGroup;
    }

    public void setProxyLoopGroup(EventLoopGroup proxyLoopGroup) {
        this.proxyLoopGroup = proxyLoopGroup;
    }
}
