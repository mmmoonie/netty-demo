package xyz.supermoonie;

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.file.Paths;
import java.util.concurrent.Future;

/**
 * @author moonie
 * @date 2018/2/22
 */
public class App {

    public static void main(String[] args) throws Exception {
        AsynchronousFileChannel fileChannel = AsynchronousFileChannel.open(Paths.get("C:\\markdown\\work\\TODO.md"));
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        Future<Integer> future = fileChannel.read(buffer, 0);
        System.out.println(future.get());
        buffer.flip();
        CharBuffer charBuffer = CharBuffer.allocate(1024);
        CharsetDecoder decoder = Charset.defaultCharset().newDecoder();
        decoder.decode(buffer, charBuffer, false);
        charBuffer.flip();
        String data = new String(charBuffer.array(), 0, charBuffer.limit());
        System.out.println(data);
        UnpooledByteBufAllocator.DEFAULT.directBuffer();
    }
}
