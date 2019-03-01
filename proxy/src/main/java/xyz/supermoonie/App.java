package xyz.supermoonie;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast("httpCodec", new HttpServerCodec());
                            ch.pipeline().addLast("httpObject", new HttpObjectAggregator(65536));
                            ch.pipeline().addLast("serverHandler", new HttpProxyHandler());
                        }
                    });
            // 绑定端口，同步等待成功
            ChannelFuture f = b.bind(9999).sync();
            System.out.println("the time server is start in port: 9999");
            // 等待服务端监听端口关闭
            f.channel().closeFuture().sync();
        } finally {
            // 退出
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

class HttpProxyHandler extends ChannelInboundHandlerAdapter {

    private String host;

    private int port;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            String host = request.headers().get("host");
            String[] temp = host.split(":");
            int port = 80;
            if (temp.length > 1) {
                port = Integer.parseInt(temp[1]);
            } else {
                if (request.uri().startsWith("https")) {
                    port = 443;
                }
            }
            this.host = temp[0];
            this.port = port;
            if ("CONNECT".equalsIgnoreCase(request.method().name())) {
                HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                ctx.writeAndFlush(response);
                ctx.pipeline().remove("httpCodec");
                ctx.pipeline().remove("httpObject");
                return;
            }
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(ctx.channel().eventLoop())
                    .channel(ctx.channel().getClass());
        }
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

class HttpProxyInitializer extends ChannelInitializer {

    @Override
    protected void initChannel(Channel ch) throws Exception {

    }
}
