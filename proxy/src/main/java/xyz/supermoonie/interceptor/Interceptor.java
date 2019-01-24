package xyz.supermoonie.interceptor;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @author supermoonie
 * @date 2019/1/24
 */
public interface Interceptor {

    void beforeRequest(Channel clientChannel, HttpRequest httpRequest);
}
