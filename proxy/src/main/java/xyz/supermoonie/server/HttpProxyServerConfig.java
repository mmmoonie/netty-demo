package xyz.supermoonie.server;

import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslContext;

/**
 * @author supermoonie
 * @date 2019/1/24
 */
public class HttpProxyServerConfig {

    private EventLoopGroup proxyLoopGroup;

    private SslContext clientSslCtx;

    public EventLoopGroup getProxyLoopGroup() {
        return proxyLoopGroup;
    }

    public void setProxyLoopGroup(EventLoopGroup proxyLoopGroup) {
        this.proxyLoopGroup = proxyLoopGroup;
    }

    public SslContext getClientSslCtx() {
        return clientSslCtx;
    }

    public void setClientSslCtx(SslContext clientSslCtx) {
        this.clientSslCtx = clientSslCtx;
    }
}
