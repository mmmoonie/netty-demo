package xyz.supermoonie.util;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author supermoonie
 * @date 2019/1/24
 */
public class InetUrlUtil {

    private static final Pattern HOST_PATTERN = Pattern.compile("^(?:https?://)?(?<host>[^/]*)/?.*$");

    private static final Pattern PORT_PATTERN = Pattern.compile("^(?:https?://)?(?<host>[^:]*)(?::(?<port>\\d+))?(/.*)?$");

    private InetUrlUtil() {
    }

    public static InetUrl getProtocol(HttpRequest httpRequest) {
        InetUrl inetUrl = new InetUrl();
        int port = -1;
        String hostStr = httpRequest.headers().get(HttpHeaderNames.HOST);
        if (hostStr == null) {
            Matcher matcher = HOST_PATTERN.matcher(httpRequest.uri());
            if (matcher.find()) {
                hostStr = matcher.group("host");
            } else {
                return null;
            }
        }
        String uriStr = httpRequest.uri();
        Matcher matcher = PORT_PATTERN.matcher(hostStr);
        //先从host上取端口号没取到再从uri上取端口号 issues#4
        String portTemp = null;
        if (matcher.find()) {
            inetUrl.setHost(matcher.group("host"));
            portTemp = matcher.group("port");
            if (portTemp == null) {
                matcher = PORT_PATTERN.matcher(uriStr);
                if (matcher.find()) {
                    portTemp = matcher.group("port");
                }
            }
        }
        if (portTemp != null) {
            port = Integer.parseInt(portTemp);
        }
        boolean isSsl = uriStr.indexOf("https") == 0 || hostStr.indexOf("https") == 0;
        if (port == -1) {
            if (isSsl) {
                port = 443;
            } else {
                port = 80;
            }
        }
        inetUrl.setPort(port);
        inetUrl.setSsl(isSsl);
        return inetUrl;
    }

    public static class InetUrl {

        private String host;

        private int port;

        private boolean ssl;

        public InetUrl() {
        }

        public InetUrl(String host, int port, boolean ssl) {
            this.host = host;
            this.port = port;
            this.ssl = ssl;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public boolean isSsl() {
            return ssl;
        }

        public void setSsl(boolean ssl) {
            this.ssl = ssl;
        }
    }
}
