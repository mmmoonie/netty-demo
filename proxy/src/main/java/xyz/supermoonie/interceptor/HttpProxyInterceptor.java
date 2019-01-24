package xyz.supermoonie.interceptor;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author supermoonie
 * @date 2019/1/24
 */
public class HttpProxyInterceptor implements Interceptor {
    @Override
    public void beforeRequest(Channel clientChannel, HttpRequest httpRequest) {

    }
}
