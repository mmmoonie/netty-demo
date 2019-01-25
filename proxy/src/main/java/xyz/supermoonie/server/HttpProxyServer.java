package xyz.supermoonie.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import xyz.supermoonie.handler.HttpProxyServerHandler;
import xyz.supermoonie.interceptor.Interceptor;

/**
 * @author supermoonie
 * @date 2019/1/24
 */
public class HttpProxyServer {

    private HttpProxyServerConfig serverConfig;

    private Interceptor interceptor;

    private EventLoopGroup acceptorGroup;
    private EventLoopGroup ioGroup;

    private void init() {
        if (null == serverConfig) {
            serverConfig = new HttpProxyServerConfig();
        }
        if (null == serverConfig.getProxyLoopGroup()) {
            serverConfig.setProxyLoopGroup(new NioEventLoopGroup());
        }
    }

    public void start(int port) throws InterruptedException {
        init();
        acceptorGroup = new NioEventLoopGroup();
        ioGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(acceptorGroup, ioGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast("HttpCodec", new HttpServerCodec());
                            ch.pipeline().addLast("HttpProxy", new HttpProxyServerHandler(serverConfig, interceptor));
                        }
                    });
            ChannelFuture future = bootstrap.bind(port).sync();
            future.channel().closeFuture().sync();
        } finally {
            ioGroup.shutdownGracefully();
            acceptorGroup.shutdownGracefully();
        }
    }

    public HttpProxyServerConfig getServerConfig() {
        return serverConfig;
    }

    public HttpProxyServer setServerConfig(HttpProxyServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        return this;
    }

    public Interceptor getInterceptor() {
        return interceptor;
    }

    public HttpProxyServer setInterceptor(Interceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    public void close() {
        serverConfig.getProxyLoopGroup().shutdownGracefully();
        ioGroup.shutdownGracefully();
        acceptorGroup.shutdownGracefully();
    }
}
