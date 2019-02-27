package xyz.supermoonie.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author wangchao
 * @date 2019/2/24 06:49
 */
public class Demo04 {

    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.configureBlocking(false);
        sc.connect(new InetSocketAddress(7100));
        while (!sc.finishConnect()) {

        }
        ByteBuffer buffer = ByteBuffer.allocate(100);
        int len = sc.read(buffer);
        while (len != -1) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                System.out.print((char) buffer.get());
            }
            buffer.compact();
            len = sc.read(buffer);
        }
        sc.close();
    }
}
