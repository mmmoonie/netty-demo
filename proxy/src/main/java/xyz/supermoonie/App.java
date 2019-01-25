package xyz.supermoonie;

import xyz.supermoonie.server.HttpProxyServer;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws InterruptedException {
        new HttpProxyServer().start(9999);
    }
}
