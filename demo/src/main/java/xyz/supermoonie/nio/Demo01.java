package xyz.supermoonie.nio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * @author wangchao
 * @date 2019/2/10 08:49
 */
public class Demo01 {

    public static void main(String[] args) throws IOException {
        ReadableByteChannel src = Channels.newChannel(System.in);
        WritableByteChannel dst = Channels.newChannel(System.out);
        channelCopy2(src, dst);
        src.close();
        dst.close();
    }

    public static void channelCopy1(ReadableByteChannel src, WritableByteChannel dst) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(16 * 1024);
        while (src.read(buffer) != -1) {
            buffer.flip();
            dst.write(buffer);
            buffer.compact();
        }
        buffer.flip();
        while (buffer.hasRemaining()) {
            dst.write(buffer);
        }
    }

    public static void channelCopy2(ReadableByteChannel src, WritableByteChannel dst) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(16 * 1024);
        while (src.read(buffer) != -1) {
            buffer.flip();
            while (buffer.hasRemaining()) {
                dst.write(buffer);
            }
            buffer.clear();
        }
    }
}
