package xyz.supermoonie.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpClientCodec;
import xyz.supermoonie.util.InetUrlUtil;

/**
 * HTTP代理，转发解码后的HTTP报文
 */
public class HttpProxyInitializer extends ChannelInitializer {

    private Channel clientChannel;
    private InetUrlUtil.InetUrl inetUrl;

    public HttpProxyInitializer(Channel clientChannel, InetUrlUtil.InetUrl inetUrl) {
        this.clientChannel = clientChannel;
        this.inetUrl = inetUrl;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        if (inetUrl.isSsl()) {
            ch.pipeline().addLast(
                    ((HttpProxyServerHandler) clientChannel.pipeline().get("serverHandle")).getServerConfig()
                            .getClientSslCtx()
                            .newHandler(ch.alloc(), inetUrl.getHost(), inetUrl.getPort()));
        }
        ch.pipeline().addLast("HttpCodec", new HttpClientCodec());
        ch.pipeline().addLast("proxyClientHandle", new HttpProxyClientHandler(clientChannel));
    }
}
