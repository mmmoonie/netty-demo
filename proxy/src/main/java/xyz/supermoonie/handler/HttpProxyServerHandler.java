package xyz.supermoonie.handler;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import xyz.supermoonie.interceptor.Interceptor;
import xyz.supermoonie.server.HttpProxyServerConfig;
import xyz.supermoonie.util.InetUrlUtil;

import java.net.URL;
import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * 作为服务器端，处理来自客户端的请求
 *
 * @author supermoonie
 * @date 2019/1/24
 */
public class HttpProxyServerHandler extends ChannelInboundHandlerAdapter {

    private final static HttpResponseStatus SUCCESS = new HttpResponseStatus(200,
            "Connection established");

    private final HttpProxyServerConfig serverConfig;
    private final Interceptor interceptor;
    private ChannelFuture cf;
    private List requestList;
    private int status = 0;
    private boolean isSsl = false;
    private boolean isConnect;
    private String host;
    private int port;

    public HttpProxyServerHandler(HttpProxyServerConfig serverConfig, Interceptor interceptor) {
        this.serverConfig = serverConfig;
        this.interceptor = interceptor;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            if (0 == status) {
                InetUrlUtil.InetUrl inetUrl = InetUrlUtil.getProtocol(request);
                // bad request
                if (null == inetUrl) {
                    ctx.channel().close();
                    return;
                }
                status = 1;
                this.host = inetUrl.getHost();
                this.port = inetUrl.getPort();
                //建立代理握手
                if ("CONNECT".equalsIgnoreCase(request.method().name())) {
                    status = 2;
                    HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, SUCCESS);
                    ctx.writeAndFlush(response);
                    ctx.channel().pipeline().remove("HttpCodec");
                    return;
                }
            }
            if (request.uri().indexOf("/") != 0) {
                URL url = new URL(request.uri());
                request.setUri(url.getFile());
            }
            interceptor.beforeRequest(ctx.channel(), request);
        } else if (msg instanceof HttpContent) {
            HttpContent content = (HttpContent) msg;
            System.out.println(content.content().readCharSequence(content.content().readableBytes(), Charset.defaultCharset()));
        } else {
            // TODO 暂不考虑
            System.out.println(msg);
        }
        ByteBuf resp = Unpooled.copiedBuffer("hello world".getBytes());
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, resp);
        response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        ctx.writeAndFlush(response);
    }

    private void handleProxyData(Channel channel, Object msg, boolean isHttp)
            throws Exception {
        if (cf == null) {
            //connection异常 还有HttpContent进来，不转发
            if (isHttp && !(msg instanceof HttpRequest)) {
                return;
            }
            InetUrlUtil.InetUrl requestProto = new InetUrlUtil.InetUrl(host, port, isSsl);
            ChannelInitializer channelInitializer =
                    isHttp ? new HttpProxyInitializer(channel, requestProto)
                            : new TunnelProxyInitializer(channel);
            Bootstrap bootstrap = new Bootstrap();
            // 注册线程池
            bootstrap.group(serverConfig.getProxyLoopGroup())
                    // 使用NioSocketChannel来作为连接用的channel类
                    .channel(NioSocketChannel.class)
                    .handler(channelInitializer);
            requestList = new LinkedList();
            cf = bootstrap.connect(host, port);
            cf.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    future.channel().writeAndFlush(msg);
                    synchronized (requestList) {
                        requestList.forEach(obj -> future.channel().writeAndFlush(obj));
                        requestList.clear();
                        isConnect = true;
                    }
                } else {
                    requestList.forEach(obj -> ReferenceCountUtil.release(obj));
                    requestList.clear();
                    future.channel().close();
                    channel.close();
                }
            });
        } else {
            synchronized (requestList) {
                if (isConnect) {
                    cf.channel().writeAndFlush(msg);
                } else {
                    requestList.add(msg);
                }
            }
        }
    }

    public HttpProxyServerConfig getServerConfig() {
        return serverConfig;
    }
}
