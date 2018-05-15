package xyz.supermoonie.guid.ch10;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.CharsetUtil;

import java.io.File;
import java.net.URI;

/**
 *
 * @author Administrator
 * @date 2018/5/16 0016
 */
public class HttpFileServer {

    private static final String DEFAULT_URL = "C:/Users/Administrator/IdeaProjects/netty-demo/";

    public static void main(String[] args) throws Exception {
        int port = 7100;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
        new HttpFileServer().run(port);
    }

    public void run(final int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                            ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65535));
                            ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                            ch.pipeline().addLast("http-chunk", new ChunkedWriteHandler());
                            ch.pipeline().addLast("httpFileServerHandler", new HttpFileServerHandler());
                        }
                    });
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    private class HttpFileServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

        @Override
        protected void messageReceived(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
            if (!request.getDecoderResult().isSuccess()) {
                sendError(ctx, HttpResponseStatus.BAD_REQUEST);
                return;
            }
            if (request.getMethod() != HttpMethod.GET) {
                sendError(ctx, HttpResponseStatus.METHOD_NOT_ALLOWED);
                return;
            }
            final String uri = request.getUri();
            URI httpUri = new URI(uri);
            final String path = httpUri.getPath();

        }

        private void sendList(ChannelHandlerContext ctx, File dir) {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html;charset=UTF-8");
            StringBuffer buffer = new StringBuffer();
            buffer.append("<html><body><ul>");
            buffer.append("<li>链接：<a href='../'>..</a></li>\r\n");
            for (File file : dir.listFiles()) {
                buffer.append("<li>链接：<a href='../'>");
                buffer.append(file.getName());
                buffer.append("</a></li>\r\n");
            }
            buffer.append("</ul></body></html>");
            ByteBuf buf = Unpooled.copiedBuffer(buffer, CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }

        private void sendError(ChannelHandlerContext ctx, HttpResponseStatus responseStatus) {
            FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseStatus);
            response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html;charset=UTF-8");
            StringBuffer buffer = new StringBuffer();
            buffer.append("<html><body><h1>");
            buffer.append(responseStatus.reasonPhrase());
            buffer.append("</h1></body></html>");
            ByteBuf buf = Unpooled.copiedBuffer(buffer, CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            buf.release();
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
