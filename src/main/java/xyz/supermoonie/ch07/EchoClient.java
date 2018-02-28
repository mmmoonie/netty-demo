package xyz.supermoonie.ch07;

import xyz.supermoonie.ch05.DelimiterBasedFrameDecoderClient;

/**
 *
 * Created by Administrator on 2018/2/28 0028.
 */
public class EchoClient {



    public static void main(String[] args) throws Exception {
        int port = 7100;
        if (args != null && args.length > 0) {
            try {
                port = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                // 采用默认值
            }
        }
//        new EchoClient().connect("127.0.0.1", port);
    }
}
