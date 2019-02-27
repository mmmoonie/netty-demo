package xyz.supermoonie.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @author wangchao
 * @date 2019/2/24 06:44
 */
public class Demo03 {

    private static final String HELLO = "HELLO";

    public static void main(String[] args) throws IOException, InterruptedException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(HELLO.getBytes("UTF-8").length);
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.bind(new InetSocketAddress(7100));
        while (true) {
            SocketChannel sc = ssc.accept();
            if (null != sc) {
                System.out.println("Remote Address: " + sc.socket().getRemoteSocketAddress());
                byteBuffer.rewind();
                sc.write(byteBuffer);
                sc.close();
            } else {
                Thread.sleep(200L);
            }
        }
    }
}
